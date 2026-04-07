package ci.nsu.mobile.deposit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import ci.nsu.mobile.deposit.ui.theme.DepositCalculatorTheme
import java.text.SimpleDateFormat
import java.util.*


data class DepositCalculation(
    val id: Long,
    val initialAmount: Double,
    val periodMonths: Int,
    val interestRate: Double,
    val monthlyTopUp: Double,
    val finalAmount: Double,
    val interestEarned: Double,
    val calculationDate: Long
) {
    fun getFormattedDate(): String {
        val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return format.format(Date(calculationDate))
    }
}




class MainViewModel : ViewModel()


class FirstStepViewModel : ViewModel() {
    private val _initialAmount = mutableStateOf("")
    private val _periodMonths = mutableStateOf("")
    private val _initialAmountError = mutableStateOf<String?>(null)
    private val _periodError = mutableStateOf<String?>(null)

    val initialAmount: String get() = _initialAmount.value
    val periodMonths: String get() = _periodMonths.value
    val initialAmountError: String? get() = _initialAmountError.value
    val periodError: String? get() = _periodError.value
    val isNextEnabled: Boolean get() = initialAmountError == null && periodError == null &&
            initialAmount.isNotEmpty() && periodMonths.isNotEmpty()

    fun updateInitialAmount(value: String) {
        _initialAmount.value = value
        val amount = value.toDoubleOrNull()
        _initialAmountError.value = when {
            value.isEmpty() -> "Поле обязательно"
            amount == null -> "Введите число"
            amount <= 0 -> "Сумма должна быть больше 0"
            else -> null
        }
    }

    fun updatePeriodMonths(value: String) {
        _periodMonths.value = value
        val months = value.toIntOrNull()
        _periodError.value = when {
            value.isEmpty() -> "Поле обязательно"
            months == null -> "Введите число"
            months <= 0 -> "Срок должен быть больше 0"
            else -> null
        }
    }

    fun getData(): Pair<Double, Int> {
        return Pair(initialAmount.toDoubleOrNull() ?: 0.0, periodMonths.toIntOrNull() ?: 0)
    }
}


class SecondStepViewModel : ViewModel() {
    private val _availableRates = mutableStateOf<List<Double>>(emptyList())
    private val _selectedRate = mutableStateOf<Double?>(null)
    private val _monthlyTopUp = mutableStateOf("")
    private val _errorMessage = mutableStateOf<String?>(null)

    val availableRates: List<Double> get() = _availableRates.value
    val selectedRate: Double? get() = _selectedRate.value
    val monthlyTopUp: String get() = _monthlyTopUp.value
    val errorMessage: String? get() = _errorMessage.value
    val isCalculateEnabled: Boolean get() = availableRates.isNotEmpty()

    fun updatePeriodMonths(periodMonths: Int) {
        _availableRates.value = when {
            periodMonths <= 0 -> emptyList()
            periodMonths < 6 -> listOf(15.0)
            periodMonths < 12 -> listOf(10.0)
            else -> listOf(5.0)
        }
        _selectedRate.value = _availableRates.value.firstOrNull()
        _errorMessage.value = if (periodMonths <= 0) "Некорректный срок вклада" else null
    }

    fun selectRate(rate: Double) {
        _selectedRate.value = rate
    }

    fun updateMonthlyTopUp(value: String) {
        _monthlyTopUp.value = value
    }

    fun getData(): Triple<Double, Double> {
        return Triple(selectedRate ?: 0.0, monthlyTopUp.toDoubleOrNull() ?: 0.0)
    }
}


class ResultViewModel : ViewModel() {
    private val _initialAmount = mutableStateOf(0.0)
    private val _periodMonths = mutableStateOf(0)
    private val _interestRate = mutableStateOf(0.0)
    private val _monthlyTopUp = mutableStateOf(0.0)
    private val _finalAmount = mutableStateOf(0.0)
    private val _interestEarned = mutableStateOf(0.0)

    val initialAmount: Double get() = _initialAmount.value
    val periodMonths: Int get() = _periodMonths.value
    val interestRate: Double get() = _interestRate.value
    val monthlyTopUp: Double get() = _monthlyTopUp.value
    val finalAmount: Double get() = _finalAmount.value
    val interestEarned: Double get() = _interestEarned.value

