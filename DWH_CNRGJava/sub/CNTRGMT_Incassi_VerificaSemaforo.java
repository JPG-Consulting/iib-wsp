package sub;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;
import com.sun.jmx.snmp.Timestamp;

public class CNTRGMT_Incassi_VerificaSemaforo extends MbJavaComputeNode {

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
			CallableStatement csVer = null;
			ResultSet rs = null;
			String numberRow= "";

			String sistema = "DWH_CREDITO";
			String VerifError = "SELECT * FROM (Select STATO_ELABORAZIONE from STAGING_DWH.CNRGLM_SEMAFORO where NOME_TABELLA = 'CNRGLM_IN_INCASSO' and  TIPO_ELABORAZIONE = 'R' order by TS_INIZIO desc ) WHERE ROWNUM = 1";					
			String SemaforoIns = "INSERT INTO STAGING_DWH.CNRGLM_SEMAFORO (NOME_TABELLA , TIPO_ELABORAZIONE , TS_INIZIO, STATO_ELABORAZIONE ) VALUES ('CNRGLM_IN_INCASSO','C', sysdate,'0')";


			conn = getJDBCType4Connection(sistema, JDBC_TransactionType.MB_TRANSACTION_AUTO);
			
			// Verifico che l'ultima operazione è stata eseguita con successo
			csVer = conn.prepareCall(VerifError);
			rs = csVer.executeQuery();

			rs.next();
			
		    try {
				numberRow = rs.getString(1);
		    } catch (SQLException e) {
		    	numberRow = "1";
		    }
			
			rs.close();
			csVer.close();
			
			if (numberRow.equals("0") ) {
				alt.propagate(outAssembly);		
			} else {
				csVer = conn.prepareCall(SemaforoIns);
				rs = csVer.executeQuery();
				rs.close();
				csVer.close();
				out.propagate(outAssembly);		
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

	}

}
