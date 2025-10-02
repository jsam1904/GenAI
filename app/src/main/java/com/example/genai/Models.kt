package com.example.genai

data class Sport(
    val id: String,
    val name: String,
    val description: String
)

data class TrainingGoal(
    val sportId: String,
    val objective: String,
    val availableDays: List<String>,
    val level: String,
    val minutesPerSession: Int
)

data class TrainingPlan(
    val sportId: String,
    val planMarkdown: String
)
