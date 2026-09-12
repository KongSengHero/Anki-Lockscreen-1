package com.ankilock.ui.reading

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.navigationBarsPadding 
import androidx.compose.foundation.layout.statusBarsPadding 
import androidx.compose.material.icons.Icons 
import androidx.compose.material.icons.automirrored.filled.ArrowBack 
import androidx.compose.material.icons.automirrored.filled.ArrowForward 
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties 
import android.view.Gravity 
import android.view.ViewGroup 
import android.app.Activity 
import android.content.Context 
import android.content.ContextWrapper 
import androidx.compose.foundation.layout.WindowInsets 
import androidx.compose.foundation.layout.asPaddingValues 
import androidx.compose.foundation.layout.navigationBars 
import androidx.compose.foundation.layout.statusBars 
import androidx.compose.ui.platform.LocalContext 
import androidx.compose.ui.platform.LocalDensity 
import androidx.core.view.ViewCompat 
import androidx.core.view.WindowInsetsCompat 
import androidx.compose.runtime.DisposableEffect 
import androidx.compose.ui.platform.LocalView 
import androidx.compose.ui.window.DialogWindowProvider 
import androidx.core.view.WindowCompat 
import com.ankilock.data.StoryQuizQuestion 
import com.ankilock.ui.blossom.BlossomColors 

private fun Context.findActivity(): Activity? { 
    var current = this 
    while (current is ContextWrapper) { 
        if (current is Activity) return current 
        current = current.baseContext 
    } 
    return null 
} 

