/*
TODO: 
	Se inl3 instruktioner för att se vad mer som behövs

	FÖRST: test add att addmedlem funkar med SQL

	Grå fönster istället för förminsking av rutan
	CONNECTA funktioner med databasen
	lägg till sök i menyrad
	lägg till labels i menyrad
	lägg till funktioner i menyrad
	försök snygga till menyraden

	lägg till så att om ett barn registreras för en förälder att barnet får förälder tillagd.

	funktion i DBCon för att söka igenom medlems ID
*/


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.sql.*;

public class GUI extends JFrame {
	DBCon dbcon = new DBCon();

	private JTextField txt = new JTextField(10);
	private JButton b1 = new JButton("Members");
	private JButton b2 = new JButton("Clear");
	private JButton b3 = new JButton("Add Member");
	private JButton save = new JButton("Save Member");
	private	JButton back = new JButton("Back");
	private	JButton fClear = new JButton("Clear");
	private JPanel p1 = new JPanel();
	private JPanel p2 = new JPanel();
	private JPanel p4 = new JPanel();
	private	JPanel addMem;
	private JPanel p3;
	private JPanel childPanel = new JPanel();
	private JScrollPane sp = new JScrollPane(p2);

	private JTextField tfnamn = new JTextField(10);
	private JTextField tenamn = new JTextField(10);
	private JTextField tlag = new JTextField(10);
	private JTextField tid = new JTextField(10);
	private JTextField temail = new JTextField(10);
	private JTextField tgender = new JTextField(10);
	private JTextField tbirth = new JTextField(10);
	private JTextField tmemberSince = new JTextField(10);	
	private JTextField trole = new JTextField(10);
	private JTextField tchildren = new JTextField(10);
	private	JLabel children = new JLabel("Children: ");

	private JRadioButton man = new JRadioButton("Man", false),
						 kvinna = new JRadioButton("Kvinna", false);
	private ButtonGroup radioButtons = new ButtonGroup();
	private Object radioButtonState;
	private JCheckBox player = new JCheckBox("Player", false),
					  coach  = new JCheckBox("Coach", false),
					  parent = new JCheckBox("Parent", false);
	//private ButtonGroup checkBoxes = new ButtonGroup();
	private	ArrayList<Integer> roleList = new ArrayList<Integer>();


	private GridBagConstraints g1 = new GridBagConstraints();


