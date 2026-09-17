package com.mrashish18.lifeos.feature.realitycheck

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mrashish18.lifeos.core.model.RealityCheckInput
import com.mrashish18.lifeos.core.model.RealityCheckResult
import com.mrashish18.lifeos.domain.usecase.PerformRealityCheckUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface RealityCheckUiState {
    data object Empty : RealityCheckUiState
    data class Input(val claimText: String = "", val sourceUrl: String = "") : RealityCheckUiState
    data class Loading(val currentStep: String) : RealityCheckUiState
    data class Success(val result: RealityCheckResult) : RealityCheckUiState
    data class Error(val message: String, val lastClaimText: String = "") : RealityCheckUiState
}

class RealityCheckViewModel(
    private val performRealityCheckUseCase: PerformRealityCheckUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RealityCheckUiState>(RealityCheckUiState.Empty)
    val uiState: StateFlow<RealityCheckUiState> = _uiState.asStateFlow()

    private var currentClaimText: String = ""
    private var currentSourceUrl: String = ""

    val sampleClaims = listOf(
        "Earth orbits the Sun in approximately 365 days",
        "Antibiotics kill viral infections like colds and flu",
        "Apollo 11 landed humans on the Moon in 1969",
        "Humans only use 10 percent of their brain",
        "Kotlin 1.0 was officially released in 2016",
        "Moderate coffee consumption protects against cardiovascular disease"
    )

    fun onClaimTextChanged(text: String) {
        currentClaimText = text
        _uiState.value = RealityCheckUiState.Input(claimText = currentClaimText, sourceUrl = currentSourceUrl)
    }

    fun onSourceUrlChanged(url: String) {
        currentSourceUrl = url
        _uiState.value = RealityCheckUiState.Input(claimText = currentClaimText, sourceUrl = currentSourceUrl)
    }

    fun selectSampleClaim(claim: String) {
        currentClaimText = claim
        _uiState.value = RealityCheckUiState.Input(claimText = currentClaimText, sourceUrl = currentSourceUrl)
    }

    fun clearInput() {
        currentClaimText = ""
        currentSourceUrl = ""
        _uiState.value = RealityCheckUiState.Empty
    }

    fun analyzeClaim() {
        val textToAnalyze = currentClaimText.trim()
        if (textToAnalyze.isBlank()) {
            _uiState.value = RealityCheckUiState.Error("Please enter a claim or statement to analyze.")
            return
        }

        viewModelScope.launch {
            _uiState.value = RealityCheckUiState.Loading("Extracting claim propositions...")
            delay(150) // Brief step transition for human readability in UI
            _uiState.value = RealityCheckUiState.Loading("Searching authoritative evidence repositories...")
            delay(150)
            _uiState.value = RealityCheckUiState.Loading("Evaluating source quality and cross-corroboration...")

            val input = RealityCheckInput(
                text = textToAnalyze,
                sourceUrl = currentSourceUrl.ifBlank { null }
            )

            performRealityCheckUseCase(input)
                .onSuccess { result ->
                    _uiState.value = RealityCheckUiState.Success(result)
                }
                .onFailure { error ->
                    _uiState.value = RealityCheckUiState.Error(
                        message = error.message ?: "An unexpected error occurred during investigation.",
                        lastClaimText = textToAnalyze
                    )
                }
        }
    }

    fun resetToInput() {
        _uiState.value = RealityCheckUiState.Input(
            claimText = currentClaimText,
            sourceUrl = currentSourceUrl
        )
    }

    class Factory(
        private val performRealityCheckUseCase: PerformRealityCheckUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return RealityCheckViewModel(performRealityCheckUseCase) as T
        }
    }
}
