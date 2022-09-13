package com.pdgc.general.util;

import java.io.IOException;

@FunctionalInterface
public interface CheckedConsumer<T> {
	void action(T t) throws IOException;
}