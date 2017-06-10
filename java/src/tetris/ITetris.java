package tetris;

/**
 * Tetris API. This is an API for a tetris game. No implementation details included
 * only aspects of the game such as the pieces, colors, hold, bank, and controls
 * 
 * @author Gregary Pergrossi
 */
public interface ITetris {

	/**
	 * Returns the board's width, specifying that x coordinates 0 to width-1 (inclusive) are valid.
	 */
	public int getBoardWidth();
	
	/**
	 * Returns the board's height, specifying that y coordinates 0 to height-1 (inclusive) are valid.
	 */
	public int getBoardHeight();
	
	/**
	 * Moves the current piece down according to the passage of time. This should be called
	 * at a regular interval. Usually with increasing frequency as the game goes on in order to make
	 * it more difficult as the play scores points.
	 */
	public void updateTick();

	/**
	 * Returns the tile at the position. Must be a valid coordinate as specified by getBoardWidth() and getBoardHeight();
	 */
	public Color getTile(int x, int y);
	
	/**
	 * Returns true only if the tile at the given position is a locked in tile. The tetris piece that is currently
	 * in play should show up in the tiles matrix but should return false for isTileLocked().
	 */
	public boolean isTileLocked(int x, int y);
	
	/**
	 * Checks if swap is available
	 * @return true if swap can be performed
	 */
	public boolean canSwap();
	
	/**
	 * Game control: Swaps the piece in play with the piece in the hold.
	 * @return success - true if the move swapped the pieces, else false (already swapped once, no piece in play, game over)
	 */
	public boolean swap();
	
	/**
	 * Game control: rotate the piece clockwise by 90 degrees. 
	 * @return success - true if the move rotated the piece, else false (collision restriction, no piece in play, game over)
	 */
	public boolean rotateRight();
	
	/**
	 * Game control: rotate the piece counterclockwise by 90 degrees. 
	 * @return success - true if the move rotated the piece, else false (collision restriction, no piece in play, game over)
	 */
	public boolean rotateLeft();
	
	/**
	 * Game control: Moves the piece down one row. May be used between 
	 * the game's drop ticks.
	 * @return success - true if the piece was moved down, else false (collision restriction, no piece in play, game over)
	 */
	public boolean softDrop();
	
	/**
	 * Game control: Drop the piece straight down and locks it immediately
	 * at the point of collision.
	 * @return success - true if the move is allowed (not a game over and the piece exists), else false.
	 */
	public boolean hardDrop();
	
	/**
	 * Game control: Moves the piece one column to the left.
	 * @return success - true if the piece was moved, else false (collision restriction, no piece in play, game over)
	 */
	public boolean moveLeft();
	
	/**
	 * Game control: Moves the piece one column to the right.
	 * @return success - true if the piece was moved, else false (collision restriction, no piece in play, game over)
	 */
	public boolean moveRight();
	
	/**
	 * Game control: Restarts the game
	 * @return true if a restart is allowed, false if not (for whatever reason)
	 */
	public boolean restart();

	/**
	 * Returns the piece currently in play.
	 */
	public Piece getPlayPiece();
	
	/**
	 * Returns the position and rotation of the piece that is currently in play.
	 * May return null if the game state does not have a piece. (e.g. Game Over)
	 */
	public PiecePos getPlayPiecePos();
	
	/**
	 * Returns whether this tetris implementation has a hold.
	 * @return true if there is a hold, else false
	 */
	public boolean hasHold();
	
	/**
	 * Returns the piece in the hold, or null if there is no hold.
	 * @return
	 */
	public Piece getHoldPiece();
	
	/**
	 * Returns the number of valid bank locations, specifying that
	 * the bank indexes 0 to number of banks - 1 are valid and have
	 * a piece defined at all times.
	 */
	public int getNumBanks();
	
	/**
	 * Gets the piece in the bank indexed by i. Must be valid according to getNumBanks()
	 * @param i - A valid bank index. Index 0 is the next up slot.
	 * @return Piece in the bank location.
	 */
	public Piece getBankPiece(int i);
	
	/**
	 * Return whether the current state is a game over
	 */
	public boolean isGameOver();
	
	/**
	 * Return score since last game over.
	 */
	public int getScore();
	
	/**
	 * Return lines cleared since last game over.
	 */
	public int getLinesCleared();
	
	/**
	 * Return Number of back to back tetrises or T-spins. Not reset by placing a piece
	 * only reset by clearing a line that isn't part of a tetris or T-spin.
	 */
	public int getActionCombo();
	
