/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class Resolution {

	public static Resolution getDesktopResolution() {
		return new Resolution(Display.getDesktopDisplayMode());
	}
	
	public static Resolution[] getAllResolutions() {
		try {
			DisplayMode[] modes = Display.getAvailableDisplayModes();
			Resolution[] resolutions = new Resolution[modes.length];
			
			for (int i = 0; i < modes.length; i++)
				resolutions[i] = new Resolution(modes[i]);
			
			return resolutions;
		}catch (LWJGLException e) {
			Boothy.log(Boothy.LOG_ERROR, "Failed to get any available display modes!");
			return new Resolution[0];
		}
	}
	
	public static Resolution getHighestResolution() {
		Resolution highest = null;
		int highestPixels = 0;
		
		for (Resolution resolution : getAllResolutions()) {
			int pixels = resolution.getPixels();
			
			if (pixels > highestPixels) {
				highest = resolution;
				highestPixels = pixels;
			}
		}
		
		return highest;
	}
	
	private DisplayMode mode = null;
	private boolean defaultToFullscreen = false;
	
	public Resolution(DisplayMode mode) {
		// Do not allow null modes.
		if (mode == null)
			throw new IllegalArgumentException("Resolution cannot be created with a null mode!");
		
		this.mode = mode;
	}
	
	public Resolution(int width, int height) {
		double span = Math.sqrt(Math.pow(width, 2) + Math.pow(height, 2));
		double spanBest = Double.MAX_VALUE;
		
		try {
			for (DisplayMode mode : Display.getAvailableDisplayModes()) {
				double spanCandidate = Math.sqrt(Math.pow(mode.getWidth(), 2) + Math.pow(mode.getHeight(), 2));
				spanCandidate = Math.abs(span - spanCandidate);
				
				if (spanCandidate < spanBest) {
					spanBest = spanCandidate;
					this.mode = mode;
				}
			}
		}catch (LWJGLException lwjgl) {
			Boothy.log(Boothy.LOG_ERROR, "Failed to get display modes!");
		}
		
		if (this.mode == null)
			throw new IllegalStateException("No valid display mode was found!");
	}
	
	public Resolution setFullscreenDefault(boolean dtf) {
		defaultToFullscreen = dtf;
		return this;
	}
	
 	public DisplayMode getMode() {
		return mode;
	}
	
	public int getWidth() {
		return mode.getWidth();
	}
	
	public int getHeight() {
		return mode.getHeight();
	}
	
	public int getPixels() {
		return getWidth() * getHeight();
	}
	
	public boolean isFullscreenSupported() {
		return mode.isFullscreenCapable();
	}
	
	public boolean isFullscreenDefault() {
		return isFullscreenSupported() && defaultToFullscreen;
	}
	
	@Override
	public String toString() {
		return getWidth() + "x" + getHeight() + "px " + (isFullscreenSupported() ? "fullscreen" : "nofullscreen");
	}
	
}
