package com.pdgc.general.util.dateserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class OffsetDateTimeSerializer extends StdSerializer<OffsetDateTime> {
	private static DateTimeFormatter MESSAGE_LOCAL_DATE_TIME_FORMAT = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
			.toFormatter();
	
	public OffsetDateTimeSerializer() {
        super(OffsetDateTime.class);
    }

	@Override
	public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeString(value.format(MESSAGE_LOCAL_DATE_TIME_FORMAT));
	}

}
