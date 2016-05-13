package TetrisGame;

import tetris.Piece;
import tetris.PieceBank;
import tetris.Tetris;
import tetris.TetrisColor;
import tetris.Tile;


/**
 * <p>A sweet, although not perfect by any means, Tetris
 * bot I coded. It's pretty skilled usually but has its 
 * quirks. The placement system is a calculation of the best
 * position to put the current piece or the piece in the hold
 * based on variables. See the calculateBoardScore().</p><p>
 * If this interests you and you would like to know more, 
 * you can email me, but I'm lazy when it comes to checking 
 * my mail and I may not notice.</p>
 * 
 * @author Gregary Pergrossi (gpergrossi@imsa.edu)
 */
public class Bot {
	
	public long waitTime = 10;
	
	private Tetris tetris;
	private boolean alive = false;		
	private Tile[][] board;			//A tile array describing the board, higher rows have lower y values
	private int fillCount = 0;		//Number of tiles filled out of 200
	private int highestFill = 0;	//Tallest column height
	private int stones = 0;			//Number of rows of stones at the bottom
	private int bubbles = 0;		//Number of empty spaces covered by solid tiles
	private int combo = 0;			//Number of lines broken in a row
	private int tetrisCombo = 0;	//Number of tetrises broken in a row
	private Piece currentPiece;		//Piece currently being manipulated
	
	public boolean playing = true;
	public Thread thread;
	
	/**
	 * Creates a new bot to play the tetris board given 
	 * @param tetris
	 */
	public Bot(Tetris tetris, int pauseAmount) {
		this.tetris = tetris;
		waitTime = pauseAmount;
	}
	
