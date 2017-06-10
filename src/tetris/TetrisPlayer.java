package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TetrisPlayer implements Runnable {

	public static void main(String[] args) {
		new TetrisPlayer();
	}
	
	TetrisImpl tetris;
	TetrisGraphics graphics;
	
	TetrisBot bot;
	TetrisImpl tetris2;
	TetrisGraphics graphics2;
	Slider slider;
	
	JFrame frame;
	JPanel panel;
	
	Thread thread;
	boolean running;
	
	MidiMusic music;
	
	int msPerFrame = 1000/60;
	
	public TetrisPlayer() {
		this.thread = new Thread(this);
		this.running = true;
		this.thread.start();
	}

	@Override
	public void run() {
		init();
		
		Thread botThread = new Thread(new Runnable() {
			public void run() {
				botRun();
			}
		});
		botThread.start();

		long lastUpdate = System.currentTimeMillis();
		while (running) {
			long now = System.currentTimeMillis();
			if (now - lastUpdate >= msPerFrame) {
				update((int) (now - lastUpdate));
				lastUpdate = now;
			}
			Thread.yield();
		}
	}
	
	int timeSinceLastTick = 0;
	
	public void update(int deltaMS) {
		timeSinceLastTick += deltaMS;
		
		if (timeSinceLastTick > 600) {
			tetris.updateTick();
			tetris2.updateTick();
			timeSinceLastTick = 0;
		}
		
		panel.repaint();
	}
	
	public void botRun() {
		long lastUpdate = System.currentTimeMillis();
		while (running) {
			long now = System.currentTimeMillis();
			updateBot((int) (now - lastUpdate));
			lastUpdate = now;
			Thread.yield();
		}
	}
	
	int timeSinceLastBotPlay = 0;
	int timeSinceLastMessage = 0;
	int stepsSinceLastMessage = 0;
	
	public void updateBot(int deltaMS) {
		timeSinceLastMessage += deltaMS;
		if (timeSinceLastMessage > 500) {
			System.out.println("Bot processed "+stepsSinceLastMessage+" steps");
			if (deltaMS > slider.getValue()*10) {
				System.out.println("Can't keep up, skipping "+(deltaMS - slider.getValue()*10)+" ms");
				deltaMS = slider.getValue()*10;
			}
			timeSinceLastMessage = 0;
			stepsSinceLastMessage = 0;
		}
		
		timeSinceLastBotPlay += deltaMS;
		while (timeSinceLastBotPlay > slider.getValue()) {
			bot.play();
			stepsSinceLastMessage++;
			timeSinceLastBotPlay -= slider.getValue();
		}
	}
	
	private void init() {
		this.frame = new JFrame("Tetris");
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.tetris = new TetrisImpl();
		this.graphics = new TetrisGraphics(tetris);
		
		this.tetris2 = new TetrisImpl();
		this.graphics2 = new TetrisGraphics(tetris2);
		this.bot = new TetrisBot(tetris2);
		
		this.slider = new Slider("Uninitialized", 0, 0, 0, 0, 0, 0);
		
		this.panel = new JPanel() {
			private static final long serialVersionUID = -6217983718558047505L;

			{
				addKeyListener(TetrisPlayer.this.createKeyListener());
				
				MouseAdapter mouseAdapter = TetrisPlayer.this.createMouseListener();
				addMouseListener(mouseAdapter);
				addMouseMotionListener(mouseAdapter);
				addMouseWheelListener(mouseAdapter);
				
				setPreferredSize(new Dimension(800, 600));
				setSize(new Dimension(800, 600));
				setDoubleBuffered(true);
				setFocusable(true);
				setFocusTraversalKeysEnabled(false);
				requestFocus();
			}
			
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				int areaWidth = (int) (graphics.getSize().getWidth() + 50 + graphics2.getSize().getWidth());
				int areaHeight = (int) Math.max(graphics.getSize().getHeight(), graphics2.getSize().getHeight());
				
				AffineTransform old = g2d.getTransform();
				
				g2d.translate((getWidth() - areaWidth) / 2, (getHeight() - areaHeight) / 2);
				graphics.draw(g2d);
				
				g2d.translate(graphics.getSize().getWidth() + 50, 0);
				graphics2.draw(g2d);
				
				g2d.setTransform(old);				
				slider.draw(g2d);
			}
			
		};
		
		int areaWidth = (int) (graphics.getSize().getWidth() + 50 + graphics2.getSize().getWidth());
		int areaHeight = (int) graphics2.getSize().getHeight();
		int x = (panel.getWidth() - areaWidth) / 2;
		x += graphics.getSize().getWidth() + 50;
		x += graphics2.getSize().getWidth()/2 - 100;
		int y = (panel.getHeight() - areaHeight) / 2;
		y += areaHeight + 25;
		this.slider = new Slider("AI Delay", x, y, 200, 1, 500, 100);
		
		this.frame.add(this.panel);
		this.frame.pack();
		this.frame.setVisible(true);
		
		// Start music
		music = new MidiMusic("media/Tetris.mid");
		MidiMusic.initialize();
		MidiMusic.setLoop(true);
		music.setTrack();
		MidiMusic.play();
		MidiMusic.setBPM(140.0f);
	}

	protected MouseAdapter createMouseListener() {
		return new MouseAdapter() {
			boolean leftClick = false;
			
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) leftClick = true;
				slider.update(e.getPoint(), leftClick);
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) leftClick = false;
				slider.update(e.getPoint(), leftClick);
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				int oldValue = slider.getValue();
				slider.update(e.getPoint(), leftClick);
				if (slider.getValue() != oldValue) {
					timeSinceLastBotPlay = 0;
				}
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				slider.update(e.getPoint(), false);
			}
		};
	}

	protected KeyListener createKeyListener() {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
					tetris.moveLeft();
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
					tetris.moveRight();
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
					tetris.rotateRight();
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
					tetris.softDrop();
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
					tetris.hardDrop();
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_TAB) {
					tetris.swap();
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
					tetris.restart();
				}
			}
		};
	}
	
}
