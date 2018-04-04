package CAD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import CAD.Model.*;
import CAD.View.*;
import CAD.View.OnRightPanel.*;

/**
 * @author Hunter
 * 构建框架，添加监听
 */
public class CAD {
	private JFrame cadFrame;
	private MenuBar menuBar;
	private DrawPanel drawPanel;
	private RightPanel rightPanel;
	private ToolPanel toolPanel;
	private ColorPanel colorPanel;
	
	private Color color = Color.BLACK;
	private JComponent currentColorPanel;
	private float stroke = 3;
	private int x1, y1, x2, y2, moveX1, moveY1, moveX2, moveY2; 
	private String text;
	private CADShape currentShape;
	private int indexOfShape = -1;// 默认没有图形
	private String drawTool;
	private String ShapeType = "";

	public void start() {
		cadFrame = new JFrame("mini CAD");
		cadFrame.setIconImage(new ImageIcon("CADicon.png").getImage());
		cadFrame.setSize(800, 1000);
		cadFrame.setLayout(new BorderLayout());
		
		drawPanel = new DrawPanel();
		menuBar = new MenuBar(drawPanel);
		rightPanel = new RightPanel();
		
		cadFrame.setJMenuBar(menuBar);
		cadFrame.add(rightPanel, BorderLayout.EAST);
		cadFrame.add(drawPanel, BorderLayout.CENTER);
		
		toolPanel = new ToolPanel();
		colorPanel = new ColorPanel();
		rightPanel.add(toolPanel);
		rightPanel.add(colorPanel);	
		
		cadFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		cadFrame.setResizable(true);
		cadFrame.setVisible(true);//设置框架可见
		addListener();
	}
	
	private void addListener() {
		// 添加窗口监听
		cadFrame.addWindowListener(new WindowsListener());
		// 给颜色面板添加鼠标监听器,实时更新选择的 颜色
		for (int i = 0; i < colorPanel.getBtnsNum(); i++) {
			colorPanel.getCurrentArea(i).addMouseListener(
					new ColorMouseListener());
		}
		// 给画图面板添加键盘监听器、鼠标监听器
		drawPanel.addKeyListener(new ShapeKeyListener());
		drawPanel.addMouseListener(new ShapeMouseListener());
		drawPanel.addMouseMotionListener(new ShapeMouseListener());		
	}
	
	private class WindowsListener extends WindowAdapter {
		public void windowClosing(WindowEvent e){
    		int i = JOptionPane.YES_OPTION;
			if (drawPanel.getShapeNum()!=0 && menuBar.getUpdateFlag())
				i = JOptionPane.showConfirmDialog(null,
						"当前图片尚未保存，是否确认退出？","",JOptionPane.YES_NO_OPTION);
			if(i==JOptionPane.YES_OPTION)
				System.exit(0);
    	}
	}
	
	private class ColorMouseListener extends MouseAdapter{
		public void mousePressed(MouseEvent event) {
			currentColorPanel = (JPanel) event.getSource();
			color = currentColorPanel.getBackground();
			// 如果当前有选中的图形，给图形更新为选择的颜色
			if (currentShape != null) {
				indexOfShape = drawPanel.getShapeIndex(currentShape);
				if(currentShape.getType().equals("文字")) {
					Word w = (Word)currentShape;
					text = w.getText();
				}
				updateShape();
				indexOfShape = -1;
			}
		}
	}
	
