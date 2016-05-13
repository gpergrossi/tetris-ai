package tetrisAPI;

/**
 * Tetris API. This is an API for a tetris game. No implementation details included
 * only aspects of the game such as the pieces, the colors, 
 * @author Gregary Pergrossi (gpergros@hawk.iit.edu)
 */
public interface TetrisAPI {

	/**
	 * Returns the board's width, specifying that x coordinates 0 to width-1 (inclusive) are valid.
	 */
	public int getBoardWidth();
	
	/**
	 * Returns the board's height, specifying that y coordinates 0 to height-1 (inclusive) are valid.
	 */
	public int getBoardHeight();

	/**
	 * Returns the tile at the position. Must be a valid coordinate as specified by getBoardWidth() and getBoardHeight();
	 */
	public Color getTile(int x, int y);
	
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
	 * Returns the piece currently in play.
	 */
	public Piece getPiece();
	
	/**
	 * Returns the center position of the piece in play. This position
	 * is guaranteed to be part of the in-play tetris piece and is based
	 * on the center position defined for the piece and its current rotation
	 * in the Piece enumeration.
	 */
	public int getPiecePositionX();
	
	/**
	 * Returns the center position of the piece in play. This position
	 * is guaranteed to be part of the in-play tetris piece and is based
	 * on the center position defined for the piece and its current rotation
	 * in the Piece enumeration.
	 */
	public int getPiecePositionY();
	
	/**
	 * Returns the rotation of the piece in play. This is a value from 0 to 3 inclusive.
	 * The default rotation is 0 and each increase of value is a 90 degree rotation.
	 */
	public Piece getPieceRotation();
	
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
	 * Gets the piece in the bank indexed by i or null if the index is invalid.
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
	 * Return Number of back to back tetrises. Not reset by placing a piece
	 * only reset by clearing a line that isn't part of a tetris. T-Spins count
	 * as a tetris for purposes of score and this combo count.
	 */
	public int getTetrisCombo();
	
	/**
	 * Return number of back to back line clears. Reset by placing a piece without
	 * clearing a line. Increases by 4 for tetrises and by 3 for T-Spins.
	 */
	public int getLineCombo();
	
	/**
	 * Color enumeration. A list of tetris colors.
	 */
	public enum Color {
		NONE, RED, ORANGE, YELLOW, GREEN, BLUE, CYAN, MAGENTA, GRAY;
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
		Piece(String name, Color color, boolean[][][] tiles, int[] xPositions, int[] yPositions) {
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
		public String getName() {
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
		 * Returns the boolean block map for the rotation specified of this piece.
		 * @param rotation integer number of rotations, 0 is no rotation
		 * @return boolean array of the smallest size representing this block rotation.
		 */
		public boolean[][] getRotation(int rotation) {
			return tiles[rotation];
		}
		
		/**
		 * Returns the position in the boolean block map that is the center.
		 * Use primarily for rotations.
		 * @param rotation - rotation index from 0 to 3
		 * @return
		 */
		public int getCenterPositionX(int rotation) {
			return xPositions[rotation];
		}
		
		/**
		 * Returns the position in the boolean block map that is the center.
		 * Use primarily for rotations.
		 * @param rotation - rotation index from 0 to 3
		 * @return
		 */
		public int getCenterPositionY(int rotation) {
			return yPositions[rotation];
		}
	}
	
}
