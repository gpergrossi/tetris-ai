package TetrisGame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import tetris.Tetris;

/**
 * The main class that drives this program, made for functionality not
 * appearence, or readability... It basically just gets the job done.
 * 
 * @author Gregary Pergrossi (gpergrossi@imsa.edu)
 */
public class TetrisMain extends TetrisPanel {

	public TetrisMain(int width, int height) {
		super(width, height);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("TetrisAI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TetrisMain main = new TetrisMain(800, 600);
		frame.setContentPane(main);
		frame.pack();
		main.init();
		frame.setVisible(true);
		
		while (true) {
			main.repaint();
			Thread.yield();
		}
	}
	
	public static TetrisMain instance;
	private static final long serialVersionUID = 1L;
	
	private static BufferedImage ERROR_IMAGE = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
	
	private static Tetris game;
	private static Tetris gamebot;
	
	private static MusicMid music;
	private static Bot bot;

	/**
	 * The initialize method called by the applet class. In this case it is much
	 * like a static initializer.
	 */
	public void initialize() {		
		instance = this;
		
		music = new MusicMid("tetris/Tetris.mid");
		MusicMid.initialize();
		
		Graphics g = ERROR_IMAGE.getGraphics();
		g.setColor(Color.RED);
		g.fillRect(0, 0, 30, 30);
		g.dispose();
	}

	 
	
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
		return ERROR_IMAGE;
//		try {
//			InputStream is = instance.getClass().getResourceAsStream(filename);
//			return ImageIO.read(is);
//		} catch(Exception e) {
//			e.printStackTrace();
//			return ERROR_IMAGE;
//		}
	}

	/**
	 * The main loop of the applet, provides a graphics object to draw to
	 */
	public void run(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if(menu == 1) {
			MusicMid.setLoop(true);
			music.setTrack();
			MusicMid.play();
			long time = System.currentTimeMillis();
			float BPM = MusicMid.getBPM();
			MusicMid.setBPM(BPM * 11);
			while(System.currentTimeMillis() - time < 100) {}
			MusicMid.setBPM(BPM);
			game = new Tetris(new Point(0, 51));
			gamebot = new Tetris(new Point(320, 51));
			speed = new Slider("Speed", 400, 450, 160, 5, 500, 5);
			menu = 2;
			
			bot = new Bot(gamebot, 20);
			bot.start();
			bot.update();
			bot.play(gamebot.getCurrentPiece());
		}
		if(menu == 2) {
			game.update();
			game.draw(g);
			gamebot.update();
			gamebot.draw(g);
			speed.update(mouse, mouseClick);
			bot.setSpeed(speed.getValue());
			speed.draw(g);
			
		}
	}

	public void stop() {
		MusicMid.stop();
		System.exit(0);
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mouseClicked(MouseEvent e) {}

	public void keyTyped(KeyEvent e) {}

	public void keyPressed(KeyEvent e) {
		System.out.println("key pressed: "+e.getKeyCode());
		if(e.getKeyCode() == KeyEvent.VK_UP) {
			game.rotate();
		}
		if(e.getKeyCode() == KeyEvent.VK_DOWN) {
			game.softDrop();
		}
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			game.hardDrop();
		}
		if(e.getKeyCode() == KeyEvent.VK_SHIFT) {
			game.swap();
		}
		if(e.getKeyCode() == KeyEvent.VK_LEFT) {
			game.moveLeft();
		}
		if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
			game.moveRight();
		}
	}

	public void keyReleased(KeyEvent e) {}

	public void mouseDragged(MouseEvent e) {
		mouse = e.getPoint();
	}

	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			mouseClick += 1;
		}
		if(e.getButton() == MouseEvent.BUTTON3) {
			mouseClick += 2;
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
			mouseClick -= 1;
		}
		if(e.getButton() == MouseEvent.BUTTON3) {
			mouseClick -= 2;
		}
	}

	public void mouseMoved(MouseEvent e) {
		mouse = e.getPoint();
	}



	@Override
	public void focusGained(FocusEvent e) {}



	@Override
	public void focusLost(FocusEvent e) {}

}
