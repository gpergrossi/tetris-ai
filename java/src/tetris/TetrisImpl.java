package tetris;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TetrisImpl implements ITetris {
	
	protected int width, height;
	protected ITetris.Color[][] board;
	protected ITetris.Piece playPiece = null;
	protected ITetris.Piece holdPiece = null;
	
	protected PiecePos playPiecePos;
	
	protected boolean gameOver;
	protected int score;
	protected int linesCleared;
	protected int tetrisCombo;
	protected int lineCombo;
	protected boolean swapUsed;
	
	protected int numBanks = 5;
	protected ITetris.Piece[] banks;
	
	protected List<ITetris.Piece> bag = new ArrayList<ITetris.Piece>();
	
	public TetrisImpl() {
		this.width = 10;
		this.height = 20;
		
		this.bag = new ArrayList<ITetris.Piece>();
		this.board = new ITetris.Color[width][height];
		this.banks = new ITetris.Piece[numBanks];

		reset();
	}


	public void reset() {		
		// Clear the board
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				board[x][y] = Color.NONE;
			}
		}
		
		// Clear the hold
		holdPiece = null;
		
		// Reset the bank
		bag.clear();
		for (int i = 0; i < numBanks; i++) {
			banks[i] = nextBagPiece();
		}
		
		// Reset the score
		score = 0;
		linesCleared = 0;
		tetrisCombo = 0;
		lineCombo = 0;
		gameOver = false;
		
		// Put a piece in play
		putNewPieceInPlay();
	}

	/**
	 * Gets the next piece out of the bank, shifts the bank up, and adds a new piece to the last bank slot
	 */
	protected Piece nextBankPiece() {
		Piece next = banks[0]; 
		for (int i = 0; i < numBanks-1; i++) {
			banks[i] = banks[i+1];
		}
		banks[numBanks-1] = nextBagPiece();
		return next;
	}
	
	/**
	 * Uses the bag drawing method to get a new random piece. This ensures a fair and fun play.
	 */
	protected Piece nextBagPiece() {
		if (bag.isEmpty()) fillBag();
		Piece next = bag.remove(0);
		return next;
	}
	
	/**
	 * Adds one of each type of piece in the bag and shuffles it. Call when the bag is empty.
	 */
	protected void fillBag() {
		Piece[] all = ITetris.Piece.values();
		for (int i = 0; i < all.length; i++) {
			bag.add(all[i]);
		}
		Collections.shuffle(bag);
	}

	/**
	 * Gets a new piece from the bank. Moves the playPiece position and rotation to the top starting position.
	 */
	protected void putNewPieceInPlay() {
		this.playPiece = nextBankPiece();
		this.playPiecePos = PiecePos.getStartPosition(this);
		this.swapUsed = false;
		if (!checkCollisionAndAdjust(playPiecePos)) {
			gameOver = true;
		}
	}
	
	/**
	 * Checks if the piece is allowed on the board in its current position.
	 * Adjusts the X position if the piece hitting the board's left or right wall
	 * @return true if allowed, otherwise false.
	 */
	protected boolean checkCollisionAndAdjust(PiecePos piecePos) {
		boolean[][] pieceSolidity = playPiece.getRotation(piecePos.rotation);

		int originalX = piecePos.x;
		
		boolean shiftedLeft = false;
		int left = piecePos.x + playPiece.getLeftOffset(piecePos.rotation);
		if (left < 0) {
			int adjust = -left;
			piecePos.x += adjust;
			left += adjust;
			shiftedLeft = true;
		} 
		
		boolean shiftedRight = false;
		int right = piecePos.x + playPiece.getRightOffset(piecePos.rotation);
		if (right > width-1) {
			int adjust = right - (width-1);
			piecePos.x -= adjust;
			left -= adjust;
			right -= adjust;
		}
		
		// Too tight to turn? Board should probably be wider, but okay
		if (shiftedLeft && shiftedRight) return false;
		
		int top = piecePos.y + playPiece.getTopOffset(piecePos.rotation);
		int bottom = piecePos.y + playPiece.getBottomOffset(piecePos.rotation);
		if (bottom > height-1) return false;
		
		for (int x = left; x <= right; x++) {
			int pieceX = x - left;
			for (int y = (int) Math.max(top, 0); y <= bottom; y++) {
				int pieceY = y - top;
				if (pieceSolidity[pieceX][pieceY] && board[x][y] != Color.NONE) {
					piecePos.x = originalX;
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Finalizes the current piece's position by adding it to the board.
	 * Checks for game over, line breaks, tetris clears, full clears, and t-spins and updates the score.
	 * Finally, puts a new piece into play if the settle was not a game over.
	 */
	protected void settleCurrentPiece() {
		if (!checkCollisionAndAdjust(playPiecePos)) throw new RuntimeException("Settling failed because the piece would not fit!");
		
		boolean[][] pieceSolidity = playPiece.getRotation(playPiecePos.rotation);
		
		int left = playPiecePos.x + playPiece.getLeftOffset(playPiecePos.rotation);
		int right = playPiecePos.x + playPiece.getRightOffset(playPiecePos.rotation);
		int top = playPiecePos.y + playPiece.getTopOffset(playPiecePos.rotation);
		int bottom = playPiecePos.y + playPiece.getBottomOffset(playPiecePos.rotation);
		
		if (top < 0) {
			gameOver = true;
			return;
		}
		
		boolean isTSpin = false;
		if (playPiece == Piece.T) {
			// T-Spin only valid if the piece cannot move up, left, or right
			// However the left and right conditions are guaranteed by checkScore() finding a line clear
			isTSpin = checkCollisionAndAdjust(playPiecePos.copy().up());
		}
		
		for (int x = left; x <= right; x++) {
			int pieceX = x - left;
			for (int y = (int) Math.max(top, 0); y <= bottom; y++) {
				int pieceY = y - top;
				if (pieceSolidity[pieceX][pieceY]) {
					board[x][y] = playPiece.getColor();
				}
			}
		}
		
		checkScore(isTSpin);
		putNewPieceInPlay();
	}
	
	/**
	 * Look for line breaks, tetris clears, and full board clears
	 */
	protected void checkScore(boolean tspin) {
		int lineClears = 0;
		
		for (int y = height-1; y >= 0; y--) {
			int numFilled = 0;
			for (int x = 0; x < width; x++) {
				Color boardColor = board[x][y]; 
				if (boardColor != Color.NONE && boardColor != Color.GRAY) numFilled++;
			}
			if (numFilled == width) {
				lineClears++;
				for (int y2 = y; y2 >= 1; y2--) {
					for (int x = 0; x < width; x++) {
						board[x][y2] = board[x][y2-1]; 
					}
				}
				y++;
			}
		}
		
		linesCleared += lineClears;
		score += lineClears * (tspin ? 2 : 1) * (tetrisCombo+1) * (lineCombo+1); 
		
		boolean isTetris = (lineClears == 4 || (tspin && lineClears == 2));
		if (isTetris) tetrisCombo++;
		else if (lineClears > 0) tetrisCombo = 0;
		
		if (lineClears > 0) lineCombo += lineClears;
		else lineCombo = 0;
		
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
	public synchronized void updateTick() {
		if (isGameOver()) return;
		this.softDrop();
	}
	
	@Override
	public Color getTile(int x, int y) {
		if(x < 0 || x >= width || y < 0 || y >= height) return ITetris.Color.GRAY;
		
		// Current play piece tiles
		int left = playPiecePos.x + playPiece.getLeftOffset(playPiecePos.rotation);
		int right = playPiecePos.x + playPiece.getRightOffset(playPiecePos.rotation);
		int top = playPiecePos.y + playPiece.getTopOffset(playPiecePos.rotation);
		int bottom = playPiecePos.y + playPiece.getBottomOffset(playPiecePos.rotation);
		if (x >= left && x <= right && y >= top && y <= bottom) {
			boolean[][] pieceSolidity = playPiece.getRotation(playPiecePos.rotation);
			if (pieceSolidity[x - left][y - top]) return playPiece.getColor();
		}
		
		// Ghost tiles
		PiecePos pos = getHardDropPos(playPiecePos);
		top = pos.y + playPiece.getTopOffset(playPiecePos.rotation);
		bottom = pos.y + playPiece.getBottomOffset(playPiecePos.rotation);
		if (x >= left && x <= right && y >= top && y <= bottom) {
			boolean[][] pieceSolidity = playPiece.getRotation(playPiecePos.rotation);
			if (pieceSolidity[x - left][y - top]) return Color.GHOST;
		}
		
		// Everything else
		return board[x][y];
	}

	@Override
	public boolean isTileLocked(int x, int y) {
		if(x < 0 || x >= width || y < 0 || y >= height) return true;
		return board[x][y] != Color.NONE;
	}
	
	@Override
	public boolean canSwap() {
		if (this.isGameOver()) return false;
		if (!this.hasHold()) return false;
		if (swapUsed) return false;
		return true;
	}
	
	@Override
	public synchronized boolean swap() {
		if (this.isGameOver()) return false;
		if (!this.hasHold()) return false;
		if (swapUsed) return false;
		
		ITetris.Piece holdPiece = this.holdPiece;
		this.holdPiece = playPiece;
		this.playPiece = holdPiece;
		
		if (this.playPiece == null) this.playPiece = nextBankPiece();
		this.playPiecePos = PiecePos.getStartPosition(this);
		
		swapUsed = true;
		return true;
	}
	
	@Override
	public synchronized boolean rotateRight() {
		if (this.isGameOver()) return false;
		
		PiecePos move = playPiecePos.copy().rotateRight();
		
		boolean canMove = checkCollisionAndAdjust(move);
		if (!canMove) return false;
		if (move.equals(playPiecePos)) return false;
		
		playPiecePos = move;
		return true;
	}

	@Override
	public synchronized boolean rotateLeft() {
		if (this.isGameOver()) return false;
		
		PiecePos move = playPiecePos.copy().rotateLeft();
		
		boolean canMove = checkCollisionAndAdjust(move);
		if (!canMove) return false;
		if (move.equals(playPiecePos)) return false;
		
		playPiecePos = move;
		return true;
	}

	@Override
	public synchronized boolean softDrop() {
		if (this.isGameOver()) return false;
		
		PiecePos move = playPiecePos.copy().down();
		
		boolean canMove = checkCollisionAndAdjust(move);
		if (!canMove || move.equals(playPiecePos)) {
			settleCurrentPiece();
			return true;
		}
		
		playPiecePos = move;
		return true;
	}

	@Override
	public synchronized boolean hardDrop() {
		if (this.isGameOver()) return false;
		
		playPiecePos = getHardDropPos(playPiecePos);
		settleCurrentPiece();
		return true;
	}

	private PiecePos getHardDropPos(PiecePos initialPos) {
		PiecePos move = initialPos.copy();
		do {
			move.down();
		} while (checkCollisionAndAdjust(move));
		move.up();
		
		return move;
	}


	@Override
	public synchronized boolean moveLeft() {
		if (this.isGameOver()) return false;
		
		PiecePos move = playPiecePos.copy().left();
		
		boolean canMove = checkCollisionAndAdjust(move);
		if (!canMove) return false;
		if (move.equals(playPiecePos)) return false;
		
		playPiecePos = move;
		return true;
	}

	@Override
	public synchronized boolean moveRight() {
		if (this.isGameOver()) return false;
		
		PiecePos move = playPiecePos.copy().right();
		
		boolean canMove = checkCollisionAndAdjust(move);
		if (!canMove) return false;
		if (move.equals(playPiecePos)) return false;
		
		playPiecePos = move;
		return true;
	}

	@Override
	public synchronized boolean restart() {
		reset();
		return true;
	}
	
	@Override
	public Piece getPlayPiece() {
		return playPiece;
	}

	@Override
	public PiecePos getPlayPiecePos() {
		return playPiecePos.copy();
	}

	@Override
	public boolean hasHold() {
		return true;
	}

	@Override
	public Piece getHoldPiece() {
		return this.holdPiece;
	}

	@Override
	public int getNumBanks() {
		return numBanks;
	}

	@Override
	public Piece getBankPiece(int i) {
		return banks[i];
	}

	@Override
	public boolean isGameOver() {
		return gameOver;
	}

	@Override
	public int getLinesCleared() {
		return linesCleared;
	}
	
	@Override
	public int getScore() {
		return score;
	}

	@Override
	public int getActionCombo() {
		return tetrisCombo;
	}

	@Override
	public int getLineCombo() {
		return lineCombo;
	}
	
}
