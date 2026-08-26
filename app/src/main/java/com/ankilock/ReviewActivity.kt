package com.ankilock
    
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.text.Html
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ankilock.anki.AnkiDroidHelper
import com.ankilock.data.CardInfo
import com.ankilock.data.PreferencesManager
import com.ankilock.databinding.ActivityReviewBinding
import com.ankilock.service.AnkiNotificationService
    
class ReviewActivity : AppCompatActivity() { 
    
    private lateinit var binding: ActivityReviewBinding
    private lateinit var ankiHelper: AnkiDroidHelper
    private lateinit var prefs: PreferencesManager
    
    private var currentCard: CardInfo? = null
    private var isAnswerShown = false
    private var cardStartTime = 0L
    private var remainingCards = 0
    
    override fun onCreate(savedInstanceState: Bundle?) { 
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) { 
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else { 
            @Suppress("DEPRECATION")
            window.addFlags( 
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or 
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        ankiHelper = AnkiDroidHelper(this)
        prefs = PreferencesManager(this)
        
        setupListeners()
        loadNextCard()
    }
    
    private fun setupListeners() { 
        binding.btnClose.setOnClickListener { finish() }
        
        binding.cardContainer.setOnClickListener { 
            if (!isAnswerShown) showAnswer()
        }
        
        binding.btnShowAnswer.setOnClickListener { showAnswer() }
        
        binding.btnAgain.setOnClickListener { gradeCard(1) }
        binding.btnHard.setOnClickListener { gradeCard(2) }
        binding.btnGood.setOnClickListener { gradeCard(3) }
        binding.btnEasy.setOnClickListener { gradeCard(4) }
    }
    
    private fun loadNextCard() { 
        showLoading()
        isAnswerShown = false
        
        Thread { 
            val selectedDecks = prefs.getSelectedDeckIdsAsLongs()
            val deckId = if (selectedDecks.size == 1) selectedDecks.first() else null
            val card = ankiHelper.getNextDueCard(deckId)
            
            val totalDue = ankiHelper.getDueCount( 
                if (selectedDecks.isEmpty()) null else selectedDecks
            )
            
            runOnUiThread { 
                if (card != null) { 
                    currentCard = card
                    remainingCards = totalDue
                    cardStartTime = SystemClock.elapsedRealtime()
                    showQuestion(card)
                } else { 
                    showDone()
                }
            }
        }.start()
    }
    
    private fun showQuestion(card: CardInfo) { 
        binding.loadingContainer.visibility = View.GONE
        binding.doneContainer.visibility = View.GONE
        binding.cardContainer.visibility = View.VISIBLE
        binding.showAnswerContainer.visibility = View.VISIBLE
        binding.gradeContainer.visibility = View.GONE
        
        binding.tvDeckName.text = card.deckName
        binding.tvCardsRemaining.text = getString(R.string.cards_remaining, remainingCards)
        
        binding.tvQuestion.text = stripHtml(card.question)
        binding.tvAnswer.visibility = View.GONE
        binding.divider.visibility = View.GONE
        binding.tvTapHint.visibility = View.VISIBLE
        
        parseAndShowReviewTimes(card)
    }
    
    private fun showAnswer() { 
        isAnswerShown = true
        val card = currentCard ?: return
        
        binding.tvAnswer.text = stripHtml(card.answer)
        binding.tvAnswer.visibility = View.VISIBLE
        binding.divider.visibility = View.VISIBLE
        binding.tvTapHint.visibility = View.GONE
        binding.showAnswerContainer.visibility = View.GONE
        binding.gradeContainer.visibility = View.VISIBLE
    }
    
    private fun gradeCard(ease: Int) { 
        val card = currentCard ?: return
        val timeTaken = SystemClock.elapsedRealtime() - cardStartTime
        
        binding.gradeContainer.visibility = View.GONE
        showLoading()
        
        Thread { 
            val success = ankiHelper.answerCard( 
                card.noteId, 
                card.cardOrd, 
                ease, 
                timeTaken
            )
            
            runOnUiThread { 
                if (!success) { 
                    ankiHelper.openAnkiDroidReviewer()
                    finish()
                    return@runOnUiThread
                }
                
                AnkiNotificationService.update(this)
                loadNextCard()
            }
        }.start()
    }
    
    private fun parseAndShowReviewTimes(card: CardInfo) { 
        if (card.nextReviewTimes.isBlank()) return
        
        try { 
            val times = card.nextReviewTimes.split(",")
            if (times.isNotEmpty()) binding.tvAgainTime.text = times.getOrElse(0) { "" }.trim()
            if (times.size > 1) binding.tvHardTime.text = times.getOrElse(1) { "" }.trim()
            if (times.size > 2) binding.tvGoodTime.text = times.getOrElse(2) { "" }.trim()
            if (times.size > 3) binding.tvEasyTime.text = times.getOrElse(3) { "" }.trim()
        } catch (e: Exception) { 
            binding.tvAgainTime.text = ""
            binding.tvHardTime.text = ""
            binding.tvGoodTime.text = ""
            binding.tvEasyTime.text = ""
        }
    }
    
    private fun showLoading() { 
        binding.cardContainer.visibility = View.GONE
        binding.showAnswerContainer.visibility = View.GONE
        binding.gradeContainer.visibility = View.GONE
        binding.doneContainer.visibility = View.GONE
        binding.loadingContainer.visibility = View.VISIBLE
    }
    
    private fun showDone() { 
        binding.cardContainer.visibility = View.GONE
        binding.showAnswerContainer.visibility = View.GONE
        binding.gradeContainer.visibility = View.GONE
        binding.loadingContainer.visibility = View.GONE
        binding.doneContainer.visibility = View.VISIBLE
        
        AnkiNotificationService.update(this)
    }
    
    @Suppress("DEPRECATION")
    private fun stripHtml(html: String): CharSequence { 
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { 
            Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
        } else { 
            Html.fromHtml(html)
        }
    }
}
