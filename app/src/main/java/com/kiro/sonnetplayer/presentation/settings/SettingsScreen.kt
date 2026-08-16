package com.kiro.sonnetplayer.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kiro.sonnetplayer.domain.model.Settings.BufferSize

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSpeedDialog by remember { mutableStateOf(false) }
    var showBufferDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Playback Section
            SettingsSection(title = "Playback") {
                SettingsItem(
                    title = "Default Playback Speed",
                    subtitle = "${uiState.defaultPlaybackSpeed}x",
                    onClick = { showSpeedDialog = true }
                )
            }

            Divider()

            // Performance Section
            SettingsSection(title = "Performance") {
                SettingsItem(
                    title = "Buffer Size",
                    subtitle = uiState.bufferSize.name.lowercase().replaceFirstChar { it.uppercase() },
                    onClick = { showBufferDialog = true }
                )
            }

            Divider()

            // Storage Section
            SettingsSection(title = "Storage") {
                SettingsItem(
                    title = "Cache Size",
                    subtitle = formatBytes(uiState.cacheSize),
                    onClick = { showClearCacheDialog = true }
                )
            }

            Divider()

            // About Section
            SettingsSection(title = "About") {
                SettingsItem(
                    title = "Kiro Sonnet Player",
                    subtitle = "Version 1.0",
                    onClick = {}
                )
                SettingsItem(
                    title = "Open Source Licenses",
                    subtitle = "View third-party licenses",
                    onClick = {}
                )
            }
        }
    }

    // Dialogs
    if (showSpeedDialog) {
        PlaybackSpeedDialog(
            currentSpeed = uiState.defaultPlaybackSpeed,
            onSpeedSelected = { speed ->
                viewModel.onEvent(SettingsEvent.UpdatePlaybackSpeed(speed))
                showSpeedDialog = false
            },
            onDismiss = { showSpeedDialog = false }
        )
    }

    if (showBufferDialog) {
        BufferSizeDialog(
            currentBufferSize = uiState.bufferSize,
            onBufferSizeSelected = { bufferSize ->
                viewModel.onEvent(SettingsEvent.UpdateBufferSize(bufferSize))
                showBufferDialog = false
            },
            onDismiss = { showBufferDialog = false }
        )
    }

    if (showClearCacheDialog) {
        ClearCacheDialog(
            cacheSize = uiState.cacheSize,
            onConfirm = {
                viewModel.onEvent(SettingsEvent.ClearCache)
                showClearCacheDialog = false
            },
            onDismiss = { showClearCacheDialog = false }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PlaybackSpeedDialog(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    onDismiss: () -> Unit
) {
    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Default Playback Speed") },
        text = {
            Column {
                speeds.forEach { speed ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = speed == currentSpeed,
                            onClick = { onSpeedSelected(speed) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${speed}x")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun BufferSizeDialog(
    currentBufferSize: BufferSize,
    onBufferSizeSelected: (BufferSize) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buffer Size") },
        text = {
            Column {
                BufferSize.entries.forEach { bufferSize ->
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = bufferSize == currentBufferSize,
                                onClick = { onBufferSizeSelected(bufferSize) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = bufferSize.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${bufferSize.minBufferMs / 1000}s - ${bufferSize.maxBufferMs / 1000}s",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ClearCacheDialog(
    cacheSize: Long,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clear Cache") },
        text = {
            Text("This will clear ${formatBytes(cacheSize)} of cached data. This action cannot be undone.")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Clear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> String.format("%.2f KB", bytes / 1024.0)
        bytes < 1024 * 1024 * 1024 -> String.format("%.2f MB", bytes / (1024.0 * 1024))
        else -> String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024))
    }
}
