package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	private static final String URL = "jdbc:mysql://localhost:3306/bug_tracker";
	private static final String USER = "root";
	private static final String PASSWORD = "Zath032002";

	private static Connection connection = null;

	private DatabaseConnection() {
	}

	// Returns the shared connection, reconnecting automatically if it was closed or lost
	public static Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection(URL, USER, PASSWORD);
				System.out.println("Connected to database successfully!");
			}
		} catch (SQLException e) {
			System.out.println("Failed to connect to database: " + e.getMessage());
			e.printStackTrace();
		}
		return connection;
	}

	// Call this when the app closes to cleanly shut down the connection
	public static void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
				System.out.println("Database connection closed.");
			} catch (SQLException e) {
				System.out.println("Error closing connection: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}