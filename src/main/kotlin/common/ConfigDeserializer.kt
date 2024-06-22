package common

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.awt.Color

class ConfigDeserializer : StdDeserializer<Color>(Color::class.java) {
    override fun deserialize(parser: JsonParser, deserializationContext: DeserializationContext): Color {
        val color = when (val colorStringValue = parser.readValueAs(String::class.java)) {
            "BLACK" -> Color.BLACK
            "WHITE" -> Color.WHITE
            else -> throw IllegalArgumentException("Color $colorStringValue not supported")
        }
        return color
    }
}