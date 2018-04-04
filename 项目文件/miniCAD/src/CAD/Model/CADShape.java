package CAD.Model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class CADShape implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private int x1, y1, x2, y2, startX, startY, width, height;
	private String type;
	private Color color;
	private float stroke;
	private Shape shape;
	
	public CADShape(int x1, int y1, int x2, int y2, Color color, float stroke) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		startX = Math.min(x1, x2);
		startY = Math.min(y1, y2);
		width = Math.abs(x1 - x2);
		height = Math.abs(y1 - y2);
		this.color = color;
		this.stroke = stroke;
	}

	public void draw(Graphics2D g2) {
		g2.setColor(color);
		g2.setStroke(new BasicStroke(stroke));
		g2.draw(shape);
	}
	
	public void setShape(Shape shape) { this.shape =shape; }
	public Shape getShape() { return shape; }
	
	public void setType(String type) { this.type = type; }
	public String getType() { return type; }
	
	public int getStartX() { return startX; }
	public int getStartY() { return startY; }
	public int getX1() { return x1; }
	public int getY1() { return y1; }
	public int getX2() { return x2; }
	public int getY2() { return y2; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	
	public Color getColor() { return color; }	
	public float getStroke() { return stroke; }

	public boolean isContains(Point p){
		return shape.contains((Point2D)p);
	}
}
