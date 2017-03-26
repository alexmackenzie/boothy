/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy.input;

public class MouseEvent extends DurationEvent {

	public static final int BUTTON_LEFT = 0, BUTTON_RIGHT = 1, BUTTON_MIDDLE = 2;
	
	private int button, x, y;
	
	public MouseEvent(int button, long duration, int x, int y) {
		super(duration);
		this.button = button;
		this.x = x;
		this.y = y;
	}
	
	public int getButton() {
		return button;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public boolean isLeftButton() {
		return getButton() == BUTTON_LEFT;
	}
	
	public boolean isRightButton() {
		return getButton() == BUTTON_RIGHT;
	}
	
	public boolean isMiddleButton() {
		return getButton() == BUTTON_MIDDLE;
	}

}
