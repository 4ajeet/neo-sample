package com.pb.neo4j.training.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	public List<Employee> findByName(String empName) {
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

	/**
	 * MATCH (S:Skill)<-[R1:HAS_SKILL]-(N)-[R2:MEMBER_OF]->(T:Team)
WHERE S.Name ='REST' and T.Name = "Spatial Server"
return N.Name, R1.Years

MATCH (S:Skill)<-[R1:HAS_SKILL]-(N)
WHERE S.Name ='REST'
return N.Name, R1.Years
	 */

	@Override
	public Map<String, Double> findAllBySkillType(String skillName, String team) {
		String query = null;
		if(team != null && ! team.isEmpty()){
			query = "MATCH (S:Skill)<-[R1:HAS_SKILL]-(N)-[R2:MEMBER_OF]->(T:Team) WHERE S.Name ='" + skillName + "' and T.Name = '" + team + "' return N.Name AS Name, R1.Years AS Years";
		} else{
			query = "MATCH (S:Skill)<-[R1:HAS_SKILL]-(N) WHERE S.Name ='" + skillName + "' return N.Name AS Name, R1.Years AS Years";

		}
		//String query = "START n=node:" + EntityAttributeKey.NeoSampleNodeIndex + "(" + matchClause + "') RETURN n";
		LOG.info("Query: " + query);
		Map<String, Double> result = new HashMap<String, Double>();
	    Transaction tx = null;
	    int employeeCount = 0;
		try{
			tx = m_dbWrapper.getDatabase().beginTx();
			if(m_engine == null){
				m_engine = new ExecutionEngine(m_dbWrapper.getDatabase());
			}
			ExecutionResult exeResult = m_engine.execute(query);
			
			for ( Map<String, Object> row : exeResult )
			{
				String name = null;
				Double exp = 0.0;
				Iterator<Entry<String, Object>> columns = row.entrySet().iterator();
				while(columns.hasNext()){
					Entry<String, Object> column = columns.next();
					if("Name".equals(column.getKey())){
						name =  (String) column.getValue();
					}else{
						exp =(Double) column.getValue();
					}
				}
				result.put(name, exp);
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

	@Override
	public List<String> findAllLivingRegionsByTeam(String team) {
		String query = "MATCH (T:Team {Name: '" + team + "'})<-[R1:MEMBER_OF]-(N)-[R2:LIVING]->(M) RETURN M.Name AS Region, count(N) as Count ORDER BY Count DESC" ;
		//String query = "START n=node:" + EntityAttributeKey.NeoSampleNodeIndex + "(" + matchClause + "') RETURN n";
		LOG.info("Query: " + query);
		List<String> result = new ArrayList<String>();
	    Transaction tx = null;
		try{
			tx = m_dbWrapper.getDatabase().beginTx();
			if(m_engine == null){
				m_engine = new ExecutionEngine(m_dbWrapper.getDatabase());
			}
			ExecutionResult exeResult = m_engine.execute(query);
			
			Iterator<String> nodeItr = exeResult.columnAs( "Region" );			
			int regionCount = 0;
			while(nodeItr.hasNext()){
				result.add(nodeItr.next());
				++regionCount;
			}
			
			tx.success();
		 LOG.info("Query result - employee count: " + regionCount);
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
	public String recommandAreaByTeam(String team) {
		String query = "MATCH (t:Team {Name: '" + team + "'})<-[r1:MEMBER_OF]-(n)-[r2:LIVING]->(m) "
				+ 	   "WITH DISTINCT m MATCH (m)-[r:NEAREST]->() "
				+ 	   "WITH m.Name as Name, SUM(r.Distance) AS l "
				+ 	   "RETURN Name, l/100 as Cost  ORDER BY Cost DESC LIMIT 1";

		//String query = "START n=node:" + EntityAttributeKey.NeoSampleNodeIndex + "(" + matchClause + "') RETURN n";
		LOG.info("Query: " + query);
		String result = null;
	    Transaction tx = null;
		try{
			tx = m_dbWrapper.getDatabase().beginTx();
			if(m_engine == null){
				m_engine = new ExecutionEngine(m_dbWrapper.getDatabase());
			}
			ExecutionResult exeResult = m_engine.execute(query);
			
			Iterator<String> nodeItr = exeResult.columnAs( "Name" );
			if(nodeItr.hasNext()){
				result = nodeItr.next();			
			}
			tx.success();
		 LOG.info("Query result: " + result);
		}catch(Exception ex){
			throw new NeoSampleRuntimeException(ex);
		}finally{
			if(tx != null){
				tx.close();
			}
		}

		return result;
	}


}
