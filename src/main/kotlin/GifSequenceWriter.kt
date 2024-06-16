import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageTypeSpecifier
import javax.imageio.ImageWriter
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataNode
import javax.imageio.stream.ImageOutputStream

class GifSequenceWriter(
    outputStream: ImageOutputStream, streamMetadata: IIOMetadata
) {
    private val gifWriter: ImageWriter = ImageIO.getImageWritersBySuffix("gif").next()

    init {
        gifWriter.output = outputStream
        gifWriter.prepareWriteSequence(streamMetadata)

    }

    @Throws(IOException::class)
    fun writeToSequence(img: BufferedImage, originalMetadata: IIOMetadata, width: Int, height: Int) {
        gifWriter.writeToSequence(IIOImage(img, null, originalMetadata), gifWriter.defaultWriteParam)
    }

    private fun deepCopyMetadata(root: IIOMetadataNode): IIOMetadataNode {
        val copy = IIOMetadataNode(root.nodeName)

        // Copy attributes
        val attributes = root.attributes
        for (i in 0 until attributes.length) {
            val attr = attributes.item(i) as org.w3c.dom.Attr
            copy.setAttribute(attr.name, attr.value)
        }

        // Recursively copy child nodes
        val children = root.childNodes
        for (i in 0 until children.length) {
            val child = children.item(i) as IIOMetadataNode
            copy.appendChild(deepCopyMetadata(child))
        }

        return copy
    }

    @Throws(IOException::class)
    fun close() {
        gifWriter.endWriteSequence()
    }

    private fun getNode(rootNode: IIOMetadataNode, nodeName: String): IIOMetadataNode {
        val nNodes = rootNode.length
        for (i in 0 until nNodes) {
            if (rootNode.item(i).nodeName.equals(nodeName, ignoreCase = true)) {
                return rootNode.item(i) as IIOMetadataNode
            }
        }
        val node = IIOMetadataNode(nodeName)
        rootNode.appendChild(node)
        return node
    }

    private fun addLoopContinuouslyExtension() {
        val metaData = gifWriter.getDefaultImageMetadata(
            ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB),
            gifWriter.defaultWriteParam
        )
        val metaFormatName = metaData.nativeMetadataFormatName
        val root = IIOMetadataNode(metaFormatName)

        val applicationExtensions = IIOMetadataNode("ApplicationExtensions")
        val applicationExtension = IIOMetadataNode("ApplicationExtension")
        applicationExtension.setAttribute("applicationID", "NETSCAPE")
        applicationExtension.setAttribute("authenticationCode", "2.0")
        applicationExtension.userObject = byteArrayOf(0x1, 0x0, 0x0)
        applicationExtensions.appendChild(applicationExtension)
        root.appendChild(applicationExtensions)

        metaData.setFromTree(metaFormatName, root)
    }

}