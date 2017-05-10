import java.sql.*;
//import com.microsoft.sqlserver.jbdc.*;
import java.io.*;
import java.util.*;


public class Main {

	public static String connectionString = "jdbc:sqlserver://10.1.14.17\\CSSQLSERVER:1433;";

	public static void main(String[] args) {
		String[] info = getInfo();

		connectionString += "database=" + info[0] + ";user=" + info[1] + ";password=" + info[2] + ";";

		String decision = whatToDo();
//		System.out.println(decision);
		if (decision.equals("1")) {
			insertGrades();
		}
		else if (decision.equals("2")){
			Views();
		}
		else if (decision.equals("3")){
			transcript();
		}
		main(args); // probably not good practice to call this
	}

	public static void transcript(){
		String studentName = askForStudentName();
		getTheTranscript(studentName);
	}

	public static void getTheTranscript(String studentName) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection(connectionString);
			String selectSql = "SELECT studentName, className, semester, grade, GPA as CumGPA, team FROM ListStudentsByGPA " +
							   "JOIN Grades ON ListStudentsByGPA.ID = Grades.studentId " +
							   "JOIN Classes ON Grades.classId = Classes.Id " +
							   "JOIN Sports ON ListStudentsByGPA.ID = Sports.studentId " +
							   "WHERE StudentName Like '" + studentName+ "' " +
							   "ORDER BY semester DESC;";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectSql);
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnsNumber = rsmd.getColumnCount(); // columns number

			while(resultSet.next()){
				for (int i = 1; i < columnsNumber; i++){
					System.out.print(resultSet.getString(i) + " ");
				}
				System.out.println("");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (resultSet != null) try { resultSet.close(); } catch(Exception e) {}
			if (statement != null) try { statement.close(); } catch(Exception e) {}
			if(connection != null) try { connection.close(); } catch(Exception e) {}
		}
	}

//	public static String[] getTableNames() {
////		SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('dbo.yourTableName')
//	}


	public static String askForStudentName() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter in the name of the student (first and lastname)");
		return scan.nextLine();
	}
	public static void insertGrades() {
		String[] data = acquireDataForGrades();
		insertTheAcquiredData(data);
	}


	public static String[] acquireDataForGrades() {
		String[] data = new String[4];
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the exact class name (className)");
		data[0] = scan.nextLine();
		System.out.println("Enter current Semester");
		data[1] = scan.nextLine();
		System.out.println("Enter the current students name (could be a shorter name but needs to only return 1 name");
		data[2] = scan.nextLine();
		System.out.println("enter the grade the student recieved");
		data[3] = scan.nextLine();
		return data;
	}

	public static void insertTheAcquiredData(String[] data) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;

		try {
			connection = DriverManager.getConnection(connectionString);
			String selectSql = "SELECT Id FROM Students Where studentName LIKE '%" + data[2] + "%'";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectSql);
			int count = 1;
			String id = "";
			while(resultSet.next()){
				id = resultSet.getString(count);
			}

			selectSql = "SELECT Id From Classes Where className = '" + data[0] + "'";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectSql);
			count = 1;
			String className = "";
			while(resultSet.next()){
				className = resultSet.getString(count);
			}

			selectSql = "Insert Into Grades(classId, semester, studentId, grade) values (" + className + ", '" + data[1] + "', " + Integer.valueOf(id) + ", '" + data[3] + "');";
			statement = connection.createStatement();
			statement.executeQuery(selectSql);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (resultSet != null) try { resultSet.close(); } catch(Exception e) {}
			if (statement != null) try { statement.close(); } catch(Exception e) {}
			if(connection != null) try { connection.close(); } catch(Exception e) {}
		}
	}

	public static void Views() {
		final String[] views = getViews();
		String s = "SELECT * FROM " + askForInput(views);
		String[][] data = databaseThings(s);
		printData(data);
	}


	public static String[] getInfo() {
		String fileName = "src/ignore.txt";
		String line = null;
		String[] info = new String[3];
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int count = 0;
			while((line = bufferedReader.readLine()) != null) {
				if (count == 0) {
					info[0] = line;
				}
				else if (count == 1) {
					info[1] = line;
				}
				else {
					info[2] = line;
				}
				count++;
			}
			bufferedReader.close();
		}
		catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
		return info;
	}

    public static String[] getViews() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String[] views = new String[10]; // we have 10 views
		try {
			connection = DriverManager.getConnection(connectionString);
			String selectSql = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE ='VIEW'";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectSql);
			int x = 0;
			while(resultSet.next()){
				views[x] = resultSet.getString(3); // gets the views specifically
				x++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (resultSet != null) try { resultSet.close(); } catch(Exception e) {}
			if (statement != null) try { statement.close(); } catch(Exception e) {}
			if(connection != null) try { connection.close(); } catch(Exception e) {}
		}
		return views;
    }

	public static String[][] databaseThings(String selectSql) {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		String[][]finalData = null;
		try {
			connection = DriverManager.getConnection(connectionString);
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectSql);
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnsNumber = rsmd.getColumnCount(); // columns number

//			I was trying to convert it to a 2-D array so I could easily transition this to a Jtable but never got around to doing the GUI

			ArrayList<String> tempData = new ArrayList<String>();
			while(resultSet.next()){
				String other = "";
				for (int j = 0; j < columnsNumber; j++) {
					other += resultSet.getString(j+1) +" , "; // Special character that the data won't have
				}
				tempData.add(other);
			}
			finalData = new String[tempData.size()][columnsNumber];
			for (int i = 0; i < tempData.size(); i++) {
				String part = tempData.get(i);
				String[] almostThere = part.split(" , ");
				for (int j = 0; j < columnsNumber; j++){
					finalData[i][j] = almostThere[j];
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (resultSet != null) try { resultSet.close(); } catch(Exception e) {}
			if (statement != null) try { statement.close(); } catch(Exception e) {}
			if(connection != null) try { connection.close(); } catch(Exception e) {}
		}
		return finalData;
	}

	public static String askForInput(String[] views) {
		Scanner scan = new Scanner(System.in);
		for(int i = 0; i < views.length; i++) {
			System.out.println(views[i]);
		}
		System.out.println("Enter the name of the View here ");
		String move = scan.nextLine();
		return move;
	}

	public static void printData(String[][] data) {
		for(int i = 0; i < data.length; i++){
			for(int j = 0; j < data[i].length; j++){
				System.out.print(data[i][j] + " ");
			}
			System.out.println("\n" + "-------------------");
		}
	}

	public static String whatToDo() {
		Scanner scan = new Scanner(System.in);
		System.out.println("Do you want to insert grades (1), see a list of preset views (2), or see a students transcript (3)?");
		String toDo = scan.nextLine();
		return toDo;
	}

}
