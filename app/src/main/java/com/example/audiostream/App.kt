package com.example.audiostream

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.audiostream.ui.theme.AudioStreamTheme

object App {
    val logs = mutableStateListOf<String>()

    @Composable
    fun drawLogs() {
        AudioStreamTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val state = rememberScrollState()
                LazyColumn(Modifier.horizontalScroll(state)) {
                    items(logs.toList()) {
                        Text(text = it, overflow = TextOverflow.Visible, softWrap = false, modifier = Modifier.padding(2.dp))
                    }
                }
            }
        }
    }
}