package com.pdgc.csharp;

public class BitConverter {

	public static String toString(byte[] bytes) {
		StringBuilder buffer = new StringBuilder(bytes.length * 3);
		for (int i = 0; i < bytes.length; i++) {
			if (i > 0) {
				buffer.append("-");
			}
			buffer.append(Character.toUpperCase(Character.forDigit((bytes[i] >> 4) & 0xF, 16)));
			buffer.append(Character.toUpperCase(Character.forDigit((bytes[i] & 0xF), 16)));
		}
		return buffer.toString();
	}
}
