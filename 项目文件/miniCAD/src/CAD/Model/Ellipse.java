package CAD.Model;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

public class Ellipse extends CADShape {
	private static final long serialVersionUID = 1L;

	public Ellipse(int x1, int y1, int x2, int y2, Color color, float stroke) {
		super(x1, y1, x2, y2, color, stroke);
		setType("Õ÷‘≤");
		setShape(new Ellipse2D.Double(
				getStartX(), getStartY(), getWidth(), getHeight()));
	}
}
