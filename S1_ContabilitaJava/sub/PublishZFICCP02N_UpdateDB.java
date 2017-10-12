package sub;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;


public class PublishZFICCP02N_UpdateDB extends MbJavaComputeNode {

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
			MbElement lancio = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Variables/lancio");
			String elaborato  = "E";
			String codElab = "000";
			String descrElab = "INVIATO IDOC SAP";
			String idEsterno = null;
			String idLancio = lancio.getFirstElementByPath("id_lancio").getValueAsString();
			String societa = lancio.getFirstElementByPath("soc_sap").getValueAsString();
			String tipoFlusso = lancio.getFirstElementByPath("tipo_flusso").getValueAsString();
			String sistema = null;
			
			sistema = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Variables/dbOrigine").getValueAsString();
			Connection conn = null;
			PreparedStatement s = null;
			try {
				
			
				conn = getJDBCType4Connection(sistema, JDBC_TransactionType.MB_TRANSACTION_AUTO);
				if (sistema.equals("I1")) {
					s = conn.prepareStatement("UPDATE GLCAE.EXT_SAP_FLUSSO set ELABORATO = ?,COD_ELAB = ?, " +
							"DESCR_ELAB = ? , DATA_ELAB = sysdate , ID_ESTERNO = ?  where ID_LANCIO = ? and TIPO_FLUSSO = ?");
				} else {
					s = conn.prepareStatement("UPDATE EXT_SAP_FLUSSO set ELABORATO = ?,COD_ELAB = ?, " +
							"DESCR_ELAB = ? , DATA_ELAB = sysdate , ID_ESTERNO = ?  where ID_LANCIO = ? and TIPO_FLUSSO = ?");
				}
				s.setString(1, elaborato);
				s.setString(2, codElab);
				s.setString(3, descrElab);
				s.setString(4, idEsterno);
				s.setString(5, idLancio);
				s.setString(6, tipoFlusso);
				//s.setString(7, societa);

				Integer result = s.executeUpdate();
				if (result == 0) throw new Exception();
				} catch (Exception e) {
					throw new MbUserException(e, idLancio, tipoFlusso, sistema, societa, null );
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
