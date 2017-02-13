/* 
 * Copyright 2017 Alexander Mackenzie
 */
package net.alexmack.boothy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class Window {
	
	private Thread thread = null;
	private boolean running = true;
	private Renderer renderer = null;
	private Resolution resolution = null;
	
	private int fps = 60;
	private int width = 400, height = 400;
	
	private volatile long frame = 0;
	
	private List<Runnable> queue = Collections.synchronizedList(new ArrayList<Runnable>());
	
	public Window(Resolution resolution, Renderer renderer) {
		this.resolution = resolution;
		this.width = resolution.getWidth();
		this.height = resolution.getHeight();
		
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
		
		while (running = (running && !Display.isCloseRequested())) {
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
			
			Display.sync(fps);
		}
	}
	
	private void setupMatrix() {
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width = Display.getWidth(), height = Display.getHeight(), 0, -1, 1);
		GL11.glViewport(0, 0, width, height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		// Enable 2D textures.
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		// Default scaling.
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	}
	
	/**
	 * Adds the given {@link Runnable} to the list of tasks to be run from the rendering
	 * {@link Thread} on the next frame.
	 */
	public void queue(Runnable runnable) {
		queue.add(runnable);
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
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public long getFrame() {
		return frame;
	}
	
}
