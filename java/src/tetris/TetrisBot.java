package tetris;

import tetris.ITetris.Color;
import tetris.ITetris.Piece;
import tetris.ITetris.PiecePos;

/**
 * <p>A sweet, although not perfect by any means, Tetris
 * bot I coded. It's pretty skilled usually but has its 
 * quirks. The placement system is a calculation of the best
 * position to put the current piece or the piece in the hold
 * based on variables. See the calculateBoardScore() method.</p>
 * 
 * @author Gregary Pergrossi
 */
public class TetrisBot {
		
	private ITetris tetris;
	
	int scoreAdjTiles = 100;	// Score for each adjacent game piece
	int scoreAdjWalls = 100;	// Score for adjacent walls and the bottom
	int scoreHeight = -100;		// Score for each additional height
	int scoreShadow = -1000;	// Score for each empty space covered
	int scoreBubble = -100;		// Score for each empty space covered
	int scoreRowClear = 500;	// Score for completing a row
	int scoreTetris = 900000;	// Score for making a tetris
	int scorePerfect = 1200;	// Score for clearing the entire new block by completing rows
	int scoreCliffs = -200;		// Score for each cliff depth created more than 2;
	
	ScoredPos bestPlacement;
	
	/**
	 * Creates a new bot to play the tetris board given 
	 * @param tetris
	 */
	public TetrisBot(ITetris tetris) {
		this.tetris = tetris;
	}

	protected static class ScoredPos extends PiecePos {
		public int score, linesCleared;
		public int cliffLeft, cliffRight, shadows, bubbles, totalHeight, adjTiles, adjWalls;
		
		public ScoredPos(int x, int y, int rotation) {
			super(x, y, rotation);
		}
		
	}
	
	protected static enum Move {
		NONE, SWAP, SOFT_DROP, HARD_DROP, ROTATE, LEFT, RIGHT, RESTART;
	}
	
	/**
	 * Checks the board state read by update() for a manipulatable piece.
	 * Once found, the best position is calculated and the piece is moved
	 * to that location.
	 */
	public Move getBestMove() {
		if (tetris.isGameOver()) return Move.RESTART;

		PiecePos pos = tetris.getPlayPiecePos();
		
		if (bestPlacement == null) {
			bestPlacement = getBestPlacement(tetris.getPlayPiece(), pos);
			
			Piece holdPiece = tetris.getHoldPiece();
			if (holdPiece == null && tetris.canSwap()) holdPiece = tetris.getBankPiece(0);
			ScoredPos scoredHoldPos = getBestPlacement(holdPiece, PiecePos.getStartPosition(tetris));
			
			if (scoredHoldPos.score > bestPlacement.score && tetris.canSwap()) {
				bestPlacement = scoredHoldPos;
				return Move.SWAP;
			}
		}
		
//		ScoredPos p = bestPlacement;
//		System.out.print("Recommended "+p.x+","+p.y+":"+p.rotation+" with score "+p.score);
//		System.out.print(", cliffLeft="+p.cliffLeft+", cliffRight="+p.cliffRight);
//		System.out.print(", shadows="+p.shadows+", bubbles="+p.bubbles+", totalHeight="+p.totalHeight);
//		System.out.print(", adjTiles="+p.adjTiles+", adjWalls="+p.adjWalls);
//		System.out.println();
		
		if (pos.rotation != bestPlacement.rotation) {
			return Move.ROTATE;
		}
		
		if (pos.x < bestPlacement.x) {
			return Move.RIGHT;
		}
		
		if (pos.x > bestPlacement.x) {
			return Move.LEFT;
		}

		bestPlacement = null;
		return Move.HARD_DROP;
	}

	public void play() {
		Move move = getBestMove();
		switch (move) {
			case SWAP: tetris.swap(); return;
			case SOFT_DROP: tetris.softDrop(); return;
			case HARD_DROP: tetris.hardDrop(); return;
			case LEFT: tetris.moveLeft(); return;
			case RIGHT: tetris.moveRight(); return;
			case ROTATE: tetris.rotateRight(); return;
			case RESTART: tetris.restart();	return;
			case NONE:
			default: return;
		}
	}
	
