package gif

import common.Config
import common.LRUCache
import gif.GifEditor.addTextToFrames
import img.ImageUtils.Companion.copyBufferedImage
import img.ImageUtils.Companion.getWrappedLines
import io.javalin.http.Context
import java.awt.Font
import java.awt.Point
import java.io.ByteArrayOutputStream
import javax.imageio.metadata.IIOMetadata

object GifHandler {
    private val lruCache = LRUCache<String, Pair<IIOMetadata, List<GifFrame>>>(10)

    fun handle(ctx: Context, config: Config, textToAdd: String) {
        val cached = getOrInitializeCache(config.filename)
        handle(cached.first, cached.second, ctx, config, textToAdd)
    }

    fun handle(streamMetadata: IIOMetadata, frames: List<GifFrame>, ctx: Context, config: Config, textToAdd: String) {

        val graphics = frames[0].image.createGraphics()
        for (box in config.textboxes) {
            var fontSize = 40
            var fits = false
            while (!fits) {
                graphics.font = Font("Arial", Font.BOLD, fontSize)
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
            for ((idx, line) in wrappedLines.withIndex()) {
                frames.addTextToFrames(
                    line,
                    fontSize,
                    Point(box.x1, box.y1 + graphics.fontMetrics.height + (graphics.fontMetrics.height * idx)),
                    Point(box.x1, box.y1 + graphics.fontMetrics.height + (graphics.fontMetrics.height * idx)),
                    box.start,
                    box.end,
                    box.color
                )
            }
        }


        val outputStream = ByteArrayOutputStream()
        GifWriter(outputStream, streamMetadata, frames.map { it.image to it.metadata }).writeFrames()

        ctx.res().contentType = "image/gif"
        ctx.result(outputStream.toByteArray())
    }

    private fun getOrInitializeCache(filename: String): Pair<IIOMetadata, List<GifFrame>> {
        var cached = lruCache.get(filename)
        var streamMetadata = cached?.first
        var frames = cached?.second?.map {
            GifFrame(
                it.image.copyBufferedImage(), it.metadata, it.leftPosition, it.topPosition, it.delay
            )
        }
        if (cached == null) {
            val inputGifStream = object {}.javaClass.getResourceAsStream("/${filename}")!!
            val gifReader = GifReader(inputGifStream)
            val framesRead = gifReader.extractFrames()
            lruCache.put(filename, gifReader.streamMetadata to framesRead)
            streamMetadata = gifReader.streamMetadata
            frames = framesRead.map {
                GifFrame(
                    it.image.copyBufferedImage(), it.metadata, it.leftPosition, it.topPosition, it.delay
                )
            }
        }
        return streamMetadata!! to frames!!
    }
}