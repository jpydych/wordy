package com.pydych.mobile.android.wordy.practice

import com.pydych.mobile.android.wordy.utils.Sampler

class QuestionsRepository(private val questionsDao: QuestionsDao) {
    private val questions = questionsDao.getAllQuestions().toMutableList()

    fun addWord(q: String, a: String) {
        val question = Question(q = q, a = a)

        questionsDao.insertQuestion(question)
        questions.add(question)
    }

    fun getWordForPractice(): Question? {
        return Sampler(questions.map {
            Pair(it, it.score().toDouble())
        }).sample()
    }

    fun getAllQuestions(): List<Question> {
        return questions.toList()
    }

    fun updateWordStats(question: Question, wasCorrect: Boolean) {
        question.attempts++
        if (!wasCorrect) question.incorrectAttempts++

        questionsDao.updateQuestion(question)
    }

    fun removeQuestion(question: Question) {
        questionsDao.removeQuestion(question)
        questions.remove(question)
    }

    fun updateQuestion(question: Question) {
        questionsDao.updateQuestion(question)
        val index = questions.indexOfFirst { it.id == question.id }
        if (index != -1) {
            questions[index] = question
        }
    }
}