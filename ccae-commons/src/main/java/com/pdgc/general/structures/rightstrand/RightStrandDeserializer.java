package com.pdgc.general.structures.rightstrand;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.pdgc.general.structures.rightstrand.impl.NonAggregateRightStrand;

public class RightStrandDeserializer extends JsonDeserializer<NonAggregateRightStrand> {

	@Override
	public NonAggregateRightStrand deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		// TODO Auto-generated method stub
		try {
			return (NonAggregateRightStrand) new ObjectInputStream(new ByteArrayInputStream(p.getBinaryValue())).readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
