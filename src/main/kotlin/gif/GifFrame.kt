package gif

import java.awt.image.BufferedImage
import javax.imageio.metadata.IIOMetadata

data class GifFrame(
    val image: BufferedImage,
    val metadata: IIOMetadata,
    val leftPosition: Int,
    val topPosition: Int,
    val delay: Int
)
