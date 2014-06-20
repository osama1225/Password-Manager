import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddDomain extends JDialog {

	private JLabel domain;
	private JLabel pass;

	private JTextField domainT;
	private JTextField passT;

	private JButton done;
	private JButton cancel;

	private JPanel centerPanel;
	private JPanel southPanel;

	private updateView uV;

	public AddDomain(String name, String pas) {
		setLayout(new BorderLayout());
		setSize(200, 200);

		// ini
		domain = new JLabel("Domain:");
		pass = new JLabel("Pass:    ");

		domainT = new JTextField(10);
		passT = new JTextField(10);

		domainT.setText(name);
		passT.setText(pas);
		if (!name.equals(""))
			domainT.setEnabled(false);

		centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 20));
		centerPanel.setBounds(0, 0, 200, 200);
		centerPanel.add(domain);
		centerPanel.add(domainT);
		centerPanel.add(pass);
		centerPanel.add(passT);

		done = new JButton("Done");
		cancel = new JButton("Cancel");

		southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
		southPanel.add(done);
		southPanel.add(cancel);

		setListeners();

		add(centerPanel, "Center");
		add(southPanel, "South");
	}

	public void setuV(updateView uV) {
		this.uV = uV;
	}

	private void setListeners() {
		done.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				String domainName = domainT.getText();
				String pass = passT.getText();
				if (domainName == null || domainName.equals("") || pass == null
						|| pass.equals(""))
					JOptionPane.showMessageDialog(null,
							"Please fill all Fields");
				else {
					// do some stuff with the new entry
					notifyListener(domainName, pass);
				}
				close();
			}
		});
		cancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				close();
			}
		});
	}

	private void notifyListener(String name, String pass) {
		if (uV != null)
			uV.update(name, pass);
	}

	private void close() {
		centerPanel.removeAll();
		southPanel.removeAll();
		removeAll();
		invalidate();
		MainView.messageDialog.dispose();
	}

}
