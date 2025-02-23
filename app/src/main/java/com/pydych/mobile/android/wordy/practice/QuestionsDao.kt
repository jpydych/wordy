package com.pydych.mobile.android.wordy.practice

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface QuestionsDao {
    @Query("SELECT * FROM questions")
    fun getAllQuestions(): List<Question>

    @Insert
    fun insertQuestion(question: Question)

    @Update
    fun updateQuestion(question: Question)

    @Delete
    fun removeQuestion(question: Question)
}