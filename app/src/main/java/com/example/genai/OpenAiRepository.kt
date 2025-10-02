package com.example.genai

import com.example.genai.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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
data class ChatReq(
    val model: String,
    val messages: List<Message>,
    @field:Json(name = "max_tokens") val maxTokens: Int = 800
)

data class Message(val role: String, val content: String)

data class ChatRes(val choices: List<Choice>)
data class Choice(val message: Message)   // 👈 versión simple (funcionaba antes)


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

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        api = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
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

        try {
            val res = api.chat(ChatReq("gpt-4o-mini", listOf(sys, usr)))
            android.util.Log.d("AI_DEBUG", "Respuesta completa: $res")

            val content = res.choices.firstOrNull()?.message?.content ?: "⚠️ No se recibió plan"
            TrainingPlan(goal.sportId, content)
        } catch (e: Exception) {
            android.util.Log.e("AI_ERROR", "Falló request", e)
            throw e
        }
    }
}
