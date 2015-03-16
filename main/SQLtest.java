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
		String search = String.format("SELECT * FROM ((funktion left outer join medlem on funktion.id = medlem.id) left outer join children on medlem.id = children.pid or medlem.id = children.cid) where medlem.familyName = \"%s\"", searchFor);
		String nr = "2";
		System.out.println(search);
		int i;
		i = Integer.parseInt(nr);
	}
}