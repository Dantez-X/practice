package ci.nsu.mobile.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: DepositRepository
) : ViewModel() {

    private val _calculations = MutableStateFlow<List<DepositEntity>>(emptyList())
    val calculations: StateFlow<List<DepositEntity>> = _calculations.asStateFlow()

    init {
        loadCalculations()
        println("HistoryViewModel создан")
    }

    private fun loadCalculations() {
        viewModelScope.launch {
            repository.getAllCalculations().collect { list ->
                println("Загружено записей: ${list.size}")
                _calculations.value = list
            }
        }
    }

    fun addCalculation(calculation: DepositEntity) {
        viewModelScope.launch {
            println("Добавляем расчёт: ${calculation.finalAmount}")
            repository.saveCalculation(calculation)

            loadCalculations()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            loadCalculations()
        }
    }
}
