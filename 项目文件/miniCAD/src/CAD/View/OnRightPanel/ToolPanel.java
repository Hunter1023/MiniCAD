package CAD.View.OnRightPanel;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class ToolPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private String drawTool;// 工具栏选择的图形工具

	public ToolPanel() {
		setPreferredSize(new Dimension(100, 400));
		setLayout(new GridLayout(4, 1, 0, 0));
		drawTool = "移动";
		addToolBtns();
	}

	private void addToolBtns() {
		ToolBtn rect, ellipse, line, text;

		rect = new ToolBtn("rec.jpg", "矩形");
		ellipse = new ToolBtn("circle.jpg", "椭圆");
		line = new ToolBtn("line.jpg", "直线");
		text = new ToolBtn("text.jpg", "文字");
		add(rect);
		add(ellipse);
		add(line);
		add(text);
	}
	
	private class ToolBtn extends JButton{
		private static final long serialVersionUID = 1L;

		public ToolBtn(String fileName,String tip) {
			super(new ImageIcon(fileName));
			setPreferredSize(new Dimension(100, 100));
			setToolTipText(tip);
			setActionCommand(tip);
			//给按钮添加动作监听，获取actionCommand
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					drawTool = event.getActionCommand();
				}
			});
		}
	}

	public void setDrawTool(String drawTool) {
		this.drawTool = drawTool;
	}

	public String getDrawTool() {
		return drawTool;
	}

}
