package CAD.View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import CAD.Model.CADShape;

public class MenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;
	private JMenu fileMenu;
	private JMenu helpMenu;
	private JMenuItem newItem;
	private JMenuItem openItem;
	private JMenuItem saveItem;
	private JMenuItem saveAsItem;
	private JMenuItem instructionItem;

	private ObjectInputStream input;
	private ObjectOutputStream output;
	private JFileChooser chooser;
	private boolean updateFlag = false;// 内容是否更改过
	private boolean saveFlag = false;// 是否保存过
	private File file;
	private DrawPanel drawPanel;

	public MenuBar(DrawPanel drawPanel) {
		this.drawPanel = drawPanel;

		fileMenu = new JMenu("文件");
		helpMenu = new JMenu("帮助");
		add(fileMenu);// 添加文件菜单
		add(helpMenu);// 添加帮助菜单

		newItem = new JMenuItem("新建");
		openItem = new JMenuItem("打开");
		saveItem = new JMenuItem("保存");
		saveAsItem = new JMenuItem("另存为");
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);

		instructionItem = new JMenuItem("操作说明");
		helpMenu.add(instructionItem);

		addListener();
	}

	private void addListener() {
		// 给 “新建” 添加动作监听器，响应是“新建文件”
		newItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFile();
			}
		});
		// 给 “打开” 添加动作监听器，响应是“载入文件”
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		// 给 “保存” 添加动作监听器，响应是“保存文件”
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		// 给 “另存为” 添加动作监听器，响应是“另存为文件”
		saveAsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAsFile();
			}
		});

		instructionItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null,
						"这是一个mini CAD\n" + "基本操作方法与课程视频一致，如：\n" + "  +、- 改变图形大小，< 、> 改变线条粗细， r 删除图形等", "操作说明", -1);
			}
		});
	}

	// 新建文件方法
	private void newFile() {
		int i = 0;
		if (drawPanel.getShapeNum() != 0 && updateFlag) {
			i = JOptionPane.showConfirmDialog(null, "当前图片尚未保存，是否确认新建？", "", JOptionPane.YES_NO_OPTION);
		}
		if (i == 0) {
			drawPanel.clearShape();
			drawPanel.repaint();
			updateFlag = false;
			saveFlag = false;
		}
	}

	// 打开文件方法
	private void openFile() {
		int flag = 0;
		// 如果画图面板存在图形 而且 内容更改过
		if (drawPanel.getShapeNum() != 0 && updateFlag) {
			flag = JOptionPane.showConfirmDialog(null, "当前图片尚未保存，是否确认打开其他图片？", "", JOptionPane.YES_NO_OPTION);
		}
		if (flag == 0) {
			chooser = new JFileChooser();
			int result = chooser.showOpenDialog(null);
			// 如果选择了取消，不进行操作
			if (result == JFileChooser.CANCEL_OPTION) {
				return;
			}
			// 获取选择的文件，确认能否执行 另存为操作
			file = chooser.getSelectedFile();

			if (file == null || file.getName().equals("")) {
				JOptionPane.showMessageDialog(chooser, "Invalid File Name", "Invalid File Name",
						JOptionPane.ERROR_MESSAGE);
			} else {
				try {
					input = new ObjectInputStream(new FileInputStream(file));
					drawPanel.clearShape();
					CADShape inputRecord;

					int countNum = input.readInt();
					for (int i = 0; i < countNum; i++) {
						inputRecord = (CADShape) input.readObject();
						drawPanel.addShape(inputRecord);
					}
					input.close();
					drawPanel.repaint();
					saveFlag = true;
					updateFlag = false;
				} catch (FileNotFoundException e) {
					JOptionPane.showMessageDialog(this, "找不到该文件", "class not found", JOptionPane.ERROR_MESSAGE);
				} catch (ClassNotFoundException e) {
					JOptionPane.showMessageDialog(this, "Unable to Create Object", "end of file",
							JOptionPane.ERROR_MESSAGE);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "读取错误", "read Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	// 保存文件方法
	private void saveFile() {
		// 如果已经被保存过
		if (saveFlag) {
			writeToFile();
		} else {
			saveAsFile();// 如果没有被保存过，调用另存为方法
		}
	}

	// 另存为文件方法
	private void saveAsFile() {
		showSaveDialog();
		// 如果没有选择文件，或者是在空目录下
		if (file == null || file.getName().equals("")) {
			JOptionPane.showMessageDialog(chooser, "Invalid File Name", "Invalid File Name", JOptionPane.ERROR_MESSAGE);
		} else {
			writeToFile();
		}
	}

	private void writeToFile() {
		try {
			file.delete();// 删除文件

			output = new ObjectOutputStream(new FileOutputStream(file));
			output.writeInt(drawPanel.getShapeNum());// 将画布的图形数量写入到文件
			// 将图形对象写入文件
			for (int i = 0; i < drawPanel.getShapeNum(); i++) {
				CADShape s = drawPanel.getShape(i);
				output.writeObject(s);
				output.flush();
			}
			output.close();
			updateFlag = false;
			saveFlag = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void showSaveDialog() {
		chooser = new JFileChooser();
		int result = chooser.showSaveDialog(null);
		// 如果选择了取消，不进行操作
		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}
		// 获取选择的文件，确认能否执行 另存为操作
		file = chooser.getSelectedFile();
	}

	public void setUpdateFlag(boolean b) {
		updateFlag = b;
	}

	public boolean getUpdateFlag() {
		return updateFlag;
	}

}
