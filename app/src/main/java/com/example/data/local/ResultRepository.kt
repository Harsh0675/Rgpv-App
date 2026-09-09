package com.example.data.local

import com.example.data.model.StudentResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ResultRepository(private val dao: ResultDao) {

    val allSavedResults: Flow<List<StudentResult>> = dao.getAllSavedResults().map { list ->
        list.map { it.toStudentResult() }
    }

    fun getResultsForRollNo(rollNo: String): Flow<List<StudentResult>> =
        dao.getResultsByRollNo(rollNo).map { list ->
            list.map { it.toStudentResult() }
        }

    suspend fun saveResult(result: StudentResult): Long {
        val entity = SavedResultEntity(
            rollNo = result.rollNo,
            studentName = result.studentName,
            fatherName = result.fatherName,
            course = result.course,
            branch = result.branch,
            institute = result.institute,
            semester = result.semester,
            examSession = result.examSession,
            sgpa = result.sgpa,
            cgpa = result.cgpa,
            resultStatus = result.resultStatus,
            totalCredits = result.totalCredits,
            subjects = result.subjects,
            timestamp = System.currentTimeMillis()
        )
        return dao.insertResult(entity)
    }

    suspend fun deleteResult(id: Long) {
        dao.deleteResultById(id)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    private fun SavedResultEntity.toStudentResult() = StudentResult(
        rollNo = rollNo,
        studentName = studentName,
        fatherName = fatherName,
        course = course,
        branch = branch,
        institute = institute,
        semester = semester,
        examSession = examSession,
        sgpa = sgpa,
        cgpa = cgpa,
        resultStatus = resultStatus,
        totalCredits = totalCredits,
        subjects = subjects,
        timestamp = timestamp
    )
}
