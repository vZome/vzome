package com.vzome.core.model;

public interface Connector extends Manifestation, Comparable<Connector>
{
	int compareTo( Connector other );
}
