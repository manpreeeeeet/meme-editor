package gif

import gif.GifEditor.addTextToFrames
import io.javalin.http.Context
import io.javalin.http.Handler
import java.awt.Point
import java.io.ByteArrayOutputStream

object GifHandler : Handler {
    override fun handle(ctx: Context) {
        val textToAdd = ctx.pathParam("text")
        val inputGifName = ctx.pathParam("gif_name") + ".gif"
        val offsetX = ctx.req().getParameter("x")?.toInt() ?: 0
        val offsetY = ctx.req().getParameter("y")?.toInt() ?: 0


        val inputGifStream = object {}.javaClass.getResourceAsStream("/$inputGifName")!!

        val gifReader = GifReader(inputGifStream)
        val frames = gifReader.extractFrames()
        frames.addTextToFrames(textToAdd, Point(180, 220), Point(180, 220))


        val outputStream = ByteArrayOutputStream()
        val writer = GifWriter(outputStream, gifReader.streamMetadata, frames.map { it.image to it.metadata })
        writer.writeFrames()

        ctx.res().contentType = "image/gif"
        ctx.result(outputStream.toByteArray())
    }
}