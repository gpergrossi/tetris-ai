package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import tetris.ITetris.Piece;

/**
 * Draws a tetris game state by querying the ITetris interface.
 * 
 * @author Gregary Pergrossi
 */
public class TetrisGraphics {

	/**
	 * Returns a loaded image from the file name unless the file cannot be found
	 * or is not an image, in which case null is returned
	 * 
	 * @param filename
	 * @return BufferedImage
	 */
	public static BufferedImage loadImage(String filename) {
		try {
			return ImageIO.read(new File(filename));
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static BufferedImage halfSize(BufferedImage image) {
		BufferedImage half = new BufferedImage(image.getWidth()/2, image.getHeight()/2, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = half.createGraphics();
		g.scale(0.5, 0.5);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		
		return half;
	}
	
	private static BufferedImage IMAGE_RED;
	private static BufferedImage IMAGE_ORANGE;
	private static BufferedImage IMAGE_YELLOW;
	private static BufferedImage IMAGE_GREEN;
	private static BufferedImage IMAGE_BLUE;
	private static BufferedImage IMAGE_CYAN;
	private static BufferedImage IMAGE_MAGENTA;
	
	private static BufferedImage IMAGE_L_PIECE;
	private static BufferedImage IMAGE_J_PIECE;
	private static BufferedImage IMAGE_T_PIECE;
	private static BufferedImage IMAGE_I_PIECE;
	private static BufferedImage IMAGE_S_PIECE;
	private static BufferedImage IMAGE_Z_PIECE;
	private static BufferedImage IMAGE_O_PIECE;
	
	static {
		IMAGE_RED = loadImage("media/red.png");
		IMAGE_ORANGE = loadImage("media/orange.png");
		IMAGE_YELLOW = loadImage("media/yellow.png");
		IMAGE_GREEN = loadImage("media/green.png");
		IMAGE_BLUE = loadImage("media/blue.png");
		IMAGE_CYAN = loadImage("media/cyan.png");
		IMAGE_MAGENTA = loadImage("media/magenta.png");
		
		IMAGE_L_PIECE = halfSize(loadImage("media/L.png"));
		IMAGE_J_PIECE = halfSize(loadImage("media/J.png"));
		IMAGE_T_PIECE = halfSize(loadImage("media/T.png"));
		IMAGE_I_PIECE = halfSize(loadImage("media/I.png"));
		IMAGE_S_PIECE = halfSize(loadImage("media/S.png"));
		IMAGE_Z_PIECE = halfSize(loadImage("media/Z.png"));
		IMAGE_O_PIECE = halfSize(loadImage("media/O.png"));
	}
	
	ITetris tetris;
	List<PieceBank> banks;
	PieceBank hold;
	
	Point boardPos;
	Dimension size;
	
	public TetrisGraphics(ITetris tetris) {
		this.tetris = tetris;

		this.boardPos = new Point(70, 10);
		
		Point holdPos = new Point(10, 10);
		Point bankPos = new Point(81 + tetris.getBoardWidth()*18, 10);
		Point bankSeparation = new Point(0, 60);
		
		this.banks = new ArrayList<>();
		Point currentBankPos = bankPos;
		for (int i = 0; i < tetris.getNumBanks(); i++) {
			banks.add(new PieceBank(new Point(currentBankPos)));
			currentBankPos.setLocation(currentBankPos.x + bankSeparation.x, currentBankPos.y + bankSeparation.y);
		}
		
		hold = new PieceBank(holdPos);
		
		int maxX = currentBankPos.x + 60;
		int maxY = Math.max(currentBankPos.y, boardPos.y + tetris.getBoardHeight()*18) + 10;
		
		this.size = new Dimension(maxX, maxY);
	}
	
	public Dimension getSize() {
		return size;
	}

	public void draw(Graphics2D g) {
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, size.width, size.height);
		
		// Draw hold
		if (tetris.hasHold()) {
			hold.draw(g, tetris.getHoldPiece());
		}
		
		// Draw banks
		for (int i = 0; i < tetris.getNumBanks(); i++) {
			banks.get(i).draw(g, tetris.getBankPiece(i));
		}
		
		// Draw the board
		g.setColor(java.awt.Color.white);
		g.drawRect(boardPos.x-1, boardPos.y-1, tetris.getBoardWidth()*18+2, tetris.getBoardHeight()*18+2);
		
		// Draw placed pieces
		for (int x = 0; x < tetris.getBoardWidth(); x++) {
			for (int y = 0; y < tetris.getBoardHeight(); y++) {
				ITetris.Color boardColor = tetris.getTile(x, y);
				
				if (boardColor == ITetris.Color.NONE || boardColor == ITetris.Color.GRAY || boardColor == ITetris.Color.GHOST) {					
					if ((x + y) % 2 == 0) {
						g.setColor(new java.awt.Color(20,20,20));
					} else {
						g.setColor(new java.awt.Color(32,32,32));
					}
					g.fillRect(boardPos.x + x*18 + 1, boardPos.y + y*18 + 1, 16, 16);
					
					if(boardColor == ITetris.Color.GHOST) {
						g.setColor(new java.awt.Color(64, 64, 64));
						g.drawRect(boardPos.x + x*18 + 1, boardPos.y + y*18 + 1, 15, 15);
						g.setColor(new java.awt.Color(96, 96, 96));
						g.drawRect(boardPos.x + x*18, boardPos.y + y*18, 17, 17);
					} else {
						g.setColor(new Color(16,16,16));
						g.drawRect(boardPos.x + x*18, boardPos.y + y*18, 17, 17);
					}
				} else {
					BufferedImage image = getImageForColor(boardColor);
					if (image != null) {
						g.drawImage(image, boardPos.x + x*18, boardPos.y + y*18, null);
					}
				}
			}
		}

		// Display Score
		g.setColor(java.awt.Color.white);
		g.setFont(new Font("Cooper Black", Font.PLAIN, 18));
		g.drawString("Score", boardPos.x-62, boardPos.y+100);
		g.drawString(String.valueOf(tetris.getScore()), boardPos.x-56, boardPos.y+120);
		
		// Display Lines Cleared
		g.drawString("Lines", boardPos.x-62, boardPos.y+150);
		g.drawString(String.valueOf(tetris.getLinesCleared()), boardPos.x-56, boardPos.y+170);
		
		// Display Combo
		if (tetris.getLineCombo() > 0) {
			g.setColor(java.awt.Color.yellow);
			g.drawString("x"+tetris.getLineCombo(), boardPos.x-56, boardPos.y+200);
		}

		// Display Scoring Information (Fading messages like "Tetris!")
//		int fade = (int) (System.currentTimeMillis() - scoreMessageTime);
//		if(scoreMessage != "" && fade < 1000) {
//			int brightness = 255-(int)(fade*0.255);
//			g.setColor(new java.awt.Color(brightness,brightness,brightness));
//			g.drawString(scoreMessage, boardPosition.x, boardPosition.y+180);
//		}

		// Display game over
		if (tetris.isGameOver()) {
			g.setColor(java.awt.Color.white);
			g.setFont(new Font("Cooper Black", Font.PLAIN, 24));
			g.drawString("Game Over", boardPos.x + (tetris.getBoardWidth()*18+2)/2 - 70, boardPos.y + (tetris.getBoardHeight()*18+2)/2 - 10);
		}
		
	}
	
	private BufferedImage getImageForColor(tetris.ITetris.Color boardColor) {
		switch (boardColor) {
			case BLUE: return IMAGE_BLUE;
			case CYAN: return IMAGE_CYAN;
			case GHOST: return null;
			case GRAY: return null;
			case GREEN: return IMAGE_GREEN;
			case MAGENTA: return IMAGE_MAGENTA;
			case NONE: return null;
			case ORANGE: return IMAGE_ORANGE;
			case RED: return IMAGE_RED;
			case YELLOW: return IMAGE_YELLOW;
			default: return null;
		}
	}

	/**
	 * A bank for pieces. Stores, returns and draws
	 * each piece from the Piece class.
	 */
	public static class PieceBank {
		
		Point position = new Point(0,0);
		
		/**
		 * Creates a simple piece bank object with a draw position
		 */
		public PieceBank(Point pos) {
			position = pos;
		}
		
		/**
		 * Draws this piece bank
		 * @param g - graphics object
		 */
		public void draw(Graphics2D g, Piece piece) {
			g.setColor(Color.black);
			g.fillRect(position.x, position.y, 50, 50);
			
			g.setColor(Color.white);
			g.drawRect(position.x, position.y, 49, 49);
				
			if(piece == Piece.L) g.drawImage(IMAGE_L_PIECE, position.x, position.y, null);
			if(piece == Piece.J) g.drawImage(IMAGE_J_PIECE, position.x, position.y, null);
			if(piece == Piece.T) g.drawImage(IMAGE_T_PIECE, position.x, position.y, null);
			if(piece == Piece.I) g.drawImage(IMAGE_I_PIECE, position.x, position.y, null);
			if(piece == Piece.S) g.drawImage(IMAGE_S_PIECE, position.x, position.y, null);
			if(piece == Piece.Z) g.drawImage(IMAGE_Z_PIECE, position.x, position.y, null);
			if(piece == Piece.L) g.drawImage(IMAGE_L_PIECE, position.x, position.y, null);
			if(piece == Piece.O) g.drawImage(IMAGE_O_PIECE, position.x, position.y, null);
		}
		
	}

	
}
