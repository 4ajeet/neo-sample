package com.pb.neo4j.training.db;

import java.io.File;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.omg.CORBA.portable.ApplicationException;

import com.pb.neo4j.training.model.EntityAttributeKey;

@SuppressWarnings("deprecation")
public final class Neo4jDatabaseServerWrapper{

	private static Logger LOG = Logger.getLogger(Neo4jDatabaseServerWrapper.class);
	private static GraphDatabaseService m_databaseInstance;	
	public  final String m_dbdir;
	private final GraphDatabaseFactory m_graphDatabaseFactory;
	private final String m_configPath;	
	private Index<Node> neoSampleNodeIndex;
	private Index<Relationship> streetIndex;
    private static WrappingNeoServerBootstrapper server;
    
    
	public Neo4jDatabaseServerWrapper(String dbLocation, String configPath, GraphDatabaseFactory graphDatabaseFactory){
		this.m_dbdir = dbLocation;
		this.m_configPath = configPath;
		this.m_graphDatabaseFactory = graphDatabaseFactory;
	}
	
	public GraphDatabaseService getDatabase() throws ApplicationException {
		if (null == m_databaseInstance) {
			startDatabase();
		}
		return m_databaseInstance;
	}

	public synchronized GraphDatabaseService startDatabase() throws ApplicationException {		
		if (null == m_databaseInstance) {
			initialize();
		} 
		return m_databaseInstance;
	}

	private synchronized void initialize(){
		File file = new File(m_dbdir);		
		LOG.info(String.format("Opening database located at %s", file.getAbsolutePath()));
		
		m_databaseInstance = m_graphDatabaseFactory.newEmbeddedDatabaseBuilder( m_dbdir )
	   										.loadPropertiesFromFile( m_configPath )
	   										.newGraphDatabase();		
	   	//Wrap Neo4j embedded server as a Server to enable web console and REST calls & web admin
		server = new WrappingNeoServerBootstrapper((GraphDatabaseAPI)m_databaseInstance);
		server.start();		
		createIndices();
		LOG.info("Food ponit's index has been created: " + EntityAttributeKey.NeoSampleNodeIndex);
		
		registerShutdownHook(m_databaseInstance);		
		LOG.info(String.format("Database successfuly started at %s", file.getAbsolutePath()));		
	}

	private void createIndices(){
	   	Transaction tx = m_databaseInstance.beginTx();
		neoSampleNodeIndex = m_databaseInstance.index().forNodes(EntityAttributeKey.NeoSampleNodeIndex.toString());
		tx.success();
		tx.close();	

	}
	public void shutdownDatabase() {
		LOG.info("Shuting down database ...");
		server.stop();
		m_databaseInstance.shutdown();
		LOG.info("Database successfuly stoped.");		
	}
	
	public Index<Node> getNodeIndex() throws ApplicationException {
		if (null == neoSampleNodeIndex) {
			startDatabase();
		}
		return neoSampleNodeIndex;
	}
	
	public Index<Relationship> getRelationshipIndex() throws ApplicationException {
		if (null == streetIndex) {
			startDatabase();
		}
		return streetIndex;
	}	



	// Registers a shutdown hook for the Neo4j instance so that it
	// shuts down nicely when the VM exits (even if you "Ctrl-C" the running example before it's completed)
	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.stop();
				graphDb.shutdown();
			}
		});
	}
}
