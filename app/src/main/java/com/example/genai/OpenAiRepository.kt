package com.example.genai

import com.example.genai.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// DTOs
data class ChatReq(val model: String, val messages: List<Message>, val maxTokens: Int = 800)
data class Message(val role: String, val content: String)
data class ChatRes(val choices: List<Choice>)
data class Choice(val message: Message)

// API
interface OpenAiService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun chat(@Body req: ChatReq): ChatRes
}

class OpenAiRepository {
    private val api: OpenAiService

    init {
        val auth = Interceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
                    .build()
            )
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(auth)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()
        api = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(OpenAiService::class.java)
    }

    suspend fun generatePlan(goal: TrainingGoal): TrainingPlan = withContext(Dispatchers.IO) {
        val sys = Message("system", "You are a sports coach. Return a 4-week training plan in markdown.")
        val usr = Message("user", """
            Sport: ${goal.sportId}
            Objective: ${goal.objective}
            Days: ${goal.availableDays.joinToString()}
            Level: ${goal.level}
            Minutes: ${goal.minutesPerSession}
        """.trimIndent())

        val res = api.chat(ChatReq("gpt-4o-mini", listOf(sys, usr)))
        TrainingPlan(goal.sportId, res.choices.first().message.content)
    }
}
