package CAD.Model;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Line extends CADShape {
	private static final long serialVersionUID = 1L;
	
	private double x1, y1, x2, y2;

	public Line(int x1, int y1, int x2, int y2, Color color, float stroke) {
		super(x1, y1, x2, y2, color, stroke);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		setType("Ö±Ïß");
		setShape(new Line2D.Double(this.x1, this.y1, this.x2, this.y2));
	}
	
	@Override
	public boolean isContains(Point p) {
		Line2D line = (Line2D)getShape();
		if(line.ptLineDist((Point2D)p) <= 2 ) {
			return true;
		}else
			return false;
	}

}
