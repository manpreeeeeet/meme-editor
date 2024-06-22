package img

import java.awt.FontMetrics

class ImageUtils {
    companion object {
        fun getWrappedLines(text: String, fontMetrics: FontMetrics, widthLimit: Int): List<String> {
            val lines = mutableListOf<String>()
            val words = text.split("_")
            var currentLine = StringBuilder()

            for (word in words) {
                if (fontMetrics.stringWidth("$currentLine $word") < widthLimit) {
                    if (currentLine.isNotEmpty()) {
                        currentLine.append(" ")
                    }
                    currentLine.append(word)
                } else {
                    lines.add(currentLine.toString())
                    currentLine = StringBuilder(word)
                }
            }
            // Add the last line
            if (currentLine.isNotEmpty()) {
                lines.add(currentLine.toString())
            }

            return lines
        }
    }
}