package com.pdgc.general.structures.rightstrand;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.pdgc.general.structures.rightstrand.impl.NonAggregateRightStrand;

public class RightStrandSerializer extends JsonSerializer<NonAggregateRightStrand>{

	@Override
	public void serialize(NonAggregateRightStrand value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {
		// TODO Auto-generated method stub
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(value);
        oos.close();
        System.out.println("Byte size = " + baos.size());
        byte[] b = baos.toByteArray();
        gen.writeBinary(b);
	}

}
