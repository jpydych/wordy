package com.pydych.mobile.android.wordy

import android.app.Application
import androidx.room.Room
import com.pydych.mobile.android.wordy.practice.QuestionsDatabase

class MyApplication : Application() {
    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            QuestionsDatabase::class.java,
            "questions_database"
        ).allowMainThreadQueries().build()
    }
}