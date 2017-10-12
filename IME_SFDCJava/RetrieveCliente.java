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


public class RetrieveCliente extends MbJavaComputeNode {

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
			String nome = null;
			String cognome = null;
			String cf = null;
			String rSociale = null;
			String pIva = null;
			String gestionale = null;
			
			MbElement tmp = null;
			if (param != null) {
				tmp = param.getFirstElementByPath("Nome");	
				if (tmp != null) {
					if (!tmp.getValueAsString().equalsIgnoreCase("null")) {
						nome = tmp.getValueAsString();
					}
				}
				tmp = null;
				tmp = param.getFirstElementByPath("Cognome");	
				if (tmp != null) {
					if (!tmp.getValueAsString().equalsIgnoreCase("null")) {
						cognome = tmp.getValueAsString();
					}
				}
				tmp = null;
				tmp = param.getFirstElementByPath("PIva");	
				if (tmp != null) {
					if (!tmp.getValueAsString().equalsIgnoreCase("null")) {
						pIva = tmp.getValueAsString();
					}
				}
				tmp = null;
				tmp = param.getFirstElementByPath("Cf");	
				if (tmp != null) {
					if (!tmp.getValueAsString().equalsIgnoreCase("null")) {
						cf = tmp.getValueAsString();
					}
				}
				tmp = null;
				tmp = param.getFirstElementByPath("RSociale");	
				if (tmp != null) {
					if (!tmp.getValueAsString().equalsIgnoreCase("null")) {
						rSociale = tmp.getValueAsString();
					}
				}
				tmp = null;
				tmp = param.getFirstElementByPath("Gestionale");	
				if (tmp != null) {
					if (!tmp.getValueAsString().equalsIgnoreCase("null")) {
						gestionale = tmp.getValueAsString();
					}
				}
			}
				
			
			//Crea array di elementi object: sotto la root crea il parser JSON e genera un primo elemento di tipo ARRAY
			MbElement jsonData = outMessage.getRootElement().createElementAsLastChild(MbJSON.PARSER_NAME).createElementAsLastChild(MbJSON.ARRAY,MbJSON.DATA_ELEMENT_NAME, null);


			if (nome!=null || cognome!=null || cf != null ||rSociale != null|| pIva != null	|| gestionale != null) {
	        conn = getJDBCType4Connection("HUB_EAI",
	                     JDBC_TransactionType.MB_TRANSACTION_AUTO);

	        cs=conn.prepareCall("{call PKG_IIB.GetCliente(?,?,?,?,?,?,?)}");
	        cs.registerOutParameter(7,-10);
	        cs.setString(1,nome);
	        cs.setString(2,cognome);
	        cs.setString(3,cf);
	        cs.setString(4,rSociale);
	        cs.setString(5,pIva);
	        cs.setString(6,gestionale);
	        
	        cs.execute();
	        
	        rs = (ResultSet)cs.getObject(7);

	        
	        while (rs.next()) {
	        MbElement jsonArrayItem = 
			   jsonData.createElementAsLastChild(MbElement.TYPE_NAME, MbJSON.ARRAY_ITEM_NAME, null);
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_COD_SOGGETTO", rs.getString(1));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_NOME", rs.getString(2));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_COGNOME", rs.getString(3));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_RAGIONE_SOCIALE", rs.getString(4));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_CF", rs.getString(5));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_INDIRIZZO_TOPONIMO", rs.getString(6));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_INDIRIZZO_VIA", rs.getString(7));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_INDIRIZZO_CIVICO", rs.getString(8));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_INDIRIZZO_TIPO_CIVICO", rs.getString(9));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_INDIRIZZO_ESTENSIONE", rs.getString(10));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_INDIRIZZO_CAP", rs.getString(11));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_INDIRIZZO_ISTAT", rs.getString(12));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_INDIRIZZO_COMUNE", rs.getString(13));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "AMMINISTRATORE_INDIRIZZO_PROVINCIA", rs.getString(14));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_NOME", rs.getString(15));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_COGNOME", rs.getString(16));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_RAGIONE_SOCIALE", rs.getString(17));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_CF", rs.getString(18));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_PIVA", rs.getString(19));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_TELEFONO", rs.getString(20));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_EMAIL", rs.getString(21));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_PEC", rs.getString(22));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_CELLULARE", rs.getString(23));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_FAX", rs.getString(24));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_CF_STRANIERO", rs.getString(25));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CATEGORIA_CLIENTE", rs.getString(26));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_DOCUMENTI_COMUNE_NASCITA", rs.getString(27));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_DOCUMENTI_DATA_NASCITA", rs.getString(28));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_TOPONIMO", rs.getString(29));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_VIA", rs.getString(30));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_CIVICO", rs.getString(31));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_TIPO_CIVICO", rs.getString(32));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_ESTENSIONE", rs.getString(33));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_CAP", rs.getString(34));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_ISTAT", rs.getString(35));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_COMUNE", rs.getString(36));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_PROVINCIA", rs.getString(37));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_SCALA", rs.getString(38));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_PIANO", rs.getString(39));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_INTERNO", rs.getString(40));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_FRAZIONE", rs.getString(41));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_PRESSO", rs.getString(42));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_NAZIONE", rs.getString(43));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CLIENTE_FINALE_INDIRIZZO_ALTRO", rs.getString(44));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_TOPONIMO", rs.getString(45));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_VIA", rs.getString(46));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_CIVICO", rs.getString(47));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_TIPO_CIVICO", rs.getString(48));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_ESTENSIONE", rs.getString(49));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_CAP", rs.getString(50));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_ISTAT", rs.getString(51));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_COMUNE", rs.getString(52));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_PROVINCIA", rs.getString(53));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_SCALA", rs.getString(54));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_PIANO", rs.getString(55));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_INTERNO", rs.getString(56));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_FRAZIONE", rs.getString(57));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_PRESSO", rs.getString(58));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_NAZIONE", rs.getString(59));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RECAPITO_INDIRIZZO_ALTRO", rs.getString(60));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "SISTEMA_FATTURAZIONE", rs.getString(61));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CONSENSO_PRIVACY", rs.getString(62));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "CONDIZIONI_PAGAMENTO", rs.getString(63));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_DATI_INTESTATARIO", rs.getString(64));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_DATI_CF_INTESTATARIO", rs.getString(65));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_DATI_NOME_FIRMATARIO", rs.getString(66));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_DATI_COGNOME_FIRMATARIO", rs.getString(67));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_DATI_CF_FIRMATARIO", rs.getString(68));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_DATI_IBAN", rs.getString(69));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_DATI_SWIFT", rs.getString(70));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_INDIRIZZO_TOPONIMO", rs.getString(71));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_INDIRIZZO_VIA", rs.getString(72));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_INDIRIZZO_CIVICO", rs.getString(73));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_INDIRIZZO_TIPO_CIVICO", rs.getString(74));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_INDIRIZZO_ESTENSIONE", rs.getString(75));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_INDIRIZZO_CAP", rs.getString(76));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_INDIRIZZO_ISTAT", rs.getString(77));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "DOMICILIAZIONE_INDIRIZZO_COMUNE", rs.getString(78));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "BOLLETTA_ONLINE", rs.getString(79));
			   jsonArrayItem.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "MAIL_NOTIFICA_BOLLETTA_ONLINE", rs.getString(80));
			   
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
