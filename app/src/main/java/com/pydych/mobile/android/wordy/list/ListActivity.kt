package com.pydych.mobile.android.wordy.list

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.pydych.mobile.android.wordy.MyApplication
import com.pydych.mobile.android.wordy.R
import com.pydych.mobile.android.wordy.practice.Question
import com.pydych.mobile.android.wordy.practice.QuestionsRepository
import com.pydych.mobile.android.wordy.ui.theme.WordyTheme

class ListActivity : ComponentActivity() {
    private val viewModel: ListViewModel by viewModels {
        ListViewModelFactory(
            QuestionsRepository((application as MyApplication).database.questionsDao())
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val questions by viewModel.questions.collectAsState()
                    val context = LocalContext.current
                    val activity = context as ComponentActivity

                    var editingQuestion by remember { mutableStateOf<Question?>(null) }

                    val importLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.OpenDocument()
                    ) { uri ->
                        uri?.let {
                            val content = activity.contentResolver.openInputStream(uri)?.bufferedReader()?.readText() ?: ""
                            viewModel.importQuestions(content)
                        }
                    }

                    val exportLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.CreateDocument("text/plain")
                    ) { uri ->
                        uri?.let {
                            val content = viewModel.exportQuestions()
                            activity.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { writer ->
                                writer.write(content)
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(innerPadding)) {
                        TopAppBar(
                            title = { Text(stringResource(R.string.questions_list)) },
                            navigationIcon = {
                                IconButton(onClick = { activity.finish() }) {
                                    Icon(
                                        Icons.AutoMirrored.Default.ArrowBack,
                                        contentDescription = stringResource(R.string.go_back)
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { importLauncher.launch(arrayOf("text/plain")) }) {
                                    Icon(
                                        Icons.Default.ShoppingCart,
                                        contentDescription = stringResource(R.string.import_words)
                                    )
                                }
                                IconButton(onClick = { exportLauncher.launch("wordy_export.txt") }) {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = stringResource(R.string.export_words)
                                    )
                                }
                            }
                        )

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(questions) { question ->
                                ListQuestionItem(
                                    question = question,
                                    onEdit = { editingQuestion = question },
                                    onRemove = { viewModel.removeQuestion(question) }
                                )
                            }
                        }
                    }

                    // Edit Dialog
                    editingQuestion?.let { question ->
                        EditQuestionDialog(
                            question = question,
                            onDismiss = { editingQuestion = null },
                            onSave = { newQuestion, newAnswer ->
                                viewModel.updateQuestion(question, newQuestion, newAnswer)
                                editingQuestion = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditQuestionDialog(
    question: Question,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var editedQuestion by remember { mutableStateOf(question.q) }
    var editedAnswer by remember { mutableStateOf(question.a) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_question)) },
        text = {
            Column {
                OutlinedTextField(
                    value = editedQuestion,
                    onValueChange = { editedQuestion = it },
                    label = { Text(stringResource(R.string.question)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editedAnswer,
                    onValueChange = { editedAnswer = it },
                    label = { Text(stringResource(R.string.answer)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (editedQuestion.isNotBlank() && editedAnswer.isNotBlank()) {
                        onSave(editedQuestion, editedAnswer)
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