	private class ShapeKeyListener extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent event) {
			if(event.isShiftDown() && event.getKeyChar() == '+') {
				moveX1 = 0;
				moveY1 = 0;
				moveX2 = -1;
				moveY2 = 1;
				resizeShape();
			}else if(event.getKeyChar() == '-') {
				moveX1 = 0;
				moveY1 = 0;
				moveX2 = 1;
				moveY2 = -1;
				resizeShape();
			}else if(event.isShiftDown() && event.getKeyChar() == '<') {
				//细化图形线条
				if(stroke > 0) {
					stroke = currentShape.getStroke()-1;
				}
				indexOfShape = drawPanel.getShapeIndex(currentShape);
				updateShape();
				indexOfShape = -1;
			}else if(event.isShiftDown() && event.getKeyChar() == '>') {
				//加粗图形线条
				stroke = currentShape.getStroke()+1;
				indexOfShape = drawPanel.getShapeIndex(currentShape);
				updateShape();
				indexOfShape = -1;
			}else if (event.getKeyChar() == 'r') {
				//drawPanel 删除当前图形并重画 
				indexOfShape = drawPanel.getShapeIndex(currentShape);
				drawPanel.deleteShape(indexOfShape);
				indexOfShape = -1;
				cadFrame.repaint();
			}
		}
	}
	
	// 创建应用于 画图面板的 内部鼠标监听器类
	private class ShapeMouseListener extends MouseAdapter {
		@Override
		public void mouseMoved(MouseEvent event) {
			if(drawPanel.getShape(event.getPoint()) != null) {
				if(drawTool == "移动") {
					drawPanel.setCursor( Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)); 
				}
			}else{
				drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 
			}
			drawPanel.requestFocus();//画布面板请求得到输入焦点
		}
		 
		public void mousePressed(MouseEvent event) {
			drawTool = toolPanel.getDrawTool();// 获取当前画图工具
			currentShape = drawPanel.getShape(event.getPoint());// 获取当前图形
			color = Color.BLACK;
			// 获取鼠标按下时的坐标
			x1 = x2 = event.getX();
			y1 = y2 = event.getY();
			// 如果没有选择工具
			if (drawTool.equals("移动")) {
				if (currentShape != null) {// 当前处于图形区域
					x1 = currentShape.getX1();
					y1 = currentShape.getY1();
					x2 = currentShape.getX2();
					y2 = currentShape.getY2();
					moveX1 = event.getX(); // 获取当前坐标为移动的起始点
					moveY1 = event.getY();
					ShapeType = currentShape.getType();// 获取当前图形的类型、笔画粗细、颜色
					if(ShapeType.equals("文字")) {
						Word w = (Word)currentShape;
						text = w.getText();
					}
					stroke = currentShape.getStroke();
					color = currentShape.getColor();
					indexOfShape = drawPanel.getShapeIndex(currentShape);// 获取当前图形索引
				}
			} else {
				// 如果是文字工具，展示输入对话框，获取输入文本
				if (drawTool.equals("文字")&& text == null) {
					text = JOptionPane.showInputDialog(null, "文字", "请输入");
				}
				if (!(drawTool.equals("文字") && text == null)) {// 如果选择了工具，且如果“文字”时输入了文本
					ShapeType = drawTool;// 图形的类型就是 选择工具的类型
					createShape();// 创建图形
					drawPanel.addShape(currentShape);// 添加当前图形到画图面板
					cadFrame.repaint();
					indexOfShape = drawPanel.getShapeNum() - 1;// 把图形索引设为最后一位					
				}
			}
		}

		//当鼠标进入到画布面板时
		public void mouseEntered(MouseEvent e){
			drawPanel.requestFocus();//画布面板请求得到输入焦点
		}
		
		//当鼠标移出画布面板时
		public void mouseExited(MouseEvent e){	
			drawPanel.requestFocus();//画布面板请求得到输入焦点
		}
		
		public void mouseDragged(MouseEvent event) {
			if (drawTool.equals("移动")) {
				if (indexOfShape != -1) {
					// 如果是移动图形 改变鼠标样式
					drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					moveX2 = event.getX(); // 获取实时的移动坐标
					moveY2 = event.getY();
					moveShape();// 移动图形
				}

			} else if (!drawTool.equals("直线")) {// 如果选择了工具
				x2 = event.getX();// 获取实时的坐标
				y2 = event.getY();
				drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				updateShape();
			} else {
				x2 = event.getX();// 获取实时的坐标
				y2 = event.getY();
				updateShape();
			}
			drawPanel.requestFocus();//画布面板请求得到输入焦点
		}

		public void mouseReleased(MouseEvent event) {
			drawPanel.setCursor(Cursor.getDefaultCursor());
			toolPanel.setDrawTool("移动");// 将工具面板的当前画图工具设置为移动
			text = null;
			drawTool = toolPanel.getDrawTool();// 获取当前画图工具
			indexOfShape = -1;// 图形索引设置为-1
			drawPanel.requestFocus();//画图面板得到输入焦点
		}
	}
	
	// 创建Shape
	private void createShape() {
		menuBar.setUpdateFlag(true);
		if (ShapeType.equals("直线")) {
			currentShape = new Line(x1, y1, x2, y2, color, stroke);
		} else if (ShapeType.equals("矩形")) {
			currentShape = new Rect(x1, y1, x2, y2, color, stroke);
		} else if (ShapeType.equals("椭圆")) {
			currentShape = new Ellipse(x1, y1, x2, y2, color, stroke);
		} else if (ShapeType.equals("文字")) {
			currentShape = new Word(x1, y1, x2, y2, color, stroke, text);
		}
	}

	// 更新图形
	private void updateShape() {
		createShape();
		drawPanel.setShape(indexOfShape, currentShape);
		cadFrame.repaint();
	}

	private void moveShape() {
		x1 = currentShape.getX1() + moveX2 - moveX1;
		y1 = currentShape.getY1() + moveY2 - moveY1;
		x2 = currentShape.getX2() + moveX2 - moveX1;
		y2 = currentShape.getY2() + moveY2 - moveY1;
		updateShape();
		moveX1 = moveX2;
		moveY1 = moveY2;
	}

	private void resizeShape() {
		x1 = currentShape.getX1() + moveX2 - moveX1;
		y1 = currentShape.getY1() + moveY1 - moveY2;
		x2 = currentShape.getX2() + moveX1 - moveX2;
		y2 = currentShape.getY2() + moveY2 - moveY1;
		indexOfShape = drawPanel.getShapeIndex(currentShape);
		updateShape();
		indexOfShape = -1;
	}

	public static void main(String[] args) {
		new CAD().start();
	}

}
