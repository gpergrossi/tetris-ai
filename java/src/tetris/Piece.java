package tetris;
import java.awt.Point;

/**
 * Pieces for a tetris game, each piece is defined by a three
 * dimensional boolean array containing 4 2 dimensional boolean
 * arrays that describe the piece at each rotation. They also have
 * two arrays for the center position of each piece and one color
 * specifier from the Tile class for placing them on the board.
 * This class is unwieldy and should really only be used with this 
 * project. 
 * @author Gregary Pergrossi (gpergrossi@imsa.edu)
 *
 */
public class Piece {

	public static Piece LPiece;
	public static Piece JPiece;
	public static Piece TPiece;
	public static Piece SPiece;
	public static Piece ZPiece;
	public static Piece IPiece;
	public static Piece OPiece;
	
	static {
		LPiece = new Piece("L", new boolean[][][] {
			{{false, true}, {false, true}, {true, true}},
			{{true, true, true}, {false, false, true}},
			{{true, true}, {true, false}, {true, false}},
			{{true, false, false}, {true, true, true}},	
		}, new int[] {1,0,1,1}, new int[] {1,1,0,1}, TetrisColor.orange);
		JPiece = new Piece("J", new boolean[][][] {
			{{true, true}, {false, true}, {false, true}},
			{{true, true, true}, {true, false, false}},
			{{true, false}, {true, false}, {true, true}},
			{{false, false, true}, {true, true, true}},	
		}, new int[] {1,0,1,1}, new int[] {1,1,0,1}, TetrisColor.blue);
		TPiece = new Piece("T", new boolean[][][] {
			{{false, true}, {true, true}, {false, true}},
			{{true, true, true}, {false, true, false}},
			{{true, false}, {true, true}, {true, false}},
			{{false, true, false}, {true, true, true}},	
		}, new int[] {1,0,1,1}, new int[] {1,1,0,1}, TetrisColor.magenta);
		SPiece = new Piece("S", new boolean[][][] {
			{{false, true}, {true, true}, {true, false}},
			{{true, true, false}, {false, true, true}},
			{{false, true}, {true, true}, {true, false}},
			{{true, true, false}, {false, true, true}},
		}, new int[] {1,0,1,1}, new int[] {1,1,0,1}, TetrisColor.green);
		ZPiece = new Piece("Z", new boolean[][][] {
			{{true, false}, {true, true}, {false, true}},
			{{false, true, true}, {true, true, false}},
			{{true, false}, {true, true}, {false, true}},
			{{false, true, true}, {true, true, false}},
		}, new int[] {1,0,1,1}, new int[] {1,1,0,1}, TetrisColor.red);
		IPiece = new Piece("I", new boolean[][][] {
			{{true}, {true}, {true}, {true}},
			{{true, true, true, true}},
			{{true}, {true}, {true}, {true}},
			{{true, true, true, true}},
		}, new int[] {2,0,1,0}, new int[] {0,2,0,1}, TetrisColor.cyan);
		OPiece = new Piece("O", new boolean[][][] {
			{{true, true}, {true, true}},
			{{true, true}, {true, true}},
			{{true, true}, {true, true}},
			{{true, true}, {true, true}},
		}, new int[] {1,1,1,1}, new int[] {1,1,1,1}, TetrisColor.yellow);
	}
	
	private String name;
	private int[] xPositions;
	private int[] yPositions;
	private boolean[][][] tiles;
	private TetrisColor color;
	
	/**
	 * Constructs a piece from all of its rotations in the form of 2 
	 * dimensional boolean arrays of the smallest size and the rotated
	 * positions of each piece.
	 * @param tiles
	 * @param color
	 */
	private Piece(String name, boolean[][][] tiles, int[] xPositions, int[] yPositions, TetrisColor color) {
		this.name = name;
		this.tiles = tiles;
		this.xPositions = xPositions;
		this.yPositions = yPositions;
		this.color = color;
	}
	
	/**
	 * Returns the boolean block map for the rotation specified of this piece.
	 * @param rotation integer number of rotations, 0 is no rotation
	 * @return boolean array of the smallest size representing this block rotation.
	 */
	public boolean[][] getRotation(int rotation) {
		return tiles[rotation];
	}
	
	public Point getCenterPosition(int rotation) {
		return new Point(xPositions[rotation], yPositions[rotation]);
	}
	
	/**
	 * Returns the single character that represents this piece.
	 * @return Piece character
	 */
	public String getName() {
		return name;
	}
	
	public TetrisColor getColor() {
		return color;
	}
	
}
