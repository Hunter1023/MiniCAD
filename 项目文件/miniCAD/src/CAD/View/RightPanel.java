package CAD.View;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;


public class RightPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public RightPanel() {
		setPreferredSize(new Dimension(100, 1000));
		setBackground(Color.DARK_GRAY);
		setLayout(new FlowLayout(2, 0, 0));
	}
}
