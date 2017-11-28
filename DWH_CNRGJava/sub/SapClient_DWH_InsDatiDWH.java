package sub;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.sun.jmx.snmp.Timestamp;

public class SapClient_DWH_InsDatiDWH extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below

			Connection conn = null;
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			CallableStatement csIns = null;
			ResultSet rs = null;
			MbElement param = inMessage.getRootElement();

			
			String R1 = null;
			String R2 = null;
			String R3 = null;
			String R4 = null;

			String sistema = "DWH_SAP500";
			String Insert = "INSERT INTO STAGING_DWH.SAPSD5_IN_CLIENTE (ID_SISTEMA_PROVENIENZA_DATI , ID_SOCIETA_COMPETENZA , ID_CLIENTE, CLASSIFICAZIONE_CLIENTE ) VALUES ('"+R1+"','"+R2+"', "+R3 +",'"+R4+"')";

			MbElement	tmp = param.getFirstElementByPath("DFDL").getFirstChild(); 
			
			MbElement rec;
			
			while (tmp != null) {
				
			rec = tmp.getFirstChild();
			
			R1 = rec.getValueAsString();
			rec = rec.getNextSibling();
			R2 = rec.getValueAsString();
			rec = rec.getNextSibling();
			R3 = rec.getValueAsString();
			rec = rec.getNextSibling();
			R4 = rec.getValueAsString();
			rec = rec.getNextSibling();

			
			conn = getJDBCType4Connection(sistema,JDBC_TransactionType.MB_TRANSACTION_AUTO); 
			
			csIns = conn.prepareCall(Insert);
			csIns.execute();

			
			tmp = tmp.getNextSibling();
			
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
