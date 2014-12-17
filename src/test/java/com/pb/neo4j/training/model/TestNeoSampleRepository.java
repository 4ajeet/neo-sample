package com.pb.neo4j.training.model;

import java.util.List;
import java.util.Map;

import org.junit.AfterClass;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.pb.neo4j.training.db.Neo4jDatabaseServerWrapper;

public class TestNeoSampleRepository {
	
	private static INeoSampleRepository repository;
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
	public void findAllBySkillType(){
		Map<String, Double> result = repository.findAllBySkillType("Java", "Spatial Server");
		assertEquals(18, result.size());
		assertEquals(5.0, result.get("Anurag Mudgal"), 0.0);
		assertEquals(10.0, result.get("Rohan Punia"), 0.0);
	}
	
	
	@Test
	public void findAllLivingRegionsByTeam(){
		List<String> result = repository.findAllLivingRegionsByTeam("Spatial Server");
		assertEquals(3, result.size());
		assertTrue(result.contains("Ghaziabad"));
		assertTrue(result.contains("NOIDA"));
		assertTrue(result.contains("Delhi"));
	}
	

	@Test
	public void recommandAreaByTeam(){
		String result = repository.recommandAreaByTeam("Spatial Server");
		assertEquals("Ghaziabad", result);
	}

}
