import io.javalin.Javalin
import org.slf4j.LoggerFactory
import java.awt.Color
import java.awt.Font
import java.awt.FontMetrics
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import javax.imageio.stream.MemoryCacheImageInputStream
import javax.imageio.stream.MemoryCacheImageOutputStream

val logger = LoggerFactory.getLogger("Main")

private fun getWrappedLines(text: String, fm: FontMetrics, widthLimit: Int): List<String> {
    val lines = mutableListOf<String>()
    val words = text.split("_")
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
    val app = Javalin.create(/*config*/).get("/abc/{msg}") { ctx ->
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
                        line, box.x1, box.y1 + graphics.fontMetrics.height + (graphics.fontMetrics.height * idx)
                    )
                }
            }

            val outputStream = ByteArrayOutputStream()
            ImageIO.write(image, "png", outputStream)
            ctx.res().contentType = "image/png"
            ctx.result(outputStream.toByteArray())
        }.get("/test") { ctx ->
            val startTime = System.currentTimeMillis()


            val inputGif = object {}.javaClass.getResourceAsStream("uwu.gif")!!
            val textToAdd = "Sample Text"

            // Extract frames from GIF
            val framesAndMetadata = GifHelper.extractFrames(inputGif)

            // Determine the maximum width and height of all frames
            val maxWidth = framesAndMetadata.maxOf { it.first.width }
            val maxHeight = framesAndMetadata.maxOf { it.first.height }

            val inputGif2 = object {}.javaClass.getResourceAsStream("uwu.gif")!!
            val stream = MemoryCacheImageInputStream(inputGif2)
            val reader = ImageIO.getImageReadersBySuffix("GIF").next();
            reader.input = stream

            val offsets = GifHelper.extract(object {}.javaClass.getResourceAsStream("uwu.gif")!!)
            // Resize all frames to the maximum dimensions and add text
            val modifiedFramesAndMetadata = framesAndMetadata.mapIndexed() { idx, (frame, metadata) ->
                GifHelper.addTextToFrame(frame, "hello world", offsets[idx].first, offsets[idx].second)
                frame to metadata
            }
            // Write frames to new GIF
            val outputStream = ByteArrayOutputStream()
            val output = MemoryCacheImageOutputStream(outputStream)


            val writer = GifSequenceWriter(output, reader.streamMetadata)

            for ((frame, metadata) in modifiedFramesAndMetadata) {
                writer.writeToSequence(frame, metadata, maxWidth, maxHeight)
            }
            writer.close()
            output.close()

            ctx.res().contentType = "image/gif"
            ctx.result(outputStream.toByteArray())

            // Calculate response time
            val endTime = System.currentTimeMillis()
            val responseTime = endTime - startTime

            // Log response time
            logger.info("Response time for ${ctx.method()} ${ctx.path()} : $responseTime ms")
        }.start(8080)


}