package sm;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

public class ProgressDialog extends JDialog {
	private JProgressBar progressbar;
	private JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

	public ProgressDialog(String title, String message, int s) {
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		progressbar = new JProgressBar(0, s);
		progressbar.setValue(0);
		progressbar.setStringPainted(true);
		mainPanel.add(progressbar, BorderLayout.CENTER);
		mainPanel.add(new JLabel(message), BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER);
		pack();
		setTitle(title);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void step() {
		progressbar.setValue(progressbar.getValue() + 1);
		progressbar.setString(progressbar.getValue() + " / " + progressbar.getMaximum());
	}
}
