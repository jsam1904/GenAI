package com.example.genai

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(viewModel: TrainingViewModel) {
    val st by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    // ✅ Estados locales
    var selectedDays by remember { mutableStateOf(setOf<String>()) }
    var obj by remember { mutableStateOf(st.objective) }
    var level by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) { viewModel.onIntent(TrainingIntent.LoadSports) }

    Scaffold { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(Color(0xFFF8F9FD))
        ) {
            // 🔵 Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "SportCoach AI",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Tu entrenador inteligente",
                        color = Color.LightGray,
                        fontSize = 16.sp
                    )
                }
            }

            // 🏀 Deportes
            Text(
                "Deportes disponibles",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val sports = listOf(
                    "Fútbol" to Icons.Filled.SportsSoccer,
                    "Basket" to Icons.Filled.SportsBasketball,
                    "Tenis" to Icons.Filled.SportsTennis,
                    "Gym" to Icons.Filled.FitnessCenter,
                    "Running" to Icons.AutoMirrored.Filled.DirectionsRun,
                    "Natación" to Icons.Filled.Pool
                )

                items(sports) { (name, icon) ->
                    val isSelected = st.selected?.name == name
                    Card(
                        modifier = Modifier
                            .width(140.dp)
                            .height(150.dp)
                            .clickable {
                                viewModel.onIntent(
                                    TrainingIntent.SelectSport(Sport(name, name, ""))
                                )
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF4F46E5) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            Modifier
                                .padding(16.dp)
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else Color(0xFF4F46E5),
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                name,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            // 🎯 Objetivo
            Text(
                "Tu objetivo",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            OutlinedTextField(
                value = obj,
                onValueChange = { obj = it },
                placeholder = { Text("Ejemplo: mejorar resistencia") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // 📅 Días (dos filas)
            Text(
                "Selecciona días disponibles",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            val weekDays = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
            Column(Modifier.padding(horizontal = 16.dp)) {
                weekDays.chunked(4).forEach { rowDays ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowDays.forEach { day ->
                            val isSelected = day in selectedDays
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) Color(0xFF4F46E5)
                                        else Color(0xFFE5E7EB)
                                    )
                                    .clickable {
                                        selectedDays =
                                            if (isSelected) selectedDays - day
                                            else selectedDays + day
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day,
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // 📊 Nivel
            Spacer(Modifier.height(16.dp))
            Text(
                "Nivel de entrenamiento",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Column(Modifier.padding(horizontal = 16.dp)) {
                Slider(
                    value = level,
                    onValueChange = { level = it },
                    valueRange = 0f..1f,
                    steps = 3,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF4F46E5),
                        activeTrackColor = Color(0xFF4F46E5)
                    )
                )
                val levelText = when {
                    level < 0.25f -> "Principiante"
                    level < 0.5f -> "Intermedio"
                    level < 0.75f -> "Avanzado"
                    else -> "Profesional"
                }
                Text(levelText, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(20.dp))

            // 🚀 Botón generar
            Button(
                onClick = {
                    viewModel.onIntent(
                        TrainingIntent.UpdateGoal(
                            objective = obj,
                            days = selectedDays.toList(),
                            level = when {
                                level < 0.25f -> "Principiante"
                                level < 0.5f -> "Intermedio"
                                level < 0.75f -> "Avanzado"
                                else -> "Profesional"
                            },
                            minutes = st.minutes
                        )
                    )
                    viewModel.onIntent(TrainingIntent.GeneratePlan)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                shape = RoundedCornerShape(12.dp),
                enabled = st.selected != null && obj.isNotBlank()
            ) {
                Text("Generar Plan con IA", fontSize = 18.sp, color = Color.White)
            }

            if (st.isLoading) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            // 📋 Plan generado (con scroll y selección)
            st.plan?.let {
                Spacer(Modifier.height(20.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6))
                ) {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text("Plan generado", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        SelectionContainer {
                            Text(
                                text = it.planMarkdown,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            // ❗Mostrar errores si ocurren
            st.error?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}
