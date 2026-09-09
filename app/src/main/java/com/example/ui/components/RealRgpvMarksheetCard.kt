package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.StudentResult
import com.example.ui.theme.RgpvAmber
import com.example.ui.theme.RgpvBlueAccent
import com.example.ui.theme.RgpvEmerald
import com.example.ui.theme.RgpvNavyDark
import com.example.ui.theme.RgpvNavyPrimary
import com.example.ui.theme.RgpvRose
import com.example.util.ResultShareHelper
import java.util.Locale

/**
 * Authentic replica of the Official RGPV Result Grade Sheet
 * matching the exact format, typography, headers, tabular borders,
 * watermark, and university verification stamp as seen on result.rgpv.ac.in.
 */
@Composable
fun RealRgpvMarksheetCard(
    result: StudentResult,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isPass = result.resultStatus.contains("PASS", ignoreCase = true)
    val statusColor = if (isPass) Color(0xFF1B5E20) else Color(0xFFB71C1C)

    val division = when {
        result.cgpa >= 8.5 -> "FIRST DIVISION WITH HONOURS"
        result.cgpa >= 6.5 -> "FIRST DIVISION"
        result.cgpa >= 5.0 -> "SECOND DIVISION"
        else -> "PASS DIVISION"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("real_rgpv_marksheet_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF8B0000)), // Classic RGPV Maroon border
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Faint University Emblem Watermark in background
            Image(
                painter = painterResource(id = R.drawable.ic_rgpv_logo),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.Center)
                    .alpha(0.04f)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                // Official Black University Header Banner (Replicating exact user image)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF000000))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.dp, Color(0xFFD32F2F), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_rgpv_logo),
                                contentDescription = "RGPV Emblem",
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Rajiv Gandhi Proudyogiki Vishwavidyalaya (RGPV)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                letterSpacing = 0.2.sp
                            )
                            Text(
                                text = "(State Technological University of M.P)",
                                color = Color(0xFFB0B0B0),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Airport Bypass Road, Gandhi Nagar, Bhopal - 462033",
                                color = Color(0xFF888888),
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // Sub-header: Official Document Title
                Surface(
                    color = Color(0xFFF3E5F5).copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth(),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "TABULATION REGISTER / STATEMENT OF GRADES",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.5.sp,
                            color = Color(0xFF4A148C),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "EXAMINATION: ${result.examSession.uppercase()} • SEMESTER: ${result.semester} • SYSTEM: CBGS",
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color(0xFF333333)
                        )
                    }
                }

                Column(modifier = Modifier.padding(14.dp)) {
                    // Candidate Particulars Table (Government marksheet format)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCCCCCC)),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ParticularsItem(label = "Enrollment No.", value = result.rollNo, isHighlighted = true)
                                ParticularsItem(label = "Examination Roll No.", value = result.rollNo)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFEEEEEE))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ParticularsItem(label = "Student Name", value = result.studentName.uppercase(), isHighlighted = true)
                                ParticularsItem(label = "Father's Name", value = result.fatherName.uppercase())
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFEEEEEE))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ParticularsItem(label = "Course", value = result.course)
                                ParticularsItem(label = "Branch", value = result.branch)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp), color = Color(0xFFEEEEEE))

                            ParticularsItem(label = "Institution", value = result.institute)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Subjects & Grades Grid Header
                    Text(
                        text = "ACADEMIC PERFORMANCE IN EXAMINATION COURSES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF333333),
                        letterSpacing = 0.3.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Authentic Table
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF263238)),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            // Header Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF263238))
                                    .padding(horizontal = 8.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Code",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    modifier = Modifier.width(62.dp)
                                )
                                Text(
                                    text = "Subject Name",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Cr",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(28.dp)
                                )
                                Text(
                                    text = "Th",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(26.dp)
                                )
                                Text(
                                    text = "Pr",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(26.dp)
                                )
                                Text(
                                    text = "Grd",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color(0xFFFFD54F),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(34.dp)
                                )
                            }

                            // Subject Rows
                            result.subjects.forEachIndexed { index, sub ->
                                val rowBg = if (index % 2 == 0) Color.White else Color(0xFFF8F9FA)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(rowBg)
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = sub.subjectCode,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color(0xFF0D47A1),
                                        modifier = Modifier.width(62.dp)
                                    )
                                    Text(
                                        text = sub.subjectName,
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF212121),
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "${sub.credits}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF424242),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(28.dp)
                                    )
                                    Text(
                                        text = sub.theoryGrade,
                                        fontSize = 10.sp,
                                        color = Color(0xFF424242),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(26.dp)
                                    )
                                    Text(
                                        text = sub.practicalGrade,
                                        fontSize = 10.sp,
                                        color = Color(0xFF424242),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(26.dp)
                                    )
                                    Text(
                                        text = sub.totalGrade,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.5.sp,
                                        color = if (sub.totalGrade.startsWith("A") || sub.totalGrade == "O") Color(0xFF1B5E20) else Color(0xFF0D47A1),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.width(34.dp)
                                    )
                                }
                                if (index < result.subjects.size - 1) {
                                    HorizontalDivider(color = Color(0xFFE0E0E0))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Final Performance Matrix
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF8B0000)),
                        color = Color(0xFFFFF8E1),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "SEMESTER SGPA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                                    Text(
                                        text = String.format(Locale.US, "%.2f", result.sgpa),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0D47A1)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "CUMULATIVE CGPA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                                    Text(
                                        text = String.format(Locale.US, "%.2f", result.cgpa),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF8B0000)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "FINAL RESULT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                                    Text(
                                        text = result.resultStatus.uppercase(),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = statusColor
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFFFD54F))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Division: $division",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                                Text(
                                    text = "Total Credits Earned: ${result.totalCredits}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF424242)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Official Verification & Seal Block
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.QrCode2,
                                    contentDescription = "QR Code",
                                    tint = Color(0xFF263238),
                                    modifier = Modifier.size(42.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Official Net Marksheet",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Text(
                                        text = "RGPV-VERIFIED • result.rgpv.ac.in/result/BErslt.aspx",
                                        fontSize = 8.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF757575)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Sd/-",
                                    fontSize = 10.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = Color(0xFF616161)
                                )
                                Text(
                                    text = "Controller of Examinations",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF212121)
                                )
                                Text(
                                    text = "RGPV, Bhopal (M.P.)",
                                    fontSize = 9.sp,
                                    color = Color(0xFF757575)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Statutory Disclaimer
                    Text(
                        text = "Disclaimer: Rajiv Gandhi Proudyogiki Vishwavidyalaya is not responsible for any inadvertent error that may have crept in the results being published on NET. The results published on net are for immediate information to the examinees. These cannot be treated as original mark sheets.",
                        fontSize = 8.5.sp,
                        color = Color(0xFF757575),
                        lineHeight = 12.sp,
                        textAlign = TextAlign.Justify
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { ResultShareHelper.shareResult(context, result) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B0000))
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share Official Result", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParticularsItem(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Column {
        Text(
            text = label,
            fontSize = 9.5.sp,
            color = Color(0xFF616161),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            fontSize = 11.5.sp,
            color = if (isHighlighted) Color(0xFF0D47A1) else Color(0xFF212121),
            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}
