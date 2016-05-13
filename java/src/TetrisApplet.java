


import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * A quick and probably badly structure but easy to use applet shell.
 * It simply allows an extending class to run with little problems. 
 * Avoid using this for anything large or requiring a GUI. 
 * (I make due without them a lot).
 * @author Gregary Pergrossi (gpergrossi@imsa.edu)
 */

public abstract class TetrisApplet extends Applet implements MouseListener, MouseMotionListener, KeyListener, FocusListener {
	
	/**
	 * Pointless and annoying requirement of anything that extends an Applet
	 */
	private static final long serialVersionUID = 1L;

	public TetrisApplet(int width, int height) {
	}
	
	//System
	public static final boolean showFullStackTrace = false;
	public static boolean initialized = false;
	
	//Display
	public static int screenWidth, screenHeight;
	public static BufferedImage backbufferImage;
	public static Graphics2D backbuffer;
	public static double FPS = 100;
	
	//Clock
	public static long startTime = System.nanoTime();
	public static long frameTime = 0;
	public static final long oneSecond = 1000000000;
	public static long averageFrameTime = (long)(oneSecond/FPS);
	
	//Text
    public static final Font font = new Font("Courier", Font.PLAIN, 12);
    public static final int fontWidth = 9;
    public static final int fontHeight = 12;
    
    //Input
    public static Slider speed = new Slider("speed", 0, 0, 200, 1, 100, 20);
    public static Point mouse;
    public static int menu, mouseClick;
    
	public void init() {
		screenWidth = super.getSize().width;
	    screenHeight = super.getSize().height;
	    setBackground(new java.awt.Color(0,0,0,128));
	    backbufferImage = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
	    backbuffer = backbufferImage.createGraphics();

	    addMouseListener(this);
	    addMouseMotionListener(this);
	    addKeyListener(this);
	    addFocusListener(this);
	    setLayout(null);
	   
	    initialize();
	}
	
	public void paint(Graphics appletGraphics) {
		try {
			backbuffer.setColor(this.getBackground());
			backbuffer.fillRect(0, 0, screenWidth, screenHeight);
			run(backbuffer);
			while((System.nanoTime()-startTime) < (oneSecond/FPS)) {
			}
			frameTime = (System.nanoTime()-startTime);
			startTime = System.nanoTime();
			appletGraphics.drawImage(backbufferImage, 0, 0, this);
			repaint();
		} catch (Exception e) {
			debug(e);
		}
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	
	private void debug(Exception e) { 
		stop();
		StackTraceElement[] stack = e.getStackTrace();
		System.err.println(e.getClass().getName()+": "+e.getMessage());
		String thisPackage = this.getClass().getPackage().getName();
		CharSequence packageName = thisPackage.subSequence(0, thisPackage.length()-1);
		for(int s = 0; s < stack.length; s++) {
			if(stack[s].toString().contains(packageName) || showFullStackTrace) {
				System.err.print("\t");
				System.err.println("at "+stack[s].toString());
			}
		}
		System.err.print("\t");
		System.err.println("at sun.awt.*(Unknown Source)");
		System.err.print("\t");
		System.err.println("~Stack trace shortened (see showFullStackTrace in AppletBase)");
	}
	
	public static double getFPS() {
		return (double)(1000000000.0/frameTime);
	}
	
	//====================LISTENERS and EVENT HANDLERS=====================
	
	public abstract void initialize();
	public abstract void run(Graphics2D g);
	public abstract void stop();
	
	public abstract void mouseEntered(MouseEvent e);
	public abstract void mouseExited(MouseEvent e);
	public abstract void mouseClicked(MouseEvent e);
	public abstract void keyTyped(KeyEvent e);
	public abstract void keyPressed(KeyEvent e);
	public abstract void keyReleased(KeyEvent e);
	public abstract void mouseDragged(MouseEvent e);
	public abstract void mousePressed(MouseEvent e);
	public abstract void mouseReleased(MouseEvent e);
	public abstract void mouseMoved(MouseEvent e);
	
	public abstract void focusGained(FocusEvent e);
	public abstract void focusLost(FocusEvent e);
	
}
