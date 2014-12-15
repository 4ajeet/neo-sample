package com.pb.neo4j.training.db;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.pb.neo4j.training.model.LinkTypes;

public final class DummyDataParser {
	private final Scanner m_scanner;
	
	public DummyDataParser(String fileName) throws IOException{
		m_scanner = new Scanner(new File(fileName), "UTF-8");
	}
	
	public Map<String, Object> next() throws IOException{
		String line = m_scanner.nextLine();
	       // note that Scanner suppresses exceptions
        if (m_scanner.ioException() != null) {
            throw m_scanner.ioException();
        }
        if(line.startsWith("//") && m_scanner.hasNext()){
        	return next();
        }else if(line.startsWith("//")){
        	return null;
        }
		String[] pairs = line.split(",");
		Map<String, Object> result = new HashMap<String, Object>();
		for(String pair : pairs){
			String[] keyValue = pair.split(":");
			String key = keyValue[0].trim();
			Object valueObject = keyValue[1].trim();
			if(keyValue[0].trim().equals("RelationshipType")){
				valueObject = LinkTypes.valueOf(keyValue[1].trim());
			} else if(key.equals("Years") || key.equals("Experience") || key.equals("Duration") || key.equals("Distance")){
				valueObject = Double.parseDouble(keyValue[1].trim());
			}
			result.put(keyValue[0].trim(), valueObject);
		}
		return result;
	}

	public boolean hasNext(){
		if(m_scanner != null && m_scanner.hasNext()){
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) throws IOException {
		DummyDataParser parser = new DummyDataParser("src\\main\\resources\\Relationships.txt");
		while(parser.hasNext()){
			System.out.println("========================================================================");
			System.out.println(parser.next());
			System.out.println("========================================================================");
		}
	}
}
