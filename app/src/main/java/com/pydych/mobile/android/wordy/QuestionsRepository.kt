package com.pydych.mobile.android.wordy

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

    fun updateWordStats(question: Question, wasCorrect: Boolean) {
        question.attempts++
        if (!wasCorrect) question.incorrectAttempts++

        questionsDao.updateQuestion(question)
    }
}