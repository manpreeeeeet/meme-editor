package img

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import common.Config
import img.ImageUtils.Companion.getWrappedLines
import io.javalin.http.Context
import io.javalin.http.Handler
import java.awt.Font
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

object ImageHandler : Handler {
    private val objectMapper = jacksonObjectMapper()


    override fun handle(ctx: Context) {
        val image = ImageIO.read(object {}.javaClass.getResourceAsStream("/just_need_to.png"))
        val configString = String(object {}.javaClass.getResourceAsStream("/just_need_to.json")!!.readAllBytes())
        val config: Config = objectMapper.readValue(configString)

        val msg = ctx.pathParam("msg")
        val graphics = image.createGraphics()

        var fontSize = 18

        for (box in config.textboxes) {
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
    }

}