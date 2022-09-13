package com.pdgc.general.util.dateserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class LocalDateDeserializer extends  StdDeserializer<LocalDate> {
	private static DateTimeFormatter MESSAGE_LOCAL_DATE_FORMAT = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd")
			.toFormatter();
	
    protected LocalDateDeserializer() {
        super(LocalDate.class);
    }

	@Override
	public LocalDate deserialize(JsonParser parser, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		return LocalDate.parse(parser.getValueAsString(), MESSAGE_LOCAL_DATE_FORMAT);
	}

}
