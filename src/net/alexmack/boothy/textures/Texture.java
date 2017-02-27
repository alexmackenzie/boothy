/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.textures;

import java.awt.image.BufferedImage;

import org.lwjgl.opengl.GL11;

public class Texture {
	
	public static final int[] TRANSLATION_NORMAL = new int[] {
			// Top left.
			0, 2,
			// Top right.
			1, 2,
			// Bottom right.
			1, 3,
			// Bottom left.
			0, 3
	};
	
	public static final int[] TRANSLATION_FLIP_H = new int[] {
			1, 3,
			0, 3,
			0, 2,
			1, 2
	};
	
	public static final int[] TRANSLATION_FLIP_V = new int[] {
			1, 2,
			0, 2,
			0, 3,
			1, 3
	};
	
	// The default texture coordinates, encompassing the entire image.
	private static final float[] DEFAULT_COORDINATES = new float[] {
			// X origin and end.
			0, 1,
			// Y origin and end.
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
	
	public void draw(int x, int y, int[] t) {
		draw(x, y, width, height, t);
	}
	
	public void draw(int x, int y, int w, int h) {
		draw(x, y, w, h, TRANSLATION_NORMAL);
	}

	public void draw(int x, int y, int w, int h, int[] t) {
		// Don't draw using a binding that hasn't been loaded.
		if (!isReady())
			return;

		binding.bind();

		GL11.glBegin(GL11.GL_QUADS);
		{
			// First corner, top left when using standard translation.
			GL11.glTexCoord2f(coordinates[t[0]], coordinates[t[1]]);
			GL11.glVertex2f(x, y);

			// Second corner, top right when using standard translation.
			GL11.glTexCoord2f(coordinates[t[2]], coordinates[t[3]]);
			GL11.glVertex2f(x + w, y);

			// Third corner, bottom right when using standard translation.
			GL11.glTexCoord2f(coordinates[t[4]], coordinates[t[5]]);
			GL11.glVertex2f(x + w, y + h);

			// Fourth corner, bottom left when using standard translation.
			GL11.glTexCoord2f(coordinates[t[6]], coordinates[t[7]]);
			GL11.glVertex2f(x, y + h);
		}
		GL11.glEnd();
	}
	
	public Texture cut(int x, int y, int w, int h) {
		// Check the coordinates are valid.
		if (x < 0 || y < 0 || x >= width || y >= width)
			throw new IllegalArgumentException("Invalid coordinates for cut!");
		
		// Check the width and height are >= 1.
		if (w <= 0 || h <= 0)
			throw new IllegalArgumentException("Width or height is too small for cut!");
		
		// Check the area being cut actually exists.
		if ((w - x) > width || (h - y) > height)
			throw new IllegalArgumentException("Cut specifies a non-existent area!");
		
		// Get coordinates of the original image.
		float originX = coordinates[0];
		float originY = coordinates[2];
		float endX = coordinates[1];
		float endY = coordinates[3];
		
		// Width and height in texture terms.
		float texWidth = endX - originX;
		float texHeight = endY - originY;
		
		// Ratio between pixels and texture coordinates.
		float texXRatio = texWidth / ((float) width);
		float texYRatio = texHeight / ((float) height);
		
		// Calculate texture width of the cut texture.
		float cutWidth = ((float) w) * texXRatio;
		float cutHeight = ((float) h) * texYRatio;
		
		// Generate texture coordinates of the cut texture.
		float cutOriginX = originX + (((float) x) * texXRatio);
		float cutOriginY = originY + (((float) y) * texYRatio);
		float cutEndX = cutOriginX + cutWidth;
		float cutEndY = cutOriginY + cutHeight;
		
		// Put these into an array.
		float[] cut = new float[] {
				cutOriginX, cutEndX,
				cutOriginY, cutEndY
		};
		
		return new Texture(binding, w, h, cut);
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
	
	public boolean is404() {
		return false;
	}

	public TextureBinding getBinding() {
		return binding;
	}

}
