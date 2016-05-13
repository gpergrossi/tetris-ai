package tetris;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;


/**
 * An instantiable tetris class that can be played
 * by calling update and draw regularly with the methods
 * moveLeft(), moveRight(), softDrop(), hardDrop(), 
 * rotate() and swap().
 * @author Gregary Pergrossi (gpergros@hawk.iit.edu)
 *
 */
public class Tetris {
	
	//Points awarded for certain actions
	private static final int POINTS_FULL_CLEAR = 16;	//Points for emptying the board
	private static final int POINTS_TETRIS = 8;			//Points for a tetris
	private static final int POINTS_TSPIN = 8;			//Points for a tspin
	private static final int POINTS_ROW = 1;			//Points for each row cleared
	private static final int COMBO_MULTIPLIER = 2;		//Point multiplier per combo
	
	//Some variables to sort out the update method
	private boolean dropping = false;		//Whether the piece was just dropped
	private boolean sliding = true;			//Whether the piece is sliding (it has already hit the bottom)
	private int fallenSinceSliding = 0;		//Number of spaces fallen since sliding was declared
	private boolean forceUpdate = false;	//Whether to force a time step
	private long touchTime = System.currentTimeMillis()-1;		//Time when the block first hit the something solid + 5 seconds, the time this piece will lock down
	private long lastMove = System.currentTimeMillis();			//Time when the latest movement was made
	private long lastStep = System.currentTimeMillis()-1000;	//Last time the update step was done
	private int initialTimeStep = 250;		//Starting time between steps in ms
	private int timeStep = 250;				//Current time between steps (decreases with score so the game gets faster)
	
	private Point boardPosition;			//Position to draw the tetris object
	private Point content;					//Position to draw the game board
	private PieceBank[] banks;				//The PieceBank objects
	private PieceGenerator generator;		//The generator object
	private Tile[][] board;					//The actual board state

	private long scoreMessageTime = 0;		//Time the score message was updated
	private String scoreMessage = "";		//Message to display
	private boolean swapUsed = false;		//Whether the swap was used on this placement phase
	private boolean gameover = false;		//Game over state
	private long gameoverTime = 0;			//When the game over started, reset after 3 seconds
	private int combo = 0;					//current combo
	private int tetrisCombo = 0;			//current tetris in a row combo
	private int tetrisComboBackToBack = 0;	//number of back to back tetrises
	
	private Piece currentPiece;				//current piece being manipulated
	private Point currentPiecePosition;		//the piece's position
	private int currentPieceRotation;		//the piece's rotation
	
	private int score = 0;
	
	/**
	 * Makes a new tetris board instance at the point specified.
	 * The board size is 315x377.
	 * @param pos - position of the upper left corner
	 */
	public Tetris(Point pos) {
		boardPosition = pos;
		banks = new PieceBank[6];
		banks[0] = new PieceBank(new Point(boardPosition.x+9, boardPosition.y+27));
		banks[1] = new PieceBank(new Point(boardPosition.x+257, boardPosition.y+27));
		banks[2] = new PieceBank(new Point(boardPosition.x+257, boardPosition.y+95));
		banks[3] = new PieceBank(new Point(boardPosition.x+257, boardPosition.y+163));
		banks[4] = new PieceBank(new Point(boardPosition.x+257, boardPosition.y+231));
		banks[5] = new PieceBank(new Point(boardPosition.x+257, boardPosition.y+299));
		content = new Point(boardPosition.x+68, boardPosition.y+9);
		generator = new PieceGenerator(0);
		for(int i = 1; i <= 5; i++) {
			banks[i].setPiece(generator.getNextPiece());
		}
		board = new Tile[12][25];
		for(int x = 0; x <= 11; x++) {
			for(int y = 0; y <= 24; y++) {
				if(x == 0 || x == 11 || y == 24) {
					board[x][y] = new Tile(TetrisColor.gray, false);
				} else {
					board[x][y] = new Tile(TetrisColor.none, false);
				}
			}
		}
	}
	
