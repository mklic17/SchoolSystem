import java.sql.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
//import com.microsoft.sqlserver.jbdc.*;
import java.io.*;
import java.util.*;
import javax.swing.*; 

public class Main {
	
	public static String connectionString = "jdbc:sqlserver://10.1.14.17\\CSSQLSERVER:1433;";
	public static void main(String[] args) {
		String[] info = getInfo();
		
		connectionString += "database=" + info[0] + ";user=" + info[1] + ";password=" + info[2] + ";";
		while (1!=2){
//			String[][]data = null;
			final String[] views = getViews();
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI(views);
	            }
	        });
			final String[][] data = databaseThings();
			printData(data);
	        
		}
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
	
	
    private static void createAndShowGUI(String[] views) {
        JFrame frame = new JFrame("Student Body Database");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400, 500);
        addComponentsToPane(frame.getContentPane(), views);
//        JTable(Object[][] rowData, Object[] columnNames)

        frame.pack();
        frame.setVisible(true);
    }
    
    public static void addComponentsToPane(Container pane, String[] views){
    	  JButton button = new JButton("Button 1 (PAGE_START)");
//          pane.add(button, BorderLayout.PAGE_START);
          JComboBox viewList = new JComboBox(views);
          pane.add(viewList, BorderLayout.PAGE_START);
           
          //Make the center component big, since that's the
          //typical usage of BorderLayout.
          button = new JButton("Button 2 (CENTER)");
          button.setPreferredSize(new Dimension(200, 100));
          pane.add(button, BorderLayout.CENTER);
           
          button = new JButton("Button 3 (LINE_START)");
          pane.add(button, BorderLayout.LINE_START);
           
          button = new JButton("Long-Named Button 4 (PAGE_END)");
          pane.add(button, BorderLayout.PAGE_END);
           
          button = new JButton("5 (LINE_END)");
          pane.add(button, BorderLayout.LINE_END);
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
	
	
	public static String[][] databaseThings() {
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;	
		String[][]finalData = null;
		try {
//			connection = DriverManager.getConnection(connectionString);
//			String selectSql = "SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE ='VIEW'";
//			statement = connection.createStatement();
//			resultSet = statement.executeQuery(selectSql);
//			while(resultSet.next()){
//				System.out.println("   " + resultSet.getString(3)); // gets the views specifically
//			}
//			
			String s = "SELECT * FROM " + askForInput();
			String selectSql = s;
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectSql);
			ResultSetMetaData rsmd = resultSet.getMetaData();
			int columnsNumber = rsmd.getColumnCount(); // columns number

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
	
	public static String askForInput() {
		Scanner scan = new Scanner(System.in);
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
	
}
