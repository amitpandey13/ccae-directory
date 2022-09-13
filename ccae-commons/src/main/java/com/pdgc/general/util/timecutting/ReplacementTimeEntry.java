package com.pdgc.general.util.timecutting;

import java.util.Objects;

public class ReplacementTimeEntry<E> {

	private E newEntry;
	private E oldEntry;
	private boolean usesNewInfo;
	
	public ReplacementTimeEntry(
		E newEntry,
		E oldEntry,
		boolean usesNewInfo
	) {
		this.newEntry = newEntry;
		this.oldEntry = oldEntry;
		this.usesNewInfo = usesNewInfo;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		
		return newEntry.equals(((ReplacementTimeEntry<?>)obj).newEntry)
			&& Objects.equals(oldEntry, ((ReplacementTimeEntry<?>)obj).oldEntry)
			&& usesNewInfo == ((ReplacementTimeEntry<?>)obj).usesNewInfo;
	}
	
	@Override
	public int hashCode() {
		return newEntry.hashCode()
			^ Objects.hashCode(oldEntry)
			^ Boolean.hashCode(usesNewInfo);
	}
	
	public E getNewEntry() {
		return newEntry;
	}
	
	public E getOldEntry() {
		return oldEntry;
	}
	
	public boolean getUsesNewInfo() {
		return usesNewInfo;
	}
	
}
