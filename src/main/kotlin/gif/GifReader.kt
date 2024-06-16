package gif

import java.io.InputStream
import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.stream.MemoryCacheImageInputStream

class GifReader(inputStream: InputStream) {

    private val imageInputStream = MemoryCacheImageInputStream(inputStream)
    private val imageReader: ImageReader = ImageIO.getImageReadersBySuffix("GIF").next()
    val streamMetadata: IIOMetadata

    init {;
        imageReader.input = imageInputStream
        streamMetadata = imageReader.streamMetadata
    }

    fun extractFrames(): List<GifFrame> {
        val frames = mutableListOf<GifFrame>()
        val numImages = imageReader.getNumImages(true)
        for (i in 0 until numImages) {
            val image = imageReader.read(i)
            val imageMetadata = imageReader.getImageMetadata(i)
            val left = (imageMetadata
                .getAsTree(GIF_FORMAT_NAME) as IIOMetadataNode).getImageAttribute("imageLeftPosition")

            val top = (imageMetadata
                .getAsTree(GIF_FORMAT_NAME) as IIOMetadataNode).getImageAttribute("imageTopPosition")

            val delay =
                (imageMetadata.getAsTree(GIF_FORMAT_NAME) as IIOMetadataNode).getElementsByTagName("GraphicControlExtension")
                    .item(0).attributes.getNamedItem("delayTime").nodeValue.toInt()
            frames += GifFrame(image, imageMetadata, left, top, delay)
        }
        return frames
    }

    private fun IIOMetadataNode.getImageAttribute(attributeName: String): Int {
        return this.getElementsByTagName("ImageDescriptor")
            .item(0).attributes.getNamedItem(attributeName).nodeValue.toInt()
    }

    companion object {
        private val GIF_FORMAT_NAME = "javax_imageio_gif_image_1.0"
    }


}