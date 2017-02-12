/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.textures;

import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

public class Texture {

	// The default texture coordinates, encompassing the entire image.
	private static final float[] DEFAULT_COORDINATES = new float[] {
			0, 0,
			1, 0,
			1, 1,
			0, 1
	};

	private TextureBinding binding;

	private int width, height;
	private float[] coordinates;

	public Texture(BufferedImage image) {
		this(new TextureBinding(image));
	}

	public Texture(TextureBinding binding) {
		this(binding, binding.getWidth(), binding.getHeight(), DEFAULT_COORDINATES);
	}

	public Texture(TextureBinding binding, int width, int height, float[] coordinates) {
		this.binding = binding;
		this.width = width;
		this.height = height;
		this.coordinates = coordinates;
	}

	public void draw(int x, int y) {
		draw(x, y, width, height);
	}

	public void draw(int x, int y, int w, int h) {
		// Don't draw using a binding that hasn't been loaded.
		if (!isReady())
			return;

		binding.bind();

		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(coordinates[0], coordinates[1]);
			GL11.glVertex2f(x, y);

			GL11.glTexCoord2f(coordinates[2], coordinates[3]);
			GL11.glVertex2f(x + w, y);

			GL11.glTexCoord2f(coordinates[4], coordinates[5]);
			GL11.glVertex2f(x + w, y + h);

			GL11.glTexCoord2f(coordinates[6], coordinates[7]);
			GL11.glVertex2f(x, y + h);
		}
		GL11.glEnd();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean isReady() {
		return binding.isReady();
	}

	public TextureBinding getBinding() {
		return binding;
	}

}
