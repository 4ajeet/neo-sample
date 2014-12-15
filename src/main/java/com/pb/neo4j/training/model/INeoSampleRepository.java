package com.pb.neo4j.training.model;

import java.util.List;

public interface INeoSampleRepository {

	List<Employee> searchByName(String empName);

	List<Employee> findAllBySkillType(String skillType, String team);

	List<Employee> findAllByTeam(String team, String designation);

	List<Employee> findAllByManager(String manager, String designation);

}
