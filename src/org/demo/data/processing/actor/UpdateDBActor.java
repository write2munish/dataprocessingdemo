package org.demo.data.processing.actor;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.demo.data.processing.messages.InstrumentationMsg;
import org.demo.data.processing.messages.MockData;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.Duration;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class UpdateDBActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	Connection conn = null;
	PreparedStatement stat = null;
	int noOfMessages = 0;

	long lastCommitTime;

	ActorRef instrumentationActor;

	public UpdateDBActor(ActorRef instrumentation) {
		instrumentationActor = instrumentation;
	}

	@Override
	public void preStart() {

		try {
			// Load the JDBC driver
			String driver = "com.mysql.jdbc.Driver";
			Class.forName(driver);

			// Create a connection to the database
			String url = "jdbc:mysql://localhost/mockDB?rewriteBatchedStatements=true&useLocalSessionState=true";
			String username = "root";
			String password = "root";

			conn = (Connection) DriverManager.getConnection(url, username,
					password);

			// Disabled the auto commit. By default, it is always true.
			conn.setAutoCommit(false);

			// Create the prepared statement
			String updateSQL = "UPDATE MOCK_DATA SET calculatedValue = ?, timesProcessed = ? WHERE id = ?";
			stat = (PreparedStatement) conn.prepareStatement(updateSQL);

		} catch (ClassNotFoundException e) {
			log.error(e.toString());
		} catch (SQLException e) {
			log.error(e.toString());
		}
	}

	@Override
	public void postStop() {
		try {
			executeBatch();
			stat.close();
			conn.close();
		} catch (SQLException e) {

			log.error(e.toString());
		}

	}

	@Override
	public void onReceive(Object msg) throws Exception {
		if (msg instanceof MockData) {
			MockData data = (MockData) msg;
			if (stat != null) {
				stat.setDouble(1, data.getCalculatedValue());
				stat.setInt(2, data.getTimesProcessed());
				stat.setDouble(3, data.getId());
				stat.addBatch();
				noOfMessages++;
			}
			if (noOfMessages > 200) {
				executeBatch();
			}
			getContext()
					.system()
					.scheduler()
					.scheduleOnce(Duration.create(10, TimeUnit.MILLISECONDS),
							getSelf(), "Next");
		}
		if (msg instanceof String) {
			long timeSinceLastCommit = System.currentTimeMillis()
					- lastCommitTime;
			if (timeSinceLastCommit > 50)
				executeBatch();
		}
	}

	private void executeBatch() throws SQLException {
		int[] updateCounts = stat.executeBatch();
		int totalRowUpdate = 0;
		boolean updateAll = false;
		boolean updateEmpty = false;
		boolean updateFail = false;
		for (int i = 1; i < updateCounts.length; i++) {
			if (updateCounts[i] >= 1) {
				totalRowUpdate = totalRowUpdate + 1;
				updateAll = true;
			} else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
				updateEmpty = true;
			} else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
				updateFail = true;
			}
		}

		// Error found. Rollback.
		// No error, Commit the batch
		if (updateEmpty || updateFail) {
			conn.rollback();
			log.info("Batch getting rolled back");
		} else if (updateAll) {
			lastCommitTime = System.currentTimeMillis();
			conn.commit();
			// log.info("Total Row Update: " + totalRowUpdate);
			instrumentationActor.tell(new InstrumentationMsg(2, System
					.currentTimeMillis()));
		}

		// Once the batch is executed, it is a good practice to clear
		// the batch before adding any new query.
		stat.clearBatch();

		// reset the counter
		noOfMessages = 0;
	}
}
