package com.example.genai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TrainingState(
    val sports: List<Sport> = emptyList(),
    val selected: Sport? = null,
    val objective: String = "",
    val days: List<String> = emptyList(),
    val level: String = "Beginner",
    val minutes: Int = 45,
    val plan: TrainingPlan? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class TrainingIntent {
    object LoadSports : TrainingIntent()
    data class SelectSport(val sport: Sport) : TrainingIntent()
    data class UpdateGoal(val objective: String, val days: List<String>, val level: String, val minutes: Int) : TrainingIntent()
    object GeneratePlan : TrainingIntent()
    object Clear : TrainingIntent()
}

class TrainingViewModel(
    private val getSports: GetSportsUseCase,
    private val generatePlan: GeneratePlanUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TrainingState())
    val state: StateFlow<TrainingState> = _state.asStateFlow()

    fun onIntent(intent: TrainingIntent) {
        when (intent) {
            is TrainingIntent.LoadSports -> _state.update { it.copy(sports = getSports()) }
            is TrainingIntent.SelectSport -> _state.update { it.copy(selected = intent.sport) }
            is TrainingIntent.UpdateGoal -> _state.update {
                it.copy(objective = intent.objective, days = intent.days, level = intent.level, minutes = intent.minutes)
            }
            is TrainingIntent.GeneratePlan -> generate()
            is TrainingIntent.Clear -> _state.update { it.copy(plan = null, error = null) }
        }
    }

    private fun generate() {
        val st = _state.value
        val sport = st.selected ?: return
        if (st.objective.isBlank() || st.days.isEmpty()) {
            _state.update { it.copy(error = "Faltan datos") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val goal = TrainingGoal(sport.id, st.objective, st.days, st.level, st.minutes)
                val plan = generatePlan(goal)
                _state.update { it.copy(plan = plan, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}
