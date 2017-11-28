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

public class CNRG_Cliente_UpdateErrorCli extends MbJavaComputeNode {

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
			CallableStatement csVer = null;
			ResultSet rs = null;

			String sistema = "DWH_CREDITO";

			conn = getJDBCType4Connection(sistema, JDBC_TransactionType.MB_TRANSACTION_AUTO);


			String SemaforoUP = "UPDATE STAGING_DWH.CNRGDIS_SEMAFORO SET STATO_ELABORAZIONE = 2, MESSAGGIO_ERRORE = 'Errore nel caricamento', TS_FINE = SYSDATE WHERE NOME_TABELLA = 'CNRGDIS_IN_CLIENTE' and STATO_ELABORAZIONE = 0 and TIPO_ELABORAZIONE = 'C'";
			csVer = conn.prepareCall(SemaforoUP);
			rs = csVer.executeQuery();
			rs.close();

			
			out.propagate(outAssembly);		

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
