package gif

import java.awt.Color
import java.awt.Font
import java.awt.Point
import java.awt.image.BufferedImage
import kotlin.math.pow

object GifEditor {
    private fun BufferedImage.addTextToFrame(text: String, x: Int, y: Int) {
        val frame = this
        val graphics = frame.graphics
        graphics.font = Font("Arial", Font.BOLD, 40);
        graphics.color = Color.WHITE
        graphics.drawString(text, x, y)
        graphics.dispose()
    }

    fun List<GifFrame>.addTextToFrames(text: String, start: Point, end: Point) {
        val points = interpolatePoints(start, end, this.size)
        val frames = this
        for (i in points.indices) {
            val point = points[i]
            val frame = frames[i]
            val image = frames[i].image
            image.addTextToFrame(text, point.x - frame.leftPosition, point.y - frame.topPosition)
        }
    }

    private fun interpolatePoints(start: Point, end: Point, steps: Int): List<Point> {
        val points = mutableListOf<Point>()
        for (i in 0 until steps) {
            val t = (i.toDouble() / steps).pow(1.4)
            val x = start.x + t * (end.x - start.x)
            val y = start.y + t * (end.y - start.y)
            points.add(Point(x.toInt(), y.toInt()))
        }
        return points
    }

}