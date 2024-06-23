package img

import common.Config
import img.ImageUtils.Companion.getWrappedLines
import io.javalin.http.Context
import java.awt.Font
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object ImageHandler {

    fun handle(ctx: Context, config: Config, textToAdd: String) {

        val image = ImageIO.read(object {}.javaClass.getResourceAsStream("/${config.filename}"))
        val graphics = image.createGraphics()

        for (box in config.textboxes) {
            var fontSize = 18
            var fits = false
            while (!fits) {
                graphics.font = Font("Roboto Flex", Font.BOLD, fontSize)
                val wrappedLines = getWrappedLines(textToAdd, graphics.fontMetrics, box.x2 - box.x1)

                // Calculate the total height of the wrapped text
                val textBlockHeight = wrappedLines.size * graphics.fontMetrics.height
                val boxHeight = box.y2 - box.y1
                if (textBlockHeight <= boxHeight) {
                    fits = true;
                } else {
                    fontSize--; // Decrease font size
                }
            }
            val wrappedLines = getWrappedLines(textToAdd, graphics.fontMetrics, box.x2 - box.x1)
            graphics.color = box.color
            for ((idx, line) in wrappedLines.withIndex()) {
                graphics.drawString(
                    line, box.x1, box.y1 + graphics.fontMetrics.height + (graphics.fontMetrics.height * idx)
                )
            }
        }

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "png", outputStream)
        ctx.res().contentType = "image/png"
        ctx.result(outputStream.toByteArray())
    }

}