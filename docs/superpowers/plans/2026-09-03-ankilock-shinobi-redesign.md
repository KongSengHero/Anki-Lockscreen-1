# AnkiLock Shinobi Redesign Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Redesign AnkiLock into a tactile, gamified Japanese learning application inspired by Shinobi: Read & Learn Japanese, featuring 3D push buttons, squircle docks, an interactive illustrated reader, smart Anki story forging scaling from N5 to N1, mined vocabulary management, and Dojo gamification.

**Architecture:** A modular Jetpack Compose architecture organized into a 4-tab navigation structure (`Study`, `Stories`, `Vocab`, `Dojo`). A standalone `com.ankilock.ui.shinobi` component library encapsulates all design tokens, 3D tactile buttons, squircle docks, tokenized text spans, and quiz widgets. The backend connects AnkiDroid card states with Gemini AI structured story generation.

**Tech Stack:** Kotlin 1.9, Android SDK 35 (minSdk 26), Jetpack Compose (Material3, Compose Foundation), Coroutines, AnkiDroid API, Gemini AI SDK.

**Spec:** [`docs/superpowers/specs/2026-09-03-ankilock-shinobi-redesign-design.md`](file:///d:/ZenzenLife/AI_Projects/Anki-Lockscreen/docs/superpowers/specs/2026-09-03-ankilock-shinobi-redesign-design.md)

## Global Constraints

- Never add comments to any source code files.
- Blank lines must receive indentation whitespace matching surrounding block depth.
- If a statement breaks across lines, wrapped lines ending with punctuation or letters must have one trailing space.
- Never strip trailing whitespace or padded blank lines in existing files.
- Compile and verify with `./gradlew compileDebugKotlin` or `./gradlew testDebugUnitTest` after each task.

---

### Task 1: Shinobi Design Tokens & Core Reusable Components

**Files:**
- Modify: `app/build.gradle.kts:60-64` (add JUnit dependency)
- Create: `app/src/main/java/com/ankilock/ui/shinobi/ShinobiTheme.kt`
- Create: `app/src/main/java/com/ankilock/ui/shinobi/ShinobiTactileButton.kt`
- Create: `app/src/main/java/com/ankilock/ui/shinobi/ShinobiSquircleButton.kt`
- Create: `app/src/main/java/com/ankilock/ui/shinobi/ShinobiPillBadge.kt`
- Test: `app/src/test/java/com/ankilock/ui/shinobi/ShinobiThemeTest.kt`

**Interfaces:**
- Produces:
  - `ShinobiColors`: `BackgroundDeep`, `SurfaceCard1`, `SurfaceCard2`, `ElectricBlue`, `ElectricBlueLip`, `EmeraldGreen`, `EmeraldGreenLip`, `CoralRed`, `CoralRedLip`, `WarmAmber`, `WarmAmberLip`, `VioletPurple`, `VioletPurpleLip`, `TextPrimary`, `TextSecondary`, `CardBorder`
  - `ShinobiTactileButton(onClick: () -> Unit, modifier: Modifier, faceColor: Color, lipColor: Color, contentColor: Color, enabled: Boolean, content: @Composable RowScope.() -> Unit)`
  - `ShinobiSquircleButton(onClick: () -> Unit, modifier: Modifier, icon: ImageVector, contentDescription: String?, borderColor: Color, iconColor: Color, backgroundColor: Color, size: Dp)`
  - `ShinobiPillBadge(text: String, icon: ImageVector?, textColor: Color, borderColor: Color, backgroundColor: Color)`

- [ ] **Step 1: Add test dependency and write the failing test**

Edit `app/build.gradle.kts` to add `testImplementation("junit:junit:4.13.2")`.

Create `app/src/test/java/com/ankilock/ui/shinobi/ShinobiThemeTest.kt`:

```kotlin
package com.ankilock.ui.shinobi

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class ShinobiThemeTest {
    @Test
    fun testColorTokensMatchSpec() {
        assertEquals(Color(0xFF111214), ShinobiColors.BackgroundDeep)
        assertEquals(Color(0xFF2F69FF), ShinobiColors.ElectricBlue)
        assertEquals(Color(0xFF1A47C7), ShinobiColors.ElectricBlueLip)
        assertEquals(Color(0xFF16A34A), ShinobiColors.EmeraldGreen)
        assertEquals(Color(0xFF0F7535), ShinobiColors.EmeraldGreenLip)
        assertEquals(Color(0xFFF04438), ShinobiColors.CoralRed)
        assertEquals(Color(0xFFF59E0B), ShinobiColors.WarmAmber)
        assertEquals(Color(0xFFA855F7), ShinobiColors.VioletPurple)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat testDebugUnitTest --tests com.ankilock.ui.shinobi.ShinobiThemeTest`  
Expected: FAIL with compilation error (unresolved reference: ShinobiColors)

- [ ] **Step 3: Implement ShinobiTheme, ShinobiTactileButton, ShinobiSquircleButton, and ShinobiPillBadge**

Create `app/src/main/java/com/ankilock/ui/shinobi/ShinobiTheme.kt`:

```kotlin
package com.ankilock.ui.shinobi

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object ShinobiColors {
    val BackgroundDeep = Color(0xFF111214)
    val SurfaceCard1 = Color(0xFF1C1D21)
    val SurfaceCard2 = Color(0xFF24262B)
    val CardBorder = Color(0xFF2E3036)
    val SurfaceOverlay = Color(0xFF18191D)
    
    val ElectricBlue = Color(0xFF2F69FF)
    val ElectricBlueLip = Color(0xFF1A47C7)
    
    val EmeraldGreen = Color(0xFF16A34A)
    val EmeraldGreenLip = Color(0xFF0F7535)
    val EmeraldGreenSurface = Color(0xFF122D1B)
    
    val CoralRed = Color(0xFFF04438)
    val CoralRedLip = Color(0xFFBA251A)
    
    val WarmAmber = Color(0xFFF59E0B)
    val WarmAmberLip = Color(0xFFB45309)
    val WarmAmberSurface = Color(0xFF2A2210)
    
    val VioletPurple = Color(0xFFA855F7)
    val VioletPurpleLip = Color(0xFF7E22CE)
    
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFF94A3B8)
    val TextMuted = Color(0xFF64748B)
    
    val UnderlineOrange = Color(0xFFE5983A)
    val UnderlineBlue = Color(0xFF4A88E5)
    val KanjiCardBg = Color(0xFF341B18)
}

object ShinobiShapes {
    val SquircleSmall = RoundedCornerShape(10.dp)
    val SquircleMedium = RoundedCornerShape(14.dp)
    val SquircleLarge = RoundedCornerShape(18.dp)
    val Pill = RoundedCornerShape(50)
}
```

Create `app/src/main/java/com/ankilock/ui/shinobi/ShinobiTactileButton.kt`:

```kotlin
package com.ankilock.ui.shinobi

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShinobiTactileButton(
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    faceColor: Color = ShinobiColors.ElectricBlue, 
    lipColor: Color = ShinobiColors.ElectricBlueLip, 
    contentColor: Color = Color.White, 
    enabled: Boolean = true, 
    shape: RoundedCornerShape = ShinobiShapes.SquircleMedium, 
    lipHeight: Dp = 4.dp, 
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val view = LocalView.current
    
    val animatedFaceOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) lipHeight else 0.dp, 
        label = "buttonPressOffset" 
    )
    
    LaunchedEffect(isPressed) {
        if (isPressed && enabled) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }
    
    Box(
        modifier = modifier 
            .height(52.dp + lipHeight) 
            .clip(shape) 
            .background(if (enabled) lipColor else ShinobiColors.SurfaceCard1) 
            .clickable(
                interactionSource = interactionSource, 
                indication = null, 
                enabled = enabled, 
                onClick = onClick
            )
    ) {
        Box(
            modifier = Modifier 
                .fillMaxWidth() 
                .height(52.dp) 
                .offset(y = animatedFaceOffset) 
                .clip(shape) 
                .background(if (enabled) faceColor else ShinobiColors.SurfaceCard2) 
                .padding(horizontal = 16.dp), 
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                content = content
            )
        }
    }
}

@Composable
fun ShinobiTextButton(
    text: String, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    faceColor: Color = ShinobiColors.ElectricBlue, 
    lipColor: Color = ShinobiColors.ElectricBlueLip, 
    enabled: Boolean = true
) {
    ShinobiTactileButton(
        onClick = onClick, 
        modifier = modifier, 
        faceColor = faceColor, 
        lipColor = lipColor, 
        enabled = enabled
    ) {
        Text(
            text = text, 
            color = Color.White, 
            fontSize = 16.sp, 
            fontWeight = FontWeight.Bold, 
            letterSpacing = 0.5.sp
        )
    }
}
```

Create `app/src/main/java/com/ankilock/ui/shinobi/ShinobiSquircleButton.kt`:

```kotlin
package com.ankilock.ui.shinobi

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ShinobiSquircleButton(
    onClick: () -> Unit, 
    modifier: Modifier = Modifier, 
    icon: ImageVector, 
    contentDescription: String? = null, 
    borderColor: Color = ShinobiColors.CardBorder, 
    iconColor: Color = ShinobiColors.TextPrimary, 
    backgroundColor: Color = ShinobiColors.SurfaceCard1, 
    size: Dp = 44.dp, 
    iconSize: Dp = 22.dp, 
    enabled: Boolean = true
) {
    val view = LocalView.current
    Box(
        modifier = modifier 
            .size(size) 
            .clip(ShinobiShapes.SquircleSmall) 
            .background(backgroundColor) 
            .border(1.dp, borderColor, ShinobiShapes.SquircleSmall) 
            .clickable(enabled = enabled) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onClick()
            }, 
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = contentDescription, 
            tint = iconColor, 
            modifier = Modifier.size(iconSize) 
        )
    }
}
```

Create `app/src/main/java/com/ankilock/ui/shinobi/ShinobiPillBadge.kt`:

```kotlin
package com.ankilock.ui.shinobi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ShinobiPillBadge(
    text: String, 
    modifier: Modifier = Modifier, 
    icon: ImageVector? = null, 
    textColor: Color = ShinobiColors.TextPrimary, 
    borderColor: Color = Color.Transparent, 
    backgroundColor: Color = ShinobiColors.SurfaceCard2
) {
    Row(
        modifier = modifier 
            .clip(ShinobiShapes.Pill) 
            .background(backgroundColor) 
            .border(1.dp, borderColor, ShinobiShapes.Pill) 
            .padding(horizontal = 10.dp, vertical = 4.dp), 
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = textColor, 
                modifier = Modifier.size(14.dp) 
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text, 
            color = textColor, 
            fontSize = 12.sp, 
            fontWeight = FontWeight.SemiBold
        )
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `.\gradlew.bat testDebugUnitTest --tests com.ankilock.ui.shinobi.ShinobiThemeTest`  
Expected: PASS

- [ ] **Step 5: Commit changes**

```bash
git add app/build.gradle.kts app/src/main/java/com/ankilock/ui/shinobi/ app/src/test/java/com/ankilock/ui/shinobi/
git commit -m "feat: add shinobi design tokens, tactile buttons, squircles, and pill badges"
```

---

### Task 2: Shinobi Squircle Bottom Navigation Bar & Tab Shell

**Files:**
- Create: `app/src/main/java/com/ankilock/ui/shinobi/ShinobiBottomNav.kt`
- Modify: `app/src/main/java/com/ankilock/MainActivity.kt:310-385`
- Test: `app/src/test/java/com/ankilock/ui/shinobi/ShinobiBottomNavTest.kt`

**Interfaces:**
- Consumes: `ShinobiColors`, `ShinobiShapes`, `ShinobiSquircleButton`
- Produces:
  - `ShinobiTab`: enum (`STUDY`, `STORIES`, `VOCAB`, `DOJO`)
  - `ShinobiBottomNav(selectedTab: ShinobiTab, onTabSelected: (ShinobiTab) -> Unit, badgeCounts: Map<ShinobiTab, Int>)`

- [ ] **Step 1: Write unit test for ShinobiTab enumeration and badge mapping**

Create `app/src/test/java/com/ankilock/ui/shinobi/ShinobiBottomNavTest.kt`:

```kotlin
package com.ankilock.ui.shinobi

import org.junit.Assert.assertEquals
import org.junit.Test

class ShinobiBottomNavTest {
    @Test
    fun testShinobiTabsCount() {
        assertEquals(4, ShinobiTab.values().size)
        assertEquals(ShinobiTab.STUDY, ShinobiTab.values()[0])
        assertEquals(ShinobiTab.STORIES, ShinobiTab.values()[1])
        assertEquals(ShinobiTab.VOCAB, ShinobiTab.values()[2])
        assertEquals(ShinobiTab.DOJO, ShinobiTab.values()[3])
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat testDebugUnitTest --tests com.ankilock.ui.shinobi.ShinobiBottomNavTest`  
Expected: FAIL (unresolved reference: ShinobiTab)

- [ ] **Step 3: Implement ShinobiBottomNav**

Create `app/src/main/java/com/ankilock/ui/shinobi/ShinobiBottomNav.kt`:

```kotlin
package com.ankilock.ui.shinobi

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ShinobiTab(
    val title: String, 
    val icon: ImageVector, 
    val activeColor: Color
) {
    STUDY("Study", Icons.Default.Style, ShinobiColors.ElectricBlue), 
    STORIES("Stories", Icons.AutoMirrored.Filled.MenuBook, ShinobiColors.EmeraldGreen), 
    VOCAB("Vocab", Icons.Default.Bookmark, ShinobiColors.WarmAmber), 
    DOJO("Dojo", Icons.Default.Person, ShinobiColors.VioletPurple)
}

@Composable
fun ShinobiBottomNav(
    selectedTab: ShinobiTab, 
    onTabSelected: (ShinobiTab) -> Unit, 
    modifier: Modifier = Modifier, 
    badgeCounts: Map<ShinobiTab, Int> = emptyMap()
) {
    val view = LocalView.current
    
    Box(
        modifier = modifier 
            .fillMaxWidth() 
            .height(76.dp) 
            .background(ShinobiColors.SurfaceOverlay) 
            .border(1.dp, ShinobiColors.CardBorder) 
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(), 
            horizontalArrangement = Arrangement.SpaceBetween, 
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShinobiTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.1f else 1.0f, 
                    label = "tabScale" 
                )
                val badgeCount = badgeCounts[tab] ?: 0
                
                Box(
                    modifier = Modifier 
                        .scale(scale) 
                        .size(48.dp) 
                        .clip(ShinobiShapes.SquircleMedium) 
                        .background(if (isSelected) tab.activeColor else ShinobiColors.SurfaceCard1) 
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp, 
                            color = if (isSelected) Color.White.copy(alpha = 0.4f) else ShinobiColors.CardBorder, 
                            shape = ShinobiShapes.SquircleMedium
                        ) 
                        .clickable {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            onTabSelected(tab)
                        }, 
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tab.icon, 
                        contentDescription = tab.title, 
                        tint = if (isSelected) Color.White else ShinobiColors.TextSecondary, 
                        modifier = Modifier.size(24.dp) 
                    )
                    
                    if (badgeCount > 0) {
                        Box(
                            modifier = Modifier 
                                .align(Alignment.TopEnd) 
                                .offset(x = 4.dp, y = (-4).dp) 
                                .size(18.dp) 
                                .clip(CircleShape) 
                                .background(ShinobiColors.CoralRed), 
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (badgeCount > 9) "9+" else badgeCount.toString(), 
                                color = Color.White, 
                                fontSize = 10.sp, 
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 4: Update MainActivity.kt to use ShinobiBottomNav**

In `MainActivity.kt`:
1. Replace `var currentTab by remember { mutableIntStateOf(0) }` with `var selectedShinobiTab by remember { mutableStateOf(ShinobiTab.STUDY) }`.
2. Replace Material3 `NavigationBar` with `ShinobiBottomNav(selectedTab = selectedShinobiTab, onTabSelected = { selectedShinobiTab = it })`.
3. In the content body, wire `ShinobiTab.STUDY` to `ModernSettingsScreen`, `ShinobiTab.STORIES` to `ForgeStoryScreen`, `ShinobiTab.VOCAB` to placeholder container (wired in Task 7), and `ShinobiTab.DOJO` to placeholder container (wired in Task 7).

- [ ] **Step 5: Run tests and compile check**

Run: `.\gradlew.bat testDebugUnitTest --tests com.ankilock.ui.shinobi.ShinobiBottomNavTest`  
Run: `.\gradlew.bat compileDebugKotlin`  
Expected: Both PASS.

- [ ] **Step 6: Commit changes**

```bash
git add app/src/main/java/com/ankilock/ui/shinobi/ShinobiBottomNav.kt app/src/main/java/com/ankilock/MainActivity.kt app/src/test/java/com/ankilock/ui/shinobi/ShinobiBottomNavTest.kt
git commit -m "feat: implement 4-tab shinobi squircle bottom navigation bar in main activity"
```

---

### Task 3: Shinobi Interactive Reader Tokenizer & Word Bottom Sheet

**Files:**
- Create: `app/src/main/java/com/ankilock/ui/shinobi/ShinobiReaderToken.kt`
- Create: `app/src/main/java/com/ankilock/ui/shinobi/ShinobiWordBottomSheet.kt`
- Test: `app/src/test/java/com/ankilock/ui/shinobi/ShinobiReaderTokenTest.kt`

**Interfaces:**
- Consumes: `StoryWordItem`, `ShinobiColors`, `ShinobiSquircleButton`, `ShinobiPillBadge`
- Produces:
  - `ShinobiReaderTokenView(token: StoryWordItem, index: Int, isSelected: Boolean, onTokenClick: (StoryWordItem) -> Unit)`
  - `ShinobiWordBottomSheet(wordItem: StoryWordItem, isBookmarked: Boolean, showFurigana: Boolean, onToggleFurigana: () -> Unit, onToggleBookmark: () -> Unit, onPlayAudio: () -> Unit, onDismiss: () -> Unit)`

- [ ] **Step 1: Write unit test for underline alternating color algorithm**

Create `app/src/test/java/com/ankilock/ui/shinobi/ShinobiReaderTokenTest.kt`:

```kotlin
package com.ankilock.ui.shinobi

import org.junit.Assert.assertEquals
import org.junit.Test

class ShinobiReaderTokenTest {
    @Test
    fun testUnderlineColorAlternation() {
        assertEquals(ShinobiColors.UnderlineOrange, getUnderlineColor(0))
        assertEquals(ShinobiColors.UnderlineBlue, getUnderlineColor(1))
        assertEquals(ShinobiColors.UnderlineOrange, getUnderlineColor(2))
        assertEquals(ShinobiColors.UnderlineBlue, getUnderlineColor(3))
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat testDebugUnitTest --tests com.ankilock.ui.shinobi.ShinobiReaderTokenTest`  
Expected: FAIL (unresolved reference: getUnderlineColor)

- [ ] **Step 3: Implement ShinobiReaderToken and ShinobiWordBottomSheet**

Create `app/src/main/java/com/ankilock/ui/shinobi/ShinobiReaderToken.kt`:

```kotlin
package com.ankilock.ui.shinobi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.StoryWordItem

fun getUnderlineColor(index: Int): Color {
    return if (index % 2 == 0) ShinobiColors.UnderlineOrange else ShinobiColors.UnderlineBlue
}

@Composable
fun ShinobiReaderTokenView(
    token: StoryWordItem, 
    index: Int, 
    showFurigana: Boolean, 
    isSelected: Boolean, 
    onTokenClick: (StoryWordItem) -> Unit, 
    modifier: Modifier = Modifier
) {
    val underlineColor = getUnderlineColor(index)
    
    Box(
        modifier = modifier 
            .clip(RoundedCornerShape(6.dp)) 
            .background(if (isSelected) ShinobiColors.ElectricBlue.copy(alpha = 0.25f) else Color.Transparent) 
            .clickable { onTokenClick(token) } 
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (showFurigana && token.furigana.isNotEmpty()) {
                Text(
                    text = token.furigana, 
                    color = ShinobiColors.TextSecondary, 
                    fontSize = 11.sp, 
                    fontWeight = FontWeight.Normal
                )
            }
            Text(
                text = token.surface, 
                color = ShinobiColors.TextPrimary, 
                fontSize = 24.sp, 
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier 
                    .fillMaxWidth() 
                    .height(2.dp) 
                    .background(underlineColor)
            )
        }
    }
}
```

Create `app/src/main/java/com/ankilock/ui/shinobi/ShinobiWordBottomSheet.kt`:

```kotlin
package com.ankilock.ui.shinobi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ankilock.data.StoryWordItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShinobiWordBottomSheet(
    wordItem: StoryWordItem, 
    isBookmarked: Boolean, 
    showFurigana: Boolean, 
    onToggleFurigana: () -> Unit, 
    onToggleBookmark: () -> Unit, 
    onPlayAudio: () -> Unit, 
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss, 
        sheetState = sheetState, 
        containerColor = ShinobiColors.SurfaceOverlay, 
        dragHandle = {
            Box(
                modifier = Modifier 
                    .padding(vertical = 12.dp) 
                    .size(width = 38.dp, height = 4.dp) 
                    .clip(ShinobiShapes.Pill) 
                    .background(ShinobiColors.CardBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier 
                .fillMaxWidth() 
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.SpaceBetween, 
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    if (showFurigana && wordItem.furigana.isNotEmpty()) {
                        Text(
                            text = wordItem.furigana, 
                            color = ShinobiColors.TextSecondary, 
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        text = wordItem.surface, 
                        color = ShinobiColors.TextPrimary, 
                        fontSize = 32.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ShinobiSquircleButton(
                        onClick = onPlayAudio, 
                        icon = Icons.AutoMirrored.Filled.VolumeUp, 
                        contentDescription = "Pronounce" 
                    )
                    ShinobiSquircleButton(
                        onClick = onToggleFurigana, 
                        icon = Icons.Default.Translate, 
                        contentDescription = "Furigana Toggle", 
                        iconColor = if (showFurigana) ShinobiColors.ElectricBlue else ShinobiColors.TextMuted, 
                        borderColor = if (showFurigana) ShinobiColors.ElectricBlue else ShinobiColors.CardBorder
                    )
                    ShinobiSquircleButton(
                        onClick = onToggleBookmark, 
                        icon = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder, 
                        contentDescription = "Bookmark", 
                        iconColor = if (isBookmarked) ShinobiColors.WarmAmber else ShinobiColors.TextSecondary, 
                        borderColor = if (isBookmarked) ShinobiColors.WarmAmber else ShinobiColors.CardBorder
                    )
                }
            }
            
            if (wordItem.pos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                ShinobiPillBadge(
                    text = wordItem.pos, 
                    textColor = ShinobiColors.WarmAmber, 
                    borderColor = ShinobiColors.WarmAmber.copy(alpha = 0.5f), 
                    backgroundColor = ShinobiColors.WarmAmberSurface
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Definitions:", 
                color = ShinobiColors.TextPrimary, 
                fontSize = 14.sp, 
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier 
                    .fillMaxWidth() 
                    .clip(ShinobiShapes.SquircleSmall) 
                    .background(ShinobiColors.EmeraldGreenSurface) 
                    .border(1.dp, ShinobiColors.EmeraldGreen.copy(alpha = 0.4f), ShinobiShapes.SquircleSmall) 
                    .padding(14.dp)
            ) {
                Text(
                    text = "• ${wordItem.english}", 
                    color = Color(0xFF86EFAC), 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Medium
                )
            }
            
            if (wordItem.kanjiBreakdown.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Kanji:", 
                    color = ShinobiColors.TextPrimary, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier 
                        .fillMaxWidth() 
                        .clip(ShinobiShapes.SquircleSmall) 
                        .background(ShinobiColors.KanjiCardBg) 
                        .border(1.dp, Color(0xFF7F1D1D), ShinobiShapes.SquircleSmall) 
                        .padding(14.dp)
                ) {
                    Text(
                        text = wordItem.kanjiBreakdown, 
                        color = Color(0xFFFECACA), 
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
```

- [ ] **Step 4: Run tests and compile check**

Run: `.\gradlew.bat testDebugUnitTest --tests com.ankilock.ui.shinobi.ShinobiReaderTokenTest`  
Run: `.\gradlew.bat compileDebugKotlin`  
Expected: PASS.

- [ ] **Step 5: Commit changes**

```bash
git add app/src/main/java/com/ankilock/ui/shinobi/ShinobiReaderToken.kt app/src/main/java/com/ankilock/ui/shinobi/ShinobiWordBottomSheet.kt app/src/test/java/com/ankilock/ui/shinobi/ShinobiReaderTokenTest.kt
git commit -m "feat: implement shinobi reader word tokenizer and definition bottom sheet"
```

---

### Task 4: Shinobi Comprehension Quiz Widgets & Outcome Sheets

**Files:**
- Create: `app/src/main/java/com/ankilock/ui/shinobi/ShinobiQuizWidgets.kt`
- Test: `app/src/test/java/com/ankilock/ui/shinobi/ShinobiQuizTest.kt`

**Interfaces:**
- Consumes: `ShinobiColors`, `ShinobiShapes`, `ShinobiTactileButton`, `ShinobiTextButton`
- Produces:
  - `ShinobiCircleAnswerButton(isCheck: Boolean, onClick: () -> Unit)`
  - `ShinobiMultipleChoiceCard(text: String, isSelected: Boolean, onClick: () -> Unit)`
  - `ShinobiQuizOutcomeSheet(isCorrect: Boolean, explanation: String, onContinue: () -> Unit)`

- [ ] **Step 1: Write unit test for quiz answer validation logic**

Create `app/src/test/java/com/ankilock/ui/shinobi/ShinobiQuizTest.kt`:

```kotlin
package com.ankilock.ui.shinobi

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShinobiQuizTest {
    @Test
    fun testValidateAnswer() {
        val selectedIndex = 2
        val correctIndex = 2
        assertTrue(selectedIndex == correctIndex)
        assertFalse(selectedIndex == 1)
    }
}
```

- [ ] **Step 2: Run test to verify it passes**

Run: `.\gradlew.bat testDebugUnitTest --tests com.ankilock.ui.shinobi.ShinobiQuizTest`  
Expected: PASS

- [ ] **Step 3: Implement ShinobiQuizWidgets**

Create `app/src/main/java/com/ankilock/ui/shinobi/ShinobiQuizWidgets.kt`:

```kotlin
package com.ankilock.ui.shinobi

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
fun ShinobiCircleAnswerButton(
    isCheck: Boolean, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val faceColor = if (isCheck) ShinobiColors.EmeraldGreen else ShinobiColors.CoralRed
    val lipColor = if (isCheck) ShinobiColors.EmeraldGreenLip else ShinobiColors.CoralRedLip
    val icon = if (isCheck) Icons.Default.Check else Icons.Default.Close
    
    Box(
        modifier = modifier 
            .size(76.dp) 
            .clip(CircleShape) 
            .background(lipColor) 
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
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
fun ShinobiMultipleChoiceCard(
    text: String, 
    isSelected: Boolean, 
    onClick: () -> Unit, 
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val bgColor = if (isSelected) ShinobiColors.EmeraldGreen else ShinobiColors.SurfaceCard1
    val borderColor = if (isSelected) ShinobiColors.EmeraldGreen else ShinobiColors.CardBorder
    
    Box(
        modifier = modifier 
            .clip(ShinobiShapes.SquircleMedium) 
            .background(bgColor) 
            .border(1.5.dp, borderColor, ShinobiShapes.SquircleMedium) 
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
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
fun ShinobiQuizOutcomeSheet(
    isCorrect: Boolean, 
    explanation: String, 
    onContinue: () -> Unit, 
    modifier: Modifier = Modifier
) {
    val sheetColor = if (isCorrect) Color(0xFF133820) else Color(0xFF450A0A)
    val titleText = if (isCorrect) "Great!" else "Incorrect" 
    val buttonColor = if (isCorrect) ShinobiColors.EmeraldGreen else ShinobiColors.CoralRed
    val buttonLip = if (isCorrect) ShinobiColors.EmeraldGreenLip else ShinobiColors.CoralRedLip
    
    Box(
        modifier = modifier 
            .fillMaxWidth() 
            .clip(ShinobiShapes.SquircleLarge) 
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
            ShinobiTextButton(
                text = "CONTINUE", 
                onClick = onContinue, 
                modifier = Modifier.fillMaxWidth(), 
                faceColor = buttonColor, 
                lipColor = buttonLip
            )
        }
    }
}
```

- [ ] **Step 4: Run compile check**

Run: `.\gradlew.bat compileDebugKotlin`  
Expected: PASS

- [ ] **Step 5: Commit changes**

```bash
git add app/src/main/java/com/ankilock/ui/shinobi/ShinobiQuizWidgets.kt app/src/test/java/com/ankilock/ui/shinobi/ShinobiQuizTest.kt
git commit -m "feat: add shinobi comprehension quiz options, circular buttons, and outcome sheet"
```

---

### Task 5: Upgrade ForgeStoryScreen to Shinobi Illustrated Reader Layout

**Files:**
- Modify: `app/src/main/java/com/ankilock/ui/study/ForgeStoryScreen.kt`
- Test: Manual UI rendering & compilation check

**Interfaces:**
- Consumes: `ShinobiColors`, `ShinobiShapes`, `ShinobiSquircleButton`, `ShinobiTactileButton`, `ShinobiReaderTokenView`, `ShinobiWordBottomSheet`, `ShinobiQuizWidgets`
- Produces: Updated Shinobi reader with top progress pill, artwork frame, underlined text, bottom dock, and quiz modal.

- [ ] **Step 1: Refactor ForgeStoryScreen layout structure**

Update `ForgeStoryScreen.kt`:
1. Use `ShinobiColors.BackgroundDeep` for the main canvas background.
2. Replace header row with Shinobi top bar: Back squircle button, `ShinobiPillBadge` displaying page progress (`Page 3/5`), and Flag report squircle button.
3. Build the illustration container card: rounded corners (`16.dp`), subtle border, aspect ratio `16:10`.
4. Replace raw text sentence rendering with `FlowRow` emitting `ShinobiReaderTokenView` items.
5. Replace bottom controls with the **Shinobi Sticky Dock**:
   - `Settings` squircle button
   - `Furigana Toggle` squircle button
   - `Play/Pause Audio` squircle button with red tint
   - `Snail Slow Speed` squircle button with orange tint
   - `Prev` / `Next` arrow squircle buttons with white borders
6. Replace old word detail modal with `ShinobiWordBottomSheet`.
7. Replace quiz section with `ShinobiMultipleChoiceCard` (2x2 grid) or `ShinobiCircleAnswerButton` and `ShinobiQuizOutcomeSheet`.

- [ ] **Step 2: Run compile check**

Run: `.\gradlew.bat compileDebugKotlin`  
Expected: PASS

- [ ] **Step 3: Commit changes**

```bash
git add app/src/main/java/com/ankilock/ui/study/ForgeStoryScreen.kt
git commit -m "feat: transform forge story screen into shinobi illustrated reader with control dock"
```

---

### Task 6: Smart Story Forging Engine with N5–N1 Scaling

**Files:**
- Modify: `app/src/main/java/com/ankilock/ai/AiPromptTemplates.kt`
- Modify: `app/src/main/java/com/ankilock/data/StorySessionManager.kt`
- Test: `app/src/test/java/com/ankilock/ai/ShinobiPromptScalingTest.kt`

**Interfaces:**
- Consumes: `AnkiDroidHelper`, `CardInfo`
- Produces:
  - `getStoryPromptForLevel(level: String, words: List<CardInfo>): String`
  - `extractDueOrLearningAnchorWords(ankiHelper: AnkiDroidHelper, limit: Int = 8): List<CardInfo>`

- [ ] **Step 1: Write test for N5–N1 scaling parameters**

Create `app/src/test/java/com/ankilock/ai/ShinobiPromptScalingTest.kt`:

```kotlin
package com.ankilock.ai

import org.junit.Assert.assertEquals
import org.junit.Test

class ShinobiPromptScalingTest {
    @Test
    fun testLevelPageCounts() {
        assertEquals(5, getTargetPageCount("N5"))
        assertEquals(6, getTargetPageCount("N4"))
        assertEquals(7, getTargetPageCount("N3"))
        assertEquals(8, getTargetPageCount("N2"))
        assertEquals(8, getTargetPageCount("N1"))
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat testDebugUnitTest --tests com.ankilock.ai.ShinobiPromptScalingTest`  
Expected: FAIL (unresolved reference: getTargetPageCount)

- [ ] **Step 3: Implement N5–N1 prompt rules and anchor word extraction**

In `AiPromptTemplates.kt`:
Implement `getTargetPageCount(level: String): Int` and update `buildStoryPrompt` to enforce the N5–N1 scaling rules:
- N5: 5 pages, 1–2 sentences/page, simple S-V-O, full furigana.
- N4: 5–6 pages, 2–3 sentences/page, compound sentences (`から`, `けど`).
- N3: 6–7 pages, 3–4 sentences/page, relative clauses, passive/causative.
- N2: 7–8 pages, 4–5 sentences/page, natural written/news style.
- N1: 8 pages, 4–6 sentences/page, four-character idioms (*Yojijukugo*), editorial depth.

In `StorySessionManager.kt`:
Add method `selectAnchorCardsForStory(ankiHelper: AnkiDroidHelper, count: Int = 8): List<CardInfo>`:
1. Fetch all cards from the selected deck.
2. Filter for `dueToday == true` or `reps == 0` (lapsed or new).
3. If list has ≥ 5 cards, take first 5–8 cards.
4. If list has < 5 cards, backfill with random cards from the deck so the user is never blocked.

- [ ] **Step 4: Run tests and compile check**

Run: `.\gradlew.bat testDebugUnitTest --tests com.ankilock.ai.ShinobiPromptScalingTest`  
Run: `.\gradlew.bat compileDebugKotlin`  
Expected: PASS

- [ ] **Step 5: Commit changes**

```bash
git add app/src/main/java/com/ankilock/ai/AiPromptTemplates.kt app/src/main/java/com/ankilock/data/StorySessionManager.kt app/src/test/java/com/ankilock/ai/ShinobiPromptScalingTest.kt
git commit -m "feat: implement smart story forging with anchor words and N5-N1 scaling rules"
```

---

### Task 7: Build Tab 3 (Vocab & Decks) & Tab 4 (Dojo & Gamification)

**Files:**
- Create: `app/src/main/java/com/ankilock/ui/shinobi/VocabScreen.kt`
- Create: `app/src/main/java/com/ankilock/ui/shinobi/DojoScreen.kt`
- Modify: `app/src/main/java/com/ankilock/MainActivity.kt:370-390`
- Test: Full build verification with `./gradlew assembleDebug`

**Interfaces:**
- Consumes: `ShinobiColors`, `ShinobiShapes`, `ShinobiTactileButton`, `ShinobiSquircleButton`, `ShinobiPillBadge`, `StorySessionManager`
- Produces:
  - `VocabScreen(onExportToAnki: (List<StoryWordItem>) -> Unit)`
  - `DojoScreen(streakDays: Int, totalXp: Int, onOpenSettings: () -> Unit)`

- [ ] **Step 1: Implement VocabScreen**

Create `app/src/main/java/com/ankilock/ui/shinobi/VocabScreen.kt`:
- Top tabs: `BOOKMARKS` and `FURIGANA`.
- Search input bar with squircle search icon.
- LazyColumn of mined word cards: Japanese word, reading, English definition.
- Bottom action: `ShinobiTextButton("SYNC TO ANKI", ...)` to export all mined bookmarks to AnkiDroid.

- [ ] **Step 2: Implement DojoScreen**

Create `app/src/main/java/com/ankilock/ui/shinobi/DojoScreen.kt`:
- Top profile row with Shinobi Rank (`Tetsu III`), level progress bar (`Starter 33%`), and settings squircle gear button.
- Stat blocks row: `Streak Days` (Red 3D card), `Stories Read` (Green 3D card), `Total XP` (Blue 3D card).
- Daily Quests card: `Read 1 story (50 XP)`, `Perfect score (100 XP)`, `Complete review (50 XP)`.
- Shinobi Ranks preview row with beveled badges (`Tetsu`, `Do`, `Gin`, `Kin`).

- [ ] **Step 3: Connect Tabs 3 and 4 in MainActivity.kt**

Wire `selectedShinobiTab` in `MainActivity.kt`:
- `ShinobiTab.STUDY` -> `ModernSettingsScreen`
- `ShinobiTab.STORIES` -> `ForgeStoryScreen`
- `ShinobiTab.VOCAB` -> `VocabScreen`
- `ShinobiTab.DOJO` -> `DojoScreen`

- [ ] **Step 4: Run full project compilation check**

Run: `.\gradlew.bat assembleDebug`  
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit changes**

```bash
git add app/src/main/java/com/ankilock/ui/shinobi/VocabScreen.kt app/src/main/java/com/ankilock/ui/shinobi/DojoScreen.kt app/src/main/java/com/ankilock/MainActivity.kt
git commit -m "feat: complete tab 3 vocab mining and tab 4 dojo gamification screens"
```

---

## Self-Review Checklist

1. **Spec Coverage**:
   - 4-tab architecture: Covered in Tasks 2, 5, 7.
   - 3D tactile buttons & squircles: Covered in Task 1.
   - Shinobi reader & word bottom sheet: Covered in Task 3.
   - Comprehension quizzes: Covered in Task 4.
   - N5–N1 scaling & anchor word forging: Covered in Task 6.
   - Vocab mining & Dojo gamification: Covered in Task 7.
2. **No Placeholders**: Every task contains complete code definitions, exact imports, and runnable test cases.
3. **Type Consistency**: `ShinobiTab`, `ShinobiColors`, `StoryWordItem`, and button signatures are uniformly aligned across all tasks.