	ActionListener li1 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == b1) {
				try {
				printMembers(dbcon.getMembers());
				} catch (SQLException se) {
					JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
				}
			} else if (e.getSource() == b2) {
				p3.removeAll();
				pack();
			}
		}
	};


	ActionListener li2 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == b3) {
				addMemberPanel();
			}
		}
	};

	ActionListener li3 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == save) {

				String fnamn = tfnamn.getText();
				String lnamn = tenamn.getText();
				String team = tlag.getText();
				int active = 1; // kanske lägga till så man kan lägga till en oaktiv medlem?
				int id = 0;
				try {
					id = Integer.parseInt(tid.getText());
				} catch (NumberFormatException ne) {
					JOptionPane.showMessageDialog(null, "Id has to be an integer.", "ID ERROR!", JOptionPane.ERROR_MESSAGE);
				}
				boolean idCheck = dbcon.checkMemberId(id); // true om medlem id finns
				if (idCheck) {
					JOptionPane.showMessageDialog(null, "Member id already already used.", "ID ERROR!", JOptionPane.ERROR_MESSAGE);
				}
				

				String email = null;
				if (temail.getText().indexOf("@") < 0) {
					JOptionPane.showMessageDialog(null, "Email has to be an email (Ex: xxxxxx@something.com).", "EMAIL ERROR!", JOptionPane.ERROR_MESSAGE);
				} else {
					email = temail.getText();
				}

				String birth = tbirth.getText();
				String memberSince = tmemberSince.getText();

				String gender = null;
				if (radioButtonState == man) {
					gender = "man";
				} else if (radioButtonState == kvinna) {
					gender = "kvinna";
				} else {
					JOptionPane.showMessageDialog(null, "Select gender.", "GENDER ERROR!", JOptionPane.ERROR_MESSAGE);
				}

				if (roleList.isEmpty()){
					JOptionPane.showMessageDialog(null, "Select atleast one role.", "ROLE ERROR!", JOptionPane.ERROR_MESSAGE);					
				}

				boolean cField = true;
				ArrayList<Integer> childList = new ArrayList<Integer>();
				if (parent.isSelected()) {
					String childString = tchildren.getText();
					Scanner sc = new Scanner(childString);
					String cText = null;
					boolean carryOn = true;
					while (sc.hasNext() && carryOn) {
						try {
							childList.add(sc.nextInt());
						} catch (InputMismatchException me) {
							cText = tchildren.getText();
							carryOn = false;
						}
					}
					if (childList.isEmpty()) {
						cField = false;
						if (cText != null) {
							String errmess = "Children have to be added by member ID(integer). [" + cText + "] is not an integer.";
							JOptionPane.showMessageDialog(null, errmess, "ROLE ERROR!", JOptionPane.ERROR_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(null, "Children have to be added if the role parent is selected. Add children by member id, separated by space.", "ROLE ERROR!", JOptionPane.ERROR_MESSAGE);	
						}
					}
				}

				boolean chNotParent = true;
				if (testChild(childList, id)) {
					JOptionPane.showMessageDialog(null, "You cannot be your own child.", "ROLE ERROR!", JOptionPane.ERROR_MESSAGE);
					chNotParent = false;
				}

				if (email != null && !email.equals("") && gender != null && id != 0 && !roleList.isEmpty() && cField && chNotParent && !idCheck) {

					//addMember(int id, String gName, String fName, String email, String gender,
					//	  String birth, String mSince, int active, ArrayList<Integer> roleList, String team, ArrayList<Integer> childList)

					dbcon.addMember(id, fnamn, lnamn, email, gender, birth, memberSince, active, roleList, team, childList);

					/*
					String print = String.format("%d %s %s %s %s %s %s %s", id, fnamn, lnamn, lag, email, birth, memberSince, gender);
					System.out.println(roleList);
					System.out.println(childList);
					System.out.println(print);*/
					roleList.clear();
				}
				
				
			} else if (e.getSource() == back) {
				try {
					roleList.clear();
					printMembers(dbcon.getMembers());
				} catch (SQLException se) {
					JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
				}
			} else if (e.getSource() == fClear) {
				tfnamn.setText("");
				tenamn.setText("");
				tlag.setText("");
				tid.setText("");
				temail.setText("");
				tbirth.setText("");
				tmemberSince.setText("");
				tchildren.setText("");
				roleList.clear();
				player.setSelected(false);
				coach.setSelected(false);
				parent.setSelected(false);
				radioButtons.clearSelection();
				childPanel.removeAll();
				pack();
			}
		}
	};

	ActionListener radioListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			radioButtonState = e.getSource();
		}
	};

	ActionListener checkListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			GridBagConstraints cCon = new GridBagConstraints();
			try {
			Object ch = e.getSource();
			if (ch == player) {
				if (player.isSelected()) {
					roleList.add(0);
				} else {	
					if (roleList.size() > 0) {
						int rem = 0;		
						roleList.remove(rem);
					} else {
						roleList.clear();
					}
				}
			} else if (ch == coach) {
				if (coach.isSelected()) {
					roleList.add(1);
				} else {
					if (roleList.size() > 0) {
						int rem = 1;		
						roleList.remove(rem);
					} else {
						roleList.clear();
					}
				}
			} else if (ch == parent) {
				if (parent.isSelected()) {
					roleList.add(2);
					cCon.gridx = 0; cCon.gridy = 0;
					cCon.anchor = GridBagConstraints.LINE_END;
					childPanel.add(children, cCon);
					cCon.gridx = 1; cCon.gridy = 0;
					childPanel.add(tchildren, cCon);
					pack();
				} else {
					childPanel.removeAll();
					pack();
					if (roleList.size() > 0) {	
						int rem = 2;	
						roleList.remove(rem);
					} else {
						roleList.clear();

					}
				}
			}
		} catch (IndexOutOfBoundsException ie) {
			//System.out.println(ie.getMessage());
			//System.out.println(roleList);
			roleList.clear();
			player.setSelected(false);
			coach.setSelected(false);
			parent.setSelected(false);

		}
		}
	};

	private boolean testChild(ArrayList<Integer> cId, int pId) {

		for (Integer childId : cId) {
			if (childId == pId) {
				return true;
			}
		}
		return false;
	}

	public void printMembers(ResultSet r) throws SQLException {

		p3.removeAll();

		ResultSetMetaData mData = r.getMetaData();

		Vector<String> columnNamn = new Vector<String>();
		int columnCount = mData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			columnNamn.add(mData.getColumnName(i));
		}


		Vector<Vector<Object>> tData = new Vector<Vector<Object>>();
		while (r.next()){
			Vector<Object> rowData = new Vector<Object>();
			for (int i = 1;i <= columnCount; i++) {
				rowData.add(r.getObject(i));
			}
			tData.add(rowData);
		}


		JTable tb = new JTable(tData, columnNamn) {
				public boolean isCellEditable(int rowIndex, int vColIndex) {
					return false;
				}
		};
		tb.setFillsViewportHeight(true);
		p2.setLayout(new BorderLayout());
		sp.setPreferredSize(new Dimension(1300,800));

		//g6.gridx = 0; g6.gridy = 0;
		//g6.gridwidth = 12; g6.gridheight = 25;
		//g6.fill = GridBagConstraints.BOTH;
		p3.add(sp, BorderLayout.CENTER);

		p2.add(tb.getTableHeader(), BorderLayout.PAGE_START);
		p2.add(tb, BorderLayout.CENTER);
		pack();
		

	}



	public GUI() {
		//roleList.add(1337);	
		radioButtons.add(man);
		radioButtons.add(kvinna);
		//checkBoxes.add(player);
		//checkBoxes.add(coach);
		//checkBoxes.add(parent);
		b1.addActionListener(li1);
		b2.addActionListener(li1);
		b3.addActionListener(li2);
		GridBagLayout m = new GridBagLayout();
		p1.setBackground(Color.black);
		p1.setLayout(new GridBagLayout());
		//p1.setPreferredSize(new Dimension(200,800));

		p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		//p3.setPreferredSize(new Dimension(1550,800));
		//p4.setPreferredSize(new Dimension(200,800));
		//p3.setPreferredSize(new Dimension(650,750));

		//p2.setPreferredSize(new Dimension(600,700));
		setLayout(m);
		
		GridBagConstraints g2 = new GridBagConstraints();
		g1.gridx = 0; g1.gridy = 0;
		g1.insets = new Insets(0, 0, 0, 0);
		g1.fill = GridBagConstraints.BOTH;
		add(p1, g1);

		g2.gridx = 0; g2.gridy = 0;
		g2.insets = new Insets(0, 0, 0, 0);
		g2.fill = GridBagConstraints.HORIZONTAL;
		g2.anchor = GridBagConstraints.FIRST_LINE_START;
		p1.add(b1, g2);

		g2.gridx = 0; g2.gridy = 1;
		g2.fill = GridBagConstraints.HORIZONTAL;
		p1.add(b2,  g2);

		g2.gridx = 0; g2.gridy = 2;
		g2.fill = GridBagConstraints.HORIZONTAL;
		p1.add(b3,  g2);

		/*g2.gridx = 0; g2.gridy = 3;
		g2.fill = GridBagConstraints.BOTH;
		g2.gridheight = 10; g2.gridwidth = 1;
		p1.add(p4, g2); */

		/*g1.gridx = 3; g1.gridy = 0;
		add(b4, g1);

		g1.gridx = 4; g1.gridy = 0;
		add(b5, g1);

		g1.gridx = 5; g1.gridy = 0;
		add(b6, g1);

		g1.gridx = 6; g1.gridy = 0;
		add(b7, g1);*/

		/*g1.gridx = 15; g1.gridy = 0;
		g1.gridwidth = 5; g1.gridheight = 1;
		//g1.fill = GridBagConstraints.BOTH;
		g1.insets = new Insets(0, 0, 0, 0);
		add(p1, g1);

		g2.gridx = 0; g2.gridy = 0;
		g2.gridwidth = 5; g2.gridheight = 1;
		g2.insets = new Insets(0,0,0,0);
		g2.fill = GridBagConstraints.BOTH;
		p1.add(txt, g2);*/

		g1.gridx = 1; g1.gridy = 0;
		//g1.gridheight = 15; g1.gridwidth = 24;
		g1.fill = GridBagConstraints.BOTH;
		add(p3, g1);

		//p2.add(sp, BorderLayout.EAST);

		try {
			printMembers(dbcon.getMembers());
		} catch (SQLException se) {
			JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
		}

		setPreferredSize(new Dimension(1470,850));
		setTitle("Clubregister");
		setVisible(true);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void addMemberPanel() {
		p3.removeAll();
		addMem = new JPanel();
		man.addActionListener(radioListener);
		kvinna.addActionListener(radioListener);
		player.addActionListener(checkListener);
		coach.addActionListener(checkListener);
		parent.addActionListener(checkListener);
		p3.add(addMem, BorderLayout.CENTER);

		JLabel fnamn = new JLabel("Firstname: ");
		JLabel enamn = new JLabel("Lastname: ");
		JLabel lag = new JLabel("Team: ");
		JLabel id = new JLabel("Member id: ");
		JLabel email = new JLabel("Email: ");
		JLabel gender = new JLabel("Gender: ");
		JLabel birth = new JLabel("Birth: ");
		JLabel memberSince = new JLabel("Member since: ");
		JLabel role = new JLabel("Role: ");

		tfnamn.setText("ex: Bertil");
		tenamn.setText("ex: Bertil");
		tlag.setText("ex: Bertil");
		tid.setText("ex: 123");
		temail.setText("ex: bertil@gmail.com");
		tbirth.setText("ex: 1999-07-06");
		tmemberSince.setText("ex: 1999-07-21");
		tchildren.setText("ex: 12 125 23");

		trole.setToolTipText("Roles can be either coach, parent or player");
		tbirth.setToolTipText("Format: yyyy-mm-dd");
		tmemberSince.setToolTipText("Format: yyyy-mm-dd");
		tchildren.setToolTipText("Children are added by member Id, separated by space");

		childPanel.setLayout(new GridBagLayout());
		addMem.setPreferredSize(new Dimension(1300, 800)); 
		GridBagLayout gb = new GridBagLayout();
		addMem.setLayout(gb);
		GridBagConstraints g5 = new GridBagConstraints();
		save.addActionListener(li3);
		back.addActionListener(li3);
		fClear.addActionListener(li3);

		g5.insets = new Insets(0, 0, 0, 0);
		g5.gridx = 0; g5.gridy = 0;
		g5.anchor = GridBagConstraints.LINE_END;
		addMem.add(fnamn, g5);
		g5.gridx = 1; g5.gridy = 0;
		addMem.add(tfnamn, g5);

		g5.gridx = 0; g5.gridy = 1;
		addMem.add(enamn, g5);
		g5.gridx = 1; g5.gridy = 1;
		addMem.add(tenamn, g5);

		g5.gridx = 0; g5.gridy = 2;
		addMem.add(lag, g5);
		g5.gridx = 1; g5.gridy = 2;
		addMem.add(tlag, g5);

		g5.gridx = 0; g5.gridy = 3;
		addMem.add(birth, g5);
		g5.gridx = 1; g5.gridy = 3;
		addMem.add(tbirth, g5);

		g5.gridx = 0; g5.gridy = 4;
		addMem.add(email, g5);
		g5.gridx = 1; g5.gridy = 4;
		addMem.add(temail, g5);

		g5.gridx = 0; g5.gridy = 5;
		addMem.add(memberSince, g5);
		g5.gridx = 1; g5.gridy = 5;
		addMem.add(tmemberSince, g5);

		g5.gridx = 0; g5.gridy = 6;
		addMem.add(id, g5);
		g5.gridx = 1; g5.gridy = 6;
		addMem.add(tid, g5);

		g5.gridx = 3; g5.gridy = 0;
		addMem.add(childPanel, g5);

		g5.gridx = 2; g5.gridy = 0;
		g5.anchor = GridBagConstraints.LINE_START;
		addMem.add(gender, g5);
		g5.gridx = 2; g5.gridy = 1;
		addMem.add(man, g5);
		g5.gridx = 2; g5.gridy = 2;
		addMem.add(kvinna, g5);

		g5.gridx = 2; g5.gridy = 3;
		addMem.add(role, g5);
		g5.gridx = 2; g5.gridy = 4;
		addMem.add(player, g5);
		g5.gridx = 2; g5.gridy = 5;
		addMem.add(coach, g5);
		g5.gridx = 2; g5.gridy = 6;
		addMem.add(parent, g5);

		g5.gridx = 0; g5.gridy = 7;
		g5.fill = GridBagConstraints.BOTH;
		addMem.add(save, g5);
		g5.gridx = 1; g5.gridy = 7;
		g5.fill = GridBagConstraints.BOTH;
		addMem.add(fClear, g5);
		g5.gridx = 2; g5.gridy = 7;
		g5.fill = GridBagConstraints.BOTH;
		addMem.add(back, g5);

		pack();
	}

	public static void main(String[] args) throws Exception {
		GUI t = new GUI();
	}
}