package tetris;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * The piece generator I use follows something I read
 * online about Tetris. The pieces come from a "bag" of
 * all 7 pieces in random order. When the bag is empty, 
 * it is refilled in random order. This guarantees that
 * you will get 1 of every piece each 7 pieces that come
 * up.
 * 
 * @author Gregary Pergrossi (gpergrossi@imsa.edu)
 */
public class PieceGenerator {

	private ArrayList<Piece> bag;
	
	/**
	 * Creates a new piece generator using the seed given.
	 * If the seed is 0, a random seed will be used.
	 * @param seed
	 */
	public PieceGenerator(int seed) {
		this.bag = new ArrayList<Piece>();
		fillBag();
	}
	
	public Piece getNextPiece() {
		if(bag.size() == 0) fillBag();
		Piece p = bag.get(0);
		bag.remove(0);
		return p;
	}
	
	/**
	 * Fills the bag with all seven pieces in random order
	 */
	private void fillBag() {
		bag.clear();
	    bag.add(Piece.LPiece);
	    bag.add(Piece.JPiece);
	    bag.add(Piece.TPiece);
	    bag.add(Piece.SPiece);
	    bag.add(Piece.ZPiece);
	    bag.add(Piece.IPiece);
	    bag.add(Piece.OPiece);
		Collections.shuffle(bag);
	}
	
}
