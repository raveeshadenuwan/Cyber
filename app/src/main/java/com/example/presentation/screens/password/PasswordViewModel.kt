package com.example.presentation.screens.password

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.domain.model.PasswordAnalysisResult
import com.example.domain.security.GeneratedPasswordInfo
import com.example.domain.security.GeneratorPreset
import com.example.domain.security.PasswordAnalyzer
import com.example.domain.security.PasswordGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class PasswordUiState(
    val inputPassword: String = "",
    val analysisResult: PasswordAnalysisResult,
    // Generator configuration
    val genLength: Int = 16,
    val genUpper: Boolean = true,
    val genLower: Boolean = true,
    val genDigits: Boolean = true,
    val genSymbols: Boolean = true,
    val genExcludeAmbiguous: Boolean = false,
    val activePreset: GeneratorPreset? = null,
    val generatedInfo: GeneratedPasswordInfo,
    val sessionHistory: List<String> = emptyList(),
    val copyMessage: String? = null
)

class PasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val analyzer = PasswordAnalyzer()
    private val generator = PasswordGenerator()

    private val initialInfo = generator.generateDetailedInfo(
        length = 16,
        includeUpper = true,
        includeLower = true,
        includeDigits = true,
        includeSymbols = true,
        excludeAmbiguous = false
    )

    private val _uiState = MutableStateFlow(
        PasswordUiState(
            analysisResult = analyzer.analyze(""),
            generatedInfo = initialInfo,
            sessionHistory = listOf(initialInfo.password)
        )
    )
    val uiState: StateFlow<PasswordUiState> = _uiState.asStateFlow()

    fun onPasswordInputChanged(newPassword: String) {
        val analysis = analyzer.analyze(newPassword)
        _uiState.value = _uiState.value.copy(
            inputPassword = newPassword,
            analysisResult = analysis,
            copyMessage = null
        )
    }

    fun onGenLengthChanged(length: Int) {
        val clamped = length.coerceIn(4, 64)
        _uiState.value = _uiState.value.copy(
            genLength = clamped,
            activePreset = null
        )
        generateNewPassword()
    }

    fun toggleUpper(checked: Boolean) {
        _uiState.value = _uiState.value.copy(genUpper = checked, activePreset = null)
        generateNewPassword()
    }

    fun toggleLower(checked: Boolean) {
        _uiState.value = _uiState.value.copy(genLower = checked, activePreset = null)
        generateNewPassword()
    }

    fun toggleDigits(checked: Boolean) {
        _uiState.value = _uiState.value.copy(genDigits = checked, activePreset = null)
        generateNewPassword()
    }

    fun toggleSymbols(checked: Boolean) {
        _uiState.value = _uiState.value.copy(genSymbols = checked, activePreset = null)
        generateNewPassword()
    }

    fun toggleExcludeAmbiguous(checked: Boolean) {
        _uiState.value = _uiState.value.copy(genExcludeAmbiguous = checked, activePreset = null)
        generateNewPassword()
    }

    fun applyPreset(preset: GeneratorPreset) {
        _uiState.value = _uiState.value.copy(
            activePreset = preset,
            genLength = preset.defaultLength,
            genUpper = preset.upper,
            genLower = preset.lower,
            genDigits = preset.digits,
            genSymbols = preset.symbols,
            genExcludeAmbiguous = preset.excludeAmbiguous
        )
        generateNewPassword()
    }

    fun generateNewPassword() {
        val state = _uiState.value
        val info = generator.generateDetailedInfo(
            length = state.genLength,
            includeUpper = state.genUpper,
            includeLower = state.genLower,
            includeDigits = state.genDigits,
            includeSymbols = state.genSymbols,
            excludeAmbiguous = state.genExcludeAmbiguous
        )

        val updatedHistory = (listOf(info.password) + state.sessionHistory).distinct().take(6)

        _uiState.value = _uiState.value.copy(
            generatedInfo = info,
            sessionHistory = updatedHistory,
            copyMessage = null
        )
    }

    fun copyToClipboard(text: String) {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("CyberGuard Secure Key", text)
        clipboard?.setPrimaryClip(clip)
        _uiState.value = _uiState.value.copy(copyMessage = "Copied to clipboard. Paste securely.")
    }

    fun clearCopyMessage() {
        _uiState.value = _uiState.value.copy(copyMessage = null)
    }
}
