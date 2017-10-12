import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;


public class RetrieveImpianto extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		ResultSet rs = null;
		CallableStatement cs = null;
		Connection conn = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			MbElement param = inMessage.getRootElement().getFirstElementByPath("/HTTPInputHeader");
			String pod = null;

			if (param != null) {
				MbElement podParam = param.getFirstElementByPath("Pod");	
				if (podParam != null) {
					if (!podParam.getValueAsString().equalsIgnoreCase("null")) {
						pod = podParam.getValueAsString();
					}
				}		
			}
			
			//Crea object: sotto la root crea il parser JSON e genera un primo elemento di tipo OBJECT
			MbElement jsonData = outMessage.getRootElement().createElementAsLastChild(MbJSON.PARSER_NAME).createElementAsLastChild(MbJSON.OBJECT,MbJSON.DATA_ELEMENT_NAME, null);
			if (pod != null) {


	        conn = getJDBCType4Connection("HUB_EAI",
	                     JDBC_TransactionType.MB_TRANSACTION_AUTO);

	        cs=conn.prepareCall("{call PKG_IIB.GetFornituraEE(?,?)}");
	        cs.registerOutParameter(2,-10);
	        cs.setString(1,pod);
	        
	        cs.execute();
	        
	        rs = (ResultSet)cs.getObject(2);
	        
	        
	        while (rs.next()) {
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "POD", rs.getString(1));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "TIPO_CONTATORE", rs.getString(2));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "TRATTAMENTO", rs.getString(3));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CONSUMO_ANNUO_COMPLESSIVO", rs.getString(4));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CONSUMO_ANNUO_F1", rs.getString(5));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CONSUMO_ANNUO_F2", rs.getString(6));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CONSUMO_ANNUO_F3", rs.getString(7));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DISTRIBUTORE", rs.getString(8));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DISALIMENTABILITA", rs.getString(9));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "MERCATO_PROVENIENZA", rs.getString(10));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "MERCATO_RIENTRO", rs.getString(11));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "POTENZA_CONTRATTUALE", rs.getString(12));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "POTENZA_DISPONIBILE", rs.getString(13));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "TENSIONE_FASE", rs.getString(14));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "LIVELLO_TENSIONE", rs.getString(15));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "TEMPORANEA", rs.getString(16));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CONTRATTO_DISPACCIAMENTO", rs.getString(17));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "TIPO_FORNITURA", rs.getString(18));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_TOPONIMO", rs.getString(19));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_VIA", rs.getString(20));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_CIVICO", rs.getString(21));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_TIPO_CIVICO", rs.getString(22));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_ESTENSIONE", rs.getString(23));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_CAP", rs.getString(24));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_ISTAT", rs.getString(25));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_COMUNE", rs.getString(26));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_PROVINCIA", rs.getString(27));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_SCALA", rs.getString(28));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_PIANO", rs.getString(29));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_INTERNO", rs.getString(30));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_FRAZIONE", rs.getString(31));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_PRESSO", rs.getString(32));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_NAZIONE", rs.getString(33));
					jsonData.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "INDIRIZZO_ALTRO", rs.getString(34));
   
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
