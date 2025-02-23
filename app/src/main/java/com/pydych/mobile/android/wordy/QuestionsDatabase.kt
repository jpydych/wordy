package com.pydych.mobile.android.wordy

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Question::class], version = 1)
abstract class QuestionsDatabase : RoomDatabase() {
    abstract fun questionsDao(): QuestionsDao
}
