package com.pb.neo4j.training.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pb.neo4j.training.db.Neo4jDatabaseServerWrapper;

@Component
public class NeoSampleRepository implements INeoSampleRepository{
	private static Logger LOG = Logger.getLogger(NeoSampleRepository.class);

	private final Neo4jDatabaseServerWrapper m_dbWrapper;
	//Note: We should use single instance of ExecutionEngine to cache results
	private static ExecutionEngine m_engine;

	@Autowired
	public NeoSampleRepository(Neo4jDatabaseServerWrapper dbWrapper){
		m_dbWrapper = dbWrapper;
	}
	
	@Override
	public List<Employee> searchByName(String empName) {
		String query = "START n=node:" + EntityAttributeKey.NeoSampleNodeIndex + "(" + EntityAttributeKey.Name + "='" + empName + "') RETURN n";
		LOG.info("Query: " + query);
		List<Employee> result = new ArrayList<Employee>();
	    Transaction tx = null;
		try{
			tx = m_dbWrapper.getDatabase().beginTx();
			if(m_engine == null){
				m_engine = new ExecutionEngine(m_dbWrapper.getDatabase());
			}
			ExecutionResult exeResult = m_engine.execute(query);
			Iterator<Node> nodeItr = exeResult.columnAs( "n" );			
			int employeeCount = 0;
			while(nodeItr.hasNext()){
				Node node = nodeItr.next();
				//Get nodeId
				String des = (String) node.getProperty( EntityAttributeKey.Designation.toString() );
				String email = (String) node.getProperty( EntityAttributeKey.Email.toString() );
				double experience = (double) node.getProperty( EntityAttributeKey.Experience.toString() );
				
				result.add(new Employee(empName, des, experience, email));
				++employeeCount;
			}
			tx.success();
		 LOG.info("Query result - employee count: " + employeeCount);
		}catch(Exception ex){
			throw new NeoSampleRuntimeException(ex);
		}finally{
			if(tx != null){
				tx.close();
			}
		}

		return result;
	}


	@Override
	public List<Employee> findAllBySkillType(String skillType, String team) {
		String query = null;
		LOG.info("Query: " + query);
		return null;
	}

	@Override
	public List<Employee> findAllByTeam(String team, String designation) {
		String query = null;
		LOG.info("Query: " + query);
		return null;
	}

	@Override
	public List<Employee> findAllByManager(String manager, String designation) {
		String query = null;
		LOG.info("Query: " + query);
		return null;
	}


}
