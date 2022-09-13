package com.pdgc.general.structures.rightsource;

public class TestRightSource extends RightSource {

	private static final long serialVersionUID = 1L;

	public TestRightSource(RightSourceType sourceType, long sourceId) {
		super(sourceType, sourceId);
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}
}
