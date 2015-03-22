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

	FIXA ORDER by i dbcon getmembers..

*/


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.sql.*;

public class GUI extends JFrame {
	static DBCon dbcon = new DBCon();
	private JLabel clubMenuLabel = new JLabel("CLUBMENU", SwingConstants.CENTER);
	private JTextField txt = new JTextField(10);
	private JButton b1 = new JButton("Print Members");
	private JButton b2 = new JButton("Print Team");
	private JButton b3 = new JButton("Add Member");
	private JButton search = new JButton("Search");
	private JButton save = new JButton("Save Member");
	private	JButton back = new JButton("Back");
	private	JButton fClear = new JButton("Clear");
	private JButton searchOn = new JButton("Search");
	private JButton updateMember = new JButton("Update");
	private JButton removeMember = new JButton("Remove");
	private JPanel p1 = new JPanel();
	private JPanel p2 = new JPanel();
	private JPanel p4 = new JPanel();
	private JPanel searchMem;
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
	private JTextField updateChildren = new JTextField(10);
	private JTextField searchField = new JTextField(10);
	private JTextField updateIdField = new JTextField(10);
	private JTextField updateEmailField = new JTextField(10);
	private JTextField removeIdField = new JTextField(10);
	private JTextField printTeamField = new JTextField(10);
	private	JLabel children = new JLabel("Children: ");

	//private String[] searchOptions = {"Member ID", "Lastnamn", "Team"};
	//private JComboBox selectSearch = new JComboBox(searchOptions);
	private JRadioButton man = new JRadioButton("Man", false),
						 kvinna = new JRadioButton("Kvinna", false);
	private ButtonGroup radioButtons = new ButtonGroup();
	private JRadioButton searchId = new JRadioButton("Member ID", false),
						 searchLastname = new JRadioButton("Lastname", false),
						 searchTeam = new JRadioButton("Team", false);
	private ButtonGroup searchRadioButtons = new ButtonGroup();

	private JLabel orderByLabel = new JLabel("Order by:");
	private JLabel printTeamLabel = new JLabel("Print team:");
	private JRadioButton orderId = new JRadioButton("Member ID", false),
						 orderLastname = new JRadioButton("Lastname", false);
	private ButtonGroup orderByRadioButtons = new ButtonGroup();

	private Object radioButtonState;
	private Object orderByRadioState;
	private Object searchState;
	private JCheckBox player = new JCheckBox("Player", false),
					  coach  = new JCheckBox("Coach", false),
					  parent = new JCheckBox("Parent", false);

	private JCheckBox active = new JCheckBox("Yes", true),
					  inactive  = new JCheckBox("No", false);
	private ButtonGroup updateCheckBoxes = new ButtonGroup();

	private	ArrayList<Integer> roleList = new ArrayList<Integer>();
	private int updateActiveInt = 1;


	private GridBagConstraints g1 = new GridBagConstraints();

