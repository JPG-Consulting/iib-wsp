package sub;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;

public class UpdateK extends MbJavaComputeNode {

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
			MbElement lancio = inMessage.getRootElement().getFirstElementByPath("/XMLNSC/lancio");
			String dbOrigine = null;
			MbElement identifier = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/sistema");
			MbElement queryStringDB = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/societa");
			if(identifier != null) {
				dbOrigine = identifier.getValueAsString();
			} else if (queryStringDB != null) {
				dbOrigine = queryStringDB.getValueAsString();
			}
			
			String elaborato  = "K";
			String codElab = "999";
			String descrElab = "ERRORE";
			String idEsterno = null;
			if (lancio.getFirstElementByPath("id_esterno") != null) {
				idEsterno = lancio.getFirstElementByPath("id_esterno").getValueAsString();
			}
			String idLancio = lancio.getFirstElementByPath("id_lancio").getValueAsString();
			String societa = lancio.getFirstElementByPath("soc_sap").getValueAsString();
			String tipoFlusso = lancio.getFirstElementByPath("tipo_flusso").getValueAsString();
			String sistema = dbOrigine;
			
			Connection conn = null;
			PreparedStatement s = null;
			try {
				
			
				conn = getJDBCType4Connection(sistema, JDBC_TransactionType.MB_TRANSACTION_AUTO);
				
				s = conn.prepareStatement("UPDATE EXT_SAP_FLUSSO set ELABORATO = ?,COD_ELAB = ?, " +
						"DESCR_ELAB = ? , DATA_ELAB = sysdate , ID_ESTERNO = ?  where ID_LANCIO = ? and TIPO_FLUSSO = ? and SOC_SAP = ?");
				s.setString(1, elaborato);
				s.setString(2, codElab);
				s.setString(3, descrElab);
				s.setString(4, idEsterno);
				s.setString(5, idLancio);
				s.setString(6, tipoFlusso);
				s.setString(7, societa);

				Integer result = s.executeUpdate();
				if (result == 0) throw new Exception();
			} catch (Exception e) {
				throw new MbUserException(this, idLancio, tipoFlusso, sistema, societa, null);
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
