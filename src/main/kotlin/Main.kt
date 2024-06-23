import common.RequestHandler
import io.javalin.Javalin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val logger: Logger = LoggerFactory.getLogger("Main")

fun main(args: Array<String>) {
    val port = args[0].toInt()
    logger.info("${ConfigMap.configMap}")


    Javalin.create { config ->
        config.bundledPlugins.enableDevLogging()
        config.requestLogger.http { ctx, ms ->
            val req = ctx.req()
            logger.info("Request: ${req.method} ${req.requestURL} | Response: ${ctx.res().status} | Time: ${ms}ms")
        }
    }.get("<memeName>/{textToAdd}", RequestHandler).start(port)


}