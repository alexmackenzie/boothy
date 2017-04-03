/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.input;

import net.alexmack.boothy.Window;

public interface WindowHandler {

	public void onResize(Window window, int width, int height);
	
}
