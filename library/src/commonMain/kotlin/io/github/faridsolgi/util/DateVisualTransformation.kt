package io.github.faridsolgi.util

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
internal class DateVisualTransformation() : VisualTransformation {

    private val firstDelimiterOffset = 4   // after year
    private val secondDelimiterOffset = 6  // after month
    private val maxLength = 8              // YYYYMMDD

    private val offsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            return when {
                offset <= firstDelimiterOffset -> offset
                offset <= secondDelimiterOffset -> offset + 1
                offset <= maxLength -> offset + 2
                else -> maxLength + 2 // max transformed = 10 (YYYY/MM/DD)
            }
        }

        override fun transformedToOriginal(offset: Int): Int {
            return when {
                offset <= firstDelimiterOffset -> offset
                offset <= secondDelimiterOffset -> offset - 1
                offset <= maxLength + 1 -> offset - 2
                else -> maxLength
            }
        }
    }

    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(maxLength) // only digits
        val transformed = buildString {
            trimmed.forEachIndexed { index, c ->
                append(c)
                if (index + 1 == firstDelimiterOffset || index + 1 == secondDelimiterOffset) {
                    append("/")
                }
            }
        }
        return TransformedText(AnnotatedString(transformed), offsetTranslator)
    }
}

