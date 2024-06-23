package common

import ConfigMap
import gif.GifHandler
import img.ImageHandler
import io.javalin.http.Context
import io.javalin.http.Handler

object RequestHandler : Handler {
    override fun handle(ctx: Context) {
        val memeName = ctx.pathParam("memeName")
        val textToAdd = ctx.pathParam("textToAdd")
        val config = ConfigMap.configMap[memeName]
        if (config == null) {
            ctx.status(404)
            return
        }

        when (config.filename.substringAfter(".")) {
            "png" -> ImageHandler.handle(ctx, config, textToAdd)
            "gif" -> GifHandler.handle(ctx, config, textToAdd)
            else -> ctx.status(500)
        }

    }
}