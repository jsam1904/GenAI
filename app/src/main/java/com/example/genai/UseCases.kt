package com.example.genai

import com.example.genai.OpenAiRepository

class GetSportsUseCase {
    operator fun invoke(): List<Sport> = listOf(
        Sport("soccer", "Fútbol", "Deporte de resistencia y trabajo en equipo"),
        Sport("basket", "Baloncesto", "Velocidad, salto y precisión"),
        Sport("swim", "Natación", "Cardio y técnica en agua"),
        Sport("tennis", "Tenis", "Resistencia y coordinación")
    )
}

class GeneratePlanUseCase(private val repo: OpenAiRepository) {
    suspend operator fun invoke(goal: TrainingGoal): TrainingPlan {
        return repo.generatePlan(goal)
    }
}
