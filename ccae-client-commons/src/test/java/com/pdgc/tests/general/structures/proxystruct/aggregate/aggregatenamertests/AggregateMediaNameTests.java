package com.pdgc.tests.general.structures.proxystruct.aggregate.aggregatenamertests;

import com.pdgc.general.lookup.Constants;
import com.pdgc.general.structures.Media;
import com.pdgc.general.structures.hierarchy.impl.HierarchyMapEditor;
import com.pdgc.general.util.TestsHelper;

public class AggregateMediaNameTests {
	protected static final HierarchyMapEditor<Media> mediaHierarchy;
	
	protected static final Media basc = TestsHelper.createMedia("BASC");
	protected static final Media ppv = TestsHelper.createMedia("PPV");
	protected static final Media ptv = TestsHelper.createMedia("PTV");
	protected static final Media ptvc = TestsHelper.createMedia("PTV: Cab");
	protected static final Media ptvi = TestsHelper.createMedia("PTV: Int");
	protected static final Media ptvm = TestsHelper.createMedia("PTV: Mob");
	protected static final Media svod = TestsHelper.createMedia("SVOD");
	protected static final Media svodc = TestsHelper.createMedia("SVOD: Cab");
	protected static final Media svodi = TestsHelper.createMedia("SVOD: Int");

	static {
		mediaHierarchy = new HierarchyMapEditor<Media>();
		{
			mediaHierarchy.addElement(Constants.ALL_MEDIA);
			mediaHierarchy.addChild(Constants.ALL_MEDIA, basc);
			mediaHierarchy.addChild(Constants.ALL_MEDIA, ppv);
			mediaHierarchy.addChild(Constants.ALL_MEDIA, ptv);
			mediaHierarchy.addChild(Constants.ALL_MEDIA, svod);
			mediaHierarchy.addChild(ptv, ptvc);
			mediaHierarchy.addChild(ptv, ptvi);
			mediaHierarchy.addChild(ptv, ptvm);
			mediaHierarchy.addChild(svod, svodc);
			mediaHierarchy.addChild(svod, svodi);

		}
	}
}
