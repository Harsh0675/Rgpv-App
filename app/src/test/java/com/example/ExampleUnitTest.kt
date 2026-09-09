package com.example

import com.example.data.model.StudentResult
import com.example.data.model.SubjectResult
import com.example.data.scraper.RgpvPortalScraper
import com.example.util.GpaCalculator
import com.example.util.ResultShareHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testVerifiedOfficialRgpvNotices() {
        val notices = RgpvPortalScraper.getVerifiedOfficialRgpvNotices()
        assertTrue(notices.isNotEmpty())
        assertTrue(notices.any { it.title.contains("Diploma") || it.title.contains("B.Tech") })
    }

    @Test
    fun testGpaCalculatorFromSubjects() {
        val subjects = listOf(
            SubjectResult("CS601", "Machine Learning", "A+", "A+", "A+", 4, 10),    // 4 * 10 = 40
            SubjectResult("CS602", "Computer Networks", "A", "A", "A", 4, 9),       // 4 * 9 = 36
            SubjectResult("CS603", "Software Engineering", "B+", "B+", "B+", 4, 8),// 4 * 8 = 32
            SubjectResult("CS604", "Compiler Design", "B", "B", "B", 4, 7)          // 4 * 7 = 28
        )
        // Total points = 40 + 36 + 32 + 28 = 136
        // Total credits = 16
        // SGPA = 136 / 16 = 8.50
        val semResult = GpaCalculator.calculateSemesterGpa(subjects)
        assertEquals(16, semResult.totalCredits)
        assertEquals(136.0, semResult.totalQualityPoints, 0.001)
        assertEquals(8.50, semResult.sgpa, 0.001)

        // Test Cumulative GPA calculation
        // Prior credits: 80, Prior CGPA: 8.00 -> 640 pts
        // Current: 16 creds, 8.50 SGPA -> 136 pts
        // Total: 96 creds, 776 pts -> CGPA = 776 / 96 = 8.0833
        val cumulative = GpaCalculator.calculateCumulativeGpa(
            previousCredits = 80,
            previousCgpa = 8.00,
            currentCredits = semResult.totalCredits,
            currentSgpa = semResult.sgpa
        )
        assertEquals(96, cumulative.totalCumulativeCredits)
        assertEquals(8.083, cumulative.cumulativeGpa, 0.01)
        assertTrue(cumulative.division.contains("First Division"))
    }

    @Test
    fun testFormattedShareSummary() {
        val sampleResult = StudentResult(
            rollNo = "0827CS211045",
            studentName = "Rahul Sharma",
            fatherName = "Mr. Suresh Sharma",
            course = "B.Tech. Grading",
            branch = "Computer Science & Engg",
            institute = "0827 - IPS Academy",
            semester = "6",
            examSession = "Dec-2024",
            sgpa = 8.42,
            cgpa = 8.25,
            resultStatus = "PASS",
            totalCredits = 24,
            subjects = listOf(
                SubjectResult("CS601", "Machine Learning", "A+", "A+", "A+", 4, 10)
            )
        )

        val formattedText = ResultShareHelper.buildFormattedTextSummary(sampleResult)
        assertTrue(formattedText.contains("RAJIV GANDHI PROUDYOGIKI VISHWAVIDYALAYA"))
        assertTrue(formattedText.contains("0827CS211045"))
        assertTrue(formattedText.contains("Rahul Sharma"))
        assertTrue(formattedText.contains("8.42"))
        assertTrue(formattedText.contains("CS601"))
        assertTrue(formattedText.contains("Machine Learning"))
    }
}
