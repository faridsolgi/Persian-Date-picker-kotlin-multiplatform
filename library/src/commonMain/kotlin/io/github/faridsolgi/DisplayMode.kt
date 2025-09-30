package io.github.faridsolgi

import androidx.compose.runtime.Immutable
import kotlin.jvm.JvmInline

@Immutable
@JvmInline
value class DisplayMode internal constructor(internal val value: Int) {

    companion object {
        /** Date picker mode */
        val Picker = DisplayMode(0)

        /** Date text input mode */
        val Input = DisplayMode(1)
    }

    override fun toString() =
        when (this) {
            Picker -> "Picker"
            Input -> "Input"
            else -> "Unknown"
        }
}
