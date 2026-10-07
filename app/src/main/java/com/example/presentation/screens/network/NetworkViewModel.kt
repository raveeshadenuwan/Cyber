package com.example.presentation.screens.network

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.NetworkRepository
import com.example.domain.model.NetworkSecurityInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NetworkUiState(
    val isLoading: Boolean = true,
    val networkInfo: NetworkSecurityInfo? = null,
    val errorMessage: String? = null
)

class NetworkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NetworkRepository(application)

    private val _uiState = MutableStateFlow(NetworkUiState())
    val uiState: StateFlow<NetworkUiState> = _uiState.asStateFlow()

    init {
        refreshNetwork()
    }

    fun refreshNetwork() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val info = repository.getNetworkInfo()
                _uiState.value = NetworkUiState(
                    isLoading = false,
                    networkInfo = info,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to query network interfaces: ${e.localizedMessage}"
                )
            }
        }
    }
}