    fun calculate(initialAmount: Double, periodMonths: Int, interestRate: Double, monthlyTopUp: Double) {
        val monthlyRate = interestRate / 100 / 12
        var currentAmount = initialAmount

        for (month in 1..periodMonths) {
            currentAmount += monthlyTopUp
            currentAmount += currentAmount * monthlyRate
        }

        _initialAmount.value = initialAmount
        _periodMonths.value = periodMonths
        _interestRate.value = interestRate
        _monthlyTopUp.value = monthlyTopUp
        _finalAmount.value = currentAmount
        _interestEarned.value = currentAmount - initialAmount - (monthlyTopUp * periodMonths)
    }

    fun getCalculation(): DepositCalculation {
        return DepositCalculation(
            id = System.currentTimeMillis(),
            initialAmount = initialAmount,
            periodMonths = periodMonths,
            interestRate = interestRate,
            monthlyTopUp = monthlyTopUp,
            finalAmount = finalAmount,
            interestEarned = interestEarned,
            calculationDate = System.currentTimeMillis()
        )
    }
}


class HistoryViewModel : ViewModel() {
    private val _calculations = mutableStateOf<List<DepositCalculation>>(emptyList())
    val calculations: List<DepositCalculation> get() = _calculations.value

    fun addCalculation(calculation: DepositCalculation) {
        _calculations.value = listOf(calculation) + _calculations.value
    }
}


@Composable
fun MainScreen(
    onCalculateClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onExitClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Расчёт вкладов", fontSize = 28.sp, style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(48.dp))

        Button(onClick = onCalculateClick, modifier = Modifier.fillMaxWidth()) {
            Text("Рассчитать")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onHistoryClick, modifier = Modifier.fillMaxWidth()) {
            Text("История расчётов")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onExitClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
        )) {
            Text("Закрыть приложение")
        }
    }
}


@Composable
fun FirstStepScreen(
    viewModel: FirstStepViewModel = viewModel(),
    onBackClick: () -> Unit,
    onNextClick: (Double, Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Параметры вклада", fontSize = 24.sp, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = viewModel.initialAmount,
            onValueChange = { viewModel.updateInitialAmount(it) },
            label = { Text("Стартовый взнос") },
            isError = viewModel.initialAmountError != null,
            supportingText = { if (viewModel.initialAmountError != null) Text(viewModel.initialAmountError!!) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.periodMonths,
            onValueChange = { viewModel.updatePeriodMonths(it) },
            label = { Text("Срок вклада (месяцы)") },
            isError = viewModel.periodError != null,
            supportingText = { if (viewModel.periodError != null) Text(viewModel.periodError!!) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onBackClick, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("В начало")
            }
            Button(
                onClick = {
                    val (amount, months) = viewModel.getData()
                    onNextClick(amount, months)
                },
                enabled = viewModel.isNextEnabled,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Далее")
            }
        }
    }
}


