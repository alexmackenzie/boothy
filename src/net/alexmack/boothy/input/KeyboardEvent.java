/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.input;

import org.lwjgl.input.Keyboard;

public class KeyboardEvent extends DurationEvent {

	private int key;
	private char character;
	
	public KeyboardEvent(int key, char character, long duration) {
		super(duration);
		this.key = key;
		this.character = character;
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
	
}
