package com.pb.neo4j.training.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.pb.neo4j.training.model.Employee;
import com.pb.neo4j.training.model.INeoSampleRepository;
import com.pb.neo4j.training.model.NeoSampleRuntimeException;

/**
 * Response of all operations of this class should be rederable on a map - GeoJosn
 * Spring 4 MVC use Jackson 2  as a JSON processor internally so it will be easy to map GeoJson objects
 * 
 * @author aj001si
 *
 */
@Controller
@RequestMapping("/")
public class NeoSampleSearchController {
	private static Logger LOG = Logger.getLogger(NeoSampleSearchController.class);
	private final INeoSampleRepository m_neoSampleRepository;
	
	@Autowired
	public NeoSampleSearchController(INeoSampleRepository neoSampleRepository){
		this.m_neoSampleRepository = neoSampleRepository;
		LOG.info("NeoSampleSearchController succesfully initialized.");
	}

	/**
	 * Sample: http://localhost:8080/NeoSampleApp/employee?name=Ajeet%20Singh
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/employee", method=RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<Employee> findByName(@QueryParam("name") String name) {
		LOG.info("NeoSampleSearchController's findByName with name:" + name);
		if(name == null){
			throw new NeoSampleRuntimeException("'name' param can not be null. \n Sample: http://localhost:8080/NeoSampleApp/employee?name=Ajeet%20Singh");
		}
		return m_neoSampleRepository.findByName(name);
		
	}
	
	/**
	 * Sample: http://localhost:8080/NeoSampleApp/skill?name=Java&team=Spatial Server
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/skill", method=RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Map<String, Double> findAllBySkillType(@QueryParam("name") String name, @QueryParam("name") String team) {
		LOG.info("NeoSampleSearchController's findByName with name:" + name);
		if(name == null){
			throw new NeoSampleRuntimeException("'name' param can not be null. \n Sample: http://localhost:8080/NeoSampleApp/skill?name=Design&team=RJS");
		}
		return m_neoSampleRepository.findAllBySkillType(name, team);
		
	}

	/**
	 * Sample: http://localhost:8080/NeoSampleApp/region?team=Spatial Server
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/region", method=RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<String> findAllLivingRegionsByTeam(@QueryParam("team") String team) {
		LOG.info("NeoSampleSearchController's findByName with name:" + team);
		if(team == null){
			throw new NeoSampleRuntimeException("'name' param can not be null. \n Sample: http://localhost:8080/NeoSampleApp/region?team=Spatial Server");
		}
		return m_neoSampleRepository.findAllLivingRegionsByTeam(team);
		
	}

	/**
	 * Sample: http://localhost:8080/NeoSampleApp/recommandArea?team=Spatial Server
	 * 
	 * @param name
	 * @return
	 */
	@RequestMapping(value = "/recommandArea", method=RequestMethod.GET, produces = "application/json")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public String recommandAreaByTeam(@QueryParam("team") String team) {
		LOG.info("NeoSampleSearchController's findByName with name:" + team);
		if(team == null){
			throw new NeoSampleRuntimeException("'name' param can not be null. \n Sample: http://localhost:8080/NeoSampleApp/recommandArea?team=Spatial Server");
		}
		return m_neoSampleRepository.recommandAreaByTeam(team);
		
	}

	
	/**
	 *  The exception handler here is invoked whenever an IOException is thrown 
	 *  from anywhere within a request in this controller, 
	 *  then the result would be the string "Some informational message." and http response status 500.
	 *  
	 *  Bad requests: http://localhost:8080/NeoSampleApp/location?lat=100
	 *  			  http://localhost:8080/NeoSampleApp/foodpoint/WithInDistance?lat=100&lon=200
	 * @return
	 */
	@ExceptionHandler(NeoSampleRuntimeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String exceptionHandler(HttpServletRequest request, NeoSampleRuntimeException ex) {
		LOG.info("NeoSampleSearchController somthing bad happened ...");
		return "FoodPointApplicationException: " + ex.getLocalizedMessage();
	}
	
	/**
	 *  The exception handler here is invoked whenever an IOException is thrown 
	 *  from anywhere within a request in this controller, 
	 *  then the result would be the string "Some informational message." and http response status 500.
	 *  
	 *  Bad requests: http://localhost:8080/NeoSampleApp/location?lat=100
	 *  			  http://localhost:8080/NeoSampleApp/foodpoint/WithInDistance?lat=100&lon=200
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public String exceptionHandler(HttpServletRequest request, Exception ex) {
		LOG.info("NeoSampleSearchController somthing bad happened ...");
		LOG.info(ex);
		return ex.getClass().getSimpleName() + ": "+ ex.getLocalizedMessage();
	}
	
}
