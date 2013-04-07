package com.wipro.data.processing.actor;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.wipro.data.processing.messages.FetchData;
import com.wipro.data.processing.messages.InstrumentationMsg;
import com.wipro.data.processing.messages.MockData;

public class LoadDataActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	Connection conn = null;
	PreparedStatement stat = null;
	ActorRef processingActor;
	ActorRef instrumentationActor;

	public LoadDataActor(ActorRef processing, ActorRef instrumentation) {
		processingActor = processing;
		instrumentationActor = instrumentation;
	}

	@Override
	public void preStart() {

		try {
			// Load the JDBC driver
			String driver = "com.mysql.jdbc.Driver";
			Class.forName(driver);

			// Create a connection to the database
			String url = "jdbc:mysql://localhost/mockDB?useLocalSessionState=true";
			String username = "root";
			String password = "root";

			conn = (Connection) DriverManager.getConnection(url, username,
					password);

			// Disabled the auto commit. By default, it is always true.
			conn.setAutoCommit(false);

			// Create the prepared statement
			String updateSQL = "Select * from MOCK_DATA WHERE id > ? and id <= ?";
			stat = (PreparedStatement) conn.prepareStatement(updateSQL);

		} catch (ClassNotFoundException e) {
			log.error(e.toString());
		} catch (SQLException e) {
			log.error(e.toString());
		}

		instrumentationActor.tell(new InstrumentationMsg(1, System
				.currentTimeMillis()));
	}

	@Override
	public void postStop() {
		try {
			stat.close();
			conn.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof FetchData) {

			FetchData data = (FetchData) msg;
			if (stat != null) {
				stat.setInt(1, data.getStartRowId());
				stat.setInt(2, data.getStartRowId() + data.getNoOfRows());
				ResultSet resultSet = stat.executeQuery();
				processResultSet(resultSet);
			}

			int count = data.getStartRowId() + data.getNoOfRows();
	//		log.info("Loaded and send data for processing from "
	// 				+ data.getStartRowId() + " to " + count);
		}
	}

	private void processResultSet(ResultSet resultSet) throws SQLException {
		if (resultSet != null)
			while (resultSet.next()) {
				int id = resultSet.getInt("Id");
				String productName = resultSet.getString("productName");
				String productType = resultSet.getString("productType");
				double rate = resultSet.getDouble("rate");
				double calculatedValue = resultSet.getDouble("calculatedValue");
				int timesProcessed = resultSet.getInt("timesProcessed");

				processingActor.tell(new MockData(id, productName, productType,
						rate, calculatedValue, timesProcessed));
			}
	}
}
