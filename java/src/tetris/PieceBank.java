package tetris;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import TetrisGame.TetrisMain;

/**
 * A bank for pieces. Stores, returns and draws
 * each piece from the Piece class.
 * @author Gregary Pergrossi (gpergrossi@imsa.edu)
 *
 */
public class PieceBank {

	private static BufferedImage lPiece;
	private static BufferedImage jPiece;
	private static BufferedImage tPiece;
	private static BufferedImage iPiece;
	private static BufferedImage sPiece;
	private static BufferedImage zPiece;
	private static BufferedImage oPiece;
	
	static {
		lPiece = TetrisMain.loadImage("media/l.png");
		jPiece = TetrisMain.loadImage("media/j.png");
		tPiece = TetrisMain.loadImage("media/t.png");
		iPiece = TetrisMain.loadImage("media/i.png");
		sPiece = TetrisMain.loadImage("media/s.png");
		zPiece = TetrisMain.loadImage("media/z.png");
		oPiece = TetrisMain.loadImage("media/o.png");
	}
	
	Piece piece = null;
	Point position = new Point(0,0);
	
	/**
	 * Creates a simple piece bank object with a draw position
	 */
	public PieceBank(Point pos) {
		position = pos;
	}
	
	/**
	 * Sets the piece of this piece bank
	 * @param piece
	 */
	public void setPiece(Piece p) {
		piece = p;
	}
	
	/**
	 * Returns the piece in this piece bank
	 * @return piece
	 */
	public Piece getPiece() {
		return piece;
	}
	
	/**
	 * Draws this piece bank
	 * @param g - graphics object
	 */
	public void draw(Graphics g) {
		if(piece == null) {
			g.setColor(Color.black);
			g.fillRect(position.x, position.y, 50, 50);
		}
		if(piece == Piece.LPiece) {
			g.drawImage(lPiece, position.x, position.y, null);
		}
		if(piece == Piece.JPiece) {
			g.drawImage(jPiece, position.x, position.y, null);
		}
		if(piece == Piece.TPiece) {
			g.drawImage(tPiece, position.x, position.y, null);
		}
		if(piece == Piece.IPiece) {
			g.drawImage(iPiece, position.x, position.y, null);
		}
		if(piece == Piece.SPiece) {
			g.drawImage(sPiece, position.x, position.y, null);
		}
		if(piece == Piece.ZPiece) {
			g.drawImage(zPiece, position.x, position.y, null);
		}
		if(piece == Piece.LPiece) {
			g.drawImage(lPiece, position.x, position.y, null);
		}
		if(piece == Piece.OPiece) {
			g.drawImage(oPiece, position.x, position.y, null);
		}
		g.setColor(Color.white);
		g.drawRect(position.x, position.y, 49, 49);
	}
	
}
