import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MainView extends JFrame {

	private JPanel bodyPanel;
	private JPanel buttonsPanel;
	private JTable table;

	private JButton addButton;
	private JButton removeButton;
	private JButton ChangeMasterPass;

	private JScrollPane scroll;

	protected static JDialog messageDialog;
	protected static JFrame reference;

	protected static PasswordManager manger;

	private String name, pass;
	private AddDomain newDomain;

	private DefaultTableModel dModel;
	private String masterPassWord;

	public MainView() {
		setSize(500, 500);
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

		table = new JTable(200, 2);
		table.setBounds(0, 0, 500, 400);
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] {
				"Domain Name", "Password" }) {
			/**
				 * 
				 */
			private static final long serialVersionUID = 1L;
			boolean[] columnEditables = new boolean[] { false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		scroll = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(0, 0, 485, 385);
		scroll.setAutoscrolls(true);

		bodyPanel = new JPanel();
		bodyPanel.setLayout(null);
		bodyPanel.setBounds(0, 0, 500, 400);
		bodyPanel.add(scroll);

		addButton = new JButton("Add");
		removeButton = new JButton("Remove");
		ChangeMasterPass = new JButton("Settings");

		buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
		buttonsPanel.add(addButton);
		buttonsPanel.add(removeButton);
		buttonsPanel.add(ChangeMasterPass);

		// add panels to frame
		add(bodyPanel, "Center");
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
				int row = table.getSelectedRow();
				if (row >= 0)
					table.remove(table.getSelectedRow());
			}
		});
		ChangeMasterPass.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
			}
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					System.out.println("Do some stuff");
					int row = table.getSelectedRow();
					name = (String) table.getValueAt(row, 0);
					pass = (String) table.getValueAt(row, 1);
					add();
				}
			}
		});

	}

	private void add() {
		if (name.equals(""))
			newDomain = new AddDomain(name, pass, -1);
		else
			newDomain = new AddDomain(name, pass, table.getSelectedRow());
		newDomain.setuV(new updateView() {

			@Override
			public void update(String name, String pass, int row) {
				// check if it's an update or new
				dModel = (DefaultTableModel) table.getModel();
				if (row == -1)
					dModel.addRow(new Object[] { name, pass });
				else {
					// this an update
					dModel.setValueAt(name, row, 0);
					dModel.setValueAt(pass, row, 1);
				}

			}
		});

		messageDialog.setSize(newDomain.getSize());
		messageDialog.setContentPane(newDomain.getContentPane());
		messageDialog.setLocationRelativeTo(reference);
		messageDialog.setVisible(true);
		name = "";
		pass = "";
	}

	public void getMasterPassWord() {
		masterPassWord = JOptionPane.showInputDialog(
				"Please, Enter The Master Password:", null);

	}

	public static void main(String[] args) {
		MainView v = new MainView();
	}

}
