/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.input;

public interface MouseHandler {

	public void onMove(MouseEvent event);
	
	public void onButtonDown(MouseEvent event);
	
	public void onButtonUp(MouseEvent event);
	
	public void onButtonClicked(MouseEvent event);
	
}
