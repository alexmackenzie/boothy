/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.input;

import org.lwjgl.input.Keyboard;

public class KeyboardEvent {

	private int key;
	private char character;
	private long duration;
	
	public KeyboardEvent(int key, char character, long duration) {
		this.key = key;
		this.character = character;
		this.duration = duration;
	}
	
	public int getKey() {
		return key;
	}
	
	public String getKeyName() {
		return Keyboard.getKeyName(getKey());
	}
	
	public char getCharacter() {
		return character;
	}
	
	public long getDuration() {
		return duration;
	}
	
}
