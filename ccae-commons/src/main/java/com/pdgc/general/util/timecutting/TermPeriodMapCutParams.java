package com.pdgc.general.util.timecutting;

import java.util.function.Function;

public class TermPeriodMapCutParams<E> {

	Function<E, E> valueDeepCopy;
	Function<E, E> valueUpdater;
	
	public TermPeriodMapCutParams(
		Function<E, E> valueDeepCopy,
		Function<E, E> valueUpdater
	) {
		this.valueDeepCopy = valueDeepCopy;
		this.valueUpdater = valueUpdater;
	}
}
