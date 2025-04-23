package com.example.ocrscanner.util

import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import org.apache.commons.text.similarity.LevenshteinDistance

object OcrUtils {

    fun processImage(imageProxy: ImageProxy, onResult: (String) -> Unit) {
        val mediaImage = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val text = visionText.text.trim()
                if (text.isNotBlank()) onResult(text)
            }
            .addOnFailureListener {
                Log.e("OcrUtils", "Text recognition failed", it)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun getCorrectedText(result: String, validWords: List<String>): String {
        val cleaned = result.replace("\n", " ").lowercase().trim()
        val levenshtein = LevenshteinDistance()

        validWords.forEach { word ->
            if (cleaned.contains(word)) return word
        }

        return validWords.minByOrNull { word ->
            levenshtein.apply(cleaned, word)
        }?.takeIf { word ->
            levenshtein.apply(cleaned, word) <= 2
        } ?: result
    }
}
