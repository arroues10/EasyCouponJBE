package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import MyExceptions.SystemMalfunctionException;

/*
 * Singleton is a design pattern which allows only a single instantiation of a class.
 */
public class ConnectionPool {

	private static ConnectionPool instance;

	private static final int MAX_CONNECTIONS = 10;

	private BlockingQueue<Connection> connections;

	static {
		try {
			// This makes sure that the Driver class ( from the mysql-connector ) is indeed
			// loaded.
			// It is loaded automatically when we run the project locally, though when
			// running it from tomcat it is not being loaded for some reason.
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			// Should never happen.
		}
	}

	public static synchronized ConnectionPool getInstance() throws SystemMalfunctionException {
		if (instance == null) {
			instance = new ConnectionPool();
		}
		return instance;
	}

	private ConnectionPool() throws SystemMalfunctionException {
		connections = new LinkedBlockingQueue<Connection>(MAX_CONNECTIONS);

		int remainingCapacity = connections.remainingCapacity();

		for (int i = 0; i < remainingCapacity; ++i) {
			connections.offer(createConnection());
		}
	}

	/**
	 * @return A connection from the pool.
	 * @throws SystemMalfunctionException
	 */
	public synchronized Connection getConnection() throws SystemMalfunctionException {
		try {
			return connections.take();
		} catch (InterruptedException e) {
			// Should never happen.
			throw new SystemMalfunctionException(
					"FATAL: Thread got interrupted while waiting for available connection.");
		}
	}

	/**
	 * Returns a given connection to the queue.
	 * 
	 * @param connection The given connection you'd like to insert back into the
	 *                   queue.
	 * @throws SystemMalfunctionException
	 */
	public synchronized void returnConnection(Connection connection) throws SystemMalfunctionException {
		try {
			connections.put(connection);
		} catch (InterruptedException e) {
			// Should never happen.
			throw new SystemMalfunctionException(
					"FATAL: Thread got interrupted while waiting for available space to become available.");
		}
	}

	/**
	 * Retrieves and removes all the elements of the maintained queue, and closes
	 * all resources.
	 * 
	 * @throws SystemMalfunctionException
	 */
	public void closeAllConnections() throws SystemMalfunctionException {
		Connection connection;
		while ((connection = connections.poll()) != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new SystemMalfunctionException("FATAL: unable to close all connections.");
			}
		}
	}

	private static Connection createConnection() throws SystemMalfunctionException {
		try {
			return DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/couponsproject?useSSL=false&serverTimezone=UTC", "root", "D5equity");
		} catch (SQLException e) {
			throw new SystemMalfunctionException("FATAL: Unable to create a connection.");
		}
	}
}
