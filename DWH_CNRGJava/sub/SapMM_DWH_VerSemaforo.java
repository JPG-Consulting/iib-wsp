package sub;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;
import com.sun.jmx.snmp.Timestamp;

public class SapMM_DWH_VerSemaforo extends MbJavaComputeNode {

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


			String sistema = "DWH_CNRG";
			String VerifError = "Select count(*) from STAGING_DWH.CNRGDIS_SEMAFORO where STATO_ELABORAZIONE != 1 and NOME_TABELLA = 'CNRGDIS_IN_CLIENTE'";
			String SemaforoIns = "INSERT INTO STAGING_DWH.CNRGDIS_SEMAFORO (NOME_TABELLA , TIPO_ELABORAZIONE , TS_INIZIO, STATO_ELABORAZIONE ) VALUES ('CNRGDIS_IN_CLIENTE','W', to_date(sysdate, 'dd/mm/yy'),'0')";

			try{
			conn = getJDBCType4Connection(sistema, JDBC_TransactionType.MB_TRANSACTION_AUTO);
			
			// Verifico che l'ultima operazione è stata eseguita con successo
			csVer = conn.prepareCall(VerifError);
			rs = csVer.executeQuery();
			int numberRow = 0;
			rs.next();
			numberRow = rs.getInt("count(*)");
			rs.close();
			
			if (numberRow != 0) {
				throw new Exception("Esistono flussi in errore ");
			} else {
				csVer = conn.prepareCall(SemaforoIns);
				rs = csVer.executeQuery();
				rs.close();
				out.propagate(outAssembly);		
			}
			}
		
			finally {
				//if (stmt!= null) stmt.close();
				if (conn != null && !conn.isClosed()) {
					 conn.close();
					 }			
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
