package com.example.data.gemini

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.util.Base64
import com.example.BuildConfig
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
import java.util.concurrent.TimeUnit
import kotlin.math.sin

class LyriaMusicService(private val context: Context) {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val requestAdapter = moshi.adapter(GeminiAudioRequest::class.java)
    private val responseAdapter = moshi.adapter(GeminiAudioResponse::class.java)

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private var mediaPlayer: MediaPlayer? = null
    private var synthTrack: AudioTrack? = null
    private var isPlayingSynth = false

    suspend fun generateAndPlayMusic(
        prompt: String,
        userApiKey: String? = null,
        onProgress: (String) -> Unit,
        onPlaybackStarted: () -> Unit,
        onPlaybackFinished: () -> Unit
    ): Result<String> = withContext(Dispatchers.IO) {
        val key = resolveApiKey(userApiKey)

        if (key.isNullOrBlank() || key == "MY_GEMINI_API_KEY") {
            onProgress("Playing local celebration victory anthem (add Gemini API key for Lyria AI generation)...")
            playCelebrationSynthFanfare(onPlaybackStarted, onPlaybackFinished)
            return@withContext Result.success("Played local victory fanfare")
        }

        onProgress("Generating audio with lyria-3-clip-preview...")

        val request = GeminiAudioRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(GeminiPart(text = prompt))
                )
            ),
            generationConfig = GeminiGenerationConfig(
                responseModalities = listOf("AUDIO")
            )
        )

        val jsonBody = requestAdapter.toJson(request)
        val url = "https://generativelanguage.googleapis.com/v1beta/models/lyria-3-clip-preview:generateContent?key=$key"

        try {
            val httpRequest = Request.Builder()
                .url(url)
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = httpClient.newCall(httpRequest).execute()
            val respBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                onProgress("Lyria API error: ${response.code}. Playing celebratory fanfare fallback...")
                playCelebrationSynthFanfare(onPlaybackStarted, onPlaybackFinished)
                return@withContext Result.success("Played celebratory fanfare fallback")
            }

            val parsed = responseAdapter.fromJson(respBody)
            val base64Data = parsed?.candidates?.firstOrNull()?.content?.parts?.firstOrNull { it.inlineData != null }?.inlineData?.data

            if (base64Data.isNullOrEmpty()) {
                onProgress("Lyria did not return audio stream, playing celebratory fanfare...")
                playCelebrationSynthFanfare(onPlaybackStarted, onPlaybackFinished)
                return@withContext Result.success("Played celebratory fanfare fallback")
            }

            onProgress("Buffering Lyria clip and starting playback...")
            val audioBytes = Base64.decode(base64Data, Base64.DEFAULT)
            val audioFile = File(context.cacheDir, "lyria_anthem_clip.mp3")
            FileOutputStream(audioFile).use { it.write(audioBytes) }

            withContext(Dispatchers.Main) {
                playAudioFile(audioFile, onPlaybackStarted, onPlaybackFinished)
            }

            Result.success("Playing Lyria generated track (${audioBytes.size / 1024} KB)")
        } catch (e: Exception) {
            onProgress("Lyria connection issue: ${e.message}. Playing celebratory fanfare...")
            playCelebrationSynthFanfare(onPlaybackStarted, onPlaybackFinished)
            Result.success("Played celebratory fanfare fallback")
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

    /**
     * Algorithmic celebration fanfare using AudioTrack (polyphonic brass/chime celebration arpeggio)
     */
    private suspend fun playCelebrationSynthFanfare(onStarted: () -> Unit, onFinished: () -> Unit) = withContext(Dispatchers.IO) {
        stopAudio()
        isPlayingSynth = true

        withContext(Dispatchers.Main) { onStarted() }

        val sampleRate = 44100
        val minBufSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBufSize.coerceAtLeast(8192))
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        synthTrack = track
        track.play()

        // Celebration Melody: C5, E5, G5, C6 triumph fanfares!
        val notes = listOf(
            523.25 to 250, // C5
            659.25 to 250, // E5
            783.99 to 250, // G5
            1046.50 to 600, // C6
            783.99 to 200, // G5
            1046.50 to 900  // High C6
        )

        for ((freq, durationMs) in notes) {
            if (!isPlayingSynth) break
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val samples = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                // Harmonic richness (fundamental + overtone) with gentle exponential decay envelope
                val envelope = kotlin.math.max(0.0, 1.0 - (i.toDouble() / numSamples) * 0.8)
                val wave = (sin(2.0 * Math.PI * freq * t) * 0.7 + sin(4.0 * Math.PI * freq * t) * 0.3) * envelope
                samples[i] = (wave * Short.MAX_VALUE * 0.6).toInt().toShort()
            }
            track.write(samples, 0, samples.size)
        }

        try {
            track.stop()
            track.release()
        } catch (e: Exception) {
            // Ignore
        } finally {
            synthTrack = null
            isPlayingSynth = false
            withContext(Dispatchers.Main) { onFinished() }
        }
    }

    fun stopAudio() {
        isPlayingSynth = false
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
        } catch (e: Exception) {
            // Ignore
        } finally {
            mediaPlayer = null
        }
        try {
            synthTrack?.let {
                it.stop()
                it.release()
            }
        } catch (e: Exception) {
            // Ignore
        } finally {
            synthTrack = null
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
    }
}
