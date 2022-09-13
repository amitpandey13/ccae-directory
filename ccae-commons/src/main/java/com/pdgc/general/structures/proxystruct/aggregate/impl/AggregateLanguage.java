package com.pdgc.general.structures.proxystruct.aggregate.impl;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.structures.Language;
import com.pdgc.general.structures.proxystruct.DummyLanguage;
import com.pdgc.general.structures.proxystruct.aggregate.IAggregateStruct;

public class AggregateLanguage extends DummyLanguage implements IAggregateStruct<Language> {

	private static final long serialVersionUID = 1L;
	
	protected ImmutableSet<Language> sourceLanguages;
	
	public AggregateLanguage(Iterable<Language> languages) {
		super();
		this.sourceLanguages = ImmutableSet.copyOf(languages);
		setCustomName(sourceLanguages.toString());
	}
	
	public AggregateLanguage(Language...languages) {
		super();
		this.sourceLanguages = ImmutableSet.copyOf(languages);
		setCustomName(sourceLanguages.toString());
	}
	
	@Override
	public Set<Language> getSourceObjects() {
		return sourceLanguages;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return sourceLanguages.equals(((AggregateLanguage)obj).sourceLanguages);
	}
	
	@Override
	public int hashCode() {
		return sourceLanguages.hashCode();
	}
}
