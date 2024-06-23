package gif

import java.awt.Color
import java.awt.Font
import java.awt.Point
import java.awt.image.BufferedImage
import kotlin.math.pow

object GifEditor {
    private fun BufferedImage.addTextToFrame(text: String, fontSize: Int, color: Color, x: Int, y: Int) {
        val frame = this
        val graphics = frame.graphics
        graphics.font = Font("Arial", Font.BOLD, fontSize);
        graphics.color = color
        graphics.drawString(text, x, y)
        graphics.dispose()
    }

    fun List<GifFrame>.addTextToFrames(
        text: String,
        fontSize: Int,
        start: Point,
        end: Point,
        startTime: Float = 0F,
        endTime: Float = 1.0F,
        color: Color
    ) {
        val framesToModify = this.subList((this.size * startTime).toInt(), (this.size * endTime).toInt())
        val points = interpolatePoints(start, end, framesToModify.size)
        for (i in points.indices) {
            val point = points[i]
            val frame = framesToModify[i]
            val image = framesToModify[i].image
            image.addTextToFrame(text, fontSize, color, point.x - frame.leftPosition, point.y - frame.topPosition)
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