package com.pb.neo4j.training.db;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.helpers.collection.MapUtil;
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import com.pb.neo4j.training.model.EntityAttributeKey;

public class DataUploader {
	private static Logger LOG = Logger.getLogger(DataUploader.class);
	public String DB_PATH;
	private static Logger log = Logger.getLogger(DataUploader.class);	
	private BatchInserter inserter;	
	// Index provider
	private BatchInserterIndexProvider indexProvider;
	//Node index
	private BatchInserterIndex employeeIndex;
	private static long nodeCount = 0;
	private static long linkCount = 0;

	Map<String, Long> nodes = new HashMap<String, Long>();

	
	public void upload(String dbDir, String nodesFile, String relationshipsFile) throws IOException {
		DB_PATH = dbDir;

		startDataUploader();
		
		long startTime = System.currentTimeMillis();
		try{
			//Read sample data and insert in to Neo4j db
			insert(new DummyDataParser(nodesFile));
			insert(new DummyDataParser(relationshipsFile));
		} finally{
			shutDataUploader();
		}
		long endTime = System.currentTimeMillis();

		LOG.info("=======================Print summary===================================");
		LOG.info("Total nodes created: " +  nodeCount);
		LOG.info("Total links created: " + linkCount);
		LOG.info("Time taken by upload process: " + Utils.getDurationBreakdown(endTime - startTime) );
		LOG.info("=======================================================================");		
	}
	
	private void insert(DummyDataParser parser) throws IOException{
		while(parser.hasNext()){
			Map<String, Object> properties = parser.next();
			if(properties == null){
				continue;
			}
			String label = (String) properties.get("Label");
			//Labeled will be used as node labels and it is not required as property so we are removing it.
			properties.remove("Label");
			insert(properties, label);
		}

	}
	
	private void insert(Map<String, Object> properties, String labelName) throws IOException {
		if (properties.containsKey("RelationshipType")) {
			createAndIndexRelationship(properties);
		} else {			
			createAndIndexNode(properties, labelName);
		}
	}


	private void createAndIndexNode(final Map<String, Object> properties, String labelName) throws IOException{		
		try {	
			//Check for duplicate nodes
			if(nodes.containsKey(properties.get("Name"))){
				return;
			}
			Label label = DynamicLabel.label(labelName);
			//Insert in to db
			long nativeNodeId  = inserter.createNode( properties , label);
			//Add node to index,Need to get a small id (may be by using hash)
//			if(!labelName.equals(EntityAttributeKey.Team.toString()) &&  !labelName.equals(EntityAttributeKey.Skill.toString() )){
//			}
			String name =  (String) properties.get("Name");
			Map<String, Object> indexProperties = new HashMap<String, Object>(1);
			indexProperties.put("Name", name);
			employeeIndex.add(nativeNodeId, indexProperties);
			LOG.info("Node with name: " + name + " has been indexed as Employee");

			nodes.put((String) properties.get("Name"), nativeNodeId);
			//To print statistics
			nodeCount++;
			
			LOG.info("Node created: " + nativeNodeId);
		} catch (Exception e) {
			log.error(properties);
			e.printStackTrace();
			log.error(e.toString());
		}
	}

	private void createAndIndexRelationship(final Map<String, Object> properties) throws IOException {
		String start = null;
		String end = null;
		try {
			// Get domain IDs (not native Neo4j IDs) from the domain link read from flat file
			start = (String) properties.get("Source");
			end = (String) properties.get("Destination");

			// Get node stored under domain ID property -- from index.
			long nodeIdA = nodes.get(start); 
			long nodeIdB = nodes.get(end);
			RelationshipType relType = (RelationshipType) properties.get("RelationshipType");
			//These properties required only to identify source , destination and relation between them,
			properties.remove("RelationshipType"); 
			properties.remove("Source");
			properties.remove("Destination");
			if(nodeIdA != nodeIdB){
				inserter.createRelationship( nodeIdA, nodeIdB, relType, properties);
			}  else{
				return;
			}
			LOG.info("Relationship created between '" + start + "' and '" + end + "'");
			linkCount++;
		} catch (Exception e) {
			log.error("Start: " + start + ", Destination: " + end, e);
			e.printStackTrace();
			log.error(e.toString());
		}
	}

	private void shutDataUploader() {
		indexProvider.shutdown();
		inserter.shutdown();
	}


	private void startDataUploader() {
		if (null == inserter){
			inserter = BatchInserters.inserter(DB_PATH);
		}
		if (null == indexProvider){
			indexProvider = new LuceneBatchInserterIndexProvider(inserter);
		}
		if (null == employeeIndex){
			employeeIndex = indexProvider.nodeIndex("NeoSampleNodeIndex", MapUtil.stringMap( "type", "exact" ) );
		}
		//Set cache capacity for better performance, it will depends avilable memory
		employeeIndex.setCacheCapacity("NeoSampleNodeIndex", 134000);		
	}

	public void flushToIndex() {
		employeeIndex.flush();
	}

	public void shutdownDbInserter() throws IOException {
		indexProvider.shutdown();
		inserter.shutdown();
	}

	public static void main(String[] args) throws IOException {
		DataUploader uploader = new DataUploader();
		uploader.upload("E:\\Neo4j\\NeoSampleDB", "src\\main\\resources\\Nodes.txt", "src\\main\\resources\\Relationships.txt");
	}
}
