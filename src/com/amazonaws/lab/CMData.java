package com.amazonaws.lab;

import java.util.ArrayList;
import java.util.HashMap;


public class CMData {
	public ArrayList<Entity> entities;
	public String[] UnmappedAttributes;
	public String rawCMOutput;
	
	private ArrayList<String> entityTextList;
	
	private HashMap<String,String> entTextMap; 

	public String[] getUnmappedAttributes() {
		return UnmappedAttributes;
	}

	public void setUnmappedAttributes(String[] unmappedAttributes) {
		UnmappedAttributes = unmappedAttributes;
	}

	public ArrayList<Entity> getEntities() {
		return entities;
	}

	public void setEntities(ArrayList<Entity> entities) {
		this.entities = entities;
	}

	public String getRawCMOutput() {
		return rawCMOutput;
	}

	public void setRawCMOutput(String rawCMOutput) {
		this.rawCMOutput = rawCMOutput;
	}

	public void initEntityTextList() {
		for(Entity ent:entities) {
			entityTextList.add(ent.getText());
			entTextMap.put(ent.getText(),"False");
		}
	}

	public ArrayList<String> getEntityTextList() {
		return entityTextList;
	}

	public void setEntityTextList(ArrayList<String> entityTextList) {
		this.entityTextList = entityTextList;
	}
	
	public HashMap<String,String> getEntityTextMap(){
		return entTextMap;
	}

	
}
