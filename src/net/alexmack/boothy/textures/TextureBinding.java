/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.textures;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TextureBinding implements Runnable {

	private static final int PIXEL_SIZE = 4;
	private static final int PIXEL_FORMAT = GL11.GL_RGBA;
	private static final int PIXEL_FORMAT_INTERNAL = GL11.GL_RGBA8;
	
	private int gl = 0;
	private BufferedImage image;
	private int width, height;
	
	public TextureBinding(BufferedImage image) {
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
	}
	
	public int getGl() {
		return gl;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public boolean isReady() {
		return gl != 0;
	}
	
	@Override
	public void run() {
		if (isReady())
			return;
		
		// Create an array of pixels and load the BufferedImage into it.
		int[] pixels = new int[width * height];
		image.getRGB(0, 0, width, height, pixels, 0, width);
		
		// Create a buffer large enough to store the pixels as bytes.
		ByteBuffer buffer = BufferUtils.createByteBuffer(pixels.length * PIXEL_SIZE);
		
		// Convert from 32-bit ARGB to 4-byte RGBA.
		for (int pixel : pixels) {
			buffer.put((byte) ((pixel >> 16) & 0xFF));
			buffer.put((byte) ((pixel >> 8) & 0xFF));
			buffer.put((byte) ((pixel) & 0xFF));
			buffer.put((byte) ((pixel >> 24) & 0xFF));
		}
		
		buffer.flip();
		
		// Bind the buffer to OpenGL.
		this.gl = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, 0);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, PIXEL_FORMAT_INTERNAL, width, height, 0, PIXEL_FORMAT, GL11.GL_UNSIGNED_BYTE, buffer);
		
		// Dereference the image so the memory can be reclaimed.
		this.image = null;
	}
	
	public void bind() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, gl);
	}
	
}
