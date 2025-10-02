package com.example.genai.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.genai.*

@Composable
fun TrainingScreen(viewModel: TrainingViewModel) {
    val st by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("SportCoach AI") }) }
    ) { pad ->
        LazyColumn(Modifier.padding(pad).padding(16.dp)) {
            item {
                Text("Deportes disponibles", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
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
            item { Spacer(Modifier.height(16.dp)) }

            var obj by remember { mutableStateOf(st.objective) }
            OutlinedTextField(
                value = obj,
                onValueChange = { obj = it },
                label = { Text("Objetivo") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.onIntent(
                        TrainingIntent.UpdateGoal(obj, listOf("Lun","Mié","Vie"), st.level, st.minutes)
                    )
                    viewModel.onIntent(TrainingIntent.GeneratePlan)
                },
                enabled = st.selected != null
            ) {
                Text("Generar Plan con IA")
            }

            if (st.isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 12.dp))
            }

            st.plan?.let {
                Spacer(Modifier.height(16.dp))
                Text("Plan generado:", style = MaterialTheme.typography.titleMedium)
                Text(it.planMarkdown)
            }

            st.error?.let {
                Spacer(Modifier.height(8.dp))
                Text("Error: $it", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
