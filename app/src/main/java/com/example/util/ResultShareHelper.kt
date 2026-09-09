package com.example.util

import android.content.Context
import android.content.Intent
import com.example.data.model.StudentResult
import java.util.Locale

object ResultShareHelper {

    /**
     * Builds a comprehensive, beautifully structured text summary of the student's result
     * for sharing via messaging apps, email, or social platforms.
     */
    fun buildFormattedTextSummary(result: StudentResult): String {
        val division = when {
            result.cgpa >= 8.5 -> "First Division with Honors"
            result.cgpa >= 6.5 -> "First Division"
            result.cgpa >= 5.0 -> "Second Division"
            else -> "Pass"
        }

        val estPercentage = String.format(Locale.US, "%.1f", (result.cgpa * 10.0).coerceAtMost(100.0))

        val subjectsSection = buildString {
            result.subjects.forEachIndexed { index, sub ->
                val padCode = sub.subjectCode.padEnd(8)
                appendLine("  ${index + 1}. $padCode | Grade: ${sub.totalGrade.padEnd(3)} | Th: ${sub.theoryGrade}, Pr: ${sub.practicalGrade} (${sub.credits} cr)")
                appendLine("     └─ ${sub.subjectName}")
            }
        }

        return """
╔══════════════════════════════════════════════════╗
║  RAJIV GANDHI PROUDYOGIKI VISHWAVIDYALAYA (RGPV)  ║
║         OFFICIAL SEMESTER RESULT SUMMARY         ║
╚══════════════════════════════════════════════════╝

👤 STUDENT PARTICULARS:
• Student Name   : ${result.studentName}
• Enrollment No  : ${result.rollNo}
• Father's Name  : ${result.fatherName}
• Course         : ${result.course}
• Branch         : ${result.branch}
• Institute      : ${result.institute}
• Semester       : Semester ${result.semester} (${result.examSession})

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📊 ACADEMIC PERFORMANCE:
• Semester SGPA  : ${String.format(Locale.US, "%.2f", result.sgpa)} / 10.0
• Cumulative CGPA: ${String.format(Locale.US, "%.2f", result.cgpa)} / 10.0
• Approx. Marks  : ~$estPercentage%
• Total Credits  : ${result.totalCredits} Credits
• Result Status  : ${result.resultStatus} ($division)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📚 SUBJECT-WISE GRADES (${result.subjects.size} Courses):
$subjectsSection
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ Scraped & Verified via RGPV Result Scraper
🌐 Official University Portal: https://result.rgpv.ac.in/result/BErslt.aspx
        """.trimIndent()
    }

    /**
     * Launches the Android system share sheet with the formatted text summary.
     */
    fun shareResult(context: Context, result: StudentResult) {
        val summaryText = buildFormattedTextSummary(result)
        val subject = "RGPV Semester ${result.semester} Result - ${result.studentName} (${result.rollNo})"

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, summaryText)
            type = "text/plain"
        }

        val chooserIntent = Intent.createChooser(sendIntent, "Share Result Summary via...")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
}
