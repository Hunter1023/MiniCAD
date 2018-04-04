package CAD.View.OnRightPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;


public class ColorPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private ColorBtn[] colorAreas;

	public ColorPanel() {
		setSize(100, 100);
		setLayout(new GridLayout(3, 3, 0, 0));
		addColorBtns();
	}
	
	private void addColorBtns() {
		Color[] colors = { Color.BLACK, Color.BLUE, Color.CYAN, 
				Color.GRAY, Color.GREEN, Color.ORANGE,
				Color.PINK, Color.RED, Color.YELLOW };
		
		colorAreas = new ColorBtn[9];
		for(int i= 0; i < colors.length; i++) {
			colorAreas[i] = new ColorBtn(colors[i]);
			add(colorAreas[i]);
		}
	}
	
	private class ColorBtn extends JPanel{
		private static final long serialVersionUID = 1L;

		public ColorBtn(Color c) {
			setBackground(c);// 设置Button背景颜色,即可选的颜色
			setPreferredSize(new Dimension(33, 33));// 设置Button大小
		}
	}
	
	public int getBtnsNum() { 
		return colorAreas.length; 
	}
	
	public JPanel getCurrentArea(int i) {
		return colorAreas[i];
	}
}


