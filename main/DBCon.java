import java.sql.*;
import java.util.*;
import javax.swing.*;

public class DBCon {

	Connection con;
	Statement state;
	ResultSet res;

	public Connection createConnection() {

		Connection tcon = null;

		try {
			Class.forName("org.sqlite.JDBC");
		}	catch (ClassNotFoundException e1) {
			System.out.println("Hitta ej drivrutin");
			e1.printStackTrace();
			System.exit(1);
		}
		System.out.println("Sofarsohgood");

		try {
			tcon = DriverManager.getConnection("jdbc:sqlite:inl3");
		} catch (SQLException e2) {
			System.out.println("Kunde ej connecta till DB");
			e2.printStackTrace();
			System.exit(1);
		}

		if (tcon != null) {
			System.out.println("Connected");
		} else {
			System.out.println("");
		}

		return tcon;
	}  

	public Statement createStatem(Connection c) {
		Statement state = null;

		try {
			state = c.createStatement();
		} catch (SQLException e3) {
			System.out.println("Fel vid skapande av statement");
			System.out.println(e3.getMessage());
			System.exit(1);
		}

		return state;
	}

	public boolean checkMemberId(int testId) {
		res = null;
		try {
			res = state.executeQuery("select id from medlem");

			//ResultSetMetaData mData = res.getMetaData();
			//int colCount = mData.getColumnCount();

			while (res.next()) {
				//for (int i = 1; i <= colCount)
				if (testId == (Integer) res.getObject(1))
					return true;
			}
			return false;
		} catch (SQLException se) {
			System.out.println("executeQuery fel");
			System.out.println(se.getMessage());
			System.exit(1);
			return true;
		}
	}

	public boolean checkParentId(int testId) {
		res = null;
		try {
			String parent = String.format("select pid from children where pid = %d", testId);
			res = state.executeQuery(parent);

			//ResultSetMetaData mData = res.getMetaData();
			//int colCount = mData.getColumnCount();

			while (res.next()) {
				//for (int i = 1; i <= colCount)
				if (testId == (Integer) res.getObject(1))
					return true;
			}
			return false;
		} catch (SQLException se) {
			System.out.println("executeQuery fel");
			System.out.println(se.getMessage());
			System.exit(1);
			return true;
		}
	}

	public ResultSet getMembers() {
		res = null;

		try {
			// se över så att detta gör vad jag tänkt, altså joinar alla 3 tabeller på ett logiskt sätt
			res = state.executeQuery("SELECT medlem.id, givenName, familyName, email, gender, birth, memberSince, active, role, team, cid FROM (funktion left outer join medlem on funktion.id = medlem.id) left outer join children on medlem.id = children.pid or medlem.id = children.cid");
		} catch (SQLException se) {
			JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
			System.out.println("executeQuery fel");
			System.out.println(se.getMessage());
			System.exit(1);
		}

		return res;
	}
	// Rensa parametrar för denna så att istället för namn,email,birth etc.etc. så tar den ett Person Object.
	public void addMember(int id, String gName, String fName, String email, String gender, String birth, String mSince, int active, ArrayList<Integer> roleList, String team, ArrayList<Integer> childList) {
		
		String add = "insert into medlem values (" + id + ", " + "'" + gName + "', " + "'" + fName + "', " + "'" +
					 email + "', " + "'" + gender + "', " + "'" + birth + "', " + "'" + mSince + "', " + active + ")";
		try {
		state.executeUpdate(add);	
		for (Integer role : roleList) {
			if (role == 0 || role == 1) { // Spelare eller coach
				String plch = String.format("insert into funktion values (%d, %d, '%s')", id, role, team); 
				state.executeUpdate(plch);
			} else { // förälder
				String par = String.format("insert into funktion values (%d, %d, NULL)", id, role); 
				state.executeUpdate(par);
				for (Integer childId : childList) {
					String childAdd = String.format("insert into children values (%d, %d)", id, childId);
					state.executeUpdate(childAdd);
				}
			}
		}
		} catch (SQLException se) {
			JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
			System.out.println("SQL error vid lagring av medlem");
			System.out.println(se.getMessage());
			System.exit(1);
		}

	}

	public String getTeam(int id) {
		res = null;
		String team = null;
		try {
			String getTeamName = String.format("select distinct team from funktion where funktion.id = %d", id);
			res = state.executeQuery(getTeamName);
			while (res.next()) {
				team = res.getString(1);
			}
		} catch (SQLException se) {
			System.out.println(se.getMessage());
		}			
		return team;

	}

