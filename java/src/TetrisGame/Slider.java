package TetrisGame;


import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * A quick class thrown together to get a graphical slider
 * for the bot speed. Not intended for actual use anywhere
 * but here because it is of terrible design.
 * 
 * @author Gregary Pergrossi (gpergrossi@imsa.edu)
 */
public class Slider {
	
	private	GradientPaint paint;
	private String name;
	private int x, y, width, min, max, value;
	private boolean grabbed = false;

	public Slider(String name, int x, int y, int width, int min, int max, int value) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.min = min;
		this.max = max;
		this.value = value;
		paint = new GradientPaint(0, y-5, Color.LIGHT_GRAY, 0, y+5, Color.DARK_GRAY);
	}
	
	public void update(Point mouse, int mouseclick) {
		if(grabbed || (mouse.x >= x && mouse.x <= x+width && mouse.y >= y-6 && mouse.y <= y+6 && mouseclick == 1)) {
			int sliderx = mouse.x;
			if(sliderx < x) sliderx = x;
			if(sliderx > x+width) sliderx = x+width;
			value = ((sliderx-x)*(max-min))/width+min;
			grabbed = true;
		}
		if(mouseclick != 1) grabbed = false;
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Cooper Black", Font.PLAIN, 18));
		g.drawString(name, x+50, y-7);
		g.drawLine(x, y, x+width, y);
		g.drawLine(x, y-2, x, y+2);
		g.drawLine(x+width, y-2, x+width, y+2);
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(paint);
		g.fillOval((value-min)*width/(max-min)+x-5, y-5, 10, 10);
		g2.setPaint(null);
	}
	
	public int getValue() {
		return value;
	}
	
}
