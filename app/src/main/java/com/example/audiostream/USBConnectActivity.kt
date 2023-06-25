package com.example.audiostream

import android.content.Context
import android.hardware.usb.UsbAccessory
import android.hardware.usb.UsbManager
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.audiostream.App.drawLogs
import com.example.audiostream.App.logs
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.concurrent.thread


class USBConnectActivity: ComponentActivity() {
    lateinit var fileDescriptor: ParcelFileDescriptor
    lateinit var streamIn: FileInputStream
    lateinit var streamOut: FileOutputStream
    lateinit var at: AudioTrack

    override fun onStop() {
        super.onStop()
        logs.add("STOP")
    }

    override fun onDestroy() {
        super.onDestroy()
        logs.add("DESTROY")
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        logs.add("start")
        val usbManager = getSystemService(Context.USB_SERVICE) as UsbManager;

        setContent {
            drawLogs()
        }

        val accessory = intent.getParc elableExtra<UsbAccessory>(UsbManager.EXTRA_ACCESSORY)
        logs.add("connected to USB host $accessory")

        fileDescriptor = usbManager.openAccessory(accessory)
        logs.add("opened file descriptor to accessory ${fileDescriptor.fileDescriptor} ${fileDescriptor.fd} ${fileDescriptor.statSize}")

        streamIn = FileInputStream(fileDescriptor.fileDescriptor)
        streamOut = FileOutputStream(fileDescriptor.fileDescriptor)
        logs.add("created streams")

        val minBufferSize = AudioTrack.getMinBufferSize(
            48000,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        logs.add("min buf size $minBufferSize")
        at = AudioTrack.Builder().setAudioFormat(
            AudioFormat.Builder()
                .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(48000)
                .build()
        )
            .setTransferMode(AudioTrack.MODE_STREAM)
            .setBufferSizeInBytes(minBufferSize)
            .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
            .build()
        at.play()

        thread {
            while (true) {
                try {
                    val buf = streamIn.readNBytes(512)

                    at.write(buf, 0, buf.size)
                } catch (t: Throwable) {
                    t.printStackTrace()
                    logs.add(t.message ?: "read err")
                    throw RuntimeException("whops")
                }
            }
        }
    }
}