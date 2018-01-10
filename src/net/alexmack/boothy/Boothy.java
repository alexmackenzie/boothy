/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy;

public class Boothy {

	public static final String VERSION = "ALPHA 1.0.0";
	public static final int VERSION_NUMBER = 1;
	
	public static final String NAME = "Boothy " + VERSION;
	
	public static final int LOG_DEBUG = 0;
	public static final int LOG_INFO = 1;
	public static final int LOG_WARNING = 2;
	public static final int LOG_ERROR = 3;
	public static final int LOG_FATALITY = 4;
	
	public static void log(int level, String... strings) {
		for (String string : strings)
			System.out.println("[BTHY] " + level + "/ " + string);
	}
	
}
