package com.example.ticketmanagement

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QRScannerScreen(onScanClick = { startQRScanner() })
        }
    }



    private fun startQRScanner() {
        val scanner = GmsBarcodeScanning.getClient(this)

        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val rawValue = barcode.rawValue
                Log.d("QR_SCANNER_OUTPUT", "Scanned Code: $rawValue")
            }
            .addOnFailureListener { e ->
                Log.e("QR_SCANNER_ERROR", "Scanning failed: ${e.message}")
            }
    }
}

@Composable
fun QRScannerScreen(onScanClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onScanClick) {
            Text(text = "Scan QR Code")
        }
    }
}