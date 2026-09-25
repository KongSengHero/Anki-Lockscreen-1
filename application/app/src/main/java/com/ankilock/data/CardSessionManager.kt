package com.ankilock.data
    
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.service.AnkiNotificationService
import com.ankilock.widget.AnkiAppWidgetProvider
import java.util.concurrent.CopyOnWriteArrayList
    
object CardSessionManager { 
    
    var currentCard: CardInfo? = null
        private set
    private val reviewHistory = mutableListOf<Pair<CardInfo, Triple<Int, Int, Int>>>() 
    private val pendingReviewsInternal = mutableListOf<PendingReview>() 
    val pendingReviewsList: List<PendingReview> get() = synchronized(pendingReviewsInternal) { pendingReviewsInternal.toList() } 
    val pendingReviews: List<PendingReview> get() = pendingReviewsList 
    val pendingReviewsCount: Int get() = synchronized(pendingReviewsInternal) { pendingReviewsInternal.size } 
    val canUndo: Boolean get() = reviewHistory.isNotEmpty() 
    val previousCard: CardInfo? get() = reviewHistory.lastOrNull()?.first 
    var isRevealed: Boolean = false 
        private set 
    var currentStats: Triple<Int, Int, Int> = Triple(0, 0, 0) 
        private set 
    
    private val mainHandler by lazy { 
        try { 
            Handler(Looper.getMainLooper()) 
        } catch (e: Exception) { 
            null 
        } 
    } 
    private fun postToMain(action: () -> Unit) { 
        mainHandler?.post(action) ?: action() 
    } 
    private val listeners = CopyOnWriteArrayList<() -> Unit>() 
    
    fun addListener(listener: () -> Unit) { 
        if (!listeners.contains(listener)) { 
            listeners.add(listener) 
        } 
    } 
    
    fun removeListener(listener: () -> Unit) { 
        listeners.remove(listener) 
    } 
    
    fun getOrFetchCard(context: Context, forceRefresh: Boolean = false): CardInfo? { 
        if (currentCard == null || forceRefresh) { 
            val ankiHelper = AnkiDroidHelper(context) 
            val prefs = PreferencesManager(context) 
            val selectedDecks = prefs.getSelectedDeckIdsAsLongs() 
            currentCard = ankiHelper.getNextDueCard(selectedDecks) 
            currentStats = ankiHelper.getSelectedDeckStats(selectedDecks) 
            if (forceRefresh) isRevealed = false 
            notifyUi() 
        } 
        return currentCard 
    } 
    
    fun toggleReveal(context: Context) { 
        isRevealed = !isRevealed 
        notifyAllSurfaces(context) 
    } 
    
    fun reveal(context: Context) { 
        if (!isRevealed) { 
            isRevealed = true 
            notifyAllSurfaces(context) 
        } 
    } 
    
    fun hide(context: Context) { 
        if (isRevealed) { 
            isRevealed = false 
            notifyAllSurfaces(context) 
        } 
    } 
    
    fun gradeCard( 
        context: Context, 
        ease: Int, 
        timeTaken: Long = 5000L, 
        onComplete: ((CardInfo?) -> Unit)? = null 
    ) { 
        val card = currentCard ?: getOrFetchCard(context) 
        val ankiHelper = AnkiDroidHelper(context) 
        val prefs = PreferencesManager(context) 
        val selectedDecks = prefs.getSelectedDeckIdsAsLongs() 
        
        if (card != null) { 
            if (ease >= 2) { 
                prefs.recordNewCardLearned() 
            } 
            val oldStats = currentStats 
            reviewHistory.add(Pair(card, oldStats)) 
            Thread { 
                ankiHelper.answerCard(card.noteId, card.cardOrd, ease, timeTaken, card.deckId, card.buttonCount) 
                val nextCard = ankiHelper.getNextDueCard(selectedDecks, excludeNoteId = card.noteId) 
                val freshStats = ankiHelper.getSelectedDeckStats(selectedDecks) 
                postToMain { 
                    currentCard = nextCard 
                    currentStats = freshStats 
                    isRevealed = false 
                    notifyAllSurfaces(context) 
                    onComplete?.invoke(nextCard) 
                } 
            }.start() 
        } else { 
            currentCard = null 
            isRevealed = false 
            notifyAllSurfaces(context) 
            onComplete?.invoke(null) 
        } 
    } 
    
    fun suspendCurrentCard( 
        context: Context, 
        onComplete: ((CardInfo?) -> Unit)? = null 
    ) { 
        val card = currentCard ?: getOrFetchCard(context) 
        val ankiHelper = AnkiDroidHelper(context) 
        val prefs = PreferencesManager(context) 
        val selectedDecks = prefs.getSelectedDeckIdsAsLongs() 
        
        if (card != null) { 
            val oldStats = currentStats 
            reviewHistory.add(Pair(card, oldStats)) 
            val optNew = if (card.cardType == 0) (oldStats.first - 1).coerceAtLeast(0) else oldStats.first 
            val optLearn = if (card.cardType == 1) (oldStats.second - 1).coerceAtLeast(0) else oldStats.second 
            val optReview = if (card.cardType == 2) (oldStats.third - 1).coerceAtLeast(0) else oldStats.third 
            currentStats = Triple(optNew, optLearn, optReview) 
            notifyAllSurfaces(context) 
            Thread { 
                ankiHelper.suspendCard(card.noteId, card.cardOrd) 
                val nextCard = ankiHelper.getNextDueCard(selectedDecks, excludeNoteId = card.noteId) 
                val freshStats = ankiHelper.getSelectedDeckStats(selectedDecks) 
                
                postToMain { 
                    currentCard = nextCard 
                    currentStats = freshStats 
                    isRevealed = false 
                    notifyAllSurfaces(context) 
                    onComplete?.invoke(nextCard) 
                } 
            }.start() 
        } else { 
            onComplete?.invoke(null) 
        } 
    } 
    
    fun recordAnswered(card: CardInfo, oldStats: Triple<Int, Int, Int>) { 
        reviewHistory.add(Pair(card, oldStats)) 
    } 
    
    fun undoLastReview(context: Context) { 
        if (reviewHistory.isNotEmpty()) { 
            val (prevCard, prevStats) = reviewHistory.removeAt(reviewHistory.size - 1) 
            popLastPendingReview() 
            currentCard = prevCard 
            currentStats = prevStats 
            isRevealed = false 
            notifyAllSurfaces(context) 
        } 
    } 
    
    fun refresh(context: Context) { 
        pullSync(context) 
    } 
    
    fun pushSyncBlocking(context: Context, helper: AnkiDroidHelper? = null): Boolean { 
        val listToPush = synchronized(pendingReviewsInternal) { 
            val copy = pendingReviewsInternal.toList() 
            pendingReviewsInternal.clear() 
            copy 
        } 
        val ankiHelper = helper ?: AnkiDroidHelper(context) 
        val prefs = PreferencesManager(context) 
        val selectedDecks = prefs.getSelectedDeckIdsAsLongs() 
        var success = true 
        try { 
            for (rev in listToPush) { 
                ankiHelper.answerCard(rev.noteId, rev.cardOrd, rev.ease, rev.timeTaken, rev.deckId, rev.buttonCount) 
            } 
        } catch (e: Exception) { 
            success = false 
        } 
        val freshCard = ankiHelper.getNextDueCard(selectedDecks) 
        val freshStats = ankiHelper.getSelectedDeckStats(selectedDecks) 
        reviewHistory.clear() 
        postToMain { 
            currentCard = freshCard 
            currentStats = freshStats 
            isRevealed = false 
            notifyAllSurfaces(context) 
        } 
        return success 
    } 
    
    fun pushSync(context: Context, onComplete: ((Boolean) -> Unit)? = null) { 
        Thread { 
            val success = pushSyncBlocking(context) 
            postToMain { 
                onComplete?.invoke(success) 
            } 
        }.start() 
    } 
    
    fun pullSyncBlocking(context: Context, helper: AnkiDroidHelper? = null) { 
        synchronized(pendingReviewsInternal) { 
            pendingReviewsInternal.clear() 
        } 
        reviewHistory.clear() 
        val ankiHelper = helper ?: AnkiDroidHelper(context) 
        val prefs = PreferencesManager(context) 
        val selectedDecks = prefs.getSelectedDeckIdsAsLongs() 
        val freshCard = ankiHelper.getNextDueCard(selectedDecks) 
        val freshStats = ankiHelper.getSelectedDeckStats(selectedDecks) 
        postToMain { 
            currentCard = freshCard 
            currentStats = freshStats 
            isRevealed = false 
            notifyAllSurfaces(context) 
        } 
    } 
    
    fun pullSync(context: Context, onComplete: (() -> Unit)? = null) { 
        Thread { 
            pullSyncBlocking(context) 
            postToMain { 
                onComplete?.invoke() 
            } 
        }.start() 
    } 
    
    fun addPendingReview(review: PendingReview) { 
        synchronized(pendingReviewsInternal) { 
            pendingReviewsInternal.add(review) 
        } 
        notifyUi() 
    } 
    
    fun popLastPendingReview(): PendingReview? { 
        val removed = synchronized(pendingReviewsInternal) { 
            if (pendingReviewsInternal.isNotEmpty()) { 
                pendingReviewsInternal.removeAt(pendingReviewsInternal.size - 1) 
            } else null 
        } 
        notifyUi() 
        return removed 
    } 
    
    fun getPendingCountForDeck(deckId: Long): Int { 
        return synchronized(pendingReviewsInternal) { 
            pendingReviewsInternal.count { it.deckId == deckId } 
        } 
    } 
    
    fun clearPendingReviewsForDeck(deckId: Long) { 
        synchronized(pendingReviewsInternal) { 
            pendingReviewsInternal.removeAll { it.deckId == deckId } 
        } 
        notifyUi() 
    } 
    
    fun syncFromExternal(card: CardInfo?, revealed: Boolean, stats: Triple<Int, Int, Int>?) { 
        currentCard = card
        isRevealed = revealed
        if (stats != null) currentStats = stats
        notifyUi()
    }
    
    private fun notifyUi() { 
        try { 
            if (Looper.myLooper() == Looper.getMainLooper()) { 
                listeners.forEach { it.invoke() } 
            } else { 
                postToMain { listeners.forEach { it.invoke() } } 
            } 
        } catch (e: Exception) { 
            listeners.forEach { it.invoke() } 
        } 
    } 
    
    fun notifyAllSurfaces(context: Context) { 
        notifyUi()
        if (PreferencesManager(context).isServiceEnabled) { 
            AnkiNotificationService.update(context)
        }
        AnkiAppWidgetProvider.updateAllWidgets(context)
    }
}
