package com.pb.neo4j.training.model;

import org.neo4j.graphdb.RelationshipType;

public enum LinkTypes implements RelationshipType{
	
	MEMBER_OF,
	HAS_SKILL,
	NEAREST,
	LIVING;
}
