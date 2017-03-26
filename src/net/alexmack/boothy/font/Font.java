/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.font;

import java.util.Arrays;

import net.alexmack.boothy.textures.Texture;

public class Font {
	
	public static int width(int fcode) {
		return fcode >>> 16;
	}
	
	public static int height(int fcode) {
		return fcode & 0xFFFF;
	}
	
	private volatile Texture[] textures = new Texture[0];
	
	private Texture fallback;
	private int gap;
	
	public Font(Texture fallback) {
		this.fallback = fallback;
		this.gap = 1;
	}
	
	public int draw(int x, int y, String text) {
		return draw(x, y, 1, text.toCharArray());
	}
	
	public int draw(int x, int y, int size, String text) {
		return draw(x, y, size, text.toCharArray());
	}
	
	public int draw(int x, int y, int size, char[] characters) {
		int h = 0;
		
		for (char c : characters) {
			Texture texture = get(c);
			
			int cW = texture.getWidth() * size;
			int cH = texture.getHeight() * size;
			
			texture.draw(x, y, cW, cH);
			x += cW + (gap * size);
			h = cH > h ? cH : h;
		}
		
		return (x << 16) & h;
	}
	
	public Texture get(char character) {
		if (character >= textures.length)
			return fallback;
		
		return textures[character];
	}
	
	public boolean addCharacter(char character, Texture texture) {
		boolean overwrite = hasCharacter(character);
		
		// If the array is not big enough, expand it.
		if (textures.length <= character) {
			// New array with the fallback value.
			Texture[] texturesNew = new Texture[character + 1];
			Arrays.fill(texturesNew, fallback);
			
			// Copy the old values over.
			for (int i = 0; i < textures.length; i++)
				texturesNew[i] = textures[i];
			
			textures = texturesNew;
		}
		
		this.
		textures[character] = texture;
		return overwrite;
	}
	
	public void setFallback(Texture fallback) {
		this.fallback = fallback;
	}
	
	public void setGap(int gap) {
		this.gap = gap;
	}
	
	public boolean hasCharacter(char character) {
		return get(character) != fallback;
	}
	
	public int getGap() {
		return gap;
	}
	
}
