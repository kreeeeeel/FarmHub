package com.project.panel.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter : TypeAdapter<LocalDateTime?>() {
    @Throws(IOException::class)
    override fun write(jsonWriter: JsonWriter, localDateTime: LocalDateTime?) {
        if (localDateTime == null) {
            jsonWriter.nullValue()
        } else {
            jsonWriter.value(localDateTime.format(formatter))
        }
    }

    @Throws(IOException::class)
    override fun read(jsonReader: JsonReader): LocalDateTime? {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull()
            return null
        } else {
            return LocalDateTime.parse(jsonReader.nextString(), formatter)
        }
    }

    companion object {
        private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }
}