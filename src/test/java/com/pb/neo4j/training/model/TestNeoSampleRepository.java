package com.pb.neo4j.training.model;

import java.util.List;

import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.pb.neo4j.training.db.Neo4jDatabaseServerWrapper;

public class TestNeoSampleRepository {
	
	private static NeoSampleRepository repository;
	private static Neo4jDatabaseServerWrapper dbWrapper;
	private static final String NEO4J_FOLDER = "src/test/NeoSampleDB";
	//Configure DB as per your requirement
	private static final String configPath=  "src/main/resources/neo4j.properties";
	
	@BeforeClass
	public static void setup(){
		dbWrapper = new Neo4jDatabaseServerWrapper(NEO4J_FOLDER, configPath, new GraphDatabaseFactory());
		repository = new NeoSampleRepository(dbWrapper);
	}
	
	@AfterClass
	public static void cleanup(){
		dbWrapper.shutdownDatabase();
	}
	
	@Test
	public void searchByName(){
		List<Employee> result =  repository.searchByName("Ajeet Singh");
		assertEquals(1, result.size());
		assertEquals("Ajeet Singh", result.get(0).getName());
		assertEquals("Ajeet.Singh@pb.com", result.get(0).getEmail());
		assertEquals("Sr Advisory Software Engineer", result.get(0).getDesignation());
	}

}
