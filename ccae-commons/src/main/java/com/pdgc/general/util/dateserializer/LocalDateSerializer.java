package com.pdgc.general.util.dateserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class LocalDateSerializer extends StdSerializer<LocalDate> {
	private static DateTimeFormatter MESSAGE_LOCAL_DATE_FORMAT = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd")
			.toFormatter();
   
	public LocalDateSerializer() {
        super(LocalDate.class);
    }

	@Override
	public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString(value.format(MESSAGE_LOCAL_DATE_FORMAT));
	}

}
