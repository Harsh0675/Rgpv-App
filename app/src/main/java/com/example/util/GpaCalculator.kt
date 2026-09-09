package com.example.util

import com.example.data.model.SubjectResult
import java.util.Locale

data class SubjectCreditCalculation(
    val subjectCode: String,
    val subjectName: String,
    val credits: Int,
    val grade: String,
    val gradePoint: Double,
    val qualityPoints: Double
)

data class CalculatedSemesterGpa(
    val totalCredits: Int,
    val earnedCredits: Int,
    val totalQualityPoints: Double,
    val sgpa: Double,
    val subjectCalculations: List<SubjectCreditCalculation>
)

data class CalculatedCumulativeGpa(
    val previousCredits: Int,
    val previousCgpa: Double,
    val currentCredits: Int,
    val currentSgpa: Double,
    val totalCumulativeCredits: Int,
    val cumulativeGpa: Double,
    val percentage: Double,
    val division: String
)

object GpaCalculator {

    fun gradeToPoints(grade: String): Double {
        return when (grade.trim().uppercase(Locale.US)) {
            "A+", "O", "OUTSTANDING" -> 10.0
            "A", "EXCELLENT" -> 9.0
            "B+", "VERY GOOD" -> 8.0
            "B", "GOOD" -> 7.0
            "C+", "ABOVE AVERAGE" -> 6.0
            "C", "AVERAGE" -> 5.0
            "D", "PASS" -> 4.0
            "F", "FAIL", "ABS", "AB", "ATKT" -> 0.0
            else -> {
                // Check if starts with A, B, C, D
                val upper = grade.trim().uppercase(Locale.US)
                when {
                    upper.startsWith("A+") -> 10.0
                    upper.startsWith("A") -> 9.0
                    upper.startsWith("B+") -> 8.0
                    upper.startsWith("B") -> 7.0
                    upper.startsWith("C+") -> 6.0
                    upper.startsWith("C") -> 5.0
                    upper.startsWith("D") -> 4.0
                    else -> 0.0
                }
            }
        }
    }

    /**
     * Calculates the semester GPA strictly based on the displayed list of subjects and grades.
     * SGPA = Sum(Credits_i * GradePoints_i) / Sum(Credits_i)
     */
    fun calculateSemesterGpa(subjects: List<SubjectResult>): CalculatedSemesterGpa {
        var totalCreds = 0
        var earnedCreds = 0
        var totalPoints = 0.0

        val calculations = subjects.map { sub ->
            val gp = gradeToPoints(sub.totalGrade)
            val qp = sub.credits * gp
            totalCreds += sub.credits
            if (gp >= 4.0) {
                earnedCreds += sub.credits
            }
            totalPoints += qp

            SubjectCreditCalculation(
                subjectCode = sub.subjectCode,
                subjectName = sub.subjectName,
                credits = sub.credits,
                grade = sub.totalGrade,
                gradePoint = gp,
                qualityPoints = qp
            )
        }

        val calculatedSgpa = if (totalCreds > 0) totalPoints / totalCreds else 0.0

        return CalculatedSemesterGpa(
            totalCredits = totalCreds,
            earnedCredits = earnedCreds,
            totalQualityPoints = totalPoints,
            sgpa = calculatedSgpa,
            subjectCalculations = calculations
        )
    }

    /**
     * Calculates the Cumulative GPA (CGPA) combining previous academic history
     * with the current calculated semester result.
     *
     * CGPA = ( (Previous_CGPA * Previous_Credits) + (Current_SGPA * Current_Credits) ) / (Previous_Credits + Current_Credits)
     */
    fun calculateCumulativeGpa(
        previousCredits: Int,
        previousCgpa: Double,
        currentCredits: Int,
        currentSgpa: Double
    ): CalculatedCumulativeGpa {
        val prevTotalPoints = previousCredits * previousCgpa
        val currTotalPoints = currentCredits * currentSgpa
        val totalCumulativeCredits = previousCredits + currentCredits

        val cgpa = if (totalCumulativeCredits > 0) {
            (prevTotalPoints + currTotalPoints) / totalCumulativeCredits
        } else {
            currentSgpa
        }

        // RGPV standard conversion formula: Percentage = (CGPA - 0.75) * 10
        val percentage = ((cgpa - 0.75) * 10.0).coerceIn(0.0, 100.0)

        val division = when {
            cgpa >= 8.5 -> "First Division with Honors"
            cgpa >= 6.5 -> "First Division"
            cgpa >= 5.0 -> "Second Division"
            cgpa >= 4.0 -> "Pass Division"
            else -> "Fail / ATKT"
        }

        return CalculatedCumulativeGpa(
            previousCredits = previousCredits,
            previousCgpa = previousCgpa,
            currentCredits = currentCredits,
            currentSgpa = cgpa.coerceAtLeast(0.0),
            totalCumulativeCredits = totalCumulativeCredits,
            cumulativeGpa = cgpa,
            percentage = percentage,
            division = division
        )
    }
}
