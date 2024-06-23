package img

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import common.ConfigDeserializer
import common.ConfigSerializer
import java.awt.Color

data class Rect(
    val x1: Int,
    val y1: Int,
    val x2: Int,
    val y2: Int,
    @JsonSerialize(using = ConfigSerializer::class)
    @JsonDeserialize(using = ConfigDeserializer::class)
    val color: Color = Color.BLACK,
    val start: Float = 1.0F,
    val end: Float = 1.0F
)
