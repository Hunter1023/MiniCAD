package CAD.Model;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

public class Rect extends CADShape {
	private static final long serialVersionUID = 1L;

	public Rect(int x1, int y1, int x2, int y2, Color color, float stroke) {
		super(x1, y1, x2, y2, color, stroke);
		setType("¾ØÐÎ");
		setShape(new Rectangle2D.Double(
				getStartX(), getStartY(), getWidth(), getHeight()));
	}
}