	/**
	 * Return number of back to back line clears. Reset by placing a piece without
	 * clearing a line. Increases by 4 for tetrises and by 3 for T-Spins.
	 */
	public int getLineCombo();
	
	/**
	 * Color enumeration. A list of tetris colors.
	 */
	public enum Color {
		NONE, RED, ORANGE, YELLOW, GREEN, BLUE, CYAN, MAGENTA, GRAY, GHOST;
	}
	
	/**
	 * Piece enumeration. Represents the pieces, their names, their colors, and their different rotations
	 */
	public enum Piece {
		L ("L", Color.ORANGE, new boolean[][][] {
				{{false, true}, {false, true}, {true, true}},
				{{true, true, true}, {false, false, true}},
				{{true, true}, {true, false}, {true, false}},
				{{true, false, false}, {true, true, true}},	
			}, new int[] {1,0,1,1}, new int[] {1,1,0,1}),
		J ("J", Color.BLUE, new boolean[][][] {
				{{true, true}, {false, true}, {false, true}},
				{{true, true, true}, {true, false, false}},
				{{true, false}, {true, false}, {true, true}},
				{{false, false, true}, {true, true, true}},	
			}, new int[] {1,0,1,1}, new int[] {1,1,0,1}),
		T ("T", Color.MAGENTA, new boolean[][][] {
				{{false, true}, {true, true}, {false, true}},
				{{true, true, true}, {false, true, false}},
				{{true, false}, {true, true}, {true, false}},
				{{false, true, false}, {true, true, true}},	
			}, new int[] {1,0,1,1}, new int[] {1,1,0,1}),
		S ("S", Color.GREEN, new boolean[][][] {
				{{false, true}, {true, true}, {true, false}},
				{{true, true, false}, {false, true, true}},
				{{false, true}, {true, true}, {true, false}},
				{{true, true, false}, {false, true, true}},
			}, new int[] {1,0,1,1}, new int[] {1,1,0,1}),
		Z ("Z", Color.RED, new boolean[][][] {
				{{true, false}, {true, true}, {false, true}},
				{{false, true, true}, {true, true, false}},
				{{true, false}, {true, true}, {false, true}},
				{{false, true, true}, {true, true, false}},
			}, new int[] {1,0,1,1}, new int[] {1,1,0,1}),
		I ("I", Color.CYAN, new boolean[][][] {
				{{true}, {true}, {true}, {true}},
				{{true, true, true, true}},
				{{true}, {true}, {true}, {true}},
				{{true, true, true, true}},
			}, new int[] {2,0,1,0}, new int[] {0,2,0,1}),
		O ("O", Color.YELLOW, new boolean[][][] {
				{{true, true}, {true, true}},
				{{true, true}, {true, true}},
				{{true, true}, {true, true}},
				{{true, true}, {true, true}},
			}, new int[] {1,1,1,1}, new int[] {1,1,1,1});
		
		private String name;
		private Color color;
		private int[] xPositions;
		private int[] yPositions;
		private boolean[][][] tiles;
		
		/**
		 * Constructs a piece from all of its rotations in the form of 2 
		 * dimensional boolean arrays of the smallest size and the rotated
		 * positions of each piece.
		 * @param tiles
		 * @param color
		 */
		private Piece(String name, Color color, boolean[][][] tiles, int[] xPositions, int[] yPositions) {
			this.name = name;
			this.tiles = tiles;
			this.xPositions = xPositions;
			this.yPositions = yPositions;
			this.color = color;
		}
		
		/**
		 * Returns the single character that represents this piece.
		 * @return Piece character
		 */
		public String getLetter() {
			return name;
		}
		
		/**
		 * Returns the color of this piece
		 * @return
		 */
		public Color getColor() {
			return color;
		}
		
		/**
		 * Returns the boolean block map for the specified rotation of this piece.
		 * @param rotation - integer number of rotations, 0 is no rotation, values 
		 * below 0 and above 3 will be wrapped back onto the 0-3 range. 
		 * @return boolean array of the smallest size representing this block rotation. [x][y] order
		 */
		public boolean[][] getRotation(int rotation) {
			rotation = Math.floorMod(rotation, 4);
			return tiles[rotation];
		}
		
