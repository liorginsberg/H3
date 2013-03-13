package com.liorginsberg.homework2;

public class Marker {

	private String text;
	private int posX;
	private int posY;
	
	
	public Marker(String text, int posX, int posY) {
		this.text = text;
		this.posX = posX;
		this.posY = posY;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	
	
}
