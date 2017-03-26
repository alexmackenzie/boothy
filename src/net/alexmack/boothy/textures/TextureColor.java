package net.alexmack.boothy.textures;

import org.lwjgl.opengl.GL11;

public class TextureColor {
	
	public static void push(int r, int g, int b) {
		push((float) r / 255F, (float) g / 255F, (float) b / 255F);
	}
	
	public static void push(float r, float g, float b) {
		GL11.glColor3f(r, g, b);
	}
	
	public static void pop() {
		GL11.glColor3f(1, 1, 1);
	}
	
}
