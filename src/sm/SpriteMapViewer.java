package sm;

import im.ImgTools;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SpriteMapViewer extends JFrame {
	private ProgressDialog pd;
	private Dimension imgDimension;
	private int xTiles, yTiles, numMaps;
	private JSlider slider;
	private int selected = 0;
	private JLabel statusLabel = new JLabel();
	private JButton compressButton = new JButton("Export Compressed"), exportButton = new JButton("Export");
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
	}, lowerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
		add(lowerPanel, BorderLayout.SOUTH);
		lowerPanel.add(statusLabel);
		lowerPanel.add(exportButton);
		lowerPanel.add(compressButton);
		exportButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				export();
			}
		});
		compressButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser jfc = new JFileChooser();
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setDialogTitle("Select Directory to export into");
				jfc.setApproveButtonText("Export");
				jfc.showSaveDialog(SpriteMapViewer.this);
				if (jfc.getSelectedFile() == null)
					return;
				if (numMaps % 2 != 0)
					JOptionPane.showMessageDialog(SpriteMapViewer.this, "Number of maps not even! The last Spritemap will be left out");
				pd = new ProgressDialog("Exporting", "Exporting & Compressing", numMaps);
				new Thread(new Runnable() {

					@Override
					public void run() {
						int f = 0;
						ProgressDialog pd = new ProgressDialog("Exporting", "Compressing & Exporting", numMaps / 2);
						for (int m = 0; m < maps.length; m += 2) {
							ArrayList<BufferedImage> mapA = maps[m];
							ArrayList<BufferedImage> mapB = maps[m + 1];
							ArrayList<BufferedImage> mapC = new ArrayList<>();
							for (int k = 0; k < mapA.size(); k++) {
								mapC.add(ImgTools.merge(mapA.get(k), mapB.get(k)));
							}
							BufferedImage aImg = new BufferedImage(xTiles * imgDimension.width, yTiles * imgDimension.height,
									BufferedImage.TYPE_INT_ARGB);
							Graphics g = aImg.getGraphics();
							int row = 0, col = 0;
							for (int i = 0; i < xTiles * yTiles; i++) {
								for (int x = col * imgDimension.width; x < col * imgDimension.width + imgDimension.width; x++) {
									for (int y = row * imgDimension.height; y < row * imgDimension.height + imgDimension.height; y++) {
										aImg.setRGB(x, y, mapC.get(i).getRGB(x - col * imgDimension.width, y - row * imgDimension.height));
									}
								}
								if (col < xTiles - 1)
									col++;
								else {
									row++;
									col = 0;
								}
							}
							try {
								ImageIO.write(aImg, "png", new File(jfc.getSelectedFile().getAbsoluteFile() + "/" + f++ + ".png"));
							} catch (IOException e) {
								JOptionPane.showMessageDialog(SpriteMapViewer.this, "Export Error: " + e.getMessage());
								e.printStackTrace();
							}
							pd.step();
						}
						pd.dispose();
					}
				}).start();
			}
		});
		setSize(imgDimension.width * xTiles + 50, imgDimension.height * yTiles + 70);
		setTitle("Map View");
		updateSelection(0);
		setVisible(true);
	}

	private void export() {
		final JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setDialogTitle("Select Directory to export into");
		jfc.setApproveButtonText("Export");
		jfc.showSaveDialog(SpriteMapViewer.this);
		if (jfc.getSelectedFile() == null)
			return;
		pd = new ProgressDialog("Exporting", "Exporting Spritemaps", numMaps);
		new Thread(new Runnable() {

			@Override
			public void run() {
				int f = 0;
				for (ArrayList<BufferedImage> map : maps) {
					BufferedImage aImg = new BufferedImage(xTiles * imgDimension.width, yTiles * imgDimension.height,
							BufferedImage.TYPE_INT_ARGB);
					Graphics g = aImg.getGraphics();
					int row = 0, col = 0;
					for (int i = 0; i < xTiles * yTiles; i++) {
						g.drawImage(map.get(i), col * imgDimension.width, row * imgDimension.height, null);
						if (col < xTiles - 1)
							col++;
						else {
							row++;
							col = 0;
						}
					}
					try {
						ImageIO.write(aImg, "png", new File(jfc.getSelectedFile().getAbsoluteFile() + "/" + f++ + ".png"));
					} catch (IOException e) {
						JOptionPane.showMessageDialog(SpriteMapViewer.this, "Export Error: " + e.getMessage());
						e.printStackTrace();
					}
					pd.step();
				}
				pd.dispose();
			}
		}).start();
	}

	private void updateSelection(int selectedIndex) {
		selected = selectedIndex;
		view.repaint();
		statusLabel.setText((1 + selected) + "/" + numMaps);
	}
}
