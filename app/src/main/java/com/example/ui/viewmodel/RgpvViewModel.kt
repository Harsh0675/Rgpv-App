package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.gemini.GeminiTtsService
import com.example.data.gemini.LyriaMusicService
import com.example.data.local.AppDatabase
import com.example.data.local.ResultRepository
import com.example.data.model.NotificationCategory
import com.example.data.model.PortalConnectionStatus
import com.example.data.model.RgpvNotification
import com.example.data.model.SampleStudent
import com.example.data.model.StudentResult
import com.example.data.scraper.RgpvPortalScraper
import com.example.data.scraper.RgpvScraper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RgpvViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = ResultRepository(database.resultDao())
    val ttsService = GeminiTtsService(application)
    val musicService = LyriaMusicService(application)

    // Form Inputs
    val rollNoInput = MutableStateFlow("0827CS211045")
    val selectedSemester = MutableStateFlow("6")
    val selectedProgram = MutableStateFlow("Grading")
    val userCustomApiKey = MutableStateFlow("")

    // Result & Scraping State
    val isLoading = MutableStateFlow(false)
    val currentResult = MutableStateFlow<StudentResult?>(null)
    val errorMessage = MutableStateFlow<String?>(null)
    val statusBanner = MutableStateFlow<String?>(null)
    val isResultSaved = MutableStateFlow(false)

    // History
    val historyList: StateFlow<List<StudentResult>> = repository.allSavedResults
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // TTS state (gemini-3.1-flash-tts-preview)
    val isTtsLoading = MutableStateFlow(false)
    val isTtsPlaying = MutableStateFlow(false)
    val ttsStatus = MutableStateFlow("Tap to listen to vocal announcement")
    val selectedVoice = MutableStateFlow("Kore")

    // Music Generation state (lyria-3-clip-preview)
    val isMusicLoading = MutableStateFlow(false)
    val isMusicPlaying = MutableStateFlow(false)
    val musicStatus = MutableStateFlow("Tap to play victory celebration music")
    val selectedMusicPreset = MutableStateFlow("Victory Fanfare")
    val customMusicPrompt = MutableStateFlow("Triumphant orchestral graduation fanfare with brass, timpanis, and celebration melody")

    // Live Portal Feed from https://www.rgpv.ac.in/
    val portalNotifications = MutableStateFlow<List<RgpvNotification>>(emptyList())
    val portalConnection = MutableStateFlow(PortalConnectionStatus())
    val isPortalLoading = MutableStateFlow(false)
    val selectedPortalCategory = MutableStateFlow<NotificationCategory?>(null)
    val portalSearchQuery = MutableStateFlow("")

    // Sample roll numbers for testing
    val sampleStudents = listOf(
        SampleStudent("0827CS211045", "Rahul Sharma", "Computer Science", "6", "6th Sem - High Distinction (8.42 SGPA)"),
        SampleStudent("0101IT221015", "Ananya Verma", "Information Tech", "4", "4th Sem - Algorithms & OS (8.80 SGPA)"),
        SampleStudent("0103EC201089", "Priyanshu Gupta", "Electronics & Comm", "8", "8th Sem - VLSI Final Sem (9.10 CGPA)"),
        SampleStudent("0801ME211050", "Harshit Dubey", "Mechanical Engg", "5", "5th Sem - Machine Design (7.95 SGPA)")
    )

    init {
        // Pre-load default sample on launch so user has immediate rich preview
        fetchResult(forceSample = true)
        refreshPortalData()
    }

    fun fetchResult(forceSample: Boolean = false) {
        val roll = rollNoInput.value.trim().uppercase()
        if (roll.isEmpty()) {
            errorMessage.value = "Please enter an Enrollment Number (e.g., 0827CS211045)"
            return
        }

        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            statusBanner.value = "Fetching RGPV Semester ${selectedSemester.value} results..."

            val res = RgpvScraper.fetchResult(
                rollNo = roll,
                semester = selectedSemester.value,
                program = selectedProgram.value,
                forceSample = forceSample
            )

            res.fold(
                onSuccess = { studentResult ->
                    currentResult.value = studentResult
                    statusBanner.value = "Result loaded successfully"
                    isResultSaved.value = false
                    // Auto-save to history for student convenience
                    repository.saveResult(studentResult)
                    isResultSaved.value = true
                },
                onFailure = { err ->
                    errorMessage.value = err.message ?: "Could not fetch result"
                    statusBanner.value = null
                }
            )
            isLoading.value = false
        }
    }

    fun parsePastedHtml(html: String) {
        if (html.isBlank()) {
            errorMessage.value = "Pasted HTML is empty"
            return
        }
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            statusBanner.value = "Parsing HTML source table..."
            val parsed = RgpvScraper.parseHtmlResult(
                html = html,
                fallbackRoll = rollNoInput.value,
                fallbackSem = selectedSemester.value
            )
            if (parsed != null && parsed.subjects.isNotEmpty()) {
                currentResult.value = parsed
                statusBanner.value = "Successfully extracted ${parsed.subjects.size} subjects"
                repository.saveResult(parsed)
                isResultSaved.value = true
            } else {
                errorMessage.value = "Could not parse RGPV table from the provided HTML. Verify table contains Subject Code and Grades."
            }
            isLoading.value = false
        }
    }

    fun selectSample(sample: SampleStudent) {
        rollNoInput.value = sample.rollNo
        selectedSemester.value = sample.sem
        fetchResult(forceSample = true)
    }

    fun selectResult(result: StudentResult) {
        currentResult.value = result
        rollNoInput.value = result.rollNo
        selectedSemester.value = result.semester
        isResultSaved.value = true
    }

    fun toggleSaveCurrentResult() {
        val result = currentResult.value ?: return
        viewModelScope.launch {
            repository.saveResult(result)
            isResultSaved.value = true
            statusBanner.value = "Saved to local offline history"
        }
    }

    fun playVoiceSummary() {
        val result = currentResult.value ?: return
        if (isTtsPlaying.value) {
            stopVoiceSummary()
            return
        }

        viewModelScope.launch {
            isTtsLoading.value = true
            ttsStatus.value = "Synthesizing voice announcement..."
            val speechText = ttsService.buildNarrationText(result)

            ttsService.generateAndPlaySpeech(
                text = speechText,
                voiceName = selectedVoice.value,
                userApiKey = userCustomApiKey.value,
                onProgress = { status -> ttsStatus.value = status },
                onPlaybackStarted = {
                    isTtsLoading.value = false
                    isTtsPlaying.value = true
                    ttsStatus.value = "Announcing ${result.studentName}'s result..."
                },
                onPlaybackFinished = {
                    isTtsLoading.value = false
                    isTtsPlaying.value = false
                    ttsStatus.value = "Narration completed"
                }
            )
        }
    }

    fun stopVoiceSummary() {
        ttsService.stopAudio()
        isTtsLoading.value = false
        isTtsPlaying.value = false
        ttsStatus.value = "Voice playback paused"
    }

    fun generateAndPlayCelebrationMusic() {
        if (isMusicPlaying.value) {
            stopMusic()
            return
        }

        val prompt = when (selectedMusicPreset.value) {
            "Victory Fanfare" -> "Triumphant celebratory orchestral victory fanfare with brass, timpani, and graduation celebration melody"
            "Lo-Fi Study Beat" -> "Relaxing chill lo-fi hip hop study beat with warm piano, vinyl crackle, and soothing bassline"
            "Synthwave Victory" -> "Upbeat 80s synthwave victory anthem with energetic synthesizers, driving drums, and celebratory chords"
            else -> customMusicPrompt.value.ifBlank { "Celebratory university victory anthem" }
        }

        viewModelScope.launch {
            isMusicLoading.value = true
            musicStatus.value = "Composing celebration track with lyria-3-clip-preview..."

            musicService.generateAndPlayMusic(
                prompt = prompt,
                userApiKey = userCustomApiKey.value,
                onProgress = { status -> musicStatus.value = status },
                onPlaybackStarted = {
                    isMusicLoading.value = false
                    isMusicPlaying.value = true
                    musicStatus.value = "Playing Celebration Anthem: ${selectedMusicPreset.value}"
                },
                onPlaybackFinished = {
                    isMusicLoading.value = false
                    isMusicPlaying.value = false
                    musicStatus.value = "Track finished"
                }
            )
        }
    }

    fun stopMusic() {
        musicService.stopAudio()
        isMusicLoading.value = false
        isMusicPlaying.value = false
        musicStatus.value = "Music stopped"
    }

    fun refreshPortalData() {
        viewModelScope.launch {
            isPortalLoading.value = true
            val (notices, status) = RgpvPortalScraper.fetchPortalData()
            portalNotifications.value = notices
            portalConnection.value = status
            isPortalLoading.value = false
        }
    }

    fun narrateNotification(notice: RgpvNotification) {
        if (isTtsPlaying.value) {
            stopVoiceSummary()
            return
        }
        viewModelScope.launch {
            isTtsLoading.value = true
            ttsStatus.value = "Announcing official notice..."
            val speech = "Official announcement from Rajiv Gandhi Proudyogiki Vishwavidyalaya. " +
                    "Category: ${notice.category.label}. " +
                    "Headline: ${notice.title}. " +
                    "Details: ${notice.summary}. Date published: ${notice.date}."

            ttsService.generateAndPlaySpeech(
                text = speech,
                voiceName = selectedVoice.value,
                userApiKey = userCustomApiKey.value,
                onProgress = { ttsStatus.value = it },
                onPlaybackStarted = {
                    isTtsLoading.value = false
                    isTtsPlaying.value = true
                    ttsStatus.value = "Reading notice: ${notice.title}"
                },
                onPlaybackFinished = {
                    isTtsLoading.value = false
                    isTtsPlaying.value = false
                    ttsStatus.value = "Announcement completed"
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsService.release()
        musicService.release()
    }
}
