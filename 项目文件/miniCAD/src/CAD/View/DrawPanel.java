package CAD.View;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import CAD.Model.CADShape;

public class DrawPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private ArrayList<CADShape> shapeList = new ArrayList<CADShape>();
	
	public DrawPanel() {
		setBackground(Color.WHITE);
	}
	

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);//*将设置的背景色画出来
		Graphics2D g2 = (Graphics2D) g;
		//使图像平滑
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
		for(CADShape s : shapeList) {//画出shapeList中存放的CADShape
			s.draw(g2);
		}
	}
	
	public void addShape(CADShape s) {
		shapeList.add(s);
	}
	
	public CADShape getShape(int index) { return shapeList.get(index); }
	
	public CADShape getShape(Point p) {
		for(int i = 0; i < shapeList.size(); i++) {
			if(shapeList.get(i).isContains(p)) {
				return shapeList.get(i);
			}
		}
		return null;
	}
	
	public void setShape(int i, CADShape s) {
		shapeList.set(i, s);
	}
	
	public void deleteShape(int i) {
		shapeList.remove(i);
	}
	
	public int getShapeIndex(CADShape s) {
		return shapeList.indexOf(s);
	}
	
	public int getShapeNum() {
		return shapeList.size();
	}
	
	public void clearShape() {
		shapeList.clear();
	}	
}
