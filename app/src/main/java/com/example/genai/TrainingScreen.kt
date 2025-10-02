package com.example.genai

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.setValue


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(viewModel: TrainingViewModel) {
    val st by viewModel.state.collectAsState()

    // Si quieres cargar los deportes al abrir la pantalla
    LaunchedEffect(Unit) { viewModel.onIntent(TrainingIntent.LoadSports) }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("SportCoach AI") }) }
    ) { pad ->
        LazyColumn(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
        ) {
            item {
                Text("Deportes disponibles", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.padding(top = 8.dp))
                Row {
                    st.sports.forEach { sport ->
                        FilterChip(
                            selected = st.selected == sport,
                            onClick = { viewModel.onIntent(TrainingIntent.SelectSport(sport)) },
                            label = { Text(sport.name) },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.padding(top = 16.dp))

                var obj by remember { mutableStateOf(st.objective) }
                OutlinedTextField(
                    value = obj,
                    onValueChange = { obj = it },
                    label = { Text("Objetivo") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.padding(top = 8.dp))

                Button(
                    onClick = {
                        viewModel.onIntent(
                            TrainingIntent.UpdateGoal(
                                objective = obj,
                                days = listOf("Lun", "Mié", "Vie"),
                                level = st.level,
                                minutes = st.minutes
                            )
                        )
                        viewModel.onIntent(TrainingIntent.GeneratePlan)
                    },
                    enabled = st.selected != null
                ) {
                    Text("Generar Plan con IA")
                }

                if (st.isLoading) {
                    Spacer(Modifier.padding(top = 12.dp))
                    LinearProgressIndicator(Modifier.fillMaxWidth())
                }
            }

            item {
                st.plan?.let {
                    Spacer(Modifier.padding(top = 16.dp))
                    Text("Plan generado:", style = MaterialTheme.typography.titleMedium)
                    Text(it.planMarkdown)
                }

                st.error?.let {
                    Spacer(Modifier.padding(top = 8.dp))
                    Text("Error: $it", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
