package com.helmut.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.helmut.data.model.Task
import com.helmut.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val timerState by viewModel.timerManager.timerState.collectAsState()
    
    var showAddTask by remember { mutableStateOf(false) }
    var taskTitle by remember { mutableStateOf("") }
    var taskMinutes by remember { mutableStateOf("15") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Helmut") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTask = !showAddTask },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = if (showAddTask) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = "Add Task",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            AnimatedVisibility(visible = showAddTask) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = taskTitle,
                            onValueChange = { taskTitle = it },
                            label = { Text("Task") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedTextField(
                            value = taskMinutes,
                            onValueChange = { taskMinutes = it },
                            label = { Text("Minutes") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (taskTitle.isNotBlank() && taskMinutes.isNotBlank()) {
                                        viewModel.addTask(taskTitle, taskMinutes.toIntOrNull() ?: 15)
                                        taskTitle = ""
                                        taskMinutes = "15"
                                        showAddTask = false
                                    }
                                }
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                if (taskTitle.isNotBlank() && taskMinutes.isNotBlank()) {
                                    viewModel.addTask(taskTitle, taskMinutes.toIntOrNull() ?: 15)
                                    taskTitle = ""
                                    taskMinutes = "15"
                                    showAddTask = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Add Task")
                        }
                    }
                }
            }

            if (uiState.currentTask != null && timerState.isRunning) {
                FocusCard(
                    task = uiState.currentTask!!,
                    timerState = timerState,
                    onPause = { viewModel.pauseTimer() },
                    onResume = { viewModel.resumeTimer() },
                    onComplete = { viewModel.completeTask(uiState.currentTask!!) },
                    onSkip = { viewModel.skipTask() },
                    formatTime = { viewModel.timerManager.formatTime(it) }
                )
            }

            if (uiState.activeTasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tasks yet. Add one to get started!",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.activeTasks) { task ->
                        TaskItem(
                            task = task,
                            onStart = { viewModel.startTask(task) },
                            onDelete = { viewModel.deleteTask(task) },
                            onComplete = { viewModel.completeTask(task) },
                            isActive = task.id == uiState.currentTask?.id && timerState.isRunning
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FocusCard(
    task: Task,
    timerState: com.helmut.utils.TimerState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    formatTime: (Int) -> String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Focus Mode",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = task.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = formatTime(timerState.remainingSeconds),
                style = MaterialTheme.typography.displayLarge,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = if (timerState.totalSeconds > 0) {
                    timerState.remainingSeconds.toFloat() / timerState.totalSeconds.toFloat()
                } else 0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (timerState.isPaused) {
                    Button(
                        onClick = onResume,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, "Resume")
                        Spacer(Modifier.width(4.dp))
                        Text("Resume")
                    }
                } else {
                    Button(
                        onClick = onPause,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Pause, "Pause")
                        Spacer(Modifier.width(4.dp))
                        Text("Pause")
                    }
                }
                
                OutlinedButton(
                    onClick = onComplete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Check, "Complete")
                    Spacer(Modifier.width(4.dp))
                    Text("Done")
                }
                
                OutlinedButton(
                    onClick = onSkip,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.SkipNext, "Skip")
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onStart: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit,
    isActive: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${task.estimatedMinutes} min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            
            IconButton(onClick = onStart) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(onClick = onComplete) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Complete",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
