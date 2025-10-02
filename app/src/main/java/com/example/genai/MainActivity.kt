package com.example.genai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.genai.ui.TrainingScreen
import com.example.genai.ui.theme.GenAITheme
import com.example.genai.viewmodel.TrainingViewModel
import com.example.genai.domain.GetSportsUseCase
import com.example.genai.domain.GeneratePlanUseCase
import com.example.genai.data.OpenAiRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = TrainingViewModel(
            getSports = GetSportsUseCase(),
            generatePlan = GeneratePlanUseCase(OpenAiRepository())
        )

        setContent {
            GenAITheme {
                TrainingScreen(viewModel = viewModel)
            }
        }
    }
}
