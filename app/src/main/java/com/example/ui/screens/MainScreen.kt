package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CyberHeader
import com.example.ui.components.CyberToast
import com.example.ui.components.LightboxModal
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonGlow
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.RajdhaniFontFamily
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBackground
import com.example.ui.theme.VoidSurface
import com.example.viewmodel.LunaViewModel
import com.example.viewmodel.TabScreen

@Composable
fun MainScreen(viewModel: LunaViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val galleryAvatars by viewModel.galleryAvatars.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CyberHeader(
                    onRandomizeClick = { viewModel.randomizePrompt() }
                )
            },
            bottomBar = {
                CyberBottomBar(
                    currentTab = uiState.currentTab,
                    onTabSelected = { viewModel.switchTab(it) },
                    galleryCount = galleryAvatars.size
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AnimatedContent(
                    targetState = uiState.currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition"
                ) { targetTab ->
                    when (targetTab) {
                        TabScreen.CREATOR -> {
                            CreatorScreen(
                                prompt = uiState.promptInput,
                                onPromptChange = { viewModel.onPromptChange(it) },
                                onClearPrompt = { viewModel.clearPrompt() },
                                selectedStyle = uiState.selectedStyle,
                                onSelectStyle = { viewModel.selectStyle(it) },
                                selectedFraming = uiState.selectedFraming,
                                onSelectFraming = { viewModel.selectFraming(it) },
                                selectedLighting = uiState.selectedLighting,
                                onSelectLighting = { viewModel.selectLighting(it) },
                                selectedRatio = uiState.selectedRatio,
                                onSelectRatio = { viewModel.selectRatio(it) },
                                isGenerating = uiState.isGenerating,
                                activeAvatar = uiState.activeAvatar,
                                onGenerate = { viewModel.generateAvatar() },
                                onInspect = { viewModel.openModal(it) },
                                onDownload = { viewModel.downloadToGallery(it) }
                            )
                        }
                        TabScreen.GALLERY -> {
                            GalleryScreen(
                                avatars = galleryAvatars,
                                onAvatarClick = { viewModel.openModal(it) },
                                onClearAll = { viewModel.clearEntireGallery() },
                                onNavigateToCreator = { viewModel.switchTab(TabScreen.CREATOR) }
                            )
                        }
                    }
                }
            }
        }

        // Top notification toast
        CyberToast(
            message = uiState.toastMessage,
            onDismiss = { viewModel.dismissToast() },
            modifier = Modifier.padding(top = 65.dp)
        )

        // Inspection Lightbox Modal
        uiState.modalAvatar?.let { modalAvatar ->
            LightboxModal(
                avatar = modalAvatar,
                onClose = { viewModel.closeModal() },
                onDownload = { viewModel.downloadToGallery(modalAvatar) },
                onDelete = { viewModel.deleteAvatar(modalAvatar) },
                onReuse = { viewModel.reuseInCreator(modalAvatar) },
                onShare = {
                    viewModel.shareAvatar(modalAvatar) { shareUri ->
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "image/jpeg"
                            putExtra(Intent.EXTRA_STREAM, shareUri)
                            putExtra(Intent.EXTRA_TEXT, "¡Mira este avatar generado con Luna AI Studio!\nPrompt: ${modalAvatar.prompt}")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Compartir Avatar"))
                    }
                },
                onShowToast = { viewModel.showToast(it) }
            )
        }
    }
}

@Composable
private fun CyberBottomBar(
    currentTab: TabScreen,
    onTabSelected: (TabScreen) -> Unit,
    galleryCount: Int
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(VoidSurface.copy(alpha = 0.95f))
            .border(1.dp, GlassBorder)
            .padding(horizontal = 24.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Creator Tab Button
            val isCreator = currentTab == TabScreen.CREATOR
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(TabScreen.CREATOR) }
                    .padding(horizontal = 24.dp, vertical = 6.dp)
                    .testTag("nav_creator_tab")
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Crear",
                    tint = if (isCreator) NeonGlow else TextMuted,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Crear",
                    fontFamily = RajdhaniFontFamily,
                    fontWeight = if (isCreator) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp,
                    color = if (isCreator) NeonGlow else TextMuted
                )
            }

            // Gallery Tab Button
            val isGallery = currentTab == TabScreen.GALLERY
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelected(TabScreen.GALLERY) }
                    .padding(horizontal = 24.dp, vertical = 6.dp)
                    .testTag("nav_gallery_tab")
            ) {
                BadgedBox(
                    badge = {
                        if (galleryCount > 0) {
                            Badge(
                                containerColor = NeonViolet,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = galleryCount.toString(),
                                    fontFamily = RajdhaniFontFamily,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Collections,
                        contentDescription = "Galería",
                        tint = if (isGallery) NeonGlow else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Galería",
                    fontFamily = RajdhaniFontFamily,
                    fontWeight = if (isGallery) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp,
                    color = if (isGallery) NeonGlow else TextMuted
                )
            }
        }
    }
}
