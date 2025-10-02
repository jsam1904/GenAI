package com.example.genai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.genai.OpenAiRepository
import com.example.genai.TrainingScreen
import com.example.genai.ui.theme.GenAITheme
import com.example.genai.TrainingViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inyección manual simple
        val repository = OpenAiRepository()
        val viewModel = TrainingViewModel(
            getSports = GetSportsUseCase(),
            generatePlan = GeneratePlanUseCase(repository)
        )

        setContent {
            GenAITheme {
                TrainingScreen(viewModel = viewModel)
            }
        }
    }
}
