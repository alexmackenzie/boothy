/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.input;

public interface KeyboardHandler {

	public void onKeyDown(KeyboardEvent event);
	
	public void onKeyUp(KeyboardEvent event);
	
	public void onKeyPressed(KeyboardEvent event);
	
}
