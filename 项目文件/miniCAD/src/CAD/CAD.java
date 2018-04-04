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
 * ������ܣ���Ӽ���
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
	private int indexOfShape = -1;// Ĭ��û��ͼ��
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
		cadFrame.setVisible(true);//���ÿ�ܿɼ�
		addListener();
	}
	
	private void addListener() {
		// ��Ӵ��ڼ���
		cadFrame.addWindowListener(new WindowsListener());
		// ����ɫ��������������,ʵʱ����ѡ��� ��ɫ
		for (int i = 0; i < colorPanel.getBtnsNum(); i++) {
			colorPanel.getCurrentArea(i).addMouseListener(
					new ColorMouseListener());
		}
		// ����ͼ�����Ӽ��̼���������������
		drawPanel.addKeyListener(new ShapeKeyListener());
		drawPanel.addMouseListener(new ShapeMouseListener());
		drawPanel.addMouseMotionListener(new ShapeMouseListener());		
	}
	
	private class WindowsListener extends WindowAdapter {
		public void windowClosing(WindowEvent e){
    		int i = JOptionPane.YES_OPTION;
			if (drawPanel.getShapeNum()!=0 && menuBar.getUpdateFlag())
				i = JOptionPane.showConfirmDialog(null,
						"��ǰͼƬ��δ���棬�Ƿ�ȷ���˳���","",JOptionPane.YES_NO_OPTION);
			if(i==JOptionPane.YES_OPTION)
				System.exit(0);
    	}
	}
	
	private class ColorMouseListener extends MouseAdapter{
		public void mousePressed(MouseEvent event) {
			currentColorPanel = (JPanel) event.getSource();
			color = currentColorPanel.getBackground();
			// �����ǰ��ѡ�е�ͼ�Σ���ͼ�θ���Ϊѡ�����ɫ
			if (currentShape != null) {
				indexOfShape = drawPanel.getShapeIndex(currentShape);
				if(currentShape.getType().equals("����")) {
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
				//ϸ��ͼ������
				if(stroke > 0) {
					stroke = currentShape.getStroke()-1;
				}
				indexOfShape = drawPanel.getShapeIndex(currentShape);
				updateShape();
				indexOfShape = -1;
			}else if(event.isShiftDown() && event.getKeyChar() == '>') {
				//�Ӵ�ͼ������
				stroke = currentShape.getStroke()+1;
				indexOfShape = drawPanel.getShapeIndex(currentShape);
				updateShape();
				indexOfShape = -1;
			}else if (event.getKeyChar() == 'r') {
				//drawPanel ɾ����ǰͼ�β��ػ� 
				indexOfShape = drawPanel.getShapeIndex(currentShape);
				drawPanel.deleteShape(indexOfShape);
				indexOfShape = -1;
				cadFrame.repaint();
			}
		}
	}
	
	// ����Ӧ���� ��ͼ���� �ڲ�����������
	private class ShapeMouseListener extends MouseAdapter {
		@Override
		public void mouseMoved(MouseEvent event) {
			if(drawPanel.getShape(event.getPoint()) != null) {
				if(drawTool == "�ƶ�") {
					drawPanel.setCursor( Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)); 
				}
			}else{
				drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); 
			}
			drawPanel.requestFocus();//�����������õ����뽹��
		}
		 
		public void mousePressed(MouseEvent event) {
			drawTool = toolPanel.getDrawTool();// ��ȡ��ǰ��ͼ����
			currentShape = drawPanel.getShape(event.getPoint());// ��ȡ��ǰͼ��
			color = Color.BLACK;
			// ��ȡ��갴��ʱ������
			x1 = x2 = event.getX();
			y1 = y2 = event.getY();
			// ���û��ѡ�񹤾�
			if (drawTool.equals("�ƶ�")) {
				if (currentShape != null) {// ��ǰ����ͼ������
					x1 = currentShape.getX1();
					y1 = currentShape.getY1();
					x2 = currentShape.getX2();
					y2 = currentShape.getY2();
					moveX1 = event.getX(); // ��ȡ��ǰ����Ϊ�ƶ�����ʼ��
					moveY1 = event.getY();
					ShapeType = currentShape.getType();// ��ȡ��ǰͼ�ε����͡��ʻ���ϸ����ɫ
					if(ShapeType.equals("����")) {
						Word w = (Word)currentShape;
						text = w.getText();
					}
					stroke = currentShape.getStroke();
					color = currentShape.getColor();
					indexOfShape = drawPanel.getShapeIndex(currentShape);// ��ȡ��ǰͼ������
				}
			} else {
				// ��������ֹ��ߣ�չʾ����Ի��򣬻�ȡ�����ı�
				if (drawTool.equals("����")&& text == null) {
					text = JOptionPane.showInputDialog(null, "����", "������");
				}
				if (!(drawTool.equals("����") && text == null)) {// ���ѡ���˹��ߣ�����������֡�ʱ�������ı�
					ShapeType = drawTool;// ͼ�ε����;��� ѡ�񹤾ߵ�����
					createShape();// ����ͼ��
					drawPanel.addShape(currentShape);// ��ӵ�ǰͼ�ε���ͼ���
					cadFrame.repaint();
					indexOfShape = drawPanel.getShapeNum() - 1;// ��ͼ��������Ϊ���һλ					
				}
			}
		}

		//�������뵽�������ʱ
		public void mouseEntered(MouseEvent e){
			drawPanel.requestFocus();//�����������õ����뽹��
		}
		
		//������Ƴ��������ʱ
		public void mouseExited(MouseEvent e){	
			drawPanel.requestFocus();//�����������õ����뽹��
		}
		
		public void mouseDragged(MouseEvent event) {
			if (drawTool.equals("�ƶ�")) {
				if (indexOfShape != -1) {
					// ������ƶ�ͼ�� �ı������ʽ
					drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					moveX2 = event.getX(); // ��ȡʵʱ���ƶ�����
					moveY2 = event.getY();
					moveShape();// �ƶ�ͼ��
				}

			} else if (!drawTool.equals("ֱ��")) {// ���ѡ���˹���
				x2 = event.getX();// ��ȡʵʱ������
				y2 = event.getY();
				drawPanel.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				updateShape();
			} else {
				x2 = event.getX();// ��ȡʵʱ������
				y2 = event.getY();
				updateShape();
			}
			drawPanel.requestFocus();//�����������õ����뽹��
		}

		public void mouseReleased(MouseEvent event) {
			drawPanel.setCursor(Cursor.getDefaultCursor());
			toolPanel.setDrawTool("�ƶ�");// ���������ĵ�ǰ��ͼ��������Ϊ�ƶ�
			text = null;
			drawTool = toolPanel.getDrawTool();// ��ȡ��ǰ��ͼ����
			indexOfShape = -1;// ͼ����������Ϊ-1
			drawPanel.requestFocus();//��ͼ���õ����뽹��
		}
	}
	
	// ����Shape
	private void createShape() {
		menuBar.setUpdateFlag(true);
		if (ShapeType.equals("ֱ��")) {
			currentShape = new Line(x1, y1, x2, y2, color, stroke);
		} else if (ShapeType.equals("����")) {
			currentShape = new Rect(x1, y1, x2, y2, color, stroke);
		} else if (ShapeType.equals("��Բ")) {
			currentShape = new Ellipse(x1, y1, x2, y2, color, stroke);
		} else if (ShapeType.equals("����")) {
			currentShape = new Word(x1, y1, x2, y2, color, stroke, text);
		}
	}

	// ����ͼ��
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
