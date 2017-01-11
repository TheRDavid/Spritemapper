package sm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class SpriteMapArrangeDialog extends JDialog {
	private JTextField arrangeField = new JTextField();
	private JPanel controlsPanel = new JPanel();
	private JLabel leftovers = new JLabel();
	private JButton okButton = new JButton("Alright");
	private JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
	private int num = 0, n0, n1;

	public SpriteMapArrangeDialog(final int numImages) {
		arrangeField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				super.keyReleased(arg0);
				okButton.setEnabled(false);
				try {
					n0 = Integer.parseInt(arrangeField.getText().substring(0, arrangeField.getText().indexOf(",")));
					n1 = Integer.parseInt(arrangeField.getText().substring(arrangeField.getText().indexOf(",") + 1));
					num = n0 * n1;
					int numMaps = numImages / num;
					if ((numImages - num) >= 0) {
						leftovers.setText(numMaps + " Spritemaps, " + (numImages - numMaps * num) + " images left over");
						okButton.setEnabled(true);
					} else {
						leftovers.setText("Not enough images!");
					}
				} catch (Exception sioobe) {
					leftovers.setText("Invalid Input");
				}
			}
		});
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		leftovers.setText("Invalid Input");
		setLayout(new GridLayout(0, 1, 10, 10));
		controlsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
		controlsPanel.add(leftovers);
		controlsPanel.add(okButton);
		add(mainPanel, BorderLayout.CENTER);
		mainPanel.add(new JLabel("Specify arragnement: (xTiles,yTiles) for " + numImages + " images"), BorderLayout.NORTH);
		mainPanel.add(arrangeField, BorderLayout.CENTER);
		mainPanel.add(controlsPanel, BorderLayout.SOUTH);
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		pack();
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setTitle("Arrange Spritemaps");
		setModal(true);
		setVisible(true);
	}

	public int getXTiles() {
		return n0;
	}

	public int getYTiles() {
		return n1;
	}
}