	/**
	 * Draws this Tetris board and all of its components.
	 * @param g
	 */
	public void draw(Graphics g) {
		//Draw the banks
		for(int i = 0; i < 6; i++) {
			banks[i].draw(g);
		}
		
		//Draw the board
		g.setColor(java.awt.Color.white);
		g.drawRect(content.x-1, content.y-1, 181, 361);
		
		//Draw placed pieces
		for(int x = 1; x <= 10; x++) {
			for(int y = 4; y <= 23; y++) {
				if(board[x][y].getColor() == TetrisColor.none || board[x][y].getColor() == TetrisColor.gray || board[x][y].isGhost()) {
					if((x+y)%2 == 0) {
						g.setColor(new java.awt.Color(20,20,20));
					} else {
						g.setColor(new java.awt.Color(32,32,32));
					}
					g.fillRect(content.x+(x-1)*18+1, content.y+(y-4)*18+1, 16, 16);
					g.setColor(new java.awt.Color(16,16,16));
					if(board[x][y].isGhost()) {
						g.setColor(new java.awt.Color(64,64,64));
						g.drawRect(content.x+(x-1)*18+1, content.y+(y-4)*18+1, 15, 15);
						g.setColor(new java.awt.Color(96,96,96));
					} else {
					}
					g.drawRect(content.x+(x-1)*18, content.y+(y-4)*18, 17, 17);
				} else {
					g.drawImage(board[x][y].getImage(), content.x+18*(x-1), content.y+18*(y-4), null);
				}
			}
		}

		//Display Score
		g.setColor(java.awt.Color.white);
		g.setFont(new Font("Cooper Black", Font.PLAIN, 18));
		g.drawString("Score", boardPosition.x+8, boardPosition.y+100);
		g.drawString(String.valueOf(score), boardPosition.x+20, boardPosition.y+120);
		
		//Display Combo
		if(combo > 1) {
			g.setColor(java.awt.Color.yellow);
			g.drawString("x"+combo, boardPosition.x+20, boardPosition.y+150);
		}
		
		//Display Scoring Information (Fading messages like "Tetris!")
		int fade = (int) (System.currentTimeMillis()-scoreMessageTime);
		if(scoreMessage != "" && fade < 1000) {
			int brightness = 255-(int)(fade*0.255);
			g.setColor(new java.awt.Color(brightness,brightness,brightness));
			g.drawString(scoreMessage, boardPosition.x, boardPosition.y+180);
		}
		
		//Set the time step
		timeStep = initialTimeStep-(int)(score/4)*5;
		
		//Display game over
		if(gameover) {
			g.setColor(java.awt.Color.white);
			g.setFont(new Font("Cooper Black", Font.PLAIN, 24));
			g.drawString("Game Over", boardPosition.x+93, boardPosition.y+200);
		}
	}
	
