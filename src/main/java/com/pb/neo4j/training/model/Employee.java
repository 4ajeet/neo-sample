package com.pb.neo4j.training.model;

public class Employee {

	private final String m_empName;
	private final String m_empDesignation;
	private final String m_emailId;

	private final double m_experience;
	
	public Employee(String empName, String empDesignation, double experience, String emailId){
		m_empName = empName;
		m_empDesignation = empDesignation;
		m_experience = experience;
		m_emailId = emailId;
	}

	public String getName() {
		return m_empName;
	}

	public String getDesignation() {
		return m_empDesignation;
	}

	public double getExperience() {
		return m_experience;
	}

	public String getEmail() {
		return m_emailId;
	}

}
