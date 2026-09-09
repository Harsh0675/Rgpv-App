package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubjectResult(
    val subjectCode: String,
    val subjectName: String,
    val theoryGrade: String,
    val practicalGrade: String,
    val totalGrade: String,
    val credits: Int,
    val gradePoint: Int,
    val status: String = "Pass"
)

@JsonClass(generateAdapter = true)
data class StudentResult(
    val rollNo: String,
    val studentName: String,
    val fatherName: String,
    val course: String,
    val branch: String,
    val institute: String,
    val semester: String,
    val examSession: String,
    val sgpa: Double,
    val cgpa: Double,
    val resultStatus: String, // PASS, PASS WITH GRACE, ATKT, FAIL
    val totalCredits: Int,
    val subjects: List<SubjectResult>,
    val timestamp: Long = System.currentTimeMillis(),
    val portalSource: String = "https://result.rgpv.ac.in/result/BErslt.aspx"
)

data class SampleStudent(
    val rollNo: String,
    val name: String,
    val branch: String,
    val sem: String,
    val description: String
)
