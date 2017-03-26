/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.input;

public class DurationEvent {

	private long duration;
	
	public DurationEvent(long duration) {
		this.duration = duration;
	}
	
	public long getDuration() {
		return duration;
	}
	
}
