package com.pydych.mobile.android.wordy.practice

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Question::class], version = 1)
abstract class QuestionsDatabase : RoomDatabase() {
    abstract fun questionsDao(): QuestionsDao
}
