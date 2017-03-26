/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import net.alexmack.boothy.input.KeyboardEvent;
import net.alexmack.boothy.input.KeyboardHandler;
import net.alexmack.boothy.input.MouseEvent;
import net.alexmack.boothy.input.MouseHandler;

public class Window {
	
	private static final long NODATA = Long.MIN_VALUE;
	
	private Thread thread = null;
	private boolean running = true;
	private Renderer renderer = null;
	private Resolution resolution = null;
	
	private int fps = 60;
	private int width = 400, height = 400;
	
	private long[] keyboard = new long[Keyboard.KEYBOARD_SIZE];
	private char[] keyboardChars = new char[keyboard.length];
	private KeyboardHandler keyboardHandler = null;
	
	private long[] mouse = null;
	private MouseHandler mouseHandler = null;
	
	private volatile long frame = 0;
	
	private List<Runnable> queue = Collections.synchronizedList(new ArrayList<Runnable>());
	
	public Window(Resolution resolution, Renderer renderer) {
		this.resolution = resolution;
		this.width = resolution.getWidth();
		this.height = resolution.getHeight();
		
		// Put the NODATA value into the keyboard array.
		Arrays.fill(keyboard, NODATA);
		
		// Create and run the Window thread.
		thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Window.this.run();
				}catch (Exception e) {
					Boothy.log(Boothy.LOG_FATALITY, "Fatal error when running Window!");
				}
				
				Boothy.log(Boothy.LOG_INFO, "Destroying display...");
				Display.destroy();
			}
			
		});
		thread.setName(Boothy.NAME);
		
		setRenderer(renderer);
		thread.start();
	}
	
	private void run() throws LWJGLException {
		Boothy.log(Boothy.LOG_INFO, "Started rendering in \"" + Thread.currentThread().getName() + "\"!");
		
		// Setup the display according to the resolution given.
		Display.setDisplayMode(resolution.getMode());
		if (resolution.isFullscreenDefault()) {
			Boothy.log(Boothy.LOG_INFO, "Defaulting to fullscreen mode...");
			Display.setFullscreen(true);
		}
		
		// Setup some basic properties.
		Display.setTitle(Boothy.NAME);
		Display.setResizable(true);
		
		Boothy.log(Boothy.LOG_INFO, "Creating display using resolution \"" + resolution.toString() + "\"...");
		
		// Create a display with the default PixelFormat.
		Display.create(new PixelFormat());
		// Create the keyboard and mouse.
		Keyboard.create();
		Mouse.create();
		
		// Setup mouse data array.
		mouse = new long[Mouse.getButtonCount()];
		Arrays.fill(mouse, NODATA);
		
		while (running = (running && !Display.isCloseRequested())) {
			long now = System.currentTimeMillis();
			
			// Run any queued tasks.
			while (!queue.isEmpty())
				queue.remove(0).run();
			
			setupMatrix();
			
			// Clear the color buffer. We won't clear the depth buffer as Boothy isn't meant for 3D.
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			
			if (renderer != null)
				try {
					renderer.render(this);
				}catch (Exception e) {
					Boothy.log(Boothy.LOG_ERROR, "Error in rendering!");
					Boothy.log(Boothy.LOG_ERROR, e.getMessage());
				}
			
			frame++;
			Display.update();
			
			// Dispatch keyboard events.
			while (Keyboard.next()) {
				// Don't process events if there's no handler.
				if (keyboardHandler == null)
					break;
				
				int key = Keyboard.getEventKey();
				
				// The key state is true if the key is down.
				if (Keyboard.getEventKeyState()) {
					char character = Keyboard.getEventCharacter();
					
					// Store timestamp in the keyboard array and fire event.
					keyboard[key] = now;
					keyboardChars[key] = character;
					keyboardHandler.onKeyDown(new KeyboardEvent(key, character, NODATA));
				}else{
					// Fire the standard key up event.
					keyboardHandler.onKeyUp(new KeyboardEvent(key, keyboardChars[key], NODATA));
					
					// Fire pressed event if necessary.
					long data = keyboard[key];
					if (data != NODATA)
						keyboardHandler.onKeyPressed(new KeyboardEvent(key, keyboardChars[key], now - data));
					
					// Reset keyboard array.
					keyboard[key] = NODATA;
				}
			}
			
			// Dispatch mouse events.
			while (Mouse.next()) {
				// Don't process events with no handler.
				if (mouseHandler == null)
					continue;
				
				int button = Mouse.getEventButton();
				int x = Mouse.getEventX(), y = Mouse.getEventY();
				
				// Mouse events use bottom-left as (0, 0).
				y = height - y;
				
				// Detect mouse movement.
				if (button < 0) {
					mouseHandler.onMove(new MouseEvent(-1, 0, x, y));
					continue;
				}
				
				long data = mouse[button];
				
				if (Mouse.getEventButtonState()) {
					// Ignore button presses if they're already registered as
					// having been pressed.
					if (data == NODATA) {
						mouse[button] = now;
						mouseHandler.onButtonDown(new MouseEvent(button, NODATA, x, y));
					}
				}else{
					mouseHandler.onButtonUp(new MouseEvent(button, NODATA, x, y));
					
					// Fire the click event.
					if (data != NODATA) {
						mouse[button] = NODATA;
						mouseHandler.onButtonClicked(new MouseEvent(button, now - data, x, y));
					}
				}
			}
			
			if (fps > 0)
				Display.sync(fps);
		}
	}
	
	private void setupMatrix() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width = Display.getWidth(), height = Display.getHeight(), 0, -1, 1);
		GL11.glViewport(0, 0, width, height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		// Enable 2D textures.
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
	}
	
	/**
	 * Adds the given {@link Runnable} to the list of tasks to be run from the rendering
	 * {@link Thread} on the next frame.
	 */
	public void queue(Runnable runnable) {
		queue.add(runnable);
	}
	
	public void close() {
		running = false;
	}
	
	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
		
		if (renderer != null)
			Boothy.log(Boothy.LOG_DEBUG, "Renderer: " + renderer.getClass().getName());
	}
	
	public boolean setFullscreen(final boolean fullscreen) {
		if (!Display.getDisplayMode().isFullscreenCapable())
			return false;
		
		queue(new Runnable() {
			
			@Override
			public void run() {
				try {
					Display.setFullscreen(fullscreen);
				}catch (LWJGLException e) {
					Boothy.log(Boothy.LOG_ERROR, "Failed to change fullscreen status!");
				}
			}
			
		});
		return true;
	}
	
	public void setTitle(final String title) {
		queue(new Runnable() {
			
			@Override
			public void run() {
				Display.setTitle(title);
			}
			
		});
	}
	
	public void setResolution(final Resolution resolution) {
		queue(new Runnable() {
			
			@Override
			public void run() {
				try {
					Window.this.resolution = resolution;
					Display.setDisplayMode(resolution.getMode());
					setFullscreen(resolution.isFullscreenDefault());
					
					Boothy.log(Boothy.LOG_INFO, "Changed resolution to \"" + resolution.toString() + "\".");
				}catch (LWJGLException e) {
					Boothy.log(Boothy.LOG_ERROR, "Failed to change resolution!");
				}
			}
			
		});
	}
	
	public void setKeyboardHandler(KeyboardHandler handler) {
		keyboardHandler = handler;
	}
	
	public void setMouseHandler(MouseHandler handler) {
		mouseHandler = handler;
	}
	
	public void setFps(int fps) {
		this.fps = fps;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public long getFrame() {
		return frame;
	}
	
	public int getFps() {
		return fps > 0 ? fps : Integer.MAX_VALUE;
	}
	
}
