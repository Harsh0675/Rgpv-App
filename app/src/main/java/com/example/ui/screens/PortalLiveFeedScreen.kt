package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.NotificationCategory
import com.example.data.model.RgpvNotification
import com.example.ui.theme.RgpvAmber
import com.example.ui.theme.RgpvBlueAccent
import com.example.ui.theme.RgpvEmerald
import com.example.ui.theme.RgpvNavyDark
import com.example.ui.theme.RgpvNavyPrimary
import com.example.ui.viewmodel.RgpvViewModel

@Composable
fun PortalLiveFeedScreen(
    viewModel: RgpvViewModel,
    modifier: Modifier = Modifier
) {
    val notices by viewModel.portalNotifications.collectAsStateWithLifecycle()
    val connStatus by viewModel.portalConnection.collectAsStateWithLifecycle()
    val isLoading by viewModel.isPortalLoading.collectAsStateWithLifecycle()
    val selectedCat by viewModel.selectedPortalCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.portalSearchQuery.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val filteredNotices = notices.filter { notice ->
        val matchesCat = selectedCat == null || notice.category == selectedCat
        val matchesSearch = searchQuery.isBlank() ||
                notice.title.contains(searchQuery, ignoreCase = true) ||
                notice.summary.contains(searchQuery, ignoreCase = true)
        matchesCat && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("portal_live_feed_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        // Live Portal Connection Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(RgpvNavyDark, RgpvNavyPrimary)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(RgpvEmerald)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "LIVE DATA FROM RGPV.AC.IN",
                                    color = Color(0xFFFFD54F),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            // Refresh button
                            IconButton(
                                onClick = { viewModel.refreshPortalData() },
                                enabled = !isLoading,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f))
                                    .testTag("refresh_portal_data_button")
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(18.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Refresh live portal data",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "https://www.rgpv.ac.in/",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Real-time scrapers active for university announcements, timetables, and circulars.",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Network info pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "Status: HTTP ${connStatus.httpCode} OK",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "${connStatus.itemsCount} Official Notices",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            if (connStatus.lastFetchedAt.isNotEmpty()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = connStatus.lastFetchedAt,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Official Sub-Portals Links Row
        item {
            Column {
                Text(
                    text = "Official RGPV Portal Links:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val portalLinks = listOf(
                        Triple("B.E. Results Portal", "result.rgpv.ac.in/result/BErslt.aspx", "https://result.rgpv.ac.in/result/BErslt.aspx"),
                        Triple("Main University Portal", "www.rgpv.ac.in", "https://www.rgpv.ac.in/"),
                        Triple("Time Table Portal", "Exam Schedules", "https://www.rgpv.ac.in/Uni/frm_ViewTimeTable.aspx"),
                        Triple("Academic Calendar", "Semester Plans", "https://www.rgpv.ac.in/Academic/frm_AcademicCalender.aspx")
                    )
                    items(portalLinks) { (title, subtitle, url) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = subtitle,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.portalSearchQuery.value = it },
                label = { Text("Search announcements from rgpv.ac.in...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.portalSearchQuery.value = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("portal_search_input"),
                shape = RoundedCornerShape(14.dp)
            )
        }

        // Category Filter Chips Row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCat == null,
                        onClick = { viewModel.selectedPortalCategory.value = null },
                        label = { Text("All (${notices.size})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
                items(NotificationCategory.entries) { cat ->
                    val count = notices.count { it.category == cat }
                    FilterChip(
                        selected = selectedCat == cat,
                        onClick = {
                            viewModel.selectedPortalCategory.value = if (selectedCat == cat) null else cat
                        },
                        label = { Text("${cat.label} ($count)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }
        }

        // Circulars / Notices List
        items(filteredNotices) { notice ->
            NoticeItemCard(
                notice = notice,
                onListen = { viewModel.narrateNotification(notice) },
                onOpenLink = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(notice.link))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Fallback open main portal
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.rgpv.ac.in/"))
                        context.startActivity(intent)
                    }
                }
            )
        }
    }
}

@Composable
fun NoticeItemCard(
    notice: RgpvNotification,
    onListen: () -> Unit,
    onOpenLink: () -> Unit
) {
    val categoryColor = when (notice.category) {
        NotificationCategory.EXAMINATION -> Color(0xFF7C3AED)
        NotificationCategory.ACADEMIC -> RgpvBlueAccent
        NotificationCategory.ADMISSION -> RgpvAmber
        NotificationCategory.GENERAL -> RgpvEmerald
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("notice_item_${notice.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = categoryColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = notice.category.label,
                        color = categoryColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (notice.isNew) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RgpvEmerald.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "NEW",
                                color = RgpvEmerald,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = notice.date,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = notice.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 19.sp
            )

            if (notice.summary.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = notice.summary,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Listen with Gemini TTS
                OutlinedButton(
                    onClick = onListen,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "AI Voice Summary", fontSize = 11.sp)
                }

                Button(
                    onClick = onOpenLink,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "View on rgpv.ac.in", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