	// LISTERNER för meny knappar i menyraden
	ActionListener li1 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == b1) {
				try {
					if (orderByRadioState == orderId) {
						printMembers(dbcon.getMembers(0)); // ID
					} else if (orderByRadioState == orderLastname) {
						printMembers(dbcon.getMembers(1)); // family Name
					} else {
						JOptionPane.showMessageDialog(null, "Select order.", "GENDER ERROR!", JOptionPane.ERROR_MESSAGE);
					}
				} catch (SQLException se) {
					JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
				}
			} else if (e.getSource() == b2) {

				String team = printTeamField.getText();
				if (team != null && !team.equals("")) {	
					try {
						printMembers(dbcon.getTeam(team));
					} catch (SQLException se) {
						JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "A team has to be specified", "ERROR!", JOptionPane.ERROR_MESSAGE);
				}

			} else if (e.getSource() == search) {
				searchMemberPanel();
				clearRoles();
			} else if (e.getSource() == b3) {
				addMemberPanel();
				clearRoles();
			}

		}
	};

	// LISTERner för knapparna i addmember - Add, clear, back.
	ActionListener li3 = new ActionListener() {
		public void actionPerformed(ActionEvent e) {

			if (e.getSource() == save) {

				String fnamn = tfnamn.getText();
				String lnamn = tenamn.getText();
				String team = tlag.getText();
				String birth = tbirth.getText();
				String memberSince = tmemberSince.getText();
				String email = temail.getText();
				int active = 1; // kanske lägga till så man kan lägga till en oaktiv medlem?

				// kollar så fälten into är tomma
				boolean filledFields = true;
				if (fnamn.equals("") || lnamn.equals("") || team.equals("") || birth.equals("") || memberSince.equals("") || email.equals("@")) {
					filledFields = false;
					JOptionPane.showMessageDialog(null, "All textfields has to be filled.", "FORM ERROR!", JOptionPane.ERROR_MESSAGE);
				}
				//

				// id checks
				int id = 0;
				try {
					id = Integer.parseInt(tid.getText());
				} catch (NumberFormatException ne) {
					JOptionPane.showMessageDialog(null, "Id has to be an integer.", "ID ERROR!", JOptionPane.ERROR_MESSAGE);
				}

				boolean idCheck = dbcon.checkMemberId(id); // true om medlem id finns
				if (idCheck) {
					JOptionPane.showMessageDialog(null, "Member id already used.", "ID ERROR!", JOptionPane.ERROR_MESSAGE);
				}
				// end id checks

				// emails checks
				if (email.indexOf("@") < 0) {
					JOptionPane.showMessageDialog(null, "Email has to be an email (Ex: xxxxxx@something.com).", "EMAIL ERROR!", JOptionPane.ERROR_MESSAGE);
					filledFields = false;
				} 
				// end emailchecks



				// gender checks
				String gender = null;
				if (radioButtonState == man) {
					gender = "man";
				} else if (radioButtonState == kvinna) {
					gender = "kvinna";
				} else {
					JOptionPane.showMessageDialog(null, "Select gender.", "GENDER ERROR!", JOptionPane.ERROR_MESSAGE);
				}
				// endgender checks

				// rolelist check, tagit från variable i klassen, satt från en annan listener
				if (roleList.isEmpty()){
					JOptionPane.showMessageDialog(null, "Select atleast one role.", "ROLE ERROR!", JOptionPane.ERROR_MESSAGE);					
				}
				// end rolelistcheck

				// children/parent check
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

					if (cText != null) {
						String errmess = "Children have to be added by member ID(integer). [" + cText + "] is not an integer.";
						JOptionPane.showMessageDialog(null, errmess, "ROLE ERROR!", JOptionPane.ERROR_MESSAGE);
						cField = false;
					}

					if (childList.isEmpty()) {
						cField = false;	
						JOptionPane.showMessageDialog(null, "Children have to be added if the role parent is selected. Add children by member id, separated by space.", "ROLE ERROR!", JOptionPane.ERROR_MESSAGE);	
					}

					if (testChild(childList, id)) {
						JOptionPane.showMessageDialog(null, "You cannot be your own child.", "ROLE ERROR!", JOptionPane.ERROR_MESSAGE);
						cField = false;
					}

					for (Integer cid : childList) {
						if (!dbcon.checkMemberId(cid)) {
							cField = false;
							String childIdError = "There's no child with ID: " + cid + ".";
							JOptionPane.showMessageDialog(null, childIdError, "ROLE ERROR!", JOptionPane.ERROR_MESSAGE);
							System.out.println(childList);
						}
					}
				
					
				}
				// parent/childs checks ends

				// checks igen, lägger sedan till
				if (gender != null && id != 0 && !roleList.isEmpty() && cField && !idCheck && filledFields) {
					dbcon.addMember(id, fnamn, lnamn, email, gender, birth, memberSince, active, roleList, team, childList);
					clearRoles();
				}  else {
					System.out.println("Medlem ej tilladg, skriver ut variabler:");
					String testprint = String.format("fnamn: %s - lnamn: %s - email: %s -  gender: %s -  birth: %s - memberSince: %s", fnamn, lnamn, email, gender, birth, memberSince);
					System.out.println("roleList: " + roleList);
					System.out.println("childList: " + childList);
					System.out.println(testprint);
					System.out.println("idCheck: " + idCheck);
					System.out.println("filledFields: " + filledFields);
					System.out.println("cField: " + cField);

				}
				
				
			} else if (e.getSource() == back) {
				try {
					roleList.clear();
					if (orderByRadioState == orderId) {
						printMembers(dbcon.getMembers(0)); // ID
					} else if (orderByRadioState == orderLastname) {
						printMembers(dbcon.getMembers(1)); // family Name
					} else {
						printMembers(dbcon.getMembers(0));
					}
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
				clearRoles();
				radioButtons.clearSelection();
				childPanel.removeAll();
				pack();
			}
		}
	};

	// LISTENER för update knappen i search Framen
	ActionListener memberUpdateListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {


			int id = 0;
			try {
					id = Integer.parseInt(updateIdField.getText());
			} catch (NumberFormatException ne) {
					JOptionPane.showMessageDialog(null, "Id has to be an integer.", "ID ERROR!", JOptionPane.ERROR_MESSAGE);
			}
			boolean idCheck = dbcon.checkMemberId(id); // true om medlem id finns
			if (!idCheck) {
					JOptionPane.showMessageDialog(null, "No member with that ID.", "ID ERROR!", JOptionPane.ERROR_MESSAGE);
			}

			String email = null;
			boolean updateEmailBool = true;
			if (updateEmailField.getText().indexOf("@") < 0 && !updateEmailField.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Email has to be an email (Ex: xxxxxx@something.com).", "EMAIL ERROR!", JOptionPane.ERROR_MESSAGE);
				updateEmailBool = false;
			} else if (updateEmailField.getText().equals("")) {
				updateEmailBool = false;
			} else {
				email = updateEmailField.getText();
			}

			boolean cField = true;
			boolean chNotParent = true;
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

				if (cText != null) {
						String errmess = "Children have to be added by member ID(integer). [" + cText + "] is not an integer.";
						JOptionPane.showMessageDialog(null, errmess, "ROLE ERROR!", JOptionPane.ERROR_MESSAGE);
						cField = false;
				}
				System.out.print(childList);

				if (childList.isEmpty()) {
					cField = false;
					System.out.print(childList);
					JOptionPane.showMessageDialog(null, "Children have to be added if the role parent is selected. Add children by member id, separated by space.", "ROLE ERROR!", JOptionPane.ERROR_MESSAGE);	
				}

				if (testChild(childList, id)) {
					JOptionPane.showMessageDialog(null, "You cannot be your own child.", "ROLE ERROR!", JOptionPane.ERROR_MESSAGE);
					chNotParent = false;
				}
			}

			if (updateEmailBool && email != null && !roleList.isEmpty() && idCheck && cField && chNotParent) { // uppdaterar email, active, roles
				dbcon.updateMember(id, email, roleList, childList, updateActiveInt);
			} else if (!updateEmailBool && !roleList.isEmpty() && idCheck && cField && chNotParent) { //uppdaterar roles, active
				dbcon.updateMember(id, roleList, childList, updateActiveInt);
			} else if (updateEmailBool && roleList.isEmpty() && idCheck && cField) { // uppdaterar email, active
				dbcon.updateMember(id, email, updateActiveInt);
			} else if (!updateEmailBool && roleList.isEmpty() && idCheck && cField) { // uppdaterar active
				dbcon.updateMember(id, updateActiveInt);
			}

			clearRoles();
		}
	};

	// LISTENER för tabort medlem I search framen
	ActionListener removeListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			int id = 0;

			try {
					id = Integer.parseInt(removeIdField.getText());
			} catch (NumberFormatException ne) {
					JOptionPane.showMessageDialog(null, "Id has to be an integer.", "ID ERROR!", JOptionPane.ERROR_MESSAGE);
			}

			boolean idCheck = dbcon.checkMemberId(id); // true om medlem id finns
			if (!idCheck) {
					JOptionPane.showMessageDialog(null, "No member with that ID.", "ID ERROR!", JOptionPane.ERROR_MESSAGE);
			}

			if (idCheck) {
				dbcon.removeMember(id);
			}
		}
	};

	// Listener för man/kvinna radio buttons
	ActionListener radioListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			radioButtonState = e.getSource();
		}
	};

	// Listener för search by radioknapparna (id, efternamn, print coaches i ett lag)
	ActionListener searchRadioListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			searchState = e.getSource();
		}
	};

	// Listener för order by radio knapparna i menyraden
	ActionListener orderByRadioListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			orderByRadioState = e.getSource();
		}
	};

   	// Listener för search knappen i search framen
	ActionListener searchListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			//int searchType; // 0 = lastname, 1 = team;
			clearRoles();
			ResultSet searchRes = null;
			int id = 0;
			//try {
				if (searchState == searchId) {
					try {
						id = Integer.parseInt(searchField.getText());
					} catch (NumberFormatException ne) {
						JOptionPane.showMessageDialog(null, "Id has to be an integer.", "ID ERROR!", JOptionPane.ERROR_MESSAGE);
					} 
					if (id != 0) {
						searchRes = dbcon.searchMember(id);
					}
						
				} else if (searchState == searchLastname) {
					searchRes = dbcon.searchMember(0, searchField.getText());
				} else if (searchState == searchTeam){
					searchRes = dbcon.searchMember(1, searchField.getText());
				} else {
					JOptionPane.showMessageDialog(null, "Select search type.", "SEARCH ERROR!", JOptionPane.ERROR_MESSAGE);
				}
			//} catch (SQLException se) {
			//	System.out.println(se.getMessage());
			//}
			JTable sTable = null;
			try {
			sTable = tableSearchedMembers(searchRes);
			} catch (SQLException se) {
				System.out.println(se.getMessage());
			}	

			if (sTable != null) {
				JPanel tablePanel = new JPanel();
				tablePanel.setLayout(new BorderLayout());
				JScrollPane scrollSearch = new JScrollPane(tablePanel);
				scrollSearch.setPreferredSize(new Dimension(1175,100));
				
				GridBagConstraints g = new GridBagConstraints();
				g.gridx = 0; g.gridy = 2;
				g.gridwidth = 25; g.gridheight = 2;
				g.fill = GridBagConstraints.BOTH;
				searchMem.add(scrollSearch, g);

				tablePanel.add(sTable.getTableHeader(), BorderLayout.PAGE_START);
				tablePanel.add(sTable, BorderLayout.CENTER);

				pack();
			}
		}
	};

	// LIstener för rolecheck list... endel onödiga saker blivit klar när det inte funkade först innan jag catchade index out of bounds och resetta roleerna
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
						clearRoles();
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
						clearRoles();
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
						clearRoles();

					}
				}
			}
		} catch (IndexOutOfBoundsException ie) {
			//System.out.println(ie.getMessage());
			//System.out.println(roleList);
			clearRoles();
		}
		}
	};

	//Listener för active status i member update.
	ActionListener checkActiveListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			Object ch = e.getSource();

			if (ch == active) {
				if (active.isSelected()) {
					updateActiveInt = 1;
				}
			} else if (ch == inactive) {
				if (inactive.isSelected()) {
					updateActiveInt = 0;
				}
			} 	
		}
	};

	// clearar roleListan och role checksen - dyker upp lite varstans för att se till att arraylistan roleList stämmer med det som är i kryssat.
	public void clearRoles() {
		roleList.clear();
		player.setSelected(false);
		coach.setSelected(false);
		parent.setSelected(false);
	}

	// testar om det id som är i skrivet som id är sammsa som något id som fyllts i om rollen förälder valts
	private boolean testChild(ArrayList<Integer> cId, int pId) {

		for (Integer childId : cId) {
			if (childId == pId) {
				return true;
			}
		}
		return false;
	}

	// printar medlemmarna från ett resultset till en tabell och skriver in det i panel p3. altså huvudrutan.
	public void printMembers(ResultSet r) throws SQLException {

		p3.removeAll();
		p2.removeAll();
		pack();

		JTable tb = tableSearchedMembers(r);
		p2.setLayout(new BorderLayout());
		sp.setPreferredSize(new Dimension(1175,700));

		p3.add(sp, BorderLayout.CENTER);

		p2.add(tb.getTableHeader(), BorderLayout.PAGE_START);
		p2.add(tb, BorderLayout.CENTER);
		pack();
		

	}

	// gör en tabel av medlemmarna från ett resultset
	public JTable tableSearchedMembers(ResultSet r) throws SQLException {

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
				if (mData.getColumnName(i).equals("role")) { // ändrar rollnr till namn istället
					if ((Integer) r.getObject(i) == 0) {
						rowData.add("Player");
					} else if ((Integer) r.getObject(i) == 1) {
						rowData.add("Coach");
					} else if ((Integer) r.getObject(i) == 2) {
						rowData.add("Parent");
					} else {
						rowData.add(r.getObject(i));
						System.out.println("didnt work yo");
					}
				} else {
					rowData.add(r.getObject(i));
				}
				
			}
			tData.add(rowData);
		}


		JTable tb = new JTable(tData, columnNamn) {
				public boolean isCellEditable(int rowIndex, int vColIndex) {
					return false;
				}
		};
		//tb.setFillsViewportHeight(true);

		return tb;	

	}



	public GUI() {
		// adds into buttonsgroups så att bara ett val kan vara ifyllt samtidigt
		updateCheckBoxes.add(active);
		updateCheckBoxes.add(inactive);
		radioButtons.add(man);
		radioButtons.add(kvinna);
		orderByRadioButtons.add(orderId);
		orderByRadioButtons.add(orderLastname);

		// role Listeners for update and add member
		player.addActionListener(checkListener);
		coach.addActionListener(checkListener);
		parent.addActionListener(checkListener);

		// radio listeners for gender
		man.addActionListener(radioListener);
		kvinna.addActionListener(radioListener);

		// listeners for update active radio
		active.addActionListener(checkActiveListener);
		inactive.addActionListener(checkActiveListener);

		// buttonlisteners for search frame
		updateMember.addActionListener(memberUpdateListener);
		removeMember.addActionListener(removeListener);

		// radiolisteners for order by 
		orderId.addActionListener(orderByRadioListener);
		orderLastname.addActionListener(orderByRadioListener);

		//radio listeners for what to search on in search panel
		searchId.addActionListener(searchRadioListener);
		searchLastname.addActionListener(searchRadioListener);
		searchTeam.addActionListener(searchRadioListener);

		//listerer for search button in search panel
		searchOn.addActionListener(searchListener);

		//radiolisteners for search by 
		searchRadioButtons.add(searchId);
		searchRadioButtons.add(searchLastname);
		searchRadioButtons.add(searchTeam);

		// Listerners for buttons in add member panel
		save.addActionListener(li3);
		back.addActionListener(li3);
		fClear.addActionListener(li3);

		// Listeners for mainmenu - b1 = prints all members ordered by selected radiobutton, b2 = prints all members in the teams that been typed in
		// b3 = add member panel, search = search, update and remove memberpanel.
		b1.addActionListener(li1);
		b2.addActionListener(li1);
		b3.addActionListener(li1);
		search.addActionListener(li1);
		
		
		JSeparator menuSep1 =  new JSeparator(SwingConstants.HORIZONTAL);
		JSeparator menuSep2 =  new JSeparator(SwingConstants.HORIZONTAL);
		JSeparator menuSep3 =  new JSeparator(SwingConstants.HORIZONTAL);
		JSeparator menuSep4 =  new JSeparator(SwingConstants.HORIZONTAL);
		JSeparator menuSep5 =  new JSeparator(SwingConstants.HORIZONTAL);
		//menuSep1.setPreferredSize(new Dimension(15, 5));
		//menuSep2.setPreferredSize(new Dimension(15, 5));
		//menuSep3.setPreferredSize(new Dimension(15, 5));
		menuSep1.setBackground(Color.black);
		menuSep2.setBackground(Color.black);
		menuSep3.setBackground(Color.black);
		menuSep4.setBackground(Color.black);
		menuSep5.setBackground(Color.black);



		
		childPanel.setLayout(new GridBagLayout());
		GridBagLayout gbLayout = new GridBagLayout();
		//p1.setBackground(Color.black);
		p1.setLayout(new GridBagLayout());


		p3 = new JPanel();
		p3.setLayout(new BorderLayout());
		setLayout(gbLayout);
		
		GridBagConstraints g2 = new GridBagConstraints();
		GridBagConstraints gSep = new GridBagConstraints();
		g1.gridx = 0; g1.gridy = 0;
		g1.insets = new Insets(0, 0, 0, 8);
		g1.fill = GridBagConstraints.BOTH;
		add(p1, g1);

		g2.gridx = 0; g2.gridy = 0;
		g2.insets = new Insets(1, 0, 0, 0);
		g2.fill = GridBagConstraints.HORIZONTAL;
		g2.anchor = GridBagConstraints.CENTER;
		p1.add(clubMenuLabel, g2);

		gSep.gridx = 0; gSep.gridy = 1;
		//gSep.anchor = GridBagConstraints.CENTER;
		gSep.fill = GridBagConstraints.HORIZONTAL;
		gSep.insets = new Insets(0, 0, 0, 0);
		gSep.gridwidth = 1;
		gSep.weightx = 1.0;
		p1.add(menuSep4, gSep);

		gSep.gridx = 0; gSep.gridy = 2;
		//gSep.anchor = GridBagConstraints.CENTER;
		gSep.fill = GridBagConstraints.HORIZONTAL;
		gSep.insets = new Insets(0, 0, 0, 0);
		gSep.gridwidth = 1;
		gSep.weightx = 1.0;
		p1.add(menuSep5, gSep);

		g2.gridx = 0; g2.gridy = 3;
		g2.insets = new Insets(0, 0, 0, 0);
		g2.fill = GridBagConstraints.HORIZONTAL;
		g2.anchor = GridBagConstraints.FIRST_LINE_START;
		p1.add(orderByLabel, g2);

		g2.gridx = 0; g2.gridy = 4;
		p1.add(orderId, g2);
		g2.gridx = 0; g2.gridy = 5;
		p1.add(orderLastname, g2);

		g2.gridx = 0; g2.gridy = 6;
		g2.insets = new Insets(0, 0, 0, 0);
		g2.fill = GridBagConstraints.HORIZONTAL;
		g2.anchor = GridBagConstraints.FIRST_LINE_START;
		p1.add(b1, g2);

		gSep.gridx = 0; gSep.gridy = 7;
		//gSep.anchor = GridBagConstraints.CENTER;
		gSep.insets = new Insets(5, 0, 0, 0);
		gSep.fill = GridBagConstraints.BOTH;
		gSep.anchor = GridBagConstraints.CENTER;
		gSep.gridwidth = 1;
		gSep.weightx = 1.0;
		p1.add(menuSep1, gSep);

		g2.gridx = 0; g2.gridy = 8;
		g2.anchor = GridBagConstraints.FIRST_LINE_START;
		p1.add(printTeamLabel, g2);
		g2.gridx = 0; g2.gridy = 9;
		p1.add(printTeamField, g2);
		g2.gridx = 0; g2.gridy = 10;
		g2.fill = GridBagConstraints.HORIZONTAL;
		p1.add(b2, g2);

		gSep.gridx = 0; gSep.gridy = 11;
		//gSep.anchor = GridBagConstraints.CENTER;
		gSep.fill = GridBagConstraints.HORIZONTAL;
		gSep.insets = new Insets(5, 0, 5, 0);
		gSep.gridwidth = 1;
		gSep.weightx = 1.0;
		p1.add(menuSep2, gSep);

		g2.gridx = 0; g2.gridy = 12;
		g2.anchor = GridBagConstraints.FIRST_LINE_START;
		g2.fill = GridBagConstraints.HORIZONTAL;
		p1.add(b3,  g2);

		gSep.gridx = 0; gSep.gridy = 13;
		//gSep.anchor = GridBagConstraints.CENTER;
		gSep.fill = GridBagConstraints.HORIZONTAL;
		gSep.gridwidth = 1;
		gSep.weightx = 1.0;
		p1.add(menuSep3, gSep);

		g2.gridx = 0; g2.gridy = 14;
		g2.anchor = GridBagConstraints.FIRST_LINE_START;
		g2.fill = GridBagConstraints.HORIZONTAL;
		g2.weighty = 1.0;
		p1.add(search,  g2);

		g1.gridx = 1; g1.gridy = 0;
		g1.fill = GridBagConstraints.BOTH;
		add(p3, g1);


		try {
			printMembers(dbcon.getMembers(0));
		} catch (SQLException se) {
			JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
		}

		setPreferredSize(new Dimension(1350,750));
		setTitle("Clubregister");
		setVisible(true);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	// skapar rutan för att lägga till en medlem
	public void addMemberPanel() {
		p3.removeAll();
		addMem = new JPanel();
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
		tlag.setText("ex: P89");
		tid.setText("ex: 123");
		temail.setText("ex: bertil@gmail.com");
		tbirth.setText("ex: 1999-07-06");
		tmemberSince.setText("ex: 1999-07-21");
		tchildren.setText("ex: 12 125 23");

		trole.setToolTipText("Roles can be either coach, parent or player");
		tbirth.setToolTipText("Format: yyyy-mm-dd");
		tmemberSince.setToolTipText("Format: yyyy-mm-dd");
		tchildren.setToolTipText("Children are added by member Id, separated by space");

		//childPanel.setLayout(new GridBagLayout());
		addMem.setPreferredSize(new Dimension(1175, 700)); 
		GridBagLayout gb = new GridBagLayout();
		addMem.setLayout(gb);
		GridBagConstraints g5 = new GridBagConstraints();
		

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

	// skapar rutan för att söka efter medlemmar
	public void searchMemberPanel() {

		p3.removeAll();
		roleList.clear();
		searchMem = new JPanel();
		


		p3.add(searchMem, BorderLayout.CENTER);
		JLabel searchLabel = new JLabel("Search: ");
		JLabel searchByLabel = new JLabel("Search using: ");
		JLabel updateLabel = new JLabel("Update Member Id: ");
		JLabel updateEmail = new JLabel("Email: ");
		JLabel updateRole = new JLabel("Role: ");
		JLabel updateActive = new JLabel("Active : ");
		JLabel removeLabel = new JLabel("Remove Member Id: ");

		updateEmailField.setText("");

		searchMem.setPreferredSize(new Dimension(1175, 700)); 
		GridBagLayout sgb = new GridBagLayout();
		searchMem.setLayout(sgb);
		GridBagConstraints g7 = new GridBagConstraints();
		GridBagConstraints ug = new GridBagConstraints();

		g7.insets = new Insets(0, 0, 0, 0);
		g7.gridx = 0; g7.gridy = 0;
		g7.anchor = GridBagConstraints.LINE_END;
		searchMem.add(searchLabel, g7);
		g7.gridx = 1; g7.gridy = 0;
		searchMem.add(searchField, g7);
		g7.gridx = 2; g7.gridy = 0;
		searchMem.add(searchByLabel, g7);
		g7.gridx = 3; g7.gridy = 0;
		searchMem.add(searchId, g7);
		g7.gridx = 4; g7.gridy = 0;
		searchMem.add(searchLastname, g7);
		g7.gridx = 5; g7.gridy = 0;
		searchMem.add(searchTeam, g7);
		g7.gridx = 6; g7.gridy = 0;
		searchMem.add(searchOn, g7);

		g7.gridx = 0; g7.gridy = 5;
		searchMem.add(updateLabel, g7);
		g7.gridx = 1; g7.gridy = 5;
		searchMem.add(updateIdField, g7);

		g7.gridx = 4; g7.gridy = 7;
		searchMem.add(childPanel, g7);

		g7.gridx = 0; g7.gridy = 6;
		searchMem.add(updateEmail, g7);
		g7.gridx = 1; g7.gridy = 6;
		searchMem.add(updateEmailField, g7);

		g7.gridx = 0; g7.gridy = 7;
		searchMem.add(updateRole, g7);
		g7.anchor = GridBagConstraints.LINE_START;
		g7.gridx = 1; g7.gridy = 7;
		searchMem.add(player, g7);
		g7.gridx = 2; g7.gridy = 7;
		searchMem.add(coach, g7);
		g7.gridx = 3; g7.gridy = 7;
		searchMem.add(parent, g7);

		g7.gridx = 0; g7.gridy = 8;
		g7.anchor = GridBagConstraints.LINE_END;
		searchMem.add(updateActive, g7);
		g7.gridx = 1; g7.gridy = 8;
		g7.anchor = GridBagConstraints.LINE_START;
		searchMem.add(active, g7);
		g7.gridx = 2; g7.gridy = 8;
		searchMem.add(inactive, g7);

		g7.gridx = 0; g7.gridy = 9;
		g7.anchor = GridBagConstraints.LINE_END;
		searchMem.add(removeLabel, g7);
		g7.gridx = 1; g7.gridy = 9;
		searchMem.add(removeIdField, g7);
		g7.gridx = 2; g7.gridy = 9;
		searchMem.add(removeMember, g7);

		g7.gridx = 2; g7.gridy = 5;
		searchMem.add(updateMember, g7);
		pack();
	}

	public static void main(String[] args) throws Exception {
		GUI t = new GUI();
	}
}