import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXMLNSC;


public class ConnectionTester_JavaCompute extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage();
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);

			String sistema = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/dsn").getValueAsString();
			Connection conn = null;
			PreparedStatement s = null;
			conn = getJDBCType4Connection(sistema, JDBC_TransactionType.MB_TRANSACTION_AUTO);
			s = conn.prepareStatement("SELECT USER, SYS_CONTEXT('USERENV', 'DB_NAME') AS DB_NAME, SYS_CONTEXT('USERENV', 'SERVER_HOST') AS HOST FROM DUAL");
			ResultSet rs = s.executeQuery();
			rs.next();
			outMessage.getRootElement().createElementAsLastChild(MbXMLNSC.PARSER_NAME).createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DATA", null).createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "USER", rs.getObject("USER")).createElementAfter(MbElement.TYPE_NAME_VALUE, "DB_NAME", rs.getObject("DB_NAME")).createElementAfter(MbElement.TYPE_NAME_VALUE, "HOST", rs.getObject("HOST"));


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
