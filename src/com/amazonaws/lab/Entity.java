package com.amazonaws.lab;

import java.util.ArrayList;


public class Entity {
	public String id;
	public String beginOffset;
	public String endOffset;
	public String score;
	public String text;
	public String category;
	private String type;
	public ArrayList<Trait> traits;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBeginOffset() {
		return beginOffset;
	}
	public void setBeginOffset(String beginOffset) {
		this.beginOffset = beginOffset;
	}
	public String getEndOffset() {
		return endOffset;
	}
	public void setEndOffset(String endOffset) {
		this.endOffset = endOffset;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<Trait> getTraits() {
		return traits;
	}
	public void setTraits(ArrayList<Trait> traits) {
		this.traits = traits;
	}
	
	
}
