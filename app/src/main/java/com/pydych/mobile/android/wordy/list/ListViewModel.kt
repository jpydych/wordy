package com.pydych.mobile.android.wordy.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pydych.mobile.android.wordy.practice.Question
import com.pydych.mobile.android.wordy.practice.QuestionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListViewModel(private val repository: QuestionsRepository) : ViewModel() {
    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions = _questions.asStateFlow()

    init {
        loadQuestions()
    }

    private fun loadQuestions() {
        viewModelScope.launch(Dispatchers.IO) {
            _questions.value = repository.getAllQuestions()
        }
    }

    fun importQuestions(content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            content.lines().chunked(2).forEach { chunk ->
                if (chunk.size == 2) {
                    repository.addWord(chunk[0], chunk[1])
                }
            }
            loadQuestions()
        }
    }

    fun exportQuestions(): String {
        return questions.value.joinToString("\n") { "${it.q}\n${it.a}" }
    }

    fun removeQuestion(question: Question) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeQuestion(question)
            loadQuestions()
        }
    }

    fun updateQuestion(question: Question, newQuestion: String, newAnswer: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateQuestion(question.copy(q = newQuestion, a = newAnswer))
            loadQuestions()
        }
    }
}

class ListViewModelFactory(private val repository: QuestionsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}