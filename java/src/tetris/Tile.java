package tetris;


import java.awt.image.BufferedImage;

import TetrisGame.TetrisMain;

/**
 * A simple tile class use to represent the Tetris board
 * 
 * @author Gregary Pergrossi (gpergrossi@imsa.edu)
 */
public class Tile {
	
	private static BufferedImage IMAGE_RED;
	private static BufferedImage IMAGE_ORANGE;
	private static BufferedImage IMAGE_YELLOW;
	private static BufferedImage IMAGE_GREEN;
	private static BufferedImage IMAGE_BLUE;
	private static BufferedImage IMAGE_CYAN;
	private static BufferedImage IMAGE_MAGENTA;
	
	static {
		IMAGE_RED = TetrisMain.loadImage("media/red.png");
		IMAGE_ORANGE = TetrisMain.loadImage("media/orange.png");
		IMAGE_YELLOW = TetrisMain.loadImage("media/yellow.png");
		IMAGE_GREEN = TetrisMain.loadImage("media/green.png");
		IMAGE_BLUE = TetrisMain.loadImage("media/blue.png");
		IMAGE_CYAN = TetrisMain.loadImage("media/cyan.png");
		IMAGE_MAGENTA = TetrisMain.loadImage("media/magenta.png");
	}
	
	private BufferedImage image;
	private TetrisColor color;
	private boolean isControlled = false;
	private boolean isGhost = false;
	
	/**
	 * Constructs a new tile
	 * @param color
	 * @param controlled
	 */
	public Tile(TetrisColor color, boolean controlled) {
		if(color == TetrisColor.red) image = IMAGE_RED;
		if(color == TetrisColor.orange) image = IMAGE_ORANGE;
		if(color == TetrisColor.yellow) image = IMAGE_YELLOW;
		if(color == TetrisColor.green) image = IMAGE_GREEN;
		if(color == TetrisColor.blue) image = IMAGE_BLUE;
		if(color == TetrisColor.cyan) image = IMAGE_CYAN;
		if(color == TetrisColor.magenta) image = IMAGE_MAGENTA;
		this.color = color;
		this.isControlled = controlled;
		this.isGhost = false;
	}
	
	/**
	 * Constructs a new tile
	 * @param color
	 * @param controlled
	 * @param ghost
	 */
	public Tile(TetrisColor color, boolean controlled, boolean ghost) {
		if(color == TetrisColor.red) image = IMAGE_RED;
		if(color == TetrisColor.orange) image = IMAGE_ORANGE;
		if(color == TetrisColor.yellow) image = IMAGE_YELLOW;
		if(color == TetrisColor.green) image = IMAGE_GREEN;
		if(color == TetrisColor.blue) image = IMAGE_BLUE;
		if(color == TetrisColor.cyan) image = IMAGE_CYAN;
		if(color == TetrisColor.magenta) image = IMAGE_MAGENTA;
		this.color = color;
		this.isControlled = controlled;
		this.isGhost = ghost;
	}
	
	/**
	 * Returns the image associated with this tile
	 * @return
	 */
	public BufferedImage getImage() {
		return image;
	}
	
	/**
	 * Returns whether this tile is controlled
	 * @return
	 */
	public boolean isControlled() {
		return isControlled;
	}

	/**
	 * Returns this tile's color
	 * @return
	 */
	public TetrisColor getColor() {
		return color;
	}
	
	/**
	 * Is this piece a ghost? 
	 */
	public boolean isGhost() {
		return isGhost;
	}
	
}
