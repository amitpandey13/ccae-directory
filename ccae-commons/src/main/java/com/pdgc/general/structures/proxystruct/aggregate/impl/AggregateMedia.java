package com.pdgc.general.structures.proxystruct.aggregate.impl;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.proxystruct.DummyMedia;
import com.pdgc.general.structures.proxystruct.aggregate.IAggregateStruct;

public class AggregateMedia extends DummyMedia implements IAggregateStruct<Media> {

	private static final long serialVersionUID = 1L;
	
	protected ImmutableSet<Media> sourceMedias;
	
	public AggregateMedia(Iterable<Media> medias) {
		super();
		this.sourceMedias = ImmutableSet.copyOf(medias);
		setCustomName(sourceMedias.toString());
	}
	
	public AggregateMedia(Media...medias) {
		super();
		this.sourceMedias = ImmutableSet.copyOf(medias);
		setCustomName(sourceMedias.toString());
	}
	
	@Override
	public Set<Media> getSourceObjects() {
		return sourceMedias;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return sourceMedias.equals(((AggregateMedia)obj).sourceMedias);
	}
	
	@Override
	public int hashCode() {
		return sourceMedias.hashCode();
	}
}
