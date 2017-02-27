/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.textures;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;

import net.alexmack.boothy.Boothy;

// Provides an always-available "texture not found" texture.
public class Texture404 extends Texture {

	// This array contains a carefully encoded 32x32 image - don't change it!
	private static final int[] DATA = new int[] { 0x1f8b0800, 0x0, 0xed94, 0x510e8020, 0xc437715, 0xafe7253c,
			0xfaf49338, 0x291ddba2, 0x3124fde0, 0x633c9a02, 0x155529d6, 0x26479116, 0xff6b7cdd, 0xe5920b02, 0xb63cf2ed,
			0x760c0733, 0x3dff3761, 0x383825e2, 0x9f719292, 0xbfcb7fae, 0x5e799f64, 0xfef60a48, 0x3ef97e18, 0x27c0bf5d,
			0x24f25be7, 0xe9fc766c, 0xf8bfbcf9, 0xe72ade3f, 0x137c3e9f, 0xe14c2f1f, 0x7cb3fc24, 0xc89f8783, 0x5322fe53,
			0xfa93acd3, 0x8afe67f4, 0xbf7e5e7c, 0xcb2fd509, 0x97fc9138, 0xc0000
	};
	
	private static final int SIZE = 32;
	
	private static Texture texture;
	
	public static Texture get(TextureLoader tl) {
		if (texture == null)
			load(tl);
		
		return texture;
	}
	
	private static void load(TextureLoader tl) {
		Boothy.log(Boothy.LOG_DEBUG, "Loading 404 texture...");
		
		BufferedImage bi = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
		
		try {
			byte[] gzip = new byte[DATA.length * 4];
			int index = 0;
			
			// Split ints to bytes.
			for (int d : DATA) {
				gzip[index++] = (byte) ((d >>> 24) & 0xFF);
				gzip[index++] = (byte) ((d >>> 16) & 0xFF);
				gzip[index++] = (byte) ((d >>> 8) & 0xFF);
				gzip[index++] = (byte) ((d) & 0xFF);
			}
			
			int[] rgb = new int[SIZE * SIZE];
			GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(gzip));
			
			for (index = 0; index < rgb.length; index++) {
				// Read RGB value.
				int pixel = in.read();
				pixel <<= 8;
				pixel |= in.read();
				pixel <<= 8;
				pixel |= in.read();
				
				rgb[index] = pixel;
			}
			
			in.close();
			
			// Put RGB values into image.
			for (int x = 0; x < SIZE; x++)
				for (int y = 0; y < SIZE; y++)
					bi.setRGB(x, y, rgb[(x * SIZE) + y]);
		}catch (Exception ex) {
			Boothy.log(Boothy.LOG_ERROR, "Failed to load 404 texture!");
		}finally {
			texture = tl.process(new Texture404(bi));
		}
	}
	
	private Texture404(BufferedImage image) {
		super(image);
	}
	
	@Override
	public Texture cut(int x, int y, int w, int h) {
		return this;
	}
	
	@Override
	public void draw(int x, int y, int w, int h, int[] t) {
		super.draw(x, y, w, h, Texture.TRANSLATION_NORMAL);
	}
	
	@Override
	public boolean is404() {
		return true;
	}

}
