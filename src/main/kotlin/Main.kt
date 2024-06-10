import io.javalin.Javalin
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

val logger = LoggerFactory.getLogger("Main")

private fun getWrappedLines(text: String, fm: FontMetrics, widthLimit: Int): List<String> {
    val lines = mutableListOf<String>()
    val words = text.split(" ")
    var currentLine = StringBuilder()

    for (word in words) {
        if (fm.stringWidth("$currentLine $word") < widthLimit) {
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


val boxes = mutableListOf<Rect>()
fun main() {
    boxes += Rect(45, 103, 171, 169)
    boxes += Rect(16, 264, 115, 306)
    boxes += Rect(193, 266, 312, 311)
    boxes += Rect(101, 371, 212, 400)
    boxes += Rect(585, 95, 687, 136)
    boxes += Rect(586, 277, 689, 298, Color.WHITE)
    val app = Javalin.create(/*config*/)
        .get("/{msg}") { ctx ->
            val image = ImageIO.read(object {}.javaClass.getResourceAsStream("just_need_to.png"))
            val msg = ctx.pathParam("msg")
            val graphics = image.graphics

            graphics.color = Color.black

            var fontSize = 18

            for (box in boxes) {
                var fits = false
                while (!fits) {
                    graphics.font = Font("Roboto Flex", Font.BOLD, fontSize)
                    val wrappedLines = getWrappedLines(msg, graphics.fontMetrics, box.x2 - box.x1)

                    // Calculate the total height of the wrapped text
                    val textBlockHeight = wrappedLines.size * graphics.fontMetrics.height
                    val boxHeight = box.y2 - box.y1
                    if (textBlockHeight <= boxHeight) {
                        fits = true;
                    } else {
                        fontSize--; // Decrease font size
                    }
                }
                val wrappedLines = getWrappedLines(msg, graphics.fontMetrics, box.x2 - box.x1)
                graphics.color = box.color
                for ((idx, line) in wrappedLines.withIndex()) {
                    graphics.drawString(
                        line,
                        box.x1,
                        box.y1 + graphics.fontMetrics.height + (graphics.fontMetrics.height * idx)
                    )
                }
            }

            val outputStream = ByteArrayOutputStream()
            ImageIO.write(image, "png", outputStream)
            ctx.res().contentType = "image/png"
            ctx.result(outputStream.toByteArray())
        }
        .start(7070)


}