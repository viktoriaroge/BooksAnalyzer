package com.viroge.booksanalyzer.ui.screens.books.scanner

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class IsbnAnalyzer(
    private val onIsbnDetected: (String) -> Unit,
    private val onScanSuccess: () -> Unit,
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Only look for EAN-13 (ISBN format) to save CPU/Battery
                    val barcode = barcodes.firstOrNull {
                        it.format == Barcode.FORMAT_EAN_13 &&
                                (it.rawValue?.startsWith("978") == true || it.rawValue?.startsWith("979") == true)
                    }

                    barcode?.rawValue?.let { isbn ->
                        onScanSuccess()
                        onIsbnDetected(isbn)
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}