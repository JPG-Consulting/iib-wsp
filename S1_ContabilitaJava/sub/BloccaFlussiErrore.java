package sub;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;

public class BloccaFlussiErrore extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		String societa = null;
		String tipoFlusso = null;
		String db = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = null;
		int count = 0;
		
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			if (inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/sistema") != null) {
				db = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/sistema").getValueAsString();
			} else {
				db = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/societa").getValueAsString();
			}
			societa = inMessage.getRootElement().getFirstElementByPath("/XMLNSC/lancio/soc").getValueAsString();
			tipoFlusso = inMessage.getRootElement().getFirstElementByPath("/XMLNSC/lancio/tipo_flusso").getValueAsString();

			if (db.equals("N1") || db.equals("N2") || db.equals("N3")) {
				sql = "SELECT COUNT(*) AS NUMREC FROM EXT_SAP_FLUSSO WHERE ELABORATO = 'K' AND SOC_SIU = '" + societa + "' AND TIPO_FLUSSO = '" + tipoFlusso + "'";
			} else if (db.equals("C1") || db.equals("C2") || db.equals("C3")) {
				sql = "SELECT COUNT(*) AS NUMREC FROM EXT_SAP_FLUSSO WHERE ELABORATO = 'Z' AND SOC_CNRG = '" + societa + "' AND TIPO_FLUSSO = '" + tipoFlusso + "'";
			} else if (db.equals("I1")) {
				sql = "SELECT COUNT(*) AS NUMREC FROM GLCAE.EXT_SAP_FLUSSO WHERE ELABORATO = 'K'";
			}
			conn = getJDBCType4Connection(db, JDBC_TransactionType.MB_TRANSACTION_AUTO);
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				count = rs.getInt(1);
			}
			
			if (count>0) {
				throw new Exception("Esistono flussi in errore per la società SIU/CNRG " + societa);
			}

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
