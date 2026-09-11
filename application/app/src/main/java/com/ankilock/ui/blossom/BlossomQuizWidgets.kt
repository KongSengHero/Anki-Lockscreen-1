package com.ankilock.ui.blossom

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BlossomCircleAnswerButton( 
    isCheck: Boolean, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier 
) { 
    val context = androidx.compose.ui.platform.LocalContext.current 
    val view = LocalView.current 
    val faceColor = if (isCheck) BlossomColors.EmeraldGreen else BlossomColors.CoralRed 
    val lipColor = if (isCheck) BlossomColors.EmeraldGreenLip else BlossomColors.CoralRedLip 
    val icon = if (isCheck) Icons.Default.Check else Icons.Default.Close 
    
    Box( 
        modifier = modifier 
            .size(76.dp) 
            .clip(CircleShape) 
            .background(lipColor) 
            .clickable { 
                BlossomHaptics.click(context, view) 
                onClick() 
            } 
    ) { 
        Box( 
            modifier = Modifier 
                .size(72.dp) 
                .clip(CircleShape) 
                .background(faceColor), 
            contentAlignment = Alignment.Center 
        ) { 
            Icon( 
                imageVector = icon, 
                contentDescription = if (isCheck) "Correct" else "Incorrect", 
                tint = Color.White, 
                modifier = Modifier.size(36.dp) 
            ) 
        } 
    } 
} 

@Composable
fun BlossomMultipleChoiceCard( 
    text: String, 
    isSelected: Boolean, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier 
) { 
    val context = androidx.compose.ui.platform.LocalContext.current 
    val view = LocalView.current 
    val bgColor = if (isSelected) BlossomColors.EmeraldGreen else BlossomColors.SurfaceCard1 
    val borderColor = if (isSelected) BlossomColors.EmeraldGreen else BlossomColors.CardBorder 
    
    Box( 
        modifier = modifier 
            .clip(BlossomShapes.SquircleMedium) 
            .background(bgColor) 
            .border(1.5.dp, borderColor, BlossomShapes.SquircleMedium) 
            .clickable { 
                BlossomHaptics.click(context, view) 
                onClick() 
            } 
            .padding(vertical = 18.dp, horizontal = 12.dp), 
        contentAlignment = Alignment.Center 
    ) { 
        Text( 
            text = text, 
            color = Color.White, 
            fontSize = 18.sp, 
            fontWeight = FontWeight.Bold, 
            textAlign = TextAlign.Center 
        ) 
    } 
} 

@Composable
fun BlossomQuizOutcomeSheet( 
    isCorrect: Boolean, 
    explanation: String, 
    onContinue: () -> Unit, 
    modifier: Modifier = Modifier 
) { 
    val sheetColor = if (isCorrect) Color(0xFF133820) else Color(0xFF450A0A) 
    val titleText = if (isCorrect) "Great!" else "Incorrect" 
    val buttonColor = if (isCorrect) BlossomColors.EmeraldGreen else BlossomColors.CoralRed 
    val buttonLip = if (isCorrect) BlossomColors.EmeraldGreenLip else BlossomColors.CoralRedLip 
    
    Box( 
        modifier = modifier 
            .fillMaxWidth() 
            .clip(BlossomShapes.SquircleLarge) 
            .background(sheetColor) 
            .padding(20.dp) 
    ) { 
        Column(modifier = Modifier.fillMaxWidth()) { 
            Text( 
                text = titleText, 
                color = Color.White, 
                fontSize = 22.sp, 
                fontWeight = FontWeight.Bold 
            ) 
            Spacer(modifier = Modifier.height(6.dp)) 
            Text( 
                text = explanation, 
                color = if (isCorrect) Color(0xFF86EFAC) else Color(0xFFFCA5A5), 
                fontSize = 14.sp 
            ) 
            Spacer(modifier = Modifier.height(18.dp)) 
            BlossomTextButton( 
                text = "CONTINUE", 
                onClick = onContinue, 
                modifier = Modifier.fillMaxWidth(), 
                faceColor = buttonColor, 
                lipColor = buttonLip 
            ) 
        } 
    } 
} 
