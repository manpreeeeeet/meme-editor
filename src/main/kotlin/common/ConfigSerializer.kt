package common

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import java.awt.Color

class ConfigSerializer : StdSerializer<Color>(Color::class.java) {
    override fun serialize(color: Color?, jsonGenerator: JsonGenerator, serializerProvider: SerializerProvider) {
        val colorStringValue = when (color) {
            null, Color.BLACK -> "BLACK"
            Color.WHITE -> "WHITE"
            else -> throw IllegalArgumentException("Color: $color not supported")
        }
        jsonGenerator.writeStartObject()
        jsonGenerator.writeString(colorStringValue)
        jsonGenerator.writeEndObject()
    }

}