/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.textures;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.alexmack.boothy.Boothy;
import net.alexmack.boothy.Window;

public class TextureLoader {
	
	private Window window;
	
	public TextureLoader(Window window) {
		this.window = window;
	}
	
	protected Texture process(Texture texture) {
		// Queue the binding so it can be loaded in the render thread.
		window.queue(texture.getBinding());
		return texture;
	}
	
	public Texture fromImage(BufferedImage image) {
		return process(new Texture(image));
	}
	
	public Texture fromFile(File file) throws IOException {
		return fromImage(ImageIO.read(file));
	}
	
	public Texture fromFileSafe(File file) {
		try {
			return fromImage(ImageIO.read(file));
		}catch (IOException e) {
			Boothy.log(Boothy.LOG_ERROR, "Failed to load texture from \"" + file.getPath() + "\"!");
			return Texture404.get(this);
		}
	}
	
	public Texture fromStream(InputStream stream) throws IOException {
		return fromImage(ImageIO.read(stream));
	}
	
	public Texture fromStreamSafe(InputStream stream) {
		try {
			return fromImage(ImageIO.read(stream));
		}catch (IOException e) {
			Boothy.log(Boothy.LOG_ERROR, "Failed to load texture from stream!");
			return Texture404.get(this);
		}
	}
	
	public Texture fromBytes(byte[] bytes) throws IOException {
		return fromStream(new ByteArrayInputStream(bytes));
	}
	
	public Texture fromBytesSafe(byte[] bytes) {
		return fromStreamSafe(new ByteArrayInputStream(bytes));
	}
	
}
