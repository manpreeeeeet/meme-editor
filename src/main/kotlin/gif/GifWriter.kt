package gif

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriter
import javax.imageio.metadata.IIOMetadata
import javax.imageio.stream.ImageOutputStream
import javax.imageio.stream.MemoryCacheImageOutputStream

class GifWriter(
    outputStream: ByteArrayOutputStream,
    streamMetadata: IIOMetadata,
    private val modifiedFramesAndMetadata: List<Pair<BufferedImage, IIOMetadata>>
) {
    private val gifWriter: ImageWriter = ImageIO.getImageWritersBySuffix("gif").next()
    private val gifOutputStream: ImageOutputStream = MemoryCacheImageOutputStream(outputStream)

    init {
        gifWriter.output = gifOutputStream
        gifWriter.prepareWriteSequence(streamMetadata)
    }

    fun writeFrames() {
        for ((frame, metadata) in modifiedFramesAndMetadata) {
            gifWriter.writeToSequence(IIOImage(frame, null, metadata), gifWriter.defaultWriteParam)
        }
        gifWriter.endWriteSequence()
        gifOutputStream.close()
    }

}