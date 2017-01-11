package sm;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class SpriteMapArrangeDialog extends JFrame {
	private JTextField arrangeField = new JTextField("4:4");
	public SpriteMapArrangeDialog() {
		setLayout(new GridLayout(0,1));
		add(new JLabel("Specify arragnement: (xTiles:yTiles"));
	}
}
