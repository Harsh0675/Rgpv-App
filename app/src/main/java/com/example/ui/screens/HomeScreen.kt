package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Insights
import com.example.data.model.SampleStudent
import com.example.ui.components.CelebrationMusicCard
import com.example.ui.components.CumulativeGpaCalculatorCard
import com.example.ui.components.FormattedShareCard
import com.example.ui.components.RealRgpvMarksheetCard
import com.example.ui.components.ResultScoreCard
import com.example.ui.components.RgpvOfficialBanner
import com.example.ui.components.SubjectGradesTable
import com.example.ui.components.VoiceNarratorCard
import com.example.ui.theme.RgpvAmber
import com.example.ui.theme.RgpvBlueAccent
import com.example.ui.theme.RgpvEmerald
import com.example.ui.theme.RgpvNavyDark
import com.example.ui.theme.RgpvNavyPrimary
import com.example.ui.viewmodel.RgpvViewModel
import com.example.util.ResultShareHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    viewModel: RgpvViewModel,
    onNavigateToHistory: () -> Unit,
    onNavigateToPortal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val rollNo by viewModel.rollNoInput.collectAsStateWithLifecycle()
    val semester by viewModel.selectedSemester.collectAsStateWithLifecycle()
    val program by viewModel.selectedProgram.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val currentResult by viewModel.currentResult.collectAsStateWithLifecycle()
    val errorMsg by viewModel.errorMessage.collectAsStateWithLifecycle()
    val statusBanner by viewModel.statusBanner.collectAsStateWithLifecycle()
    val isSaved by viewModel.isResultSaved.collectAsStateWithLifecycle()

    // TTS states
    val isTtsLoading by viewModel.isTtsLoading.collectAsStateWithLifecycle()
    val isTtsPlaying by viewModel.isTtsPlaying.collectAsStateWithLifecycle()
    val ttsStatus by viewModel.ttsStatus.collectAsStateWithLifecycle()
    val selectedVoice by viewModel.selectedVoice.collectAsStateWithLifecycle()

    // Music states
    val isMusicLoading by viewModel.isMusicLoading.collectAsStateWithLifecycle()
    val isMusicPlaying by viewModel.isMusicPlaying.collectAsStateWithLifecycle()
    val musicStatus by viewModel.musicStatus.collectAsStateWithLifecycle()
    val selectedPreset by viewModel.selectedMusicPreset.collectAsStateWithLifecycle()
    val customPrompt by viewModel.customMusicPrompt.collectAsStateWithLifecycle()

    var showHtmlPaste by remember { mutableStateOf(false) }
    var pastedHtml by remember { mutableStateOf("") }
    var resultTabMode by remember { mutableStateOf(0) } // 0 = Official Real Marksheet, 1 = Analytics & Tools

    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        // Official University Brand Header Banner (matching exact official portal banner)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                RgpvOfficialBanner(
                    showLocationSubtitle = true,
                    showStatusBadge = true
                )

                // Live Scraper Status & Quick History Access Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateToPortal() }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(RgpvEmerald)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Target: result.rgpv.ac.in/result/BErslt.aspx",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { onNavigateToHistory() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Saved History",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Quick Test Profiles Row
        item {
            Column {
                Text(
                    text = "Quick Sample Profiles:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(viewModel.sampleStudents) { sample ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (rollNo == sample.rollNo) MaterialTheme.colorScheme.primary else Color.Transparent
                            ),
                            modifier = Modifier.clickable { viewModel.selectSample(sample) }
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                Text(
                                    text = sample.rollNo,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "${sample.name} • Sem ${sample.sem}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search / Scraper Inputs Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Scrape Semester Result",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Roll Number TextField
                    OutlinedTextField(
                        value = rollNo,
                        onValueChange = { viewModel.rollNoInput.value = it.uppercase() },
                        label = { Text("Enrollment / Roll Number") },
                        placeholder = { Text("e.g., 0827CS211045") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        trailingIcon = {
                            if (rollNo.isNotEmpty()) {
                                IconButton(onClick = { viewModel.rollNoInput.value = "" }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(onSearch = { viewModel.fetchResult() }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("roll_number_input"),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Semester Selector Chips
                    Text(
                        text = "Semester:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        (1..8).forEach { semNum ->
                            val semStr = semNum.toString()
                            FilterChip(
                                selected = semester == semStr,
                                onClick = { viewModel.selectedSemester.value = semStr },
                                label = { Text("Sem $semNum", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Program Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Grading", "Non-Grading").forEach { prog ->
                            FilterChip(
                                selected = program == prog,
                                onClick = { viewModel.selectedProgram.value = prog },
                                label = { Text(if (prog == "Grading") "CBGS Grading" else prog, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.fetchResult() },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("fetch_result_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Scraping BErslt.aspx...")
                            } else {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Fetch from BErslt.aspx", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Direct link to official result.rgpv.ac.in/result/BErslt.aspx
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse("https://result.rgpv.ac.in/result/BErslt.aspx")
                                )
                                context.startActivity(intent)
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🌐",
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "https://result.rgpv.ac.in/result/BErslt.aspx",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Official B.E. / B.Tech Examination Portal",
                                        fontSize = 9.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Open BErslt.aspx",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Direct HTML paste accordion
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showHtmlPaste = !showHtmlPaste }
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showHtmlPaste) "Hide Raw HTML Extractor" else "Direct HTML Source / Captcha Extractor",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    AnimatedVisibility(visible = showHtmlPaste) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            OutlinedTextField(
                                value = pastedHtml,
                                onValueChange = { pastedHtml = it },
                                label = { Text("Paste RGPV Result Page HTML Source", fontSize = 11.sp) },
                                placeholder = { Text("<table id='ctl00_ContentPlaceHolder1_GrdResult'>...", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                maxLines = 4
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                onClick = { viewModel.parsePastedHtml(pastedHtml) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Parse HTML Table", fontSize = 12.sp)
                            }
                        }
                    }

                    if (errorMsg != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMsg ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Active Result Section
        currentResult?.let { result ->
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Semester Examination Result",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            IconButton(
                                onClick = { viewModel.toggleSaveCurrentResult() },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = "Save to Offline History",
                                    tint = if (isSaved) RgpvEmerald else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = {
                                    ResultShareHelper.shareResult(context, result)
                                },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .testTag("share_result_header_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share Formatted Result Summary",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // View Mode Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = resultTabMode == 0,
                            onClick = { resultTabMode = 0 },
                            label = {
                                Text(
                                    text = "🏛️ Official Marksheet",
                                    fontWeight = if (resultTabMode == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF8B0000),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("tab_official_marksheet")
                        )

                        FilterChip(
                            selected = resultTabMode == 1,
                            onClick = { resultTabMode = 1 },
                            label = {
                                Text(
                                    text = "📊 Analytics & Tools",
                                    fontWeight = if (resultTabMode == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("tab_analytics_tools")
                        )
                    }
                }
            }

            if (resultTabMode == 0) {
                // Official University Grade Sheet (100% Real Portal Replica)
                item {
                    RealRgpvMarksheetCard(result = result)
                }

                // Cumulative GPA & SGPA Calculator based on Scraped Subjects & Grades
                item {
                    CumulativeGpaCalculatorCard(result = result)
                }

                // Formatted Share Card
                item {
                    FormattedShareCard(result = result)
                }
            } else {
                item {
                    ResultScoreCard(result = result)
                }

                // Cumulative GPA & SGPA Calculator based on Scraped Subjects & Grades
                item {
                    CumulativeGpaCalculatorCard(result = result)
                }

                // Share Formatted Result Summary Card
                item {
                    FormattedShareCard(result = result)
                }

                // Voice Result Summary (TTS with gemini-3.1-flash-tts-preview)
                item {
                    VoiceNarratorCard(
                        result = result,
                        isLoading = isTtsLoading,
                        isPlaying = isTtsPlaying,
                        statusText = ttsStatus,
                        selectedVoice = selectedVoice,
                        onVoiceSelected = { viewModel.selectedVoice.value = it },
                        onTogglePlay = { viewModel.playVoiceSummary() },
                        narrationPreview = viewModel.ttsService.buildNarrationText(result)
                    )
                }

                // Celebration Anthem (Lyria with lyria-3-clip-preview)
                item {
                    CelebrationMusicCard(
                        isLoading = isMusicLoading,
                        isPlaying = isMusicPlaying,
                        statusText = musicStatus,
                        selectedPreset = selectedPreset,
                        customPrompt = customPrompt,
                        onPresetSelected = { viewModel.selectedMusicPreset.value = it },
                        onCustomPromptChanged = { viewModel.customMusicPrompt.value = it },
                        onTogglePlay = { viewModel.generateAndPlayCelebrationMusic() }
                    )
                }

                // Subject-wise Marks & Grades Table
                item {
                    SubjectGradesTable(subjects = result.subjects)
                }
            }
        }
    }
}