	/**
	 * Updates the game state and deals with all non-graphical calculations
	 */
	public void update() {
		//Handle the game over screen
		if(gameover) {
			if(System.currentTimeMillis()-gameoverTime < 3000) {
				return;
			}
			gameover = false;
			score = 0;
			combo = 0;
			for(int x = 0; x <= 11; x++) {
				for(int y = 0; y <= 24; y++) {
					if(x == 0 || x == 11 || y == 24) {
						board[x][y] = new Tile(TetrisColor.gray, false);
					} else {
						board[x][y] = new Tile(TetrisColor.none, false);
					}
				}
			}
		}
		
		//initialize determination variables
		boolean pieceLocked = false;
		boolean tspin = false;
		
		//Drop step
		if(System.currentTimeMillis()-lastStep >= timeStep || forceUpdate) {
			lastStep = System.currentTimeMillis();
			forceUpdate = false;
			
			//Make sure there is a piece in play
			if(currentPiece == null) takeNextPiece();
			
			//Clear old piece position
			for(int x = 1; x <= 10; x++) {
				for(int y = 1; y <= 24; y++) {
					if(board[x][y].isControlled()) board[x][y] = new Tile(TetrisColor.none, false);
				}
			}

			//Move current piece downward
			if(checkCollision(currentPiece, new Point(currentPiecePosition.x, currentPiecePosition.y+1), currentPieceRotation)) {
				//If it touches, mark as sliding
				if(!sliding) {
					fallenSinceSliding = 0;
					sliding = true;
					touchTime = System.currentTimeMillis()+5000;
				}
				//If the piece is sliding, display it as the current piece
				if(System.currentTimeMillis() <= touchTime && sliding) {
					fallenSinceSliding = 0;
					addPieceToBoard(currentPiece, currentPiecePosition, currentPieceRotation, true, false);
					addGhostToBoard(currentPiece, currentPiecePosition, currentPieceRotation);
				}
				//If the piece was just dropped, the last movement was more than half a second ago, or the sliding
				//time limit was exceeded, lock the piece in place and start a new one
				long time = System.currentTimeMillis();
				if((time > touchTime || time-lastMove > 500) && sliding || dropping) {
					if(sliding && currentPiece == Piece.TPiece) {
						if(checkCollision(currentPiece, new Point(currentPiecePosition.x, currentPiecePosition.y-1), currentPieceRotation)) {
							tspin = true;	
						}
					}
					sliding = false;
					addPieceToBoard(currentPiece, currentPiecePosition, currentPieceRotation, false, false);
					pieceLocked = true;
					swapUsed = false;
					currentPiece = null;
					forceUpdate = true;
				}
				dropping = false;
			} else {
				//Update the sliding state potentially turning it off
				fallenSinceSliding++;
				if(fallenSinceSliding > 2) {
					sliding = false;
				}
				//Display the current piece
				currentPiecePosition = new Point(currentPiecePosition.x, currentPiecePosition.y+1);
				addPieceToBoard(currentPiece, currentPiecePosition, currentPieceRotation, true, false);
				addGhostToBoard(currentPiece, currentPiecePosition, currentPieceRotation);	
			}
			
		}
		
		//If a piece was just locked, calculate scoring
		if(pieceLocked) {
			//Clear filled rows
			int rowsCleared = 0;
			for(int y = 1; y <= 23; y++) {
				int inRow = 0;
				//Count number of tiles in the row
				for(int x = 1; x <= 10; x++) {
					if(board[x][y].getColor() != TetrisColor.none && !board[x][y].isControlled()) inRow++;
				}
				//If the row is full add to rowsCleared
				if(inRow == 10) {
					rowsCleared++;
					for(int x = 1; x <= 10; x++) {
						//Shift the above lines down one
						if(y > 1) {
							for(int y2 = y; y2 >= 2; y2--) {
								board[x][y2] = board[x][y2-1];
							}
						}
						board[x][1] = new Tile(TetrisColor.none, false);
					}
				}
			}
			if(rowsCleared == 4) {
				//If 4 rows are cleared, give points for a tetris
				score += POINTS_TETRIS*Math.pow(COMBO_MULTIPLIER, combo);
				scoreMessage = "Tetris!";
				scoreMessageTime = System.currentTimeMillis();
				tetrisCombo++;
				tetrisComboBackToBack++;
				
				//Tetris combos are special
				if(tetrisCombo > 1) {
					scoreMessage = "Tetris x"+tetrisCombo+"!";
					scoreMessageTime = System.currentTimeMillis();
				}
				
				//Back to back tetrises with no placement in between are worth more points
				if(tetrisComboBackToBack == 2) {
					scoreMessage = "Double Tetris!";
					scoreMessageTime = System.currentTimeMillis();
					score += POINTS_TETRIS*Math.pow(COMBO_MULTIPLIER, combo);
				}
				if(tetrisComboBackToBack == 3) {
					scoreMessage = "TRIPLE TETRIS!";
					scoreMessageTime = System.currentTimeMillis();
					score += POINTS_TETRIS*Math.pow(COMBO_MULTIPLIER, combo)*2;
				}
				
			} else {
				//If less than four lines are cleared, clear back to back 
				//tetris combo and add points for other line clears
				tetrisComboBackToBack = 0;
				
				if(rowsCleared == 2 && tspin) {
				
					//If there was a t-spin give points for it
					score += POINTS_TSPIN*Math.pow(COMBO_MULTIPLIER, combo);
					scoreMessage = "T-Spin!";
					scoreMessageTime = System.currentTimeMillis();
					tetrisCombo = 0;
					
				} else {
					
					//Add points per row
					score += POINTS_ROW*rowsCleared*Math.pow(COMBO_MULTIPLIER, combo);
					
					//Print scoring messages
					if(rowsCleared == 1) {
						scoreMessage = "Single";
						scoreMessageTime = System.currentTimeMillis();
						tetrisCombo = 0;
					}
					if(rowsCleared == 2) {
						scoreMessage = "Double";
						scoreMessageTime = System.currentTimeMillis();
						tetrisCombo = 0;
					}
					if(rowsCleared == 3) {
						scoreMessage = "Triple!";
						scoreMessageTime = System.currentTimeMillis();
						tetrisCombo = 0;
					}
				}
			}
			
			//Check for a full clear
			int pieces = 0;
			for(int x = 1; x <= 10; x++) {
				for(int y = 4; y <= 23; y++) {
					if(board[x][y].getColor() != TetrisColor.none) {
						pieces++;
					}
				}
			}
			if(pieces == 0) {
				scoreMessage = "Full Clear!";
				scoreMessageTime = System.currentTimeMillis();
				score += POINTS_FULL_CLEAR;
			}
			
			//Update the combo
			if(rowsCleared > 0) {
				combo++;
			} else {
				combo = 0;
			}
			
		}
		
	}
	