		/**
		 * Returns the width of the specified rotation of this piece
		 * @param rotation - integer number of rotations, 0 is no rotation, values 
		 * below 0 and above 3 will be wrapped back onto the 0-3 range. 
		 * @return integer width of the piece in the current rotation
		 */
		public int getWidth(int rotation) {
			return getRotation(rotation).length;
		}
		
		/**
		 * Returns the height of the specified rotation of this piece
		 * @param rotation - integer number of rotations, 0 is no rotation, values 
		 * below 0 and above 3 will be wrapped back onto the 0-3 range. 
		 * @return integer height of the piece in the current rotation
		 */
		public int getHeight(int rotation) {
			return getRotation(rotation)[0].length;
		}
		
		/**
		 * Returns the left offset (from the piece position to its left-most tile) of the specified rotation of this piece
		 * @param rotation - integer number of rotations, 0 is no rotation, values 
		 * below 0 and above 3 will be wrapped back onto the 0-3 range. 
		 * @return integer offset from piece position to left-most tile (0 or negative value)
		 */
		public int getLeftOffset(int rotation) {
			return -getCenterX(rotation);
		}
		
		/**
		 * Returns the right offset (from the piece position to its right-most tile) of the specified rotation of this piece
		 * @param rotation - integer number of rotations, 0 is no rotation, values 
		 * below 0 and above 3 will be wrapped back onto the 0-3 range. 
		 * @return integer offset from piece position to right-most tile (0 or positive value)
		 */
		public int getRightOffset(int rotation) {
			return getWidth(rotation) - 1 - getCenterX(rotation);
		}
		
		/**
		 * Returns the top offset (from the piece position to its top-most tile) of the specified rotation of this piece
		 * @param rotation - integer number of rotations, 0 is no rotation, values 
		 * below 0 and above 3 will be wrapped back onto the 0-3 range. 
		 * @return integer offset from piece position to top-most tile (0 or negative value)
		 */
		public int getTopOffset(int rotation) {
			return -getCenterY(rotation);
		}
		
		/**
		 * Returns the bottom offset (from the piece position to its bottom-most tile) of the specified rotation of this piece
		 * @param rotation - integer number of rotations, 0 is no rotation, values 
		 * below 0 and above 3 will be wrapped back onto the 0-3 range. 
		 * @return integer offset from piece position to bottom-most tile (0 or positive value)
		 */
		public int getBottomOffset(int rotation) {
			return getHeight(rotation) - 1 - getCenterY(rotation);
		}
		
		/**
		 * Returns the position in the boolean block map that is the center.
		 * Use for correctly drawing the piece location before and after rotations.
		 * @param rotation - integer number of rotations, 0 is no rotation, values 
		 * below 0 and above 3 will be wrapped back onto the 0-3 range. 
		 * @return
		 */
		public int getCenterX(int rotation) {
			rotation = Math.floorMod(rotation, 4);
			return xPositions[rotation];
		}
		
		/**
		 * Returns the position in the boolean block map that is the center.
		 * Use for correctly drawing the piece location before and after rotations.
		 * @param rotation - integer number of rotations, 0 is no rotation, values 
		 * below 0 and above 3 will be wrapped back onto the 0-3 range. 
		 * @return
		 */
		public int getCenterY(int rotation) {
			rotation = Math.floorMod(rotation, 4);
			return yPositions[rotation];
		}
	}
	
	public static class PiecePos {
		public int x, y, rotation;
		
		public static PiecePos getStartPosition(ITetris tetris) {
			return new PiecePos(tetris.getBoardWidth()/2, 0, 0);
		}
		
		public PiecePos(int x, int y, int rotation) {
			this.x = x; 
			this.y = y; 
			this.rotation = Math.floorMod(rotation, 4);
		}
		
		public PiecePos copy() {
			return new PiecePos(x, y, rotation);
		}
		
		public PiecePos left() {
			this.x--;
			return this;
		}

		public PiecePos right() {
			this.x++;
			return this;
		}
		
		public PiecePos up() {
			this.y--;
			return this;
		}

		public PiecePos down() {
			this.y++;
			return this;
		}
		
		public PiecePos rotateLeft() {
			this.rotation--;
			this.rotation = Math.floorMod(rotation, 4);
			return this;
		}

		public PiecePos rotateRight() {
			this.rotation++;
			this.rotation = Math.floorMod(rotation, 4);
			return this;
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof PiecePos)) return false;
			PiecePos other = (PiecePos) o;
			return this.x == other.x && this.y == other.y && this.rotation == other.rotation;
		}
	}
	
}
