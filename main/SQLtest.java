public class SQLtest {
	public static void main(String[] args) {
		int id = 1;
		int active = 1;
		int role = 0;
		String gName = "Hej";
		String team = "Hej";
		String fName = "Hej";
		String email = "Hej";
		String gender = "Hej";
		String birth = "Hej";
		String mSince = "Hej";

		String add = "insert into medlem values (" + id + ", " + "'" + gName + "', " + "'" + fName + "', " + "'" +
		 email + "', " + "'" + gender + "', " + "'" + birth + "', " + "'" + mSince + "', " + active + ")";
		String spel = String.format("insert into funktion values (%d, %d, '%s')", id, role, team); 
		String test = String.format("insert into funktion values (%d, %d, NULL)", id, role); 
		System.out.println(add);
		System.out.println(test);
		System.out.println(spel);
	
		String searchFor = "hej";
		String teamQuery = String.format("SELECT medlem.id, givenName, familyName, email, gender, birth, memberSince, active, role, team, cid FROM ((funktion left outer join medlem on funktion.id = medlem.id) left outer join children on medlem.id = children.pid or medlem.id = children.cid order by medlem.id) where funktion.team = \"%s\" order by medlem.id", searchFor);
		String search = String.format("SELECT * FROM ((funktion left outer join medlem on funktion.id = medlem.id) left outer join children on medlem.id = children.pid or medlem.id = children.cid) where medlem.familyName = \"%s\"", searchFor);
		String nr = "2";
		System.out.println(teamQuery);
		int i;
		i = Integer.parseInt(nr);
	}
}