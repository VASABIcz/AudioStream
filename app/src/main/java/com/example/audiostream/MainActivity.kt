package com.example.audiostream

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.lifecycle.lifecycleScope
import com.example.audiostream.App.drawLogs
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Column {
                Text(text = "LAUNCH")
                drawLogs()
            }
        }

        lifecycleScope.launch {
            while (true) {
                delay(500)
            }
        }
    }
}