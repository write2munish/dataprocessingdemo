package com.wipro.data.processing;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.wipro.data.processing.messages.MockData;

public class TestDBConn {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement stat = null;

		try {
			// Load the JDBC driver
			String driver = "com.mysql.jdbc.Driver";
			Class.forName(driver);

			// Create a connection to the database
			String url = "jdbc:mysql://localhost/mockDB";
			String username = "root";
			String password = "root";

			conn = (Connection) DriverManager.getConnection(url, username,
					password);

			// Disabled the auto commit. By default, it is always true.
			conn.setAutoCommit(false);

			// Create the prepared statement
			String updateSQL = "Select * from MOCK_DATA WHERE id > ? and id <= ?";
			stat = (PreparedStatement) conn.prepareStatement(updateSQL);
			
			stat.setInt(1, 0);
			stat.setInt(2, 10);
			ResultSet resultSet = stat.executeQuery();
			
			if (resultSet != null)
				while (resultSet.next()) {
					int id = resultSet.getInt("Id");
					String productName = resultSet.getString("productName");
					String productType = resultSet.getString("productType");
					double rate = resultSet.getDouble("rate");
					double calculatedValue = resultSet.getDouble("calculatedValue");
					int timesProcessed = resultSet.getInt("timesProcessed");

					System.out.println(id);
				
				}
			
			

		} catch (ClassNotFoundException e) {
			System.err.println(e.toString());
		} catch (SQLException e) {
			System.err.println(e.toString());
		}

	}

}