@Composable
fun ComprehensionQuizOverlay( 
    questions: List<StoryQuizQuestion>, 
    onDismiss: () -> Unit 
) { 
    if (questions.isEmpty()) return 
    
    var currentIndex by remember { mutableIntStateOf(0) } 
    val userAnswers = remember { mutableStateMapOf<Int, Int>() } 
    var isQuizCompleted by remember { mutableStateOf(false) } 
    
    val context = LocalContext.current 
    val density = LocalDensity.current 
    val activity = remember(context) { context.findActivity() } 
    val activityInsets = activity?.window?.decorView?.let { ViewCompat.getRootWindowInsets(it) } 
    val navBarPx = activityInsets?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0 
    val navBarDp = with(density) { navBarPx.toDp() } 
    val insetsNavDp = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() 
    val effectiveNavBottom = maxOf(navBarDp, insetsNavDp) 
    val safeBottomPadding = maxOf(effectiveNavBottom + 24.dp, 72.dp) 
    
    val statusBarPx = activityInsets?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0 
    val statusBarDp = with(density) { statusBarPx.toDp() } 
    val insetsStatusDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() 
    val effectiveStatusTop = maxOf(statusBarDp, insetsStatusDp) 
    val safeTopPadding = maxOf(effectiveStatusTop + 6.dp, 28.dp) 
    
    Dialog( 
        onDismissRequest = onDismiss, 
        properties = DialogProperties( 
            usePlatformDefaultWidth = false, 
            decorFitsSystemWindows = false 
        ) 
    ) { 
        val view = LocalView.current 
        DisposableEffect(view) { 
            var parent = view.parent 
            while (parent != null && parent !is DialogWindowProvider) { 
                parent = parent.parent 
            } 
            (parent as? DialogWindowProvider)?.window?.let { window -> 
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT) 
                window.setGravity(Gravity.TOP or Gravity.START) 
                window.statusBarColor = android.graphics.Color.TRANSPARENT 
                window.navigationBarColor = android.graphics.Color.TRANSPARENT 
                WindowCompat.setDecorFitsSystemWindows(window, false) 
            } 
            onDispose {} 
        } 
        
        Box( 
            modifier = Modifier 
                .fillMaxSize() 
                .background(BlossomColors.BackgroundDeep) 
        ) { 
            Column( 
                modifier = Modifier 
                    .fillMaxSize() 
                    .padding(horizontal = 20.dp) 
                    .padding(top = safeTopPadding, bottom = safeBottomPadding) 
            ) { 
                Row( 
                    modifier = Modifier.fillMaxWidth(), 
                    verticalAlignment = Alignment.CenterVertically, 
                    horizontalArrangement = Arrangement.SpaceBetween 
                ) { 
                    IconButton( 
                        onClick = onDismiss, 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(CircleShape) 
                            .background(BlossomColors.SurfaceElevated) 
                    ) { 
                        Icon( 
                            Icons.Filled.Close, 
                            contentDescription = "Close Quiz", 
                            tint = BlossomColors.TextPrimary, 
                            modifier = Modifier.size(20.dp) 
                        ) 
                    } 
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) { 
                        Text( 
                            text = "Comprehension Quiz", 
                            fontSize = 17.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = BlossomColors.TextPrimary 
                        ) 
                        if (!isQuizCompleted) { 
                            Text( 
                                text = "Question ${currentIndex + 1} of ${questions.size}", 
                                fontSize = 12.sp, 
                                color = BlossomColors.TextSecondary 
                            ) 
                        } 
                    } 
                    
                    IconButton( 
                        onClick = { 
                            userAnswers.clear() 
                            currentIndex = 0 
                            isQuizCompleted = false 
                        }, 
                        modifier = Modifier 
                            .size(38.dp) 
                            .clip(CircleShape) 
                            .background(BlossomColors.SurfaceElevated) 
                    ) { 
                        Icon( 
                            Icons.Filled.Refresh, 
                            contentDescription = "Restart", 
                            tint = BlossomColors.TextSecondary, 
                            modifier = Modifier.size(18.dp) 
                        ) 
                    } 
                } 
                
                Spacer(modifier = Modifier.height(10.dp)) 
                
                val progress = if (isQuizCompleted) 1f else (currentIndex.toFloat() / questions.size) 
                LinearProgressIndicator( 
                    progress = { progress }, 
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .height(6.dp) 
                        .clip(RoundedCornerShape(3.dp)), 
                    color = BlossomColors.SakuraRose, 
                    trackColor = BlossomColors.SurfaceCard2 
                ) 
                
                Spacer(modifier = Modifier.height(14.dp)) 
                
                AnimatedContent( 
                    targetState = isQuizCompleted, 
                    transitionSpec = { 
                        fadeIn(tween(200)) togetherWith fadeOut(tween(150)) 
                    }, 
                    label = "QuizStepTransition", 
                    modifier = Modifier 
                        .weight(1f) 
                        .fillMaxWidth() 
                ) { completed -> 
                    if (completed) { 
                        val correctCount = questions.count { userAnswers[it.id] == it.correctOptionIndex } 
                        val totalQuestions = questions.size 
                        val percent = if (totalQuestions > 0) (correctCount * 100) / totalQuestions else 0 
                        val isPassed = percent >= 80 
                        val isPerfect = correctCount == totalQuestions 
                        
                        Column( 
                            modifier = Modifier.fillMaxSize() 
                        ) { 
                            Column( 
                                modifier = Modifier 
                                    .weight(1f) 
                                    .fillMaxWidth() 
                                    .verticalScroll(rememberScrollState()), 
                                horizontalAlignment = Alignment.CenterHorizontally, 
                                verticalArrangement = Arrangement.spacedBy(14.dp) 
                            ) { 
                                Spacer(modifier = Modifier.height(12.dp)) 
                            
                            Surface( 
                                shape = CircleShape, 
                                color = if (isPassed) BlossomColors.BlossomGreenSurface else BlossomColors.BlossomRed.copy(alpha = 0.15f), 
                                border = BorderStroke( 
                                    2.dp, 
                                    if (isPassed) BlossomColors.BlossomGreen else BlossomColors.BlossomRed 
                                ), 
                                modifier = Modifier.size(90.dp) 
                            ) { 
                                Box(contentAlignment = Alignment.Center) { 
                                    Icon( 
                                        imageVector = if (isPassed) Icons.Filled.EmojiEvents else Icons.Filled.Close, 
                                        contentDescription = null, 
                                        tint = if (isPassed) BlossomColors.BlossomGreen else BlossomColors.BlossomRed, 
                                        modifier = Modifier.size(46.dp) 
                                    ) 
                                } 
                            } 
                            
                            Text( 
                                text = when { 
                                    isPerfect -> "素晴らしい！ Perfect Score!" 
                                    isPassed -> "合格！ You Passed!" 
                                    else -> "不合格 - You Failed" 
                                }, 
                                fontSize = 22.sp, 
                                fontWeight = FontWeight.Bold, 
                                color = if (isPassed) BlossomColors.TextPrimary else BlossomColors.BlossomRed 
                            ) 
                            
                            Text( 
                                text = "You got $correctCount out of $totalQuestions correct ($percent%)", 
                                fontSize = 15.sp, 
                                color = BlossomColors.TextSecondary 
                            ) 
                            
                            if (!isPassed) { 
                                Surface( 
                                    shape = RoundedCornerShape(8.dp), 
                                    color = BlossomColors.BlossomRed.copy(alpha = 0.1f), 
                                    border = BorderStroke(1.dp, BlossomColors.BlossomRed.copy(alpha = 0.3f)) 
                                ) { 
                                    Text( 
                                        text = "Passing requirement: 80%", 
                                        fontSize = 12.sp, 
                                        fontWeight = FontWeight.SemiBold, 
                                        color = BlossomColors.BlossomRed, 
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp) 
                                    ) 
                                } 
                            } 
                            
                            Spacer(modifier = Modifier.height(8.dp)) 
                            
                            Column( 
                                modifier = Modifier.fillMaxWidth(), 
                                verticalArrangement = Arrangement.spacedBy(10.dp) 
                            ) { 
                                questions.forEachIndexed { idx, q -> 
                                    val isCorrect = userAnswers[q.id] == q.correctOptionIndex 
                                    Surface( 
                                        shape = RoundedCornerShape(12.dp), 
                                        color = BlossomColors.SurfaceElevated, 
                                        border = BorderStroke( 
                                            1.dp, 
                                            if (isCorrect) BlossomColors.BlossomGreen.copy(alpha = 0.5f) 
                                            else BlossomColors.BlossomRed.copy(alpha = 0.5f) 
                                        ), 
                                        modifier = Modifier.fillMaxWidth() 
                                    ) { 
                                        Row( 
                                            modifier = Modifier.padding(14.dp), 
                                            verticalAlignment = Alignment.CenterVertically 
                                        ) { 
                                            Icon( 
                                                imageVector = if (isCorrect) Icons.Filled.Check else Icons.Filled.Close, 
                                                contentDescription = null, 
                                                tint = if (isCorrect) BlossomColors.BlossomGreen else BlossomColors.BlossomRed, 
                                                modifier = Modifier.size(20.dp) 
                                            ) 
                                            Spacer(modifier = Modifier.width(12.dp)) 
                                            Column(modifier = Modifier.weight(1f)) { 
                                                Text( 
                                                    text = "${idx + 1}. ${q.questionText}", 
                                                    fontSize = 13.sp, 
                                                    fontWeight = FontWeight.SemiBold, 
                                                    color = BlossomColors.TextPrimary 
                                                ) 
                                                val userOptIdx = userAnswers[q.id] 
                                                val correctOptText = q.options.getOrNull(q.correctOptionIndex) ?: "" 
                                                if (!isCorrect && userOptIdx != null) { 
                                                    val chosenOptText = q.options.getOrNull(userOptIdx) ?: "" 
                                                    Spacer(modifier = Modifier.height(4.dp)) 
                                                    Text( 
                                                        text = "Your answer: $chosenOptText", 
                                                        fontSize = 12.sp, 
                                                        color = BlossomColors.BlossomRed 
                                                    ) 
                                                    Text( 
                                                        text = "Correct: $correctOptText", 
                                                        fontSize = 12.sp, 
                                                        color = BlossomColors.BlossomGreen, 
                                                        fontWeight = FontWeight.Medium 
                                                    ) 
                                                } 
                                                if (q.explanation.isNotBlank()) { 
                                                    Spacer(modifier = Modifier.height(4.dp)) 
                                                    Text( 
                                                        text = q.explanation, 
                                                        fontSize = 11.sp, 
                                                        color = BlossomColors.TextSecondary 
                                                    ) 
                                                } 
                                            } 
                                        } 
                                    } 
                                } 
                            } 
                            
                            } 
                            
                            Spacer(modifier = Modifier.height(16.dp)) 
                            
                            Row( 
                                modifier = Modifier.fillMaxWidth(), 
                                horizontalArrangement = Arrangement.spacedBy(12.dp) 
                            ) { 
                                Box( 
                                    modifier = Modifier 
                                        .weight(1f) 
                                        .height(50.dp) 
                                        .clip(RoundedCornerShape(18.dp)) 
                                        .background(BlossomColors.SurfaceElevated) 
                                        .border(BorderStroke(1.dp, BlossomColors.CardBorder), shape = RoundedCornerShape(18.dp)) 
                                        .clickable { 
                                            userAnswers.clear() 
                                            currentIndex = 0 
                                            isQuizCompleted = false 
                                        }, 
                                    contentAlignment = Alignment.Center 
                                ) { 
                                    Text( 
                                        text = "Retake", 
                                        color = BlossomColors.TextPrimary, 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 15.sp 
                                    ) 
                                } 
                                
                                Box( 
                                    modifier = Modifier 
                                        .weight(1f) 
                                        .height(50.dp) 
                                        .clip(RoundedCornerShape(18.dp)) 
                                        .background(BlossomColors.SakuraRose.copy(alpha = 0.85f)) 
                                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)), shape = RoundedCornerShape(18.dp)) 
                                        .clickable { onDismiss() }, 
                                    contentAlignment = Alignment.Center 
                                ) { 
                                    Text( 
                                        text = "Done", 
                                        color = BlossomColors.BlossomWhite, 
                                        fontWeight = FontWeight.Bold, 
                                        fontSize = 15.sp 
                                    ) 
                                } 
                            } 
                        } 
                    } else { 
                        val currentQuestion = questions.getOrNull(currentIndex) ?: return@AnimatedContent 
                        val selectedOpt = userAnswers[currentQuestion.id] 
                        
                        Column( 
                            modifier = Modifier.fillMaxSize() 
                        ) { 
                            Column( 
                                modifier = Modifier 
                                    .weight(1f) 
                                    .fillMaxWidth() 
                                    .verticalScroll(rememberScrollState()), 
                                verticalArrangement = Arrangement.spacedBy(14.dp) 
                            ) { 
                                Surface( 
                                    shape = RoundedCornerShape(16.dp), 
                                    color = BlossomColors.SurfaceElevated, 
                                    border = BorderStroke(1.dp, BlossomColors.CardBorder), 
                                    modifier = Modifier.fillMaxWidth() 
                                ) { 
                                    Text( 
                                        text = currentQuestion.questionText, 
                                        fontSize = 16.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = BlossomColors.TextPrimary, 
                                        lineHeight = 23.sp, 
                                        modifier = Modifier.padding(18.dp) 
                                    ) 
                                } 
                                
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { 
                                    val optionLabels = listOf("A", "B", "C", "D") 
                                    currentQuestion.options.forEachIndexed { optIdx, optText -> 
                                        val isSelected = selectedOpt == optIdx 
                                        
                                        val bgColor = if (isSelected) BlossomColors.SakuraRoseContainer else BlossomColors.SurfaceCard1 
                                        val borderColor = if (isSelected) BlossomColors.SakuraRose else BlossomColors.CardBorderSubtle 
                                        val textColor = if (isSelected) BlossomColors.TextPrimary else BlossomColors.TextSecondary 
                                        
                                        Surface( 
                                            onClick = { 
                                                userAnswers[currentQuestion.id] = optIdx 
                                            }, 
                                            shape = RoundedCornerShape(16.dp), 
                                            color = bgColor, 
                                            border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor), 
                                            modifier = Modifier.fillMaxWidth() 
                                        ) { 
                                            Row( 
                                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp), 
                                                verticalAlignment = Alignment.CenterVertically 
                                            ) { 
                                                Surface( 
                                                    shape = CircleShape, 
                                                    color = if (isSelected) BlossomColors.SakuraRose else BlossomColors.SurfaceCard2, 
                                                    modifier = Modifier.size(30.dp) 
                                                ) { 
                                                    Box(contentAlignment = Alignment.Center) { 
                                                        Text( 
                                                            text = optionLabels.getOrElse(optIdx) { "${optIdx + 1}" }, 
                                                            fontSize = 13.sp, 
                                                            fontWeight = FontWeight.Bold, 
                                                            color = if (isSelected) BlossomColors.BlossomWhite else BlossomColors.TextSecondary 
                                                        ) 
                                                    } 
                                                } 
                                                Spacer(modifier = Modifier.width(12.dp)) 
                                                Text( 
                                                    text = optText, 
                                                    fontSize = 14.sp, 
                                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium, 
                                                    color = textColor, 
                                                    modifier = Modifier.weight(1f) 
                                                ) 
                                            } 
                                        } 
                                    } 
                                } 
                            } 
                            
                            Spacer(modifier = Modifier.height(16.dp)) 
                            val isFinalQuestion = currentIndex == questions.size - 1 
                            val hasAnsweredCurrent = userAnswers[currentQuestion.id] != null 
                            val nextButtonBackground = if (hasAnsweredCurrent) { 
                                BlossomColors.SakuraRose.copy(alpha = 0.85f) 
                            } else { 
                                BlossomColors.SurfaceElevated 
                            } 
                            val nextButtonBorder = if (hasAnsweredCurrent) { 
                                BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)) 
                            } else { 
                                BorderStroke(1.dp, BlossomColors.CardBorder) 
                            } 
                            val nextTextColor = if (hasAnsweredCurrent) { 
                                BlossomColors.BlossomWhite 
                            } else { 
                                BlossomColors.TextSecondary 
                            } 
                            
                            Row( 
                                modifier = Modifier.fillMaxWidth(), 
                                horizontalArrangement = Arrangement.spacedBy(10.dp) 
                            ) { 
                                if (currentIndex > 0) { 
                                    Box( 
                                        modifier = Modifier 
                                            .weight(1f) 
                                            .height(50.dp) 
                                            .clip(RoundedCornerShape(18.dp)) 
                                            .background(BlossomColors.SurfaceElevated) 
                                            .border( 
                                                BorderStroke(1.dp, BlossomColors.CardBorder), 
                                                shape = RoundedCornerShape(18.dp) 
                                            ) 
                                            .clickable { 
                                                currentIndex-- 
                                            }, 
                                        contentAlignment = Alignment.Center 
                                    ) { 
                                        Row( 
                                            verticalAlignment = Alignment.CenterVertically, 
                                            horizontalArrangement = Arrangement.Center 
                                        ) { 
                                            Icon( 
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                                                contentDescription = null, 
                                                tint = BlossomColors.TextSecondary, 
                                                modifier = Modifier.size(18.dp) 
                                            ) 
                                            Spacer(modifier = Modifier.width(6.dp)) 
                                            Text( 
                                                text = "Previous", 
                                                fontSize = 15.sp, 
                                                fontWeight = FontWeight.SemiBold, 
                                                color = BlossomColors.TextSecondary 
                                            ) 
                                        } 
                                    } 
                                } 
                                
                                Box( 
                                    modifier = Modifier 
                                        .weight(if (currentIndex > 0) 1.5f else 1f) 
                                        .height(50.dp) 
                                        .clip(RoundedCornerShape(18.dp)) 
                                        .background(nextButtonBackground) 
                                        .border(nextButtonBorder, shape = RoundedCornerShape(18.dp)) 
                                        .clickable(enabled = hasAnsweredCurrent) { 
                                            if (!isFinalQuestion) { 
                                                currentIndex++ 
                                            } else { 
                                                isQuizCompleted = true 
                                            } 
                                        }, 
                                    contentAlignment = Alignment.Center 
                                ) { 
                                    Row( 
                                        verticalAlignment = Alignment.CenterVertically, 
                                        horizontalArrangement = Arrangement.Center 
                                    ) { 
                                        Text( 
                                            text = if (isFinalQuestion) "Submit Quiz" else "Next Question", 
                                            fontSize = 15.sp, 
                                            fontWeight = FontWeight.Bold, 
                                            color = nextTextColor 
                                        ) 
                                        Spacer(modifier = Modifier.width(8.dp)) 
                                        Icon( 
                                            imageVector = if (isFinalQuestion) Icons.Filled.Check else Icons.AutoMirrored.Filled.ArrowForward, 
                                            contentDescription = null, 
                                            tint = nextTextColor, 
                                            modifier = Modifier.size(18.dp) 
                                        ) 
                                    } 
                                } 
                            } 
                        } 
                    } 
                } 
            } 
        } 
    } 
} 
