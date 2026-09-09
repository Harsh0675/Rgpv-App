package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentResult
import com.example.data.model.SubjectResult
import com.example.ui.theme.RgpvAmber
import com.example.ui.theme.RgpvBlueAccent
import com.example.ui.theme.RgpvEmerald
import com.example.ui.theme.RgpvNavyDark
import com.example.ui.theme.RgpvNavyPrimary
import com.example.util.CalculatedCumulativeGpa
import com.example.util.CalculatedSemesterGpa
import com.example.util.GpaCalculator
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CumulativeGpaCalculatorCard(
    result: StudentResult,
    modifier: Modifier = Modifier
) {
    // Keep local copy of subjects to allow "What-If" grade simulation
    var workingSubjects by remember(result) { mutableStateOf(result.subjects) }
    var isWhatIfMode by remember { mutableStateOf(false) }

    val semNumber = result.semester.toIntOrNull() ?: 1
    val defaultPrevCredits = ((semNumber - 1).coerceAtLeast(0)) * 22
    val defaultPrevCgpa = if (semNumber > 1) result.cgpa else 0.0

    var prevCreditsInput by remember(result) { mutableStateOf(defaultPrevCredits.toString()) }
    var prevCgpaInput by remember(result) {
        mutableStateOf(String.format(Locale.US, "%.2f", defaultPrevCgpa))
    }

    var showSubjectDetails by remember { mutableStateOf(false) }
    var showFormulaInfo by remember { mutableStateOf(false) }

    // Calculated Semester GPA based on current working subjects
    val semesterGpaResult: CalculatedSemesterGpa = remember(workingSubjects) {
        GpaCalculator.calculateSemesterGpa(workingSubjects)
    }

    // Calculated Cumulative GPA based on previous credits/CGPA + current semester
    val cumulativeGpaResult: CalculatedCumulativeGpa = remember(
        semesterGpaResult,
        prevCreditsInput,
        prevCgpaInput
    ) {
        val prevCredits = prevCreditsInput.toIntOrNull() ?: 0
        val prevCgpa = prevCgpaInput.toDoubleOrNull() ?: 0.0

        if (prevCredits <= 0 || prevCgpa <= 0.0) {
            // If first semester or no previous data, cumulative GPA is current semester's GPA
            GpaCalculator.calculateCumulativeGpa(
                previousCredits = 0,
                previousCgpa = 0.0,
                currentCredits = semesterGpaResult.totalCredits,
                currentSgpa = semesterGpaResult.sgpa
            )
        } else {
            GpaCalculator.calculateCumulativeGpa(
                previousCredits = prevCredits,
                previousCgpa = prevCgpa,
                currentCredits = semesterGpaResult.totalCredits,
                currentSgpa = semesterGpaResult.sgpa
            )
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cumulative_gpa_calculator_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(RgpvBlueAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = "GPA Calculator",
                            tint = RgpvBlueAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Cumulative GPA Calculator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Calculated from ${workingSubjects.size} scraped subjects & credits",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { showFormulaInfo = !showFormulaInfo },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Formula details",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Formula explanation box (toggleable)
            AnimatedVisibility(visible = showFormulaInfo) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Official RGPV CGPA Formula:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Semester GPA (SGPA) = Σ (Credits × Grade Points) / Σ Credits\n" +
                                    "• Cumulative GPA (CGPA) = [ (Prev CGPA × Prev Credits) + (Current SGPA × Current Credits) ] / Total Credits\n" +
                                    "• Equivalent % = (CGPA - 0.75) × 10",
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Results Highlight Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Calculated Semester GPA
                        Column {
                            Text(
                                text = "CALCULATED SGPA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format(Locale.US, "%.2f", semesterGpaResult.sgpa),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${semesterGpaResult.earnedCredits} / ${semesterGpaResult.totalCredits} Credits",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )

                        // Calculated Cumulative GPA (CGPA)
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "CUMULATIVE GPA (CGPA)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RgpvAmber,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = String.format(Locale.US, "%.2f", cumulativeGpaResult.cumulativeGpa),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RgpvAmber
                            )
                            Text(
                                text = "${cumulativeGpaResult.totalCumulativeCredits} Total Credits",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RgpvEmerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = cumulativeGpaResult.division,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RgpvEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = "Estimated Percentage: ${String.format(Locale.US, "%.1f", cumulativeGpaResult.percentage)}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Previous Semesters Input Section (for Cumulative calculation)
            if (semNumber > 1) {
                Text(
                    text = "Previous Semesters Record (Sem 1 to ${semNumber - 1}):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = prevCreditsInput,
                        onValueChange = { prevCreditsInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Prior Credits", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("prior_credits_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = prevCgpaInput,
                        onValueChange = { prevCgpaInput = it },
                        label = { Text("Prior CGPA", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("prior_cgpa_input"),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Subject-wise Credit & Grade Breakdown details toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showSubjectDetails = !showSubjectDetails }
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showSubjectDetails) "Hide Subject Calculation Breakdown" else "View Subject Breakdown (${workingSubjects.size} courses)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (showSubjectDetails) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = showSubjectDetails) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    // What-if simulator toggle & reset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isWhatIfMode) "What-If Simulator Active (Tap grade to change)" else "Scraped Grades Breakdown",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isWhatIfMode) RgpvAmber else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row {
                            if (isWhatIfMode) {
                                TextButton(
                                    onClick = {
                                        workingSubjects = result.subjects
                                        isWhatIfMode = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RestartAlt,
                                        contentDescription = "Reset",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reset", fontSize = 11.sp)
                                }
                            } else {
                                TextButton(onClick = { isWhatIfMode = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Simulate",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Simulate Grades", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    semesterGpaResult.subjectCalculations.forEachIndexed { idx, item ->
                        SubjectCalculationRow(
                            calculation = item,
                            isInteractive = isWhatIfMode,
                            onGradeChanged = { newGrade ->
                                val updated = workingSubjects.toMutableList()
                                val current = updated[idx]
                                updated[idx] = current.copy(totalGrade = newGrade)
                                workingSubjects = updated
                            }
                        )
                        if (idx < semesterGpaResult.subjectCalculations.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Quality Points: ${String.format(Locale.US, "%.1f", semesterGpaResult.totalQualityPoints)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Total Credits: ${semesterGpaResult.totalCredits}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectCalculationRow(
    calculation: com.example.util.SubjectCreditCalculation,
    isInteractive: Boolean,
    onGradeChanged: (String) -> Unit
) {
    var dropdownExpanded by remember { mutableStateOf(false) }
    val availableGrades = listOf("A+", "A", "B+", "B", "C+", "C", "D", "F")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${calculation.subjectCode} - ${calculation.subjectName}",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = "${calculation.credits} Credits × ${calculation.gradePoint.toInt()} pts = ${String.format(Locale.US, "%.1f", calculation.qualityPoints)} quality points",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isInteractive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clickable(enabled = isInteractive) { dropdownExpanded = true }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = calculation.grade,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isInteractive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                    if (isInteractive) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = "Select Grade",
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                availableGrades.forEach { g ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "$g (${GpaCalculator.gradeToPoints(g).toInt()} pts)",
                                fontSize = 12.sp
                            )
                        },
                        onClick = {
                            onGradeChanged(g)
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }
    }
}