	/**
	 * Attempts to check and score each possible position for playing the given piece
	 * on the given board. This is done by scoring each possible rotation and position 
	 * of the given piece.
	 * @param piece to be scored
	 */
	private ScoredPos getBestPlacement(Piece piece, PiecePos currentPos) {
		ScoredPos bestPos = new ScoredPos(0, 0, 0);
		bestPos.score = Integer.MIN_VALUE;
		if (piece == null) return bestPos;
		
		for (int r = 0; r < 4; r++) {
			for (int x = 0; x < tetris.getBoardWidth(); x++) {
				if (x + piece.getLeftOffset(r) < 0) continue;
				if (x + piece.getRightOffset(r) > tetris.getBoardWidth()-1) continue;
				
				PiecePos droppedPos = dropPiece(tetris, piece, new PiecePos(x, currentPos.y, r));
				if (droppedPos == null) continue;
				
				// Check board score
				ScoredPos boardScorePos = calculateBoardScore(tetris, piece, droppedPos);					
				if (boardScorePos.score > bestPos.score) bestPos = boardScorePos;
			}
		}

		return bestPos;
	}

	/**
	 * Takes a scoring board integer array using 2's to represent the falling piece
	 * and 1'2 and 3's to represent other blocks and the walls, respectively.
	 * This method will drop the falling piece downward until it collides with a
	 * solid piece. Directly modifies the given array.
	 * @param scoringboard - integer array representing the board state with the
	 * piece to be dropped
	 */
	private static PiecePos dropPiece(ITetris tetris, Piece piece, PiecePos pos) {
		if (!canPieceStay(tetris, piece, pos)) return null;
		
		PiecePos dropped = pos.copy();
		do {
			dropped.down();
		} while (canPieceStay(tetris, piece, dropped));		
		dropped.up();
		
		return dropped;
	}
	
	private static boolean canPieceStay(ITetris tetris, Piece piece, PiecePos pos) {
		boolean[][] pieceSolidity = piece.getRotation(pos.rotation);
		
		int left = pos.x + piece.getLeftOffset(pos.rotation);
		if (left < 0) return false;
		
		int right = pos.x + piece.getRightOffset(pos.rotation);
		if (right > tetris.getBoardWidth()-1) return false;
		
		int top = pos.y + piece.getTopOffset(pos.rotation);
		int bottom = pos.y + piece.getBottomOffset(pos.rotation);
		if (bottom > tetris.getBoardHeight()-1) return false;
		
		for (int x = left; x <= right; x++) {
			int pieceX = x - left;
			for (int y = (int) Math.max(top, 0); y <= bottom; y++) {
				int pieceY = y - top;
				if (!pieceSolidity[pieceX][pieceY]) continue;
				if (tetris.isTileLocked(x, y) && tetris.getTile(x, y) != Color.NONE) return false;
			}
		}
		
		return true;
	}

