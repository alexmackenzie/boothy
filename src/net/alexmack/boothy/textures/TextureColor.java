/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.textures;

import org.lwjgl.opengl.GL11;

public class TextureColor {
	
	public static void push(int rgb) {
		push(rgb >>> 16, (rgb >>> 8) & 0xFF, rgb & 0xFF);
	}
	
	public static void pushA(int rgb, int alpha) {
		pushA(rgb >>> 16, (rgb >>> 8) & 0xFF, rgb & 0xFF, alpha);
	}
	
	public static void push(int r, int g, int b) {
		push((float) r / 255F, (float) g / 255F, (float) b / 255F);
	}
	
	public static void pushA(int r, int g, int b, int alpha) {
		pushA((float) r / 255F, (float) g / 255F, (float) b / 255F, (float) alpha / 255F);
	}
	
	public static void push(float r, float g, float b) {
		GL11.glColor3f(r, g, b);
	}
	
	public static void pushA(float r, float g, float b, float alpha) {
		GL11.glColor4f(r, g, b, alpha);
	}
	
	public static void pop() {
		GL11.glColor4f(1, 1, 1, 1);
	}
	
}