	/**
	 * Checks for the recognizable tetris game using the guide image. If the
	 * game's location is identified, the bot initializes and begins running.
	 */
	public void start() {
		alive = true;
		board = new Tile[12][22];
		for(int x = 0; x < 12; x++) {
			for(int y = 0; y < 22; y++) {
				board[x][y] = new Tile(TetrisColor.none, false);
				if(x == 0 || x == 11 || y == 21) {
					board[x][y] = new Tile(TetrisColor.gray, false);
				}
			}
		}
		
		update();
		
		this.playing = true;
		thread = new Thread(new Runnable() {
			public void run() {
				while (playing && alive) {
					update();
					play(tetris.getCurrentPiece());
					Thread.yield();
					try {
						Thread.sleep(waitTime);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}
	
	/**
	 * Stops the bot causing it to ignore play() calls
	 */
	public void stop() {
		alive = false;
		playing = false;
	}
	
	/**
	 * Updates the board state in the bots memory, this includes rescanning
	 * the entire board and updating the fillCount, highestFill, and stones
	 * variables.
	 */
	public void update() {
		fillCount = 0;
		highestFill = 0;
		stones = 0;
		for(int y = 0; y < 20; y++) {
			for(int x = 0; x < 10; x++) {
				board[x+1][y+1] = tetris.getBoard()[x+1][y+4];
				if(board[x+1][y+1].getColor() != TetrisColor.none) {
					fillCount++;
					if(!board[x+1][y+1].isControlled()) {
						if(highestFill == 0) highestFill = 20-y;
					}
				}
			}
			if(board[1][y+1].getColor() == TetrisColor.gray) {
				stones++;
			}
		}
		bubbles = 0;
		for(int x = 1; x <= 10; x++) {
			boolean topFound = false;
			for(int y = 1; y <= 20; y++) {
				if(board[x][y].getColor() != TetrisColor.none && !board[x][y].isControlled()) {
					topFound = true;
				}
				if(board[x][y].getColor() == TetrisColor.none && topFound) {
					bubbles++;
				}
			}
		}
	}
	
	/**
	 * Returns the block color of the piece in each bank slot.
	 * 0 is the hold, 1-5 are the incoming pieces. This
	 * is calculated anew each time this method is called.
	 * @param slot number to check
	 * @return TetrisColor of the piece in the specified location
	 */
	public Piece getBank(int slot) {
		PieceBank[] banks = tetris.getBanks();
		if(slot == 0) {
			Piece p = banks[0].getPiece();
			if(p == null) {
				return banks[1].getPiece();
			} else {
				return p;
			}
		}
		if(slot == 1) return banks[1].getPiece();
		if(slot == 2) return banks[2].getPiece();
		if(slot == 3) return banks[3].getPiece();
		if(slot == 4) return banks[4].getPiece();
		if(slot == 5) return banks[5].getPiece();
		return null;
	}

	/**
	 * Checks the board state read by update() for a manipulatable piece.
	 * Once found, the best position is calculated and the piece is moved
	 * to that location.
	 */
	public void play(Piece inPlay) {
		if(!alive) return;
		update();
		currentPiece = inPlay;
		if(inPlay != null) {
			int[] placement;
			int[] placement1 = getBestPlacement(inPlay, board);
			int[] placement2 = getBestPlacement(getBank(0), board);
			if(placement1[0] > placement2[0]) {
				placement = placement1;
			} else {
				swap();
				placement = placement2;
			}
			int bestRotation = placement[1];
			int bestPosition = placement[2];
			int position = 6-currentPiece.getCenterPosition(bestRotation).x;
			
			//rotate
			rotate(bestRotation);
			
			//move the rest of the way
			move(bestPosition-position);
			
			//drop
			drop();

			//System.out.println("Recomended "+placement[1]+" rotation in column "+placement[2]+" with score "+placement[0]);
			//return;
		}
	}
	
	/**
	 * Rotates the current piece by the amount specified.
	 * Rotation is done by pressing and releasing the up
	 * key the appropriate number of times.
	 * @param amount
	 */
	private void rotate(int amount) {
		if(amount < 0) amount += 4;
		if(amount > 0) {
			for(int i = 0; i < amount; i++) {
				tetris.rotate();
				pause(waitTime);
			}
			return;
		}
	}

	/**
	 * Hard drops the current piece by pressing space.
	 */
	private void drop() {
		tetris.hardDrop();
		pause(waitTime);
	}
	
	/**
	 * Moves the current piece by the amount specified by pressing
	 * and releasing the left and right keys the appropriate number
	 * of times.
	 * @param amount
	 */
	private void move(int amount) {
		if(amount > 0) {
			for(int i = 0; i < amount; i++) {
				tetris.moveRight();
				pause(waitTime);
			}
			return;
		}
		if(amount < 0) {
			amount = -amount;
			for(int i = 0; i < amount; i++) {
				tetris.moveLeft();
				pause(waitTime);
			}
			return;
		}
	}
	
	/**
	 * Swaps the current piece with the piece in the hold. Doesn't 
	 * notify or follow up if the swap was impossible, be careful.
	 */
	public void swap() {
		Piece held = getBank(0);
		currentPiece = held;
		tetris.swap();
		pause(waitTime);
	}
	
	/**
	 * Pauses the current thread for the time supplied
	 * @param time
	 */
	private static void pause(long time) {
		long start = System.currentTimeMillis();
		while(System.currentTimeMillis()-start < time) {
			Thread.yield();
		}
	}

	/**
	 * Attempts to check and score each possible position for playing the given piece
	 * on the given board. This is done by scoring each possible rotation and position 
	 * of the given piece.
	 * @param piece to be scored
	 * @param board to be scored in
	 * @return integer array containing the information {score, ideal rotation, ideal position, lines cleared}
	 */
	private int[] getBestPlacement(Piece piece, Tile[][] board) {
		if(piece != null) {
			int topScore = -500000;
			int bestR = 0;
			int bestP = 1;
			int bestCombo = 0;
			int bestTetris = 0;
			int[][] bestScoreBoard = new int[12][22];
			boolean bottomFilled = false;
			int above4Count = 0;
			for(int x = 2; x <= 10; x++) {
				for(int y = 17-stones; y >= 1; y--) {
					if(board[x][y].getColor() != TetrisColor.none && !board[x][y].isControlled()) {
						above4Count++;
						break;
					}
				}
			}
			if(above4Count == 9) bottomFilled = true;
			for(int r = 0; r < 4; r++) {
				//Get piece rotation
				boolean[][] pieceMap = piece.getRotation(r);
				int width = pieceMap.length;
				int height = pieceMap[0].length;
				//Create board
				int[][] scoringboard = new int[12][22];
				//Check each position
				for(int x = 1; x <= 11-width; x++) {
					//clear scoring board
		 	 	 	for(int i = 0; i < 12; i++) {
						for(int j = 0; j < 22; j++) {
							scoringboard[i][j] = 0;
							if(board[i][j].getColor() != TetrisColor.none && !board[i][j].isControlled()) scoringboard[i][j] = 1;
							if(i == 1 && !bottomFilled && combo == 0 && highestFill < 9) scoringboard[i][j] = 3;
							if(i == 0 || i == 11 || j == 21) scoringboard[i][j] = 3;
							if(j > 20-stones) scoringboard[i][j] = 3;
						}
					}
					//add piece
					boolean possible = true;
					for(int i = 0; i < width; i++) {
						for(int j = 0; j < height; j++) {
							if(pieceMap[i][j]) {
								if(scoringboard[i+x][j] == 3 || scoringboard[i+x][j] == 1) {
									possible = false;
								}
								scoringboard[i+x][j] = 2;
							}
						}
					}
					if(possible) {
						//drop piece
						dropPiece(scoringboard);
						//check board score
						int[] scores = calculateBoardScore(scoringboard);
						int score = scores[0];
						if(x == 1) score -= 100;
						if(!isLineInBank() && piece == Piece.IPiece) score -= 400;
						if(score > topScore) {
							topScore = score;
							bestR = r;
							bestP = x;
							bestCombo = scores[1];
							bestTetris = scores[2];
							for(int i = 0; i < 12; i++) {
								for(int j = 0; j < 22; j++) {
									bestScoreBoard[i][j] = scoringboard[i][j];
								}
							}
						}
					}
				}
			}
			if(bestCombo == 0) combo = 0;
			combo += bestCombo;
			if(bestTetris == 0) tetrisCombo = 0;
			tetrisCombo += bestTetris;
			//printScoreBoard(bestScoreBoard);
			return new int[] {topScore, bestR, bestP, bestCombo};
		}
		return new int[] {-500000, 1, 0, 0};
	}
	
	/**
	 * Takes a scoring board integer array using 2's to represent the falling piece
	 * and 1'2 and 3's to represent other blocks and the walls, respectively.
	 * This method will drop the falling piece downward until it collides with a
	 * solid piece. Directly modifies the given array.
	 * @param scoringboard - integer array representing the board state with the
	 * piece to be dropped
	 */
	private static void dropPiece(int[][] scoringboard) {
		boolean atTheBottom = false;
		int rounds = 0;
		while(!atTheBottom) {
			//Lower piece by 1
			for(int j = 20; j >= 1; j--) {
				for(int i = 1; i <= 10; i++) {
					if(scoringboard[i][j] == 0 && scoringboard[i][j-1] == 2) {
						scoringboard[i][j-1] = 0;
						scoringboard[i][j] = 2;
					}
				}
			}
			//Check for a collision
			for(int j = 20; j >= 1; j--) {
				for(int i = 1; i <= 10; i++) {
					if(scoringboard[i][j] == 2 && (scoringboard[i][j+1] == 1 || scoringboard[i][j+1] == 3)) {
						atTheBottom = true;
					}
				}
			}
			rounds++;
			if(rounds > 100) {
				break;
			}
		}
	}

	/**
	 * A debugging feature that has had little use, prints the state of
	 * a scoring board.
	 * @param scoringboard
	 */
//	private void printScoreBoard(int[][] scoringboard) {
//		System.out.println("Score: "+calculateBoardScore(scoringboard)[0]);
//		System.out.println("+------------------------------------+");
//		for(int y = 0; y < scoringboard[0].length; y++) {
//			String s = "|";
//			for(int x = 0; x < scoringboard.length; x++) {
//				if(scoringboard[x][y] == 0) {
//					s += "   ";
//				} else {
//					if(scoringboard[x][y] == 2) {
//						s += "("+scoringboard[x][y]+")";
//					} else {
//						s += "["+scoringboard[x][y]+"]";
//					}
//				}
//			}
//			System.out.println(s+"|");
//		}
//		System.out.println("+------------------------------------+");
//		System.out.println("");
//	}

	/**
	 * Scores the placement of a piece, represented by twos in an integer array
	 * on a board with old pieces represented by ones and walls represented by 
	 * threes. The scoring is based on many tunable factors.
	 * @param scoringboard - integer array representing the state of the piece
	 * placement to be graded.
	 * @return int array containing {score, rowsCleared} for use in determining combos
	 * and grading decisions
	 */
	private int[] calculateBoardScore(int[][] scoringboard) {
		
		//Note: more scoring heuristics exist in the getBestPlacement method
		//	these include:
		//	subtracting 100 points for putting pieces in the first row
		//	subtracting 400 points for using a line piece when no more line pieces are in the bank
		
		int adjacent = 30;		//Score for each adjacent game piece
		int height = -50;		//score for each additional height
		int shadow = -1000;		//score for each empty space directly covered
	 	int bubble = -10;		//score for each empty space covered that was already covered
		int wall = 30;			//score for adjacent walls and the bottom
		int rows = 200;			//score for completing a row
		int tetris = 900000;	//score for making a tetris
		int perfect = 1200;		//score for clearing the entire new block by completing rows
		int cliffs = -600;		//score for each cliff depth created more than 2;
		int score = 0;
		
		rows -= 100*tetrisCombo;
		
		//Bubble popper case
		if(bubbles > 10) {
			shadow = -60000;
			rows = 6000;
		}
		
		//Combo case
		if(combo > 0) {
			rows = 400*combo-2400*tetrisCombo;
			perfect = perfect-1200*tetrisCombo;
		}
		
		//Line breaking case (critical height)
		if(highestFill >= 12 && !isLineInBank()) {
			height = -5000;
			rows = 6000;
			shadow = -800;
			wall = 0;
			adjacent = 10;
			perfect = 100000;
		}
		
		//Add points for adjacent tiles
		for(int j = 1; j <= 20; j++) {
			for(int i = 1; i <= 10; i++) {
				if(scoringboard[i][j] == 2) {
					if(scoringboard[i+1][j] == 1) score += adjacent;
					if(scoringboard[i-1][j] == 1) score += adjacent;
					if(scoringboard[i][j+1] == 1) score += adjacent;
					if(scoringboard[i][j-1] == 1) score += adjacent;
				}
			}
		}
		//Add points for adjacent walls
		for(int j = 1; j <= 20; j++) {
			for(int i = 1; i <= 10; i++) {
				if(scoringboard[i][j] == 2) {
					if(scoringboard[i+1][j] == 3) score += wall;
					if(scoringboard[i-1][j] == 3) score += wall;
					if(scoringboard[i][j+1] == 3) score += wall;
					if(scoringboard[i][j-1] == 3) score += wall;
				}
			}
		}
		//Add points for row clears 
		int tetrisCleared = 0;
		int rowsCleared = 0;
		for(int j = 1; j <= 20; j++) {
			int tilesFilled = 0;
			for(int i = 1; i <= 10; i++) {
				if(scoringboard[i][j] == 2 || scoringboard[i][j] == 1) {
					tilesFilled++;
				}
			}
			if(tilesFilled == 10) {
				rowsCleared++;
				//change blocks involved in row clears so as to not count them in following operations
				for(int i = 1; i <= 10; i++) {
					if(scoringboard[i][j] == 2) scoringboard[i][j] = 4;
				}
			}
		}
		if(rowsCleared == 1) {
			score += rows/2;
		}
		if(rowsCleared > 1 && rowsCleared < 4) {
			score += rowsCleared*rows;
		}
		if(rowsCleared == 4) {
			score += tetris;
			tetrisCleared = 1;
		}
		//Award lots of points for destroying the entire block with row clears
		boolean noneLeft = true;
		someLeft:
		for(int i = 1; i <= 10; i++) {
			for(int j = 1; j <= 20; j++) {
				if(scoringboard[i][j] == 2) {
					noneLeft = false;
					break someLeft;
				}
			}
		}
		if(noneLeft) score += perfect;
		//Subtract points for 'cliffs'
		if(currentPiece != Piece.LPiece && currentPiece != Piece.JPiece) {
			int cliffLeft = 0;
			int cliffRight = 0;
			for(int y = 1; y <= 18; y++) {
				for(int x = 1; x <= 10; x++) {
					if(scoringboard[x][y] == 2) {
						if(scoringboard[x-1][y] == 0 && scoringboard[x-1][y+1] == 0 && scoringboard[x-1][y+2] == 0 && cliffLeft == 0) {
							cliffLeft = 1;
							for(int j = y+3; j <= 18; j++) {
								if(scoringboard[x-1][j] == 0) {
									cliffLeft++;
								} else {
									break;
								}
							}
						} else {
							cliffLeft = -1;
						}
					}
				}
				for(int x = 10; x >= 1; x--) {
					if(scoringboard[x][y] == 2) {
						if(scoringboard[x+1][y] == 0 && scoringboard[x+1][y+1] == 0 && scoringboard[x+1][y+2] == 0 && cliffRight == 0) {
							cliffRight = 1;
							for(int j = y+3; j <= 18; j++) {
								if(scoringboard[x+1][j] == 0) {
									cliffRight++;
								} else {
									break;
								}
							}
						} else {
							cliffRight = -1;
						}
					}					
				}
			}
			score += cliffLeft*cliffs;
			score += cliffRight*cliffs;
		}
		//Subtract points for height
		int lowestHeight = 21;
		int averageHeight = 0;
		for(int j = 1; j <= 20; j++) {
			for(int i = 1; i <= 10; i++) {
				if(scoringboard[i][j] == 2 || scoringboard[i][j] == 4) {
					averageHeight += (21-j);
					if((21-j) < lowestHeight) lowestHeight = (21-j);
				}
			}
		}
		averageHeight = (int)Math.ceil((double)averageHeight / 4.0);
		score += averageHeight*height;
		//Subtract points for shadows
		for(int i = 1; i <= 10; i++) {
			for(int j = 1; j <= 20; j++) {
				if(scoringboard[i][j] == 2) {
					boolean solid = false;
					for(int s = j+1; s <= 20; s++) {
						if(scoringboard[i][s] == 2) break;
						if(scoringboard[i][s] == 0 && !solid && s <= (22-lowestHeight)) score += shadow;
						if(scoringboard[i][s] == 0 && (solid || s >= (21-lowestHeight))) score += bubble;
						if(scoringboard[i][s] == 1) solid = true;
					}
				}
			}
		}
		//Change modified blocks back to original state
		for(int i = 1; i <= 10; i++) {
			for(int j = 1; j <= 20; j++) {
				if(scoringboard[i][j] == 4) scoringboard[i][j] = 2;
			}
		}
		return new int[] {score, rowsCleared, tetrisCleared};
	}
	
	/**
	 * Checks each of the bank locations for a I piece
	 * @return true if there is an I piece in the bank, else false
	 */
	public boolean isLineInBank() {
		for(int i = 1; i <= 5; i++) {
			if(getBank(i) == Piece.IPiece) return true;
		}
		return false;
	}
	
	/**
	 * Checks each of the bank locations for a L piece
	 * @return true if there is an L piece in the bank, else false
	 */
	public boolean isLInBank() {
		for(int i = 1; i <= 5; i++) {
			if(getBank(i) == Piece.LPiece) return true;
		}
		return false;
	}
	
	/**
	 * Checks each of the bank locations for a J piece
	 * @return true if there is a J piece in the bank, else false
	 */
	public boolean isJInBank() {
		for(int i = 1; i <= 5; i++) {
			if(getBank(i) == Piece.JPiece) return true;
		}
		return false;
	}
	
	public void setSpeed(int waitTime) {
		this.waitTime = waitTime;
	}

	public boolean isAlive() {
		return alive;
	}
	
}
