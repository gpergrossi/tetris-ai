package tetris;


import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
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
	private boolean grabbed = false, mouseOver = false;

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
	
	public void update(Point mouse, boolean clicked) {
		mouseOver = (mouse.x >= x && mouse.x <= x+width && mouse.y >= y-6 && mouse.y <= y+6);
		
		if (grabbed || (mouseOver && clicked)) {
			int sliderx = mouse.x;
			if (sliderx < x) sliderx = x;
			if (sliderx > x+width) sliderx = x+width;
			value = ((sliderx-x)*(max-min))/width+min;
			grabbed = true;
		}
		if (!clicked) grabbed = false;
	}
	
	public void draw(Graphics2D g) {
	
		g.setColor(Color.WHITE);
		g.setFont(new Font("Cooper Black", Font.PLAIN, 18));
		g.drawString(name, (int) (x + (width-stringWidth(name))/2), y-7);
		
		if (mouseOver) {
			String value = String.valueOf(this.value);
			g.drawString(value, (int) (x + (width-stringWidth(value))/2), y+20);
		}
		
		g.drawLine(x, y, x+width, y);
		g.drawLine(x, y-2, x, y+2);
		g.drawLine(x+width, y-2, x+width, y+2);
		
		g.setPaint(paint);
		g.fillOval((value-min)*width/(max-min)+x-5, y-5, 10, 10);
		g.setPaint(null);
	}
	
	private int stringWidth(String str) {
		return str.length()*11;
	}
	
	public int getValue() {
		return value;
	}
	
}
