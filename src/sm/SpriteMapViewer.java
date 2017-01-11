package sm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpriteMapViewer extends JFrame {
	private Dimension imgDimension;
	private int xTiles, yTiles, numMaps;
	private JSlider slider;
	private int selected = 0;
	private JLabel statusLabel = new JLabel();
	private JButton compressButton = new JButton("Compress"), exportButton = new JButton("Export");
	private JPanel view = new JPanel() {
		protected void paintComponent(java.awt.Graphics arg0) {
			super.paintComponent(arg0);
			arg0.setColor(Color.darkGray);
			arg0.fillRect(0, 0, getWidth(), getHeight()); // stylish, ey?
			ArrayList<BufferedImage> currentImages = maps[selected];
			int row = 0, col = 0;
			for (int i = 0; i < xTiles * yTiles; i++) {
				arg0.drawImage(currentImages.get(i), col * imgDimension.width, row * imgDimension.height, null);
				if (col < xTiles - 1)
					col++;
				else {
					row++;
					col = 0;
				}
			}
		};
	};
	private ArrayList[] maps;

	public SpriteMapViewer(int xT, int yT, ArrayList<BufferedImage> imgs, Dimension iDim) {
		imgDimension = iDim;
		xTiles = xT;
		yTiles = yT;
		numMaps = imgs.size() / (xTiles * yTiles);
		maps = new ArrayList[numMaps];
		slider = new JSlider(0, numMaps - 1, 0);
		for (int i = 0; i < numMaps; i++) {
			maps[i] = new ArrayList<BufferedImage>();
			maps[i].addAll(imgs.subList(i * xT * yT, (i + 1) * xT * yT));
		}
		add(slider, BorderLayout.NORTH);
		slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				updateSelection(slider.getValue());
			}
		});
		add(view, BorderLayout.CENTER);
		setSize(imgDimension.width * xTiles + 50, imgDimension.height * yTiles + 70);
		setTitle("Map View");
		updateSelection(0);
		setVisible(true);
	}

	private void updateSelection(int selectedIndex) {
		selected = selectedIndex;
		view.repaint();
		statusLabel.setText(selected + "/"+numMaps);
	}
}
