/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.font;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.alexmack.boothy.Boothy;
import net.alexmack.boothy.textures.TextureLoader;

public class FontLoader {

	public static final String PATH_FALLBACK = "unknown.png";
	
	public static final String NAME_NUMBER = "_[0-9A-Fa-f]+\\.png";
	public static final String NAME_CHARACTER = ".\\.png";
	
	public static void fromFolder(Font font, TextureLoader tl, File folder) {
		// Load fallback texture.
		File fallback = new File(folder, PATH_FALLBACK);
		if (fallback.exists())
			font.setFallback(tl.fromFileSafe(fallback));
		
		ArrayList<File> folders = new ArrayList<>();
		folders.add(folder);
		
		while (!folders.isEmpty()) {
			folder = folders.remove(0);
			Boothy.log(Boothy.LOG_INFO, "Searching \"" + folder.getAbsolutePath() + "\" for font textures...");
			
			for (File file : folder.listFiles())
				if (file.isDirectory())
					folders.add(file);
				else {
					String name = file.getName();
					char character = 0;
					
					if (name.matches(NAME_NUMBER))
						try {
							character = (char) Integer.parseInt(name.split("\\.")[0].substring(1), 16);
						}catch (NumberFormatException nfe) {
							Boothy.log(Boothy.LOG_ERROR, "Couldn't load font file \"" + name + "\", invalid codepoint!");
							continue;
						}
					else if (name.matches(NAME_CHARACTER))
						character = name.charAt(0);
					else{
						Boothy.log(Boothy.LOG_INFO, "Ignoring \"" + name + "\", not a valid font file...");
						continue;
					}
					
					Boothy.log(Boothy.LOG_INFO, "Loading " + character + " from " + file.getPath());
					font.addCharacter(character, tl.fromFileSafe(file));
				}
			
			// Sort the folder list so that order is assured across operating systems.
			Collections.sort(folders, new Comparator<File>() {

				@Override
				public int compare(File f1, File f2) {
					return f1.getName().compareTo(f2.getName());
				}
				
			});
		}
	}
	
}
