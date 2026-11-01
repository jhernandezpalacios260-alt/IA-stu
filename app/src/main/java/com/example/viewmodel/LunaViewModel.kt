package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AvatarRepository
import com.example.data.LunaDatabase
import com.example.model.AspectRatioOption
import com.example.model.AvatarEntity
import com.example.model.CyberPresets
import com.example.model.FramingOption
import com.example.model.LightingOption
import com.example.model.StyleOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.UUID

enum class TabScreen {
    CREATOR,
    GALLERY
}

data class LunaUiState(
    val currentTab: TabScreen = TabScreen.CREATOR,
    val promptInput: String = "",
    val selectedStyle: StyleOption = CyberPresets.styles[0],
    val selectedFraming: FramingOption = CyberPresets.framings[0],
    val selectedLighting: LightingOption = CyberPresets.lightings[0],
    val selectedRatio: AspectRatioOption = CyberPresets.aspectRatios[0],
    val isGenerating: Boolean = false,
    val activeAvatar: AvatarEntity? = null,
    val modalAvatar: AvatarEntity? = null,
    val toastMessage: String? = null,
    val isSavingToGallery: Boolean = false
)

class LunaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AvatarRepository
    val galleryAvatars: StateFlow<List<AvatarEntity>>

    private val _uiState = MutableStateFlow(LunaUiState())
    val uiState: StateFlow<LunaUiState> = _uiState.asStateFlow()

    init {
        val db = LunaDatabase.getDatabase(application)
        repository = AvatarRepository(db.avatarDao(), application)
        galleryAvatars = repository.allAvatars.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // Random initial inspirational prompt
        randomizePrompt()
    }

    fun switchTab(tab: TabScreen) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun onPromptChange(newPrompt: String) {
        _uiState.value = _uiState.value.copy(promptInput = newPrompt)
    }

    fun clearPrompt() {
        _uiState.value = _uiState.value.copy(promptInput = "")
        showToast("Descripción borrada")
    }

    fun randomizePrompt() {
        val random = CyberPresets.randomPrompts.random()
        _uiState.value = _uiState.value.copy(promptInput = random)
        showToast("Prompt aleatorio cargado")
    }

    fun selectStyle(style: StyleOption) {
        _uiState.value = _uiState.value.copy(selectedStyle = style)
    }

    fun selectFraming(framing: FramingOption) {
        _uiState.value = _uiState.value.copy(selectedFraming = framing)
    }

    fun selectLighting(lighting: LightingOption) {
        _uiState.value = _uiState.value.copy(selectedLighting = lighting)
    }

    fun selectRatio(ratio: AspectRatioOption) {
        _uiState.value = _uiState.value.copy(selectedRatio = ratio)
    }

    fun openModal(avatar: AvatarEntity) {
        _uiState.value = _uiState.value.copy(modalAvatar = avatar)
    }

    fun closeModal() {
        _uiState.value = _uiState.value.copy(modalAvatar = null)
    }

    fun dismissToast() {
        _uiState.value = _uiState.value.copy(toastMessage = null)
    }

    fun showToast(msg: String) {
        _uiState.value = _uiState.value.copy(toastMessage = msg)
    }

    fun generateAvatar() {
        val state = _uiState.value
        val prompt = state.promptInput.trim().ifEmpty {
            "Cyberpunk synthetic avatar, biomechanical beauty, glowing eyes"
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGenerating = true)

            // Simulate high tech neural matrix synthesis steps
            delay(1800)

            val seed = (100000L..999999999L).random()
            val fullPrompt = "$prompt, ${state.selectedStyle.promptModifier}, ${state.selectedFraming.promptModifier}, ${state.selectedLighting.promptModifier}, cyber aesthetic, masterpiece, hyperdetailed 8k, sharp focus"
            val encoded = withContext(Dispatchers.IO) {
                URLEncoder.encode(fullPrompt, "UTF-8")
            }
            val imageUrl = "https://image.pollinations.ai/prompt/$encoded?width=${state.selectedRatio.width}&height=${state.selectedRatio.height}&model=flux&seed=$seed&nologo=true"

            val newAvatar = AvatarEntity(
                id = UUID.randomUUID().toString(),
                imageUrl = imageUrl,
                prompt = prompt,
                styleTitle = state.selectedStyle.title,
                framing = state.selectedFraming.title,
                lighting = state.selectedLighting.title,
                aspectRatio = state.selectedRatio.ratioLabel,
                width = state.selectedRatio.width,
                height = state.selectedRatio.height,
                seed = seed,
                createdAt = System.currentTimeMillis()
            )

            // Save to database
            repository.saveAvatar(newAvatar)

            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                activeAvatar = newAvatar
            )
            showToast("Avatar sintetizado con éxito")
        }
    }

    fun downloadToGallery(avatar: AvatarEntity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSavingToGallery = true)
            val result = repository.downloadImageToGallery(avatar.imageUrl, avatar.styleTitle)
            _uiState.value = _uiState.value.copy(isSavingToGallery = false)

            result.onSuccess {
                showToast("Guardado en la Galería del teléfono")
            }.onFailure {
                showToast("Error al guardar: ${it.localizedMessage ?: "Fallo de descarga"}")
            }
        }
    }

    fun shareAvatar(avatar: AvatarEntity, onShareReady: (Uri) -> Unit) {
        viewModelScope.launch {
            showToast("Preparando imagen para compartir...")
            val result = repository.prepareImageForShare(avatar.imageUrl)
            result.onSuccess { uri ->
                onShareReady(uri)
            }.onFailure {
                showToast("No se pudo preparar la imagen para compartir")
            }
        }
    }

    fun deleteAvatar(avatar: AvatarEntity) {
        viewModelScope.launch {
            repository.deleteAvatar(avatar)
            if (_uiState.value.modalAvatar?.id == avatar.id) {
                _uiState.value = _uiState.value.copy(modalAvatar = null)
            }
            if (_uiState.value.activeAvatar?.id == avatar.id) {
                _uiState.value = _uiState.value.copy(activeAvatar = null)
            }
            showToast("Creación eliminada de la bóveda")
        }
    }

    fun clearEntireGallery() {
        viewModelScope.launch {
            repository.clearAll()
            _uiState.value = _uiState.value.copy(activeAvatar = null, modalAvatar = null)
            showToast("Bóveda vaciada")
        }
    }

    fun reuseInCreator(avatar: AvatarEntity) {
        val foundStyle = CyberPresets.styles.firstOrNull { it.title == avatar.styleTitle } ?: CyberPresets.styles[0]
        val foundFraming = CyberPresets.framings.firstOrNull { it.title == avatar.framing } ?: CyberPresets.framings[0]
        val foundLighting = CyberPresets.lightings.firstOrNull { it.title == avatar.lighting } ?: CyberPresets.lightings[0]
        val foundRatio = CyberPresets.aspectRatios.firstOrNull { it.ratioLabel == avatar.aspectRatio } ?: CyberPresets.aspectRatios[0]

        _uiState.value = _uiState.value.copy(
            promptInput = avatar.prompt,
            selectedStyle = foundStyle,
            selectedFraming = foundFraming,
            selectedLighting = foundLighting,
            selectedRatio = foundRatio,
            activeAvatar = avatar,
            currentTab = TabScreen.CREATOR,
            modalAvatar = null
        )
        showToast("Parámetros cargados en el Creador")
    }
}