	/**
	 * Private game control method that advances the incoming pieces and fills
	 * the current piece attribute.
	 */
	private void takeNextPiece() {
		//New piece gets the first piece in the bank
		Piece next = banks[1].getPiece();
		
		//The bank shifts up and gets a new piece added to the bottom
		for(int i = 1; i <= 4; i++) {
			banks[i].setPiece(banks[i+1].getPiece());
		}
		banks[5].setPiece(generator.getNextPiece());
		
		//Set the new piece and its position and rotation
		currentPiece = next;
		currentPiecePosition = new Point(6,2);
		currentPieceRotation = 0;
		
		//Check for a game over (piece is colliding right when it spawns)
		if(!gameover && checkCollision(currentPiece, currentPiecePosition, currentPieceRotation)) {
			gameover = true;
			gameoverTime = System.currentTimeMillis();
		}
	}
	
	/**
	 * Game Control: Swaps the piece in play with the piece in bank 0.
	 * This can only be used once per placement phase.
	 */
	public void swap() {
		if(currentPiece == null) return;
		if(!swapUsed) {
			if(banks[0].getPiece() != null) {
				Piece temp = banks[0].getPiece();
				banks[0].setPiece(currentPiece);
				currentPiece = temp;
				currentPiecePosition = new Point(6,2);
				currentPieceRotation = 0;
				swapUsed = true;
			} else {
				banks[0].setPiece(currentPiece);
				currentPiece = null;
				forceUpdate = true;
				swapUsed = true;
			}
		}
	}
	
	/**
	 * Game Control: Drops the piece downward one row
	 */
	public void softDrop() {
		if(gameover) return;
		if(currentPiece == null) return;
		if(!checkCollision(currentPiece, new Point(currentPiecePosition.x, currentPiecePosition.y+1), currentPieceRotation)) {
			currentPiecePosition = new Point(currentPiecePosition.x, currentPiecePosition.y+1);
			updatePiece();
			lastMove = System.currentTimeMillis();
		}
	}

	/**
	 * Game Control: Drops the piece all the way downward and locks it
	 */
	public void hardDrop() {
		if(gameover) return;
		if(currentPiece == null) return;
		while(!checkCollision(currentPiece, new Point(currentPiecePosition.x, currentPiecePosition.y+1), currentPieceRotation)) {
			currentPiecePosition = new Point(currentPiecePosition.x, currentPiecePosition.y+1);
		}
		updatePiece();
		dropping = true;
		forceUpdate = true;
		lastMove = System.currentTimeMillis();
	}
	
	/**
	 * Game Control: Moves the piece left
	 */
	public void moveLeft() {
		if(gameover) return;
		if(currentPiece == null) return;
		if(!checkCollision(currentPiece, new Point(currentPiecePosition.x-1, currentPiecePosition.y), currentPieceRotation)) {
			currentPiecePosition = new Point(currentPiecePosition.x-1, currentPiecePosition.y);
			updatePiece();
			lastMove = System.currentTimeMillis();
		}
	}	
	
