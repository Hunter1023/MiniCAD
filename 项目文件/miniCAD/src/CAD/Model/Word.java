package CAD.Model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Word extends CADShape{
	private static final long serialVersionUID = 1L;
	
	private String text;
	private Font font;
	private int fontSize;
//	private FontMetrics fontMetrics;
	
	public Word(int x1, int y1, int x2, int y2, Color color, float stroke, String text) {
		super(x1, y1, x2, y2, color, stroke);
		this.text = text;
		setType("ÎÄ×Ö");
		fontSize = (int)getHeight();
		font = new Font("Serif", Font.PLAIN, fontSize);
		setShape(new Rectangle2D.Double(
				getStartX(), getStartY(), getWidth(), getHeight()));
	}

	@Override
	public void draw(Graphics2D g2) {
		g2.setColor(getColor());
		g2.setStroke(new BasicStroke(getStroke()));
		g2.setFont(font);
		g2.drawString(text, getStartX(), getStartY()+getHeight());
		setShape(new Rectangle2D.Double(
				getStartX(), getStartY(), g2.getFontMetrics().stringWidth(text), getHeight()));
	}
	
	public String getText() { return text; }
}
