package contrib

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import common.Config
import gif.GifHandler
import gif.GifReader
import img.ImageHandler
import io.javalin.http.Context
import io.javalin.http.Handler
import javax.imageio.ImageIO

object ContribHandler : Handler {

    val objectMapper = jacksonObjectMapper()
    override fun handle(ctx: Context) {
        val memeFile = ctx.uploadedFile("memeFile")!!
        if (!(memeFile.contentType() == "image/png" || memeFile.contentType() == "image/gif")) {
            ctx.status(400)
        }
        val memeConfig = ctx.formParam("memeConfig")!!
        val memeText = ctx.formParam("memeText")!!
        val config: Config = objectMapper.readValue(memeConfig)
        when (memeFile.contentType()) {
            "image/png" -> {
                val image = ImageIO.read(memeFile.content())
                ImageHandler.handle(image, ctx, config, "abc")
            }

            "image/gif" -> {
                val gifReader = GifReader(memeFile.content())
                val frames = gifReader.extractFrames()
                val streamMetadata = gifReader.streamMetadata
                GifHandler.handle(streamMetadata, frames, ctx, config, memeText)
            }
        }

        ctx.status(200)
    }
}