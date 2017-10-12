package sub;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import java.lang.Math;

import sun.util.resources.CalendarData;


public class Scheduler extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			String db = null;
			Calendar now = Calendar.getInstance();
			Calendar sched = Calendar.getInstance();
			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;
			String sql = null;
			int sleepTime = 1000;
			
			if (inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/TimeoutRequest/Identifier") != null) {
				db = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/TimeoutRequest/Identifier").getValueAsString();
				
				conn = getJDBCType4Connection("INT_ENGINE", JDBC_TransactionType.MB_TRANSACTION_AUTO);
				stmt = conn.createStatement();
				sql = "SELECT ORA, MINUTI FROM IIB_SCHEDULER WHERE PROCESSO = '"+db+"' ";
				
				rs = stmt.executeQuery(sql);
				while(rs.next()){
					sched.set(Calendar.HOUR_OF_DAY, rs.getInt("ORA"));
					sched.set(Calendar.MINUTE, rs.getInt("MINUTI"));
					int t = (sched.get(Calendar.HOUR_OF_DAY) - now.get(Calendar.HOUR_OF_DAY))*3600 + (sched.get(Calendar.MINUTE) - now.get(Calendar.MINUTE))*60 + (sched.get(Calendar.SECOND) - now.get(Calendar.SECOND));
					if(Math.abs(t)<Math.abs(sleepTime)) sleepTime = t;
				}
				
				if (Math.abs(sleepTime) > 25) {
					rs.close();
					return;
				}
			
			}
			
			rs.close();
			

			// End of user code
			// ----------------------------------------------------------
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

}
