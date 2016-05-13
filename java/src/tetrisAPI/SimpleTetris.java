package tetrisAPI;

import java.util.ArrayList;
import java.util.Arrays;


public class SimpleTetris implements TetrisAPI {

	private int width, height;
	private TetrisAPI.Color[][] board;
	private TetrisAPI.Piece piece = null;
	private TetrisAPI.Piece hold = null;
	private TetrisAPI.Piece[] bank;
	private ArrayList<TetrisAPI.Piece> bag = new ArrayList<TetrisAPI.Piece>();
	
	public SimpleTetris() {
		this.width = 10;
		this.height = 20;
		
		this.bag = new ArrayList<TetrisAPI.Piece>();
		
		this.board = new TetrisAPI.Color[width][height];
		Arrays.fill(board, TetrisAPI.Color.NONE);
		this.bank = new TetrisAPI.Piece[5];
		
	}
	
	@Override
	public int getBoardWidth() {
		return this.width;
	}

	@Override
	public int getBoardHeight() {
		return this.height;
	}

	@Override
	public Color getTile(int x, int y) {
		if(x < 0 || x >= width || y < 0 || y >= height) return TetrisAPI.Color.GRAY;
		return board[x][y];
	}

	@Override
	public boolean swap() {
		return false;
	}

	@Override
	public boolean rotateRight() {
		return false;
	}

	@Override
	public boolean rotateLeft() {
		return false;
	}

	@Override
	public boolean softDrop() {
		return false;
	}

	@Override
	public boolean hardDrop() {
		return false;
	}

	@Override
	public boolean moveLeft() {
		return false;
	}

	@Override
	public boolean moveRight() {
		return false;
	}

	@Override
	public Piece getPiece() {
		return null;
	}

	@Override
	public int getPiecePositionX() {
		return 0;
	}

	@Override
	public int getPiecePositionY() {
		return 0;
	}

	@Override
	public Piece getPieceRotation() {
		return null;
	}

	@Override
	public boolean hasHold() {
		return false;
	}

	@Override
	public Piece getHoldPiece() {
		return null;
	}

	@Override
	public int getNumBanks() {
		return 0;
	}

	@Override
	public Piece getBankPiece(int i) {
		return null;
	}

	@Override
	public boolean isGameOver() {
		return false;
	}

	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public int getTetrisCombo() {
		return 0;
	}

	@Override
	public int getLineCombo() {
		return 0;
	}
	
	private void fillBag() {
		if(bag == null) 
	}
	
}