	/**
	 * Scores the placement of a piece, represented by twos in an integer array
	 * on a board with old pieces represented by ones and walls represented by 
	 * threes. The scoring is based on many tunable factors.
	 * @param scoringboard - integer array representing the state of the piece
	 * placement to be graded.
	 * @return int array containing {score, rowsCleared} for use in determining combos
	 * and grading decisions
	 */
	private ScoredPos calculateBoardScore(ITetris board, Piece piece, PiecePos pos) {
		
		// Note: more scoring heuristics exist in the getBestPlacement method including:
		//	 subtracting 100 points for putting pieces in the first row when not breaking a tetris
		//	 subtracting 400 points for using a line piece when no more line pieces are in the bank

		int scoreAdjTiles = this.scoreAdjTiles;
		int scoreAdjWalls = this.scoreAdjWalls;
		int scoreHeight = this.scoreHeight;
		int scoreShadow = this.scoreShadow;
		int scoreBubble = this.scoreBubble;
		int scoreRowClear = this.scoreRowClear;
		int scoreTetris = this.scoreTetris;
		int scorePerfect = this.scorePerfect;
		int scoreCliffs = this.scoreCliffs;
		
		int score = 0;		
		scoreRowClear -= 100 * board.getActionCombo();
		
		int bubbles = 0;
		int highestFill = 0;
		boolean tetrisPossible = true;
		int[] highestTile = new int[board.getBoardWidth()];
		boolean[] rowCleared = new boolean[board.getBoardHeight()];
		int rowsCleared = 0;
		int lowestPieceHeight = board.getBoardHeight();
		int totalPieceHeight = 0;
		int adjTiles = 0;
		int adjWalls = 0;
		
		for (int j = 0; j < board.getBoardHeight(); j++) {
			int numFilled = 0;			
			for (int i = 0; i < board.getBoardWidth(); i++) {
				int tileHeight = board.getBoardHeight() - j;
				
				boolean isPiece = isPiece(piece, pos, i, j);
				
				Color color = board.getTile(i, j);
				boolean isTile = (board.isTileLocked(i, j) && color != Color.NONE && color != Color.GRAY && color != Color.GHOST);
				
				if (isPiece || isTile) {
					numFilled++;
					highestTile[i] = Math.max(highestTile[i], tileHeight);
				} else if (tileHeight < highestTile[i]) {
					bubbles++;
				}
				
				if (!isPiece) continue;
				
				lowestPieceHeight = Math.min(lowestPieceHeight, tileHeight);
				totalPieceHeight += tileHeight;
				
				// Add points for adjacent tiles
				if (i < board.getBoardWidth()-1 && board.isTileLocked(i+1, j)) adjTiles++;
				if (i > 0 && board.isTileLocked(i-1, j)) adjTiles++;
				if (j < board.getBoardHeight()-1 && board.isTileLocked(i, j+1)) adjTiles++;
				if (j > 0 && board.isTileLocked(i, j-1)) adjTiles++;

				// Add points for adjacent walls
				if (j == 0) adjWalls++;
				if (i == board.getBoardWidth()-1) adjWalls++;
				if (j == board.getBoardHeight()-1) adjWalls++;
			}
			if (numFilled == board.getBoardHeight()) {
				rowCleared[j] = true;
				rowsCleared++;
			}
		}
		
		for (int i = 0; i < board.getBoardWidth(); i++) {
			if (highestTile[i] < 4 && i > 0) {
				tetrisPossible = false;
			}
			highestFill = Math.max(highestFill, highestTile[i]);
		}
		
		int borderLeft = (tetrisPossible ? 0 : 1);
		{
			int i = borderLeft;
			for (int j = 0; j < board.getBoardHeight(); j++) {
				boolean isPiece = isPiece(piece, pos, i, j);
				if (!isPiece) continue;
				
				// Adjacent to movable wall (tetris combo enforcer)
				adjWalls++;
			}
		}
		
		int top = pos.y + piece.getTopOffset(pos.rotation);
		int bottom = pos.y + piece.getBottomOffset(pos.rotation);
		int left = pos.x + piece.getLeftOffset(pos.rotation);
		int right = pos.x + piece.getRightOffset(pos.rotation);
		
		// Discourage filling the left column before a tetris is available
		if (!tetrisPossible && left < 1) score -= 10000;
		
		// Bubble popper case
		if (bubbles > 10) {
			scoreShadow = -60000;
			scoreRowClear = 6000;
		}
		
		// Combo case
		if (board.getLineCombo() > 0) {
			int combo = board.getLineCombo();
			int actionCombo = board.getActionCombo();
			
			scoreRowClear = 400*combo - 2400*actionCombo;
			scorePerfect = scorePerfect - 1200*actionCombo;
		}
		
		// Line breaking case (critical height)
		if (highestFill >= 12 && !isSoon(5, Piece.I)) {
			scoreHeight = -5000;
			scoreRowClear = 6000;
			scoreShadow = -800;
			scoreAdjWalls = 0;
			scoreAdjTiles = 10;
			scorePerfect = 100000;
		}

		score += adjTiles * scoreAdjTiles;
		score += adjWalls * scoreAdjWalls;
		
		if (rowsCleared == 1) score += scoreRowClear/2;
		else if (rowsCleared > 1 && rowsCleared < 4) score += rowsCleared*scoreRowClear;
		else if (rowsCleared == 4) {
			score += scoreTetris;
		}
		
		// Award lots of points for destroying the entire block with row clears
		boolean noneLeft = true;
		if (top < 0) top = 0;
		for (int j = top; j <= bottom; j++) {
			if (!rowCleared[j]) {
				noneLeft = false;
				break;
			}
		}
		if (noneLeft) score += scorePerfect;
		
		// Subtract points for not saving line pieces
		if (piece == Piece.I && !isSoon(5, Piece.I)) score -= 300;
		
		// Subtract points for 'cliffs'
		int cliffLeft = 0, cliffRight = 0;
		if (left > borderLeft) cliffLeft = highestTile[left] - highestTile[left-1];
		cliffLeft = Math.max(cliffLeft-1, -1);
		if (right < board.getBoardWidth()-1) cliffRight = highestTile[right] - highestTile[right+1];
		cliffRight = Math.max(cliffRight-1, -1);
		score += cliffLeft*scoreCliffs;
		score += cliffRight*scoreCliffs;
		
		// Subtract points for height
		score += totalPieceHeight*scoreHeight;
		
		// Subtract points for shadows
		int shadows = 0;
		bubbles = 0;
		for (int i = left; i <= right; i++) {
			for (int j = top; j <= bottom; j++) {
				if (!isPiece(piece, pos, i, j)) continue;
				boolean solidTileFound = false;
				for (int s = j+1; s < board.getBoardHeight(); s++) {
					if (isPiece(piece, pos, i, s)) break;
					if (board.isTileLocked(i, s)) {
						solidTileFound = true;
					} else {
						if (!solidTileFound) shadows++;
						else bubbles++;
					}
				}
			}
		}
		score += shadows * scoreShadow;
		score += bubbles * scoreBubble;
		
		ScoredPos result = new ScoredPos(pos.x, pos.y, pos.rotation);
		result.score = score;
		result.linesCleared = rowsCleared;
		result.totalHeight = totalPieceHeight;
		result.cliffLeft = cliffLeft;
		result.cliffRight = cliffRight;
		result.shadows = shadows;
		result.bubbles = bubbles;
		result.adjTiles = adjTiles;
		result.adjWalls = adjWalls;
		
		return result;
	}

	private static boolean isPiece(Piece piece, PiecePos pos, int x, int y) {
		int left = pos.x + piece.getLeftOffset(pos.rotation);
		int right = pos.x + piece.getRightOffset(pos.rotation);
		int top = pos.y + piece.getTopOffset(pos.rotation);
		int bottom = pos.y + piece.getBottomOffset(pos.rotation);
		if (x >= left && x <= right && y >= top && y <= bottom) {
			boolean[][] pieceSolidity = piece.getRotation(pos.rotation);
			if (pieceSolidity[x - left][y - top]) return true;
		}
		return false;
	}

	/**
	 * Checks each of the bank locations for a I piece
	 * @return true if there is an I piece in the bank, else false
	 */
	public boolean isSoon(int firstN, Piece piece) {
		if (tetris.hasHold() && tetris.getHoldPiece() == piece) return true;
		for (int b = 0; b < tetris.getNumBanks() && b < firstN; b++) {
			if (tetris.getBankPiece(b) == piece) return true;
		}
		return false;
	}
	
}
