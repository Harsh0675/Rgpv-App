package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.data.model.SubjectResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "saved_results")
@TypeConverters(ResultTypeConverters::class)
data class SavedResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
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
    val resultStatus: String,
    val totalCredits: Int,
    val subjects: List<SubjectResult>,
    val timestamp: Long = System.currentTimeMillis()
)

class ResultTypeConverters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val listType = Types.newParameterizedType(List::class.java, SubjectResult::class.java)
    private val adapter = moshi.adapter<List<SubjectResult>>(listType)

    @TypeConverter
    fun fromSubjectList(list: List<SubjectResult>?): String {
        return if (list == null) "[]" else adapter.toJson(list)
    }

    @TypeConverter
    fun toSubjectList(json: String?): List<SubjectResult> {
        if (json.isNullOrEmpty()) return emptyList()
        return try {
            adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
