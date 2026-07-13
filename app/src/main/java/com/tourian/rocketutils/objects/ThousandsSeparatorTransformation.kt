package com.tourian.rocketutils.objects

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

class ThousandsSeparatorTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }

        // 1. Format the raw digits with commas
        val number = originalText.toLongOrNull() ?: 0L
        val formattedText = NumberFormat.getInstance(Locale.getDefault()).format(number)

        // 2. Map cursor offsets so the cursor doesn't jump around
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                // Find out how many commas were added before the current cursor position
                val subString = originalText.substring(0, minOf(offset, originalText.length))
                val subNumber = subString.toLongOrNull() ?: return offset
                val subFormatted = NumberFormat.getInstance(Locale.getDefault()).format(subNumber)
                return subFormatted.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                val formattedSub = formattedText.substring(0, minOf(offset, formattedText.length))
                // Just count the actual digits, ignoring the commas
                return formattedSub.count { it.isDigit() }
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}