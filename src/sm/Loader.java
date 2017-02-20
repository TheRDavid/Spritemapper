package sm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

public class Loader extends JFrame {
	private boolean relist = false;
	private ProgressDialog pd;
	private int selected = -1, margin = 5;
	private JButton importDirButton = new JButton("Import Directory"), arrangeSpritemapsButton = new JButton("Arrange Spritemaps");
	private DefaultListModel<String> dlm = new DefaultListModel<>();
	private JList<String> fileList = new JList<>(dlm);
	private ArrayList<BufferedImage> images = new ArrayList<>();
	private int imgsPerLine = 0, imgFirst = 0, imgLast = 0, xOffset = 0, yOffset = 0;
	private JPanel rightPanel = new JPanel(new BorderLayout()), controlsPanel = new JPanel(new GridLayout(0, 1)), viewPanel = new JPanel() {
		protected void paintComponent(java.awt.Graphics arg0) {
			if(relist)
			{
				relist = false;
				for(int i = 0; i < images.size(); i++)
				{
					dlm.addElement("Sprite "+i);
				}
			}
			super.paintComponent(arg0);
			arg0.setColor(Color.darkGray);
			arg0.fillRect(0, 0, getWidth(), getHeight()); // stylish, ey?
			if (imgDimension == null)
				return;
			xOffset = (viewPanel.getWidth() % imgDimension.width) / 2;
			yOffset = (viewPanel.getHeight() % imgDimension.height) / 2;
			imgFirst = scrollbar.getValue() * imgsPerLine;
			imgLast = imgFirst + imgsPerLine * (viewPanel.getHeight() / imgDimension.height);
			int row = 0;
			int col = 0;
			for (int i = imgFirst; i < imgLast && i < images.size(); i++) {
				if (col >= imgsPerLine) {
					col = 0;
					row++;
				}
				arg0.drawImage(images.get(i), margin * col + xOffset + col * imgDimension.width, margin * row + yOffset + row
						* imgDimension.height, null);
				if (selected == i) {
					arg0.setColor(Color.red);
					int x = xOffset + col * imgDimension.width, y = yOffset + row * imgDimension.height;
					arg0.drawRect(margin * col + x, margin * row + y, imgDimension.width, imgDimension.height);
				}
				col++;
			}
		};
	};
	private JScrollBar scrollbar = new JScrollBar();
	private Dimension imgDimension;

	public Loader() {
		importDirButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser jfc = new JFileChooser();
				jfc.setDialogTitle("Select Folder to import");
				jfc.setApproveButtonText("Import");
				jfc.setMultiSelectionEnabled(false);
				jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jfc.setFileFilter(new FileFilter() {

					@Override
					public String getDescription() {
						return "Directory";
					}

					@Override
					public boolean accept(File arg0) {
						return arg0.isDirectory();
					}
				});
				jfc.showOpenDialog(Loader.this);
				File dir = jfc.getSelectedFile();
				if (dir.listFiles().length == 0) {
					JOptionPane.showMessageDialog(Loader.this, dir.getName() + " is empty");
					return;
				}
				System.out.println("Found " + dir.listFiles().length + " files");
				final File[] files = dir.listFiles(new java.io.FileFilter() {

					@Override
					public boolean accept(File arg0) {
						return arg0.getName().toLowerCase().endsWith(".png") || arg0.getName().toLowerCase().endsWith("jpg")
								|| arg0.getName().toLowerCase().endsWith("jpeg") || arg0.getName().toLowerCase().endsWith("gif");
					}
				});
				pd = new ProgressDialog("Importing", "Importing Files", files.length);
				System.out.println("Found " + files.length + " images");
				new Thread(new Runnable() {

					@Override
					public void run() {
						Arrays.sort(files, new Comparator<File>() {
							public int compare(File arg0, File arg1) {
								return arg0.getName().compareTo(arg1.getName());
							}
						});
						int i = 1;
						for (File f : files) {
							System.out.println(i++ + "/" + files.length);
							BufferedImage a = null;
							try {
								a = ImageIO.read(f);
								Dimension aDim = new Dimension(a.getWidth(), a.getHeight());
								if (imgDimension == null)
									imgDimension = aDim;
								if (imgDimension.equals(aDim)) 
									images.add(a);
								else
									JOptionPane.showMessageDialog(Loader.this, f.getName() + " has the wrong resolution! " + aDim);
							} catch (IOException e) {
								JOptionPane.showMessageDialog(Loader.this, "Failed to read " + f.getName());
								return;

							}
							pd.step();
						}
						imgsPerLine = viewPanel.getWidth() / imgDimension.width;
						System.out.println("Accepted " + images.size() + " images");
						scrollbar.setMaximum(images.size() / imgsPerLine + viewPanel.getHeight() / imgDimension.height + 1);
						System.out.println("images per line: " + imgsPerLine + "\tmaxscroll: " + scrollbar.getMaximum());
						relist = true;
						viewPanel.repaint();
						arrangeSpritemapsButton.setEnabled(true);
						pd.dispose();
					}
				}).start();
			}
		});
		viewPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (pd.isVisible())
					return;
				super.mouseReleased(arg0);
				updateSelection(scrollbar.getValue() * imgsPerLine + (arg0.getX() - xOffset) / imgDimension.width + (arg0.getY() - yOffset)
						/ imgDimension.height * imgsPerLine, false);
			}
		});
		fileList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if (pd != null && pd.isVisible())
					return;
				super.mouseReleased(arg0);
				updateSelection(fileList.getSelectedIndex(), true);
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				if (pd != null && pd.isVisible())
					return;
				// TODO Auto-generated method stub
				super.componentResized(arg0);
				if (imgDimension == null)
					return;
				imgsPerLine = viewPanel.getWidth() / imgDimension.width;
				scrollbar.setMaximum(images.size() / imgsPerLine + viewPanel.getHeight() / imgDimension.height + 1);
				System.out.println("images per line: " + imgsPerLine + "\tmaxscroll: " + scrollbar.getMaximum());
			}
		});
		scrollbar.addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				if (pd != null && pd.isVisible())
					return;
				viewPanel.repaint();
			}
		});
		arrangeSpritemapsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SpriteMapArrangeDialog smad = new SpriteMapArrangeDialog(images.size());
				int xT = smad.getXTiles(), yT = smad.getYTiles();
				new SpriteMapViewer(xT, yT, images, imgDimension);

			}
		});
		controlsPanel.add(importDirButton);
		controlsPanel.add(arrangeSpritemapsButton);
		arrangeSpritemapsButton.setEnabled(false);
		rightPanel.add(new JScrollPane(fileList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
				BorderLayout.CENTER);
		rightPanel.add(controlsPanel, BorderLayout.SOUTH);
		add(viewPanel, BorderLayout.CENTER);
		add(scrollbar, BorderLayout.EAST);
		add(rightPanel, BorderLayout.WEST);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Spritemapper");
		setSize(1024, 720);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void updateSelection(int s, boolean updateView) {
		System.out.println("selected = " + s);
		selected = s;
		if(updateView)
		scrollbar.setValue(selected / imgsPerLine);
		fileList.setSelectedIndex(selected);
		fileList.ensureIndexIsVisible(selected);
		viewPanel.repaint();
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new Loader();
	}

}
