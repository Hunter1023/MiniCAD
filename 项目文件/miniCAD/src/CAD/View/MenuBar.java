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
	private boolean updateFlag = false;// �����Ƿ���Ĺ�
	private boolean saveFlag = false;// �Ƿ񱣴��
	private File file;
	private DrawPanel drawPanel;

	public MenuBar(DrawPanel drawPanel) {
		this.drawPanel = drawPanel;

		fileMenu = new JMenu("�ļ�");
		helpMenu = new JMenu("����");
		add(fileMenu);// ����ļ��˵�
		add(helpMenu);// ��Ӱ����˵�

		newItem = new JMenuItem("�½�");
		openItem = new JMenuItem("��");
		saveItem = new JMenuItem("����");
		saveAsItem = new JMenuItem("���Ϊ");
		fileMenu.add(newItem);
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		fileMenu.add(saveAsItem);

		instructionItem = new JMenuItem("����˵��");
		helpMenu.add(instructionItem);

		addListener();
	}

	private void addListener() {
		// �� ���½��� ��Ӷ�������������Ӧ�ǡ��½��ļ���
		newItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newFile();
			}
		});
		// �� ���򿪡� ��Ӷ�������������Ӧ�ǡ������ļ���
		openItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
		});
		// �� �����桱 ��Ӷ�������������Ӧ�ǡ������ļ���
		saveItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		// �� �����Ϊ�� ��Ӷ�������������Ӧ�ǡ����Ϊ�ļ���
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
						"����һ��mini CAD\n" + "��������������γ���Ƶһ�£��磺\n" + "  +��- �ı�ͼ�δ�С��< ��> �ı�������ϸ�� r ɾ��ͼ�ε�", "����˵��", -1);
			}
		});
	}

	// �½��ļ�����
	private void newFile() {
		int i = 0;
		if (drawPanel.getShapeNum() != 0 && updateFlag) {
			i = JOptionPane.showConfirmDialog(null, "��ǰͼƬ��δ���棬�Ƿ�ȷ���½���", "", JOptionPane.YES_NO_OPTION);
		}
		if (i == 0) {
			drawPanel.clearShape();
			drawPanel.repaint();
			updateFlag = false;
			saveFlag = false;
		}
	}

	// ���ļ�����
	private void openFile() {
		int flag = 0;
		// �����ͼ������ͼ�� ���� ���ݸ��Ĺ�
		if (drawPanel.getShapeNum() != 0 && updateFlag) {
			flag = JOptionPane.showConfirmDialog(null, "��ǰͼƬ��δ���棬�Ƿ�ȷ�ϴ�����ͼƬ��", "", JOptionPane.YES_NO_OPTION);
		}
		if (flag == 0) {
			chooser = new JFileChooser();
			int result = chooser.showOpenDialog(null);
			// ���ѡ����ȡ���������в���
			if (result == JFileChooser.CANCEL_OPTION) {
				return;
			}
			// ��ȡѡ����ļ���ȷ���ܷ�ִ�� ���Ϊ����
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
					JOptionPane.showMessageDialog(this, "�Ҳ������ļ�", "class not found", JOptionPane.ERROR_MESSAGE);
				} catch (ClassNotFoundException e) {
					JOptionPane.showMessageDialog(this, "Unable to Create Object", "end of file",
							JOptionPane.ERROR_MESSAGE);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "��ȡ����", "read Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	// �����ļ�����
	private void saveFile() {
		// ����Ѿ��������
		if (saveFlag) {
			writeToFile();
		} else {
			saveAsFile();// ���û�б���������������Ϊ����
		}
	}

	// ���Ϊ�ļ�����
	private void saveAsFile() {
		showSaveDialog();
		// ���û��ѡ���ļ����������ڿ�Ŀ¼��
		if (file == null || file.getName().equals("")) {
			JOptionPane.showMessageDialog(chooser, "Invalid File Name", "Invalid File Name", JOptionPane.ERROR_MESSAGE);
		} else {
			writeToFile();
		}
	}

	private void writeToFile() {
		try {
			file.delete();// ɾ���ļ�

			output = new ObjectOutputStream(new FileOutputStream(file));
			output.writeInt(drawPanel.getShapeNum());// ��������ͼ������д�뵽�ļ�
			// ��ͼ�ζ���д���ļ�
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
		// ���ѡ����ȡ���������в���
		if (result == JFileChooser.CANCEL_OPTION) {
			return;
		}
		// ��ȡѡ����ļ���ȷ���ܷ�ִ�� ���Ϊ����
		file = chooser.getSelectedFile();
	}

	public void setUpdateFlag(boolean b) {
		updateFlag = b;
	}

	public boolean getUpdateFlag() {
		return updateFlag;
	}

}
