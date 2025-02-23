package com.pydych.mobile.android.wordy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val q: String,
    val a: String,
    var attempts: Int = 0,
    var incorrectAttempts: Int = 0
) {
    fun score(): Int {
        return incorrectAttempts - attempts
    }
}