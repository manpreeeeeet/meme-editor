import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import common.Config
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.readBytes

object ConfigMap {

    val configMap = mutableMapOf<String, Config>()

    private val objectMapper = jacksonObjectMapper()

    init {
        val uri = URI.create(object {}.javaClass.getResource("/uwu.gif").toString().substringBeforeLast("/uwu.gif"))
        val dirPath = if (uri == null || "jar" == uri.scheme) {
            val fileSystem =
                FileSystems.newFileSystem(URI.create(uri.toString().substringBefore("!")), mapOf<String, Any>())
            fileSystem.getPath("/")
        } else {
            Paths.get(uri)
        }
        Files.walk(dirPath)
            .filter { Files.isRegularFile(it) && it.toString().endsWith(".json") }
            .forEach {
                val fileData = String(it.readBytes())
                val config: Config = objectMapper.readValue(fileData)
                configMap += config.filename.substringBeforeLast(".") to config
            }
    }
}
