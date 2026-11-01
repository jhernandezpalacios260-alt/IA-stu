package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.AspectRatioOption
import com.example.model.AvatarEntity
import com.example.model.CyberPresets
import com.example.model.FramingOption
import com.example.model.LightingOption
import com.example.model.StyleOption
import com.example.ui.components.glassPanel
import com.example.ui.components.neonGlowBorder
import com.example.ui.components.scanlineAnimation
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassInputBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonGlow
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.RajdhaniFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidSurface

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreatorScreen(
    prompt: String,
    onPromptChange: (String) -> Unit,
    onClearPrompt: () -> Unit,
    selectedStyle: StyleOption,
    onSelectStyle: (StyleOption) -> Unit,
    selectedFraming: FramingOption,
    onSelectFraming: (FramingOption) -> Unit,
    selectedLighting: LightingOption,
    onSelectLighting: (LightingOption) -> Unit,
    selectedRatio: AspectRatioOption,
    onSelectRatio: (AspectRatioOption) -> Unit,
    isGenerating: Boolean,
    activeAvatar: AvatarEntity?,
    onGenerate: () -> Unit,
    onInspect: (AvatarEntity) -> Unit,
    onDownload: (AvatarEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Infinite transition for synthetic spinning HUD rings
    val spinTransition = rememberInfiniteTransition(label = "spin")
    val rotation by spinTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    val reverseRotation by spinTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reverseRotation"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // PREVIEW / RESULT DISPLAY SCREEN
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 280.dp, max = 460.dp)
                .aspectRatio(selectedRatio.aspectFloat, matchHeightConstraintsFirst = false)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black.copy(alpha = 0.6f))
                .neonGlowBorder(cornerRadius = 20.dp, isActive = activeAvatar != null || isGenerating)
                .testTag("avatar_preview_container"),
            contentAlignment = Alignment.Center
        ) {
            when {
                // 1. Loading Matrix Scanner Overlay
                isGenerating -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.88f))
                            .scanlineAnimation(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                // Outer rotating ring
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .rotate(rotation)
                                        .border(2.dp, NeonViolet.copy(alpha = 0.3f), CircleShape)
                                        .border(2.dp, Brush.sweepGradient(listOf(Color.Transparent, NeonViolet)), CircleShape)
                                )
                                // Inner counter-rotating ring
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .rotate(reverseRotation)
                                        .border(2.dp, NeonCyan.copy(alpha = 0.3f), CircleShape)
                                        .border(2.dp, Brush.sweepGradient(listOf(Color.Transparent, NeonCyan)), CircleShape)
                                )
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = null,
                                    tint = NeonGlow,
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "SINTETIZANDO...",
                                fontFamily = RajdhaniFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 2.sp,
                                color = NeonGlow
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "FLUX Neural Matrix Rendering",
                                fontFamily = RajdhaniFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = NeonViolet,
                                trackColor = VoidSurface
                            )
                        }
                    }
                }

                // 2. Active Generated Image
                activeAvatar != null -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(activeAvatar.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Avatar generado por IA",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Floating Quick Actions Bar
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Inspect Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                                    .clickable { onInspect(activeAvatar) }
                                    .testTag("quick_inspect_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CropFree,
                                        contentDescription = "Inspeccionar",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Inspeccionar",
                                        fontFamily = RajdhaniFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            // Download Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(NeonViolet, NeonGlow)
                                        )
                                    )
                                    .clickable { onDownload(activeAvatar) }
                                    .testTag("quick_download_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = "Descargar HD",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Descargar HD",
                                        fontFamily = RajdhaniFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Empty State
                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .border(1.dp, GlassBorder, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonGlow.copy(alpha = 0.8f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Configura los parámetros del avatar",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Presiona \"Generar con IA\" para renderizar la imagen.",
                            fontFamily = RajdhaniFontFamily,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        // PROMPT INPUT BOX
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassPanel(cornerRadius = 16.dp)
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = NeonGlow,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DESCRIPCIÓN DEL SUJETO",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = TextSecondary
                        )
                    }
                    if (prompt.isNotEmpty()) {
                        Text(
                            text = "Limpiar",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier
                                .clickable { onClearPrompt() }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassInputBg)
                        .border(1.dp, NeonViolet.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    if (prompt.isEmpty()) {
                        Text(
                            text = "Ej: Cyberpunk synthetic model, robotic joints, sharp eyes, silver braided hair...",
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = TextMuted,
                                lineHeight = 18.sp
                            )
                        )
                    }
                    BasicTextField(
                        value = prompt,
                        onValueChange = onPromptChange,
                        textStyle = TextStyle(
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        ),
                        cursorBrush = SolidColor(NeonGlow),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 44.dp, max = 100.dp)
                            .testTag("prompt_input_field")
                    )
                }
            }
        }

        // ADVANCED CONFIGURATION CONTROLS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .glassPanel(cornerRadius = 16.dp)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // Style Selector Grid
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = NeonGlow,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "TIPO DE AVATAR / ESTILO",
                            fontFamily = RajdhaniFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp,
                            color = NeonGlow
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2 Column Grid for 6 styles
                    val styles = CyberPresets.styles
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (i in styles.indices step 2) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (j in 0..1) {
                                    if (i + j < styles.size) {
                                        val style = styles[i + j]
                                        val isSelected = style.title == selectedStyle.title
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(
                                                    if (isSelected) NeonViolet.copy(alpha = 0.25f)
                                                    else GlassInputBg
                                                )
                                                .border(
                                                    width = if (isSelected) 1.5.dp else 1.dp,
                                                    color = if (isSelected) NeonViolet else GlassBorder,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable { onSelectStyle(style) }
                                                .padding(horizontal = 10.dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = style.title,
                                                    fontFamily = RajdhaniFontFamily,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    fontSize = 12.sp,
                                                    color = if (isSelected) NeonGlow else TextSecondary
                                                )
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = NeonGlow,
                                                        modifier = Modifier.size(16.dp)
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

                // Framing & Lighting Selectors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Framing Selector Dropdown
                    var framingExpanded by remember { mutableStateOf(false) }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = NeonGlow,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ENCUADRE",
                                fontFamily = RajdhaniFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = NeonGlow
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GlassInputBg)
                                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                                .clickable { framingExpanded = true }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = selectedFraming.title,
                                fontFamily = RajdhaniFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                            DropdownMenu(
                                expanded = framingExpanded,
                                onDismissRequest = { framingExpanded = false },
                                modifier = Modifier.background(VoidSurface)
                            ) {
                                CyberPresets.framings.forEach { framing ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = framing.title,
                                                fontFamily = RajdhaniFontFamily,
                                                fontSize = 13.sp,
                                                color = if (framing.title == selectedFraming.title) NeonGlow else TextPrimary
                                            )
                                        },
                                        onClick = {
                                            onSelectFraming(framing)
                                            framingExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Lighting Selector Dropdown
                    var lightingExpanded by remember { mutableStateOf(false) }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LightMode,
                                contentDescription = null,
                                tint = NeonGlow,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ILUMINACIÓN",
                                fontFamily = RajdhaniFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = NeonGlow
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GlassInputBg)
                                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                                .clickable { lightingExpanded = true }
                                .padding(horizontal = 10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = selectedLighting.title,
                                fontFamily = RajdhaniFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                            DropdownMenu(
                                expanded = lightingExpanded,
                                onDismissRequest = { lightingExpanded = false },
                                modifier = Modifier.background(VoidSurface)
                            ) {
                                CyberPresets.lightings.forEach { lighting ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = lighting.title,
                                                fontFamily = RajdhaniFontFamily,
                                                fontSize = 13.sp,
                                                color = if (lighting.title == selectedLighting.title) NeonGlow else TextPrimary
                                            )
                                        },
                                        onClick = {
                                            onSelectLighting(lighting)
                                            lightingExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Aspect Ratio Selector
                Column {
                    Text(
                        text = "PROPORCIÓN DE PANTALLA",
                        fontFamily = RajdhaniFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = NeonGlow
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CyberPresets.aspectRatios.forEach { ratio ->
                            val isSelected = ratio.ratioLabel == selectedRatio.ratioLabel
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) NeonViolet.copy(alpha = 0.3f)
                                        else GlassInputBg
                                    )
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) NeonViolet else GlassBorder,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSelectRatio(ratio) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = ratio.ratioLabel,
                                        fontFamily = RajdhaniFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isSelected) NeonGlow else TextPrimary
                                    )
                                    Text(
                                        text = ratio.description,
                                        fontFamily = RajdhaniFontFamily,
                                        fontSize = 10.sp,
                                        color = if (isSelected) TextSecondary else TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // GENERATE BUTTON
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (isGenerating) {
                        Brush.horizontalGradient(listOf(VoidSurface, VoidSurface))
                    } else {
                        Brush.horizontalGradient(
                            listOf(Color(0xFF6D28D9), NeonViolet, Color(0xFF4F46E5))
                        )
                    }
                )
                .border(1.dp, NeonGlow.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .clickable(enabled = !isGenerating) { onGenerate() }
                .testTag("generate_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = NeonGlow,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SINTETIZANDO AVATAR...",
                        fontFamily = RajdhaniFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp,
                        color = TextPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "GENERAR CON IA (FLUX.1)",
                        fontFamily = RajdhaniFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 1.2.sp,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
