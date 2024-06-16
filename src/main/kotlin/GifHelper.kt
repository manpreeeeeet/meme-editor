import java.awt.Color
import java.awt.Font
import java.awt.image.BufferedImage
import java.io.InputStream
import javax.imageio.ImageIO
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.stream.MemoryCacheImageInputStream

class GifHelper {
    companion object {
        fun extractFrames(file: InputStream): List<Pair<BufferedImage, IIOMetadata>> {
            val frames = mutableListOf<Pair<BufferedImage, IIOMetadata>>()
            val stream = MemoryCacheImageInputStream(file)

            val reader = ImageIO.getImageReadersBySuffix("GIF").next();
            reader.input = stream
            reader.streamMetadata


            val numImgs = reader.getNumImages(true)
            for (i in 0 until numImgs) {
                frames += reader.read(i) to reader.getImageMetadata(i)
            }
            return frames
        }

        fun extract(file: InputStream): List<Pair<Int, Int>> {
            val frames = mutableListOf<Pair<Int, Int>>()
            val stream = MemoryCacheImageInputStream(file)

            val reader = ImageIO.getImageReadersBySuffix("GIF").next();
            reader.input = stream
            reader.streamMetadata


            val numImgs = reader.getNumImages(true)
            for (i in 0 until numImgs) {
                val left = (reader.getImageMetadata(i)
                    .getAsTree("javax_imageio_gif_image_1.0") as IIOMetadataNode).getElementsByTagName("ImageDescriptor")
                    .item(0).attributes.getNamedItem("imageLeftPosition").nodeValue.toInt()

                val right = (reader.getImageMetadata(i)
                    .getAsTree("javax_imageio_gif_image_1.0") as IIOMetadataNode).getElementsByTagName("ImageDescriptor")
                    .item(0).attributes.getNamedItem("imageTopPosition").nodeValue.toInt()

                frames += left to right
            }
            return frames
        }

        fun addTextToFrame(frame: BufferedImage, text: String, x: Int, y: Int) {
            val g2d = frame.graphics
            g2d.font = Font("Arial", Font.BOLD, (frame.width * 0.05).toInt());
            g2d.color = Color.BLUE;
            val fontMetrics = g2d.fontMetrics

            g2d.drawString(text, 100 - x, 100 - y)
            g2d.dispose()
        }
    }
}