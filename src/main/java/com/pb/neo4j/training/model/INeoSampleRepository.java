package com.pb.neo4j.training.model;

import java.util.List;
import java.util.Map;

public interface INeoSampleRepository {

	public List<Employee> findByName(String empName);

	public Map<String, Double> findAllBySkillType(String skillType, String team);

	public List<Employee> findAllByTeam(String team, String designation);

	public List<Employee> findAllByManager(String manager, String designation);

	public List<String> findAllLivingRegionsByTeam(String team);

	public String recommandAreaByTeam(String team);

}
