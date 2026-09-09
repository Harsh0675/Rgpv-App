package com.example.data.model

data class RgpvNotification(
    val id: String,
    val title: String,
    val date: String,
    val category: NotificationCategory,
    val link: String,
    val isNew: Boolean = true,
    val summary: String = ""
)

enum class NotificationCategory(val label: String) {
    EXAMINATION("Examinations & Results"),
    ACADEMIC("Academics & Timetable"),
    ADMISSION("Admissions & Counselling"),
    GENERAL("General Circulars")
}

data class PortalConnectionStatus(
    val url: String = "https://www.rgpv.ac.in/",
    val isOnline: Boolean = false,
    val httpCode: Int = 0,
    val responseTimeMs: Long = 0,
    val lastFetchedAt: String = "",
    val itemsCount: Int = 0
)