	public void updateMember(int id, String email, ArrayList<Integer> roleList, ArrayList<Integer> childList, int active) {
		try {

			String update = String.format("update medlem set active = %d, email = '%s' where medlem.id = %d", active, email, id);
			state.executeUpdate(update);
			String team = getTeam(id);
			String removeRoles = String.format("delete from funktion where funktion.id = %d", id);
			state.executeUpdate(removeRoles);
			

			for (Integer role : roleList) {
				if (role == 0 || role == 1) { // Spelare eller coach
					if (team == null) {
						String plch = String.format("insert into funktion values (%d, %d, NULL)", id, role); 
						state.executeUpdate(plch);
					} else {	
						String plch = String.format("insert into funktion values (%d, %d, '%s')", id, role, team); 
						state.executeUpdate(plch);
					}
				} else { // förälder

					if (checkParentId(id)) {
						String removeParent = String.format("delete from children where children.pid = %d", id);
						state.executeUpdate(removeParent);
					}

					String par = String.format("insert into funktion values (%d, %d, NULL)", id, role); 
					state.executeUpdate(par);
					for (Integer childId : childList) {
						String childAdd = String.format("insert into children values (%d, %d)", id, childId);
						state.executeUpdate(childAdd);
					}
				}
			}
		} catch (SQLException se) {
			JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
			System.out.println(se.getMessage());
			System.out.println("allt");
		}
	}

	public void updateMember(int id, ArrayList<Integer> roleList, ArrayList<Integer> childList, int active) {
		try {

			String update = String.format("update medlem set active = %d where medlem.id = %d", active, id);
			state.executeUpdate(update);
			String team = getTeam(id);
			String removeRoles = String.format("delete from funktion where funktion.id = %d", id);
			state.executeUpdate(removeRoles);
			

			for (Integer role : roleList) {
				if (role == 0 || role == 1) { // Spelare eller coach
					if (team == null) {
						String plch = String.format("insert into funktion values (%d, %d, NULL)", id, role); 
						state.executeUpdate(plch);
					} else {	
						String plch = String.format("insert into funktion values (%d, %d, '%s')", id, role, team); 
						state.executeUpdate(plch);
					}
				} else { // förälder

					if (checkParentId(id)) {
						String removeParent = String.format("delete from children where children.pid = %d", id);
						state.executeUpdate(removeParent);
					}

					String par = String.format("insert into funktion values (%d, %d, NULL)", id, role); 
					state.executeUpdate(par);
					for (Integer childId : childList) {
						String childAdd = String.format("insert into children values (%d, %d)", id, childId);
						state.executeUpdate(childAdd);
					}
				}
			}

		} catch (SQLException se) {
			JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
			System.out.println(se.getMessage());
			System.out.println("inte email");
		}
	}

	public void updateMember(int id, String email, int active) {
		try {
			String update = String.format("update medlem set active = %d, email = '%s' where medlem.id = %d", active, email, id);
			state.executeUpdate(update);
		} catch (SQLException se) {
			JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
			System.out.println(se.getMessage());
			System.out.println("inte roll");
		}
	}

	public void updateMember(int id, int active) {
		try {
			String update = String.format("update medlem set active = %d where medlem.id = %d", active, id);
			state.executeUpdate(update);
		} catch (SQLException se) {
			JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
			System.out.println(se.getMessage());
			System.out.println("bara active");
		}
	}


	public void removeMember(int id) {

		try {
			String removeMedlem = String.format("delete from medlem where medlem.id = %d", id);
			state.executeUpdate(removeMedlem);

			String removeRoles = String.format("delete from funktion where funktion.id = %d", id);
			state.executeUpdate(removeRoles);

			if (checkParentId(id)) {
						String removeParent = String.format("delete from children where children.pid = %d", id);
						state.executeUpdate(removeParent);
			}
		} catch (SQLException se) {
			System.out.println(se.getMessage());
			System.out.println("Remove error");
		}

	}

	public ResultSet searchMember(int id) {
		res = null;
		try {
		String search = String.format("SELECT medlem.id, givenName, familyName, email, gender, birth, memberSince, active, role, team, cid FROM ((funktion left outer join medlem on funktion.id = medlem.id) left outer join children on medlem.id = children.pid or medlem.id = children.cid) where medlem.id = \"%d\"", id);
		res = state.executeQuery(search);
		} catch (SQLException se) {
			JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
			System.out.println("executeQuery fel");
			System.out.println(se.getMessage());
			System.exit(1);
		}
		return res;
	}

	public ResultSet searchMember(int type, String searchFor) {
		res = null;
		try {
		if (type == 0) {
			String search = String.format("SELECT medlem.id, givenName, familyName, email, gender, birth, memberSince, active, role, team, cid FROM ((funktion left outer join medlem on funktion.id = medlem.id) left outer join children on medlem.id = children.pid or medlem.id = children.cid) where medlem.familyName = \"%s\"", searchFor);
			res = state.executeQuery(search);
		} else if (type == 1) {
			String search = String.format("SELECT medlem.id, givenName, familyName, email, gender, birth, memberSince, active, role, team, cid FROM ((funktion left outer join medlem on funktion.id = medlem.id) left outer join children on medlem.id = children.pid or medlem.id = children.cid) where funktion.team = \"%s\" and funktion.role = 1", searchFor);
			res = state.executeQuery(search);
		} else {
			System.out.println("Search member(2parameters) fel typ");
		}
		} catch (SQLException se) {
			JOptionPane.showMessageDialog(null, "Databas error", "ERROR!", JOptionPane.ERROR_MESSAGE);
			System.out.println("executeQuery fel");
			System.out.println(se.getMessage());
			System.exit(1);
		}
		return res;
	}

	//public 

	public DBCon() {
		con = createConnection();
		state = createStatem(con);
	}
}