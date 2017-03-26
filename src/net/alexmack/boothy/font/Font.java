/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.font;

import java.util.Arrays;

import net.alexmack.boothy.textures.Texture;

public class Font {
	
	private volatile Texture[] textures = new Texture[0];
	
	private Texture fallback;
	private int gap;
	
	public Font(Texture fallback) {
		this.fallback = fallback;
		this.gap = 1;
	}
	
	public void draw(int x, int y, int size, char[] characters) {
		for (char c : characters) {
			Texture texture = get(c);
			
			texture.draw(x, y);
			x += texture.getWidth() + gap;
		}
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
	
}
