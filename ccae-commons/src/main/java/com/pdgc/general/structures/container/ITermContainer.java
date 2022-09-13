package com.pdgc.general.structures.container;

import java.time.LocalDate;

import com.pdgc.general.structures.Term;

public interface ITermContainer {
	
	Term getTerm();

	LocalDate getStartDate();

	LocalDate getEndDate();

}
