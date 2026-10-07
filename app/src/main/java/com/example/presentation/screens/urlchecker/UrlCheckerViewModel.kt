package com.example.presentation.screens.urlchecker

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.example.domain.model.UrlAnalysisResult
import com.example.domain.model.UrlRiskLevel
import com.example.domain.security.UrlAnalyzer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UrlCheckerUiState(
    val inputUrl: String = "",
    val analysisResult: UrlAnalysisResult? = null,
    val showBrowserConfirmDialog: Boolean = false
)

class UrlCheckerViewModel(application: Application) : AndroidViewModel(application) {

    private val analyzer = UrlAnalyzer()

    private val _uiState = MutableStateFlow(UrlCheckerUiState())
    val uiState: StateFlow<UrlCheckerUiState> = _uiState.asStateFlow()

    fun onUrlInputChanged(newUrl: String) {
        _uiState.value = _uiState.value.copy(inputUrl = newUrl)
    }

    fun analyzeUrl(urlToAnalyze: String = _uiState.value.inputUrl) {
        val result = analyzer.analyze(urlToAnalyze)
        _uiState.value = _uiState.value.copy(
            inputUrl = urlToAnalyze,
            analysisResult = result
        )
    }

    fun requestOpenBrowser() {
        if (_uiState.value.analysisResult != null) {
            _uiState.value = _uiState.value.copy(showBrowserConfirmDialog = true)
        }
    }

    fun dismissBrowserConfirm() {
        _uiState.value = _uiState.value.copy(showBrowserConfirmDialog = false)
    }

    fun confirmOpenBrowser() {
        dismissBrowserConfirm()
        val result = _uiState.value.analysisResult ?: return
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            getApplication<Application>().startActivity(intent)
        } catch (_: Exception) {}
    }
}
