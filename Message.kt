package com.example.reminderapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    // Optional per-message background image (content:// uri as String). Null = use global default.
    val backgroundUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Auto-detects whether this message is primarily Farsi/Arabic script so the UI
     * can apply the right font + text direction automatically.
     */
    fun isFarsi(): Boolean {
        // Unicode ranges for Arabic script (covers Farsi/Persian characters too)
        val arabicRange = '\u0600'..'\u06FF'
        val extraRange = '\u0750'..'\u077F'
        val farsiCharCount = text.count { it in arabicRange || it in extraRange }
        return farsiCharCount > text.length / 3 // majority-ish of the text is Farsi script
    }
}