@Composable
fun SecondStepScreen(
    initialAmount: Double,
    periodMonths: Int,
    viewModel: SecondStepViewModel = viewModel(),
    onBackClick: () -> Unit,
    onCalculateClick: (Double, Int, Double, Double) -> Unit
) {
    LaunchedEffect(periodMonths) {
        viewModel.updatePeriodMonths(periodMonths)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Дополнительные параметры", fontSize = 24.sp, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        Text("Процентная ставка")

        if (viewModel.availableRates.isNotEmpty()) {
            Row {
                viewModel.availableRates.forEach { rate ->
                    FilterChip(
                        selected = viewModel.selectedRate == rate,
                        onClick = { viewModel.selectRate(rate) },
                        label = { Text("$rate%") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.monthlyTopUp,
            onValueChange = { viewModel.updateMonthlyTopUp(it) },
            label = { Text("Ежемесячное пополнение (необязательно)") },
            modifier = Modifier.fillMaxWidth()
        )

        if (viewModel.errorMessage != null) {
            Text(viewModel.errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onBackClick, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Назад")
            }
            Button(
                onClick = {
                    val (rate, topUp) = viewModel.getData()
                    onCalculateClick(initialAmount, periodMonths, rate, topUp)
                },
                enabled = viewModel.isCalculateEnabled,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Рассчитать")
            }
        }
    }
}


@Composable
fun ResultScreen(
    initialAmount: Double,
    periodMonths: Int,
    interestRate: Double,
    monthlyTopUp: Double,
    onSaveClick: (DepositCalculation) -> Unit,
    onBackToMainClick: () -> Unit
) {
    val viewModel: ResultViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.calculate(initialAmount, periodMonths, interestRate, monthlyTopUp)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Результат расчёта", fontSize = 24.sp, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Стартовый взнос: ${String.format("%.2f", viewModel.initialAmount)} ₽")
                Text("Срок: ${viewModel.periodMonths} месяцев")
                Text("Процентная ставка: ${viewModel.interestRate}%")
                if (viewModel.monthlyTopUp > 0) {
                    Text("Ежемесячное пополнение: ${String.format("%.2f", viewModel.monthlyTopUp)} ₽")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Text("Итоговая сумма: ${String.format("%.2f", viewModel.finalAmount)} ₽", style = MaterialTheme.typography.titleMedium)
                Text("Начисленные проценты: ${String.format("%.2f", viewModel.interestEarned)} ₽")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { onSaveClick(viewModel.getCalculation()) },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Сохранить")
            }
            Button(
                onClick = onBackToMainClick,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("В начало")
            }
        }
    }
}


@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        Text("История расчётов", fontSize = 24.sp, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.calculations.isEmpty()) {
            Text("Нет сохранённых расчётов")
        } else {
            LazyColumn {
                items(viewModel.calculations) { calc ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(calc.getFormattedDate(), fontSize = 12.sp)
                            Text("Стартовый взнос: ${String.format("%.2f", calc.initialAmount)} ₽")
                            Text("Итоговая сумма: ${String.format("%.2f", calc.finalAmount)} ₽")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBackClick, modifier = Modifier.fillMaxWidth()) {
            Text("На главную")
        }
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val historyViewModel = HistoryViewModel()

        setContent {
            DepositCalculatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf("main") }

                    // Данные для передачи между экранами
                    var savedInitialAmount by remember { mutableStateOf(0.0) }
                    var savedPeriodMonths by remember { mutableStateOf(0) }
                    var savedInterestRate by remember { mutableStateOf(0.0) }
                    var savedMonthlyTopUp by remember { mutableStateOf(0.0) }

                    when (currentScreen) {
                        "main" -> {
                            MainScreen(
                                onCalculateClick = { currentScreen = "first" },
                                onHistoryClick = { currentScreen = "history" },
                                onExitClick = { finish() }
                            )
                        }
                        "first" -> {
                            FirstStepScreen(
                                onBackClick = { currentScreen = "main" },
                                onNextClick = { amount, months ->
                                    savedInitialAmount = amount
                                    savedPeriodMonths = months
                                    currentScreen = "second"
                                }
                            )
                        }
                        "second" -> {
                            SecondStepScreen(
                                initialAmount = savedInitialAmount,
                                periodMonths = savedPeriodMonths,
                                onBackClick = { currentScreen = "first" },
                                onCalculateClick = { amount, months, rate, topUp ->
                                    savedInitialAmount = amount
                                    savedPeriodMonths = months
                                    savedInterestRate = rate
                                    savedMonthlyTopUp = topUp
                                    currentScreen = "result"
                                }
                            )
                        }
                        "result" -> {
                            ResultScreen(
                                initialAmount = savedInitialAmount,
                                periodMonths = savedPeriodMonths,
                                interestRate = savedInterestRate,
                                monthlyTopUp = savedMonthlyTopUp,
                                onSaveClick = { calculation ->
                                    historyViewModel.addCalculation(calculation)
                                },
                                onBackToMainClick = { currentScreen = "main" }
                            )
                        }
                        "history" -> {
                            HistoryScreen(
                                viewModel = historyViewModel,
                                onBackClick = { currentScreen = "main" }
                            )
                        }
                    }
                }
            }
        }
    }
}