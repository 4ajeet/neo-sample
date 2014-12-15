package com.pb.neo4j.training.model;

public class Team {

	private String m_name;
	private String m_currentVersion;
	private String m_productOwner;

	public String getM_name() {
		return m_name;
	}
	public void setM_name(String m_name) {
		this.m_name = m_name;
	}
	public String getM_currentVersion() {
		return m_currentVersion;
	}
	public void setM_currentVersion(String m_currentVersion) {
		this.m_currentVersion = m_currentVersion;
	}
	public String getM_productOwner() {
		return m_productOwner;
	}
	public void setM_productOwner(String m_productOwner) {
		this.m_productOwner = m_productOwner;
	}
}
