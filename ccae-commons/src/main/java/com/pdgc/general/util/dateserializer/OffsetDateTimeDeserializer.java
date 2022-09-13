package com.pdgc.general.util.dateserializer;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class OffsetDateTimeDeserializer extends StdDeserializer<OffsetDateTime> {
	private static DateTimeFormatter MESSAGE_LOCAL_DATE_TIME_FORMAT = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
			.toFormatter();
	
    protected OffsetDateTimeDeserializer() {
        super(OffsetDateTime.class);
    }

	@Override
	public  OffsetDateTime deserialize(JsonParser parser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return  OffsetDateTime.parse(parser.getValueAsString(), MESSAGE_LOCAL_DATE_TIME_FORMAT);
	}

}
