package ci.nsu.moble.main

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ci.nsu.moble.main.ui.theme.PracticeTheme

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ColorInputScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

val MainColors = mapOf(
    "red" to Color.Red,
    "orange" to Color(0xFFFFA500),
    "yellow" to Color.Yellow,
    "green" to Color.Green,
    "blue" to Color.Blue,
    "indigo" to Color(0xFF4B0082),
    "violet" to Color(0xFFEE82EE)
)

@Composable

fun ColorInputScreen(modifier: Modifier = Modifier) {
    var color_name by remember { mutableStateOf("") }
    var button_color by remember { mutableStateOf(Color.Gray) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = color_name,
            onValueChange = { color_name = it },
            label = { Text("Введите цвет") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val down_reg = color_name.trim().lowercase()
                val select_color = MainColors[down_reg]
                if (select_color != null) {
                    button_color = select_color
                    Log.d("Поиск цвета", "Цвет найден: $down_reg")
                    Toast.makeText(context, "Найден: $down_reg", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("Поиск цвета", "Цвет не найден: $down_reg")
                    Toast.makeText(context, "Не найден: $down_reg", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = button_color)
        ) {
            Text("Окрасить в цвет")
        }

        Spacer(modifier = Modifier.height(16.dp))


        Column {
            MainColors.forEach { (colorName, colorValue) ->
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorValue)
                        .padding(8.dp)
                ) {
                    Text(text = colorName, color = Color.White)
                }
            }
        }
    }
}