package com.helmut.ui.screens

import android.media.RingtoneManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.helmut.utils.SoundOption
import com.helmut.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()
    val currentSoundUri by viewModel.notificationSoundUri.collectAsState()
    
    var showSoundPicker by remember { mutableStateOf(false) }
    val availableSounds = remember { viewModel.getAvailableSounds() }
    
    val currentSoundName = remember(currentSoundUri) {
        availableSounds.find { it.uri == currentSoundUri }?.name ?: "Default"
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Settings") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = Color.White
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Timer Completion Alerts",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Notifications",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Show notification when timer completes",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Switch(
                                checked = notificationEnabled,
                                onCheckedChange = { viewModel.setNotificationEnabled(it) }
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Vibration",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "Vibrate when timer completes",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            Switch(
                                checked = vibrationEnabled,
                                onCheckedChange = { viewModel.setVibrationEnabled(it) },
                                enabled = notificationEnabled
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = notificationEnabled) { showSoundPicker = true },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Notification Sound",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = currentSoundName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.NavigateNext,
                            contentDescription = "Change sound",
                            tint = if (notificationEnabled) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }

    if (showSoundPicker) {
        SoundPickerDialog(
            availableSounds = availableSounds,
            currentSoundUri = currentSoundUri,
            onSoundSelected = { sound ->
                viewModel.setNotificationSound(sound.uri)
                showSoundPicker = false
            },
            onDismiss = { showSoundPicker = false }
        )
    }
}

@Composable
fun SoundPickerDialog(
    availableSounds: List<SoundOption>,
    currentSoundUri: android.net.Uri,
    onSoundSelected: (SoundOption) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Choose Notification Sound") },
        text = {
            Column {
                availableSounds.forEach { sound ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSoundSelected(sound) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sound.uri == currentSoundUri,
                            onClick = { onSoundSelected(sound) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = sound.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