	/**
	 * Game Control: Moves the piece right
	 */
	public boolean moveRight() {
		if(gameover) return false;
		if(currentPiece == null) return false;
		if(!checkCollision(currentPiece, new Point(currentPiecePosition.x+1, currentPiecePosition.y), currentPieceRotation)) {
			currentPiecePosition = new Point(currentPiecePosition.x+1, currentPiecePosition.y);
			updatePiece();
			lastMove = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	/**
	 * Game Control: Rotates the piece
	 */
	public boolean rotate() {
		if(!canPlay()) return false;
		if(currentPiece != Piece.IPiece) {
			for(int i = 0; i < 2; i++) {
				if(!checkCollision(currentPiece, new Point(currentPiecePosition.x, currentPiecePosition.y-i), (currentPieceRotation+1)%4)) {
					currentPiecePosition = new Point(currentPiecePosition.x, currentPiecePosition.y-i);
					currentPieceRotation = (currentPieceRotation+1)%4;
					updatePiece();
					lastMove = System.currentTimeMillis();
					return true;
				}
			}
			return false;
		}
		//Special case because lines rotate strangely
		for(int i = 0; i < 2; i++) {
			int rotation = ((currentPieceRotation+1)%4);
			Point testPos = null;
			if(rotation == 0) {
				testPos = new Point(currentPiecePosition.x+1, currentPiecePosition.y-i);	
			}
			if(rotation == 1) {
				testPos = new Point(currentPiecePosition.x, currentPiecePosition.y+1-i);	
			}
			if(rotation == 2) {
				testPos = new Point(currentPiecePosition.x-1, currentPiecePosition.y-i);	
			}
			if(rotation == 3) {
				testPos = new Point(currentPiecePosition.x, currentPiecePosition.y-1-i);	
			}
			if(!checkCollision(currentPiece, testPos, rotation)) {
				currentPiecePosition = testPos;
				currentPieceRotation = rotation;
				updatePiece();
				lastMove = System.currentTimeMillis();
				return true;
			}
		}			
		return false;
	}
	
	private boolean canPlay() {
		if(isGameover()) return false;
		if(currentPiece == null) return false;
		return true;
	}
	
	private boolean isGameover() {
		return gameover;
	}

	/**
	 * Checks if a piece would collide in the given position on the current board
	 * @param piece
	 * @param position
	 * @param rotation
	 * @return true if it collides, else false
	 */
	private boolean checkCollision(Piece piece, Point position, int rotation) {
		int posX = position.x-piece.getCenterPosition(rotation).x;
		int posY = position.y-piece.getCenterPosition(rotation).y;
		boolean[][] pieceMap = piece.getRotation(rotation);
		for(int x = 0; x < pieceMap.length; x++) {
			for(int y = 0; y < pieceMap[x].length; y++) {
				if((posY+y) > 0 && (posY+y) < 25 && (posX+x) > 0 && (posX+x) < 11) {
					if(pieceMap[x][y] && board[posX+x][posY+y].getColor() != TetrisColor.none && !board[posX+x][posY+y].isControlled()) {
						return true;
					}
				} else {
					if(pieceMap[x][y]) return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Updates the board state to the new piece position
	 */
	private void updatePiece() {
		//Clear old piece position
		for(int x = 1; x <= 10; x++) {
			for(int y = 1; y <= 24; y++) {
				if(board[x][y].isControlled()) board[x][y] = new Tile(TetrisColor.none, false);
			}
		}
		addGhostToBoard(currentPiece, currentPiecePosition, currentPieceRotation);
		addPieceToBoard(currentPiece, currentPiecePosition, currentPieceRotation, true, false);
	}
	
	/**
	 * Adds the ghost piece of the given piece to the board
	 * @param piece
	 * @param position
	 * @param rotation
	 */
	private void addGhostToBoard(Piece piece, Point position, int rotation) {
		Point newPos = new Point(position.x, position.y);
		while(!checkCollision(piece, new Point(newPos.x, newPos.y+1), rotation)) {
			newPos = new Point(newPos.x, newPos.y+1);
		}
		int posX = newPos.x-piece.getCenterPosition(rotation).x;
		int posY = newPos.y-piece.getCenterPosition(rotation).y;
		boolean[][] pieceMap = piece.getRotation(rotation);
		for(int x = 0; x < pieceMap.length; x++) {
			for(int y = 0; y < pieceMap[x].length; y++) {
				if(pieceMap[x][y] && board[posX+x][posY+y].getColor() == TetrisColor.none) {
					Tile temp = new Tile(piece.getColor(), true, true);
					board[posX+x][posY+y] = temp;
				}
			}
		}
	}

	/**
	 * Adds the given piece to the board
	 * @param piece
	 * @param position
	 * @param rotation
	 * @param controllable
	 * @param ghost
	 */
	private void addPieceToBoard(Piece piece, Point position, int rotation, boolean controllable, boolean ghost) {
		int posX = position.x-piece.getCenterPosition(rotation).x;
		int posY = position.y-piece.getCenterPosition(rotation).y;
		boolean[][] pieceMap = piece.getRotation(rotation);
		for(int x = 0; x < pieceMap.length; x++) {
			for(int y = 0; y < pieceMap[x].length; y++) {
				if(pieceMap[x][y]) {
					Tile temp = new Tile(piece.getColor(), controllable, ghost);
					board[posX+x][posY+y] = temp;
				}
			}
		}
	}

	public Tile[][] getBoard() {
		return board;
	}

	public PieceBank[] getBanks() {
		return banks;
	}
	
	public Piece getCurrentPiece() {
		return currentPiece;
	}
	
}
