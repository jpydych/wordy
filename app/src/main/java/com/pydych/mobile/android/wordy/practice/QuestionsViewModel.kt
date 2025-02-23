package com.pydych.mobile.android.wordy.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuestionsViewModel(private val repository: QuestionsRepository) : ViewModel() {
    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion = _currentQuestion.asStateFlow()

    private val _isAnswerVisible = MutableStateFlow(false)
    val isAnswerVisible = _isAnswerVisible.asStateFlow()

    fun addQuestion(question: String, answer: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addWord(question, answer)
        }
    }

    fun loadNextQuestion() {
        viewModelScope.launch(Dispatchers.IO) {
            _currentQuestion.value = repository.getWordForPractice()
            _isAnswerVisible.value = false
        }
    }

    fun showAnswer() {
        _isAnswerVisible.value = true
    }

    fun submitAnswer(wasCorrect: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            currentQuestion.value?.let { question ->
                repository.updateWordStats(question, wasCorrect)
                loadNextQuestion()
            }
        }
    }

    fun skipQuestion() {
        loadNextQuestion()
    }
}

class QuestionsViewModelFactory(private val repository: QuestionsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuestionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuestionsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
