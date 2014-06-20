import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MainView extends JFrame {

	private JPanel buttonsPanel;

	private JButton addButton;
	private JButton removeButton;
	private JButton LookUpButton;

	protected static JDialog messageDialog;
	protected static JFrame reference;

	private PasswordManager manger;
	private HashMap<byte[], byte[]> refMap;

	private String name, pass;
	private AddDomain newDomain;

	private String masterPassWord;

	public MainView() {
		setSize(500, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// setResizable(false);
		setTitle("PassWord Manager");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				MainView.class.getResource("/key.png")));
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);

		// ini
		reference = this;
		manger = new PasswordManager();

		messageDialog = new JDialog();

		addButton = new JButton("Add/Modify");
		removeButton = new JButton("Remove");
		LookUpButton = new JButton("LookUp");

		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
		buttonsPanel.add(addButton);
		buttonsPanel.add(removeButton);
		buttonsPanel.add(LookUpButton);

		// add panels to frame
		add(buttonsPanel, "South");

		// set listeners
		setListeners();

		name = "";
		pass = "";
		setVisible(true);
		getMasterPassWord();

	}

	private void setListeners() {
		addButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (masterPassWord == null || masterPassWord.equals("")) {
					getMasterPassWord();
					if (masterPassWord == null || masterPassWord.equals(""))
						return;
				}

				add();
			}
		});
		removeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				String name = JOptionPane.showInputDialog(
						"Enter DomainName to remove:", null);
				if (name != null) {
					manger.removeDomain(name);
				}
			}
		});
		LookUpButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				String name = JOptionPane.showInputDialog("Enter Domain Name:",
						null);
				if (name != null) {
					String pass = manger.getPassword(name);
					if (!pass.equals(""))
						JOptionPane.showMessageDialog(reference,
								"Password is : " + pass);
					else
						JOptionPane.showMessageDialog(null,
								"Domain Named Not Found!" + pass);

				}
			}
		});

	}

	private void add() {
		name = "";
		pass = "";
		newDomain = new AddDomain(name, pass);
		newDomain.setuV(new updateView() {

			@Override
			public void update(String name, String pass) {
				// check if it's an update or new
				// add to hashmap
				manger.encrypt(name, pass);
			}
		});

		messageDialog.setSize(newDomain.getSize());
		messageDialog.setContentPane(newDomain.getContentPane());
		messageDialog.setLocationRelativeTo(reference);
		messageDialog.setVisible(true);

	}

	public void getMasterPassWord() {
		boolean match = false;
		try {
			while (!match) {
				masterPassWord = JOptionPane.showInputDialog(
						"Please, Enter The Master Password:", null);
				if (masterPassWord == null)
					break;
				match = manger.loginDone(masterPassWord);
			}
		} catch (Exception e) {
			System.err.println("Error in getMasterPass method");
		}

	}

	public static void main(String[] args) {
		MainView v = new MainView();
	}

}
