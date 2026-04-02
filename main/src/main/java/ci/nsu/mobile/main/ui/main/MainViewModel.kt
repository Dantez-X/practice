package ci.nsu.mobile.main.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class CounterUiState(
    val count: Int = 0,
    val history: List<String> = emptyList()
)

class MainViewModel : ViewModel() {

    private val _uiState = MutableLiveData<CounterUiState>(CounterUiState())
    val uiState: LiveData<CounterUiState> = _uiState

    fun increment() {
        val currentState = _uiState.value ?: CounterUiState()
        val newCount = currentState.count + 1
        val newHistory = listOf("+1 (итого: $newCount)") + currentState.history.take(4)

        _uiState.value = currentState.copy(
            count = newCount,
            history = newHistory
        )
    }

    fun decrement() {
        val currentState = _uiState.value ?: CounterUiState()
        val newCount = currentState.count - 1
        val newHistory = listOf("-1 (итого: $newCount)") + currentState.history.take(4)

        _uiState.value = currentState.copy(
            count = newCount,
            history = newHistory
        )
    }

    fun reset() {
        val currentState = _uiState.value ?: CounterUiState()
        val newHistory = listOf("Сброс (итого: 0)") + currentState.history.take(4)

        _uiState.value = CounterUiState(
            count = 0,
            history = newHistory
        )
    }
    fun clearHistory() {
        val currentState = _uiState.value ?: CounterUiState()

        _uiState.value = currentState.copy(
            history = emptyList()
        )
    }
}