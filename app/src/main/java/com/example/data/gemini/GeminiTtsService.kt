package com.example.data.gemini

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.speech.tts.TextToSpeech
import android.util.Base64
import com.example.BuildConfig
import com.example.data.model.StudentResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.concurrent.TimeUnit

class GeminiTtsService(private val context: Context) {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val requestAdapter = moshi.adapter(GeminiAudioRequest::class.java)
    private val responseAdapter = moshi.adapter(GeminiAudioResponse::class.java)

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private var mediaPlayer: MediaPlayer? = null
    private var androidTts: TextToSpeech? = null
    private var isTtsInitialized = false

    init {
        try {
            androidTts = TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    androidTts?.language = Locale.ENGLISH
                    isTtsInitialized = true
                }
            }
        } catch (e: Exception) {
            // Android TTS init fallback
        }
    }

    /**
     * Builds narration speech text for RGPV student result
     */
    fun buildNarrationText(result: StudentResult): String {
        val topSubjects = result.subjects.filter { it.totalGrade == "A+" || it.totalGrade == "A" }
            .take(2)
            .joinToString(" and ") { it.subjectName }

        val subjectHighlight = if (topSubjects.isNotEmpty()) {
            "You demonstrated academic excellence in $topSubjects."
        } else {
            "You completed all course modules for this academic session."
        }

        return "Official Rajiv Gandhi Proudyogiki Vishwavidyalaya examination result announcement. " +
                "Congratulations ${result.studentName}! " +
                "For enrollment number ${result.rollNo} in ${result.branch}, Semester ${result.semester}, " +
                "your result status is ${result.resultStatus}. " +
                "You have secured a Semester Grade Point Average of ${result.sgpa}, " +
                "with an overall Cumulative Grade Point Average of ${result.cgpa}, " +
                "earning a total of ${result.totalCredits} credits. " +
                "$subjectHighlight " +
                "Wishing you continued success in your engineering journey!"
    }

    suspend fun generateAndPlaySpeech(
        text: String,
        voiceName: String = "Kore",
        userApiKey: String? = null,
        onProgress: (String) -> Unit,
        onPlaybackStarted: () -> Unit,
        onPlaybackFinished: () -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        val key = resolveApiKey(userApiKey)

        if (key.isNullOrBlank() || key == "MY_GEMINI_API_KEY") {
            onProgress("Using local speech engine (configure Gemini key for AI voice)...")
            speakWithAndroidTts(text, onPlaybackStarted, onPlaybackFinished)
            return@withContext Result.success("Spoke using system engine")
        }

        onProgress("Connecting to gemini-3.1-flash-tts-preview...")

        val request = GeminiAudioRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = text))
                )
            ),
            generationConfig = GeminiGenerationConfig(
                responseModalities = listOf("AUDIO"),
                speechConfig = GeminiSpeechConfig(
                    voiceConfig = GeminiVoiceConfig(
                        prebuiltVoiceConfig = GeminiPrebuiltVoiceConfig(voiceName = voiceName)
                    )
                )
            )
        )

        val jsonBody = requestAdapter.toJson(request)
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-flash-tts-preview:generateContent?key=$key"

        try {
            val httpRequest = Request.Builder()
                .url(url)
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = httpClient.newCall(httpRequest).execute()
            val respBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                onProgress("Gemini TTS HTTP error: ${response.code}. Switching to local speech fallback...")
                speakWithAndroidTts(text, onPlaybackStarted, onPlaybackFinished)
                return@withContext Result.success("Fell back to system speech")
            }

            val parsed = responseAdapter.fromJson(respBody)
            val base64Data = parsed?.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.inlineData != null }?.inlineData?.data

            if (base64Data.isNullOrEmpty()) {
                onProgress("Audio stream not returned, using fallback...")
                speakWithAndroidTts(text, onPlaybackStarted, onPlaybackFinished)
                return@withContext Result.success("Fell back to system speech")
            }

            onProgress("Decoding audio and playing...")
            val audioBytes = Base64.decode(base64Data, Base64.DEFAULT)
            val audioFile = File(context.cacheDir, "gemini_tts_preview.mp3")
            FileOutputStream(audioFile).use { it.write(audioBytes) }

            withContext(Dispatchers.Main) {
                playAudioFile(audioFile, onPlaybackStarted, onPlaybackFinished)
            }

            Result.success("Played Gemini TTS audio (${audioBytes.size / 1024} KB)")
        } catch (e: Exception) {
            onProgress("Network error: ${e.message}. Using speech fallback...")
            speakWithAndroidTts(text, onPlaybackStarted, onPlaybackFinished)
            Result.success("Fell back to system speech: ${e.message}")
        }
    }

    private fun playAudioFile(file: File, onStarted: () -> Unit, onFinished: () -> Unit) {
        stopAudio()
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnPreparedListener {
                    it.start()
                    onStarted()
                }
                setOnCompletionListener {
                    onFinished()
                }
                setOnErrorListener { _, _, _ ->
                    onFinished()
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            onFinished()
        }
    }

    private fun speakWithAndroidTts(text: String, onStarted: () -> Unit, onFinished: () -> Unit) {
        androidTts?.let { tts ->
            onStarted()
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "rgpv_tts_utterance")
            // Schedule finish callback estimate or completion listener
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                onFinished()
            }, (text.length * 75L).coerceIn(2000L, 20000L))
        } ?: run {
            onFinished()
        }
    }

    fun stopAudio() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            // Ignore stop errors
        } finally {
            mediaPlayer = null
        }
        try {
            androidTts?.stop()
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun resolveApiKey(userProvided: String?): String? {
        if (!userProvided.isNullOrBlank()) return userProvided.trim()
        val buildKey = BuildConfig.GEMINI_API_KEY
        if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") return buildKey
        return null
    }

    fun release() {
        stopAudio()
        try {
            androidTts?.shutdown()
        } catch (e: Exception) {
            // Ignore
        }
    }
}
