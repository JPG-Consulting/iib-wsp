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
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;

public class CNRG_PianoRate_InsertPianoRate extends MbJavaComputeNode {

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
			String sistema = "DWH_CNRG";
			String C1 = null;
			String V1 = null;
			String ColonneIns = null;
			String ValueIns = null;
			String ValueUpdate = null;
			String ValueQuery  = null;
			MbElement tmp  = null;			
			String Insert = null;
			CallableStatement csVer = null;
			ResultSet rs = null;

			
			MbElement param = inMessage.getRootElement();
			MbElement record = param.getLastChild().getFirstChild().getFirstChild(); 

			conn = getJDBCType4Connection(sistema,JDBC_TransactionType.MB_TRANSACTION_AUTO); 	
			
			while (record != null) {
				
				tmp = record.getFirstChild(); 

			C1 = tmp.getName();
			
			// Elimino dal nome della colonna il codice della tabella Ossia B2111_
			C1 = C1.substring(6);
			
			// Modifico il nome dei campi come quelli della tabella DWH
			C1 = C1.replaceAll("SIST_PROV", "SISTEMA_PROVENIENZA");
			C1 = C1.replaceAll("SOC_COMP", "SOCIETA_COMPETENZA");	
			C1 = C1.replaceAll("_FATT", "FATTURA");
			C1 = C1.replaceAll("FORN", "FORNITURA");
			C1 = C1.replaceAll("IMP_", "IMPORTO_");	
			C1 = C1.replaceAll("_SCAD", "_SCADENZA");
			C1 = C1.replaceAll("_CONT", "_CONTABILE");		
			C1 = C1.replaceAll("_PAGAM", "_PAGAMENTO");	
			C1 = C1.replaceAll("_COMP_", "_COMPETENZA_");
			
			if (C1.equals("FLG_ELAB")){
				C1 = "AUDIT_FLG_ELAB";	
			}

	
			V1 = tmp.getValueAsString();
			
			ColonneIns = C1.concat(",");
			ValueIns = V1.concat("','");
			
			ValueUpdate = C1.concat(" = '").concat(V1).concat("', ");
					
			if (C1.equals("ID_SISTEMA_PROVENIENZA_DATI") ||C1.equals("ID_SOCIETA_COMPETENZA") || C1.equals("ID_FORNITURA")|| C1.equals("ID_FATTURA") || C1.equals("ID_RATA")){
				ValueQuery = C1.concat(" = '").concat(V1).concat("' AND ");
			}

			tmp = tmp.getNextSibling();
			
			while (tmp != null) {

			C1 = tmp.getName();
			// Elimino dal nome della colonna il codice della tabella Ossia B2110_
			C1 = C1.substring(6);
			
			// Modifico il nome dei campi come quelli della tabella DWH
			C1 = C1.replaceAll("SIST_PROV", "SISTEMA_PROVENIENZA");
			C1 = C1.replaceAll("SOC_COMP", "SOCIETA_COMPETENZA");	
			C1 = C1.replaceAll("_FATT", "_FATTURA");
			C1 = C1.replaceAll("FORN", "FORNITURA");
			C1 = C1.replaceAll("IMP_", "IMPORTO_");	
			C1 = C1.replaceAll("_SCAD", "_SCADENZA");
			C1 = C1.replaceAll("_CONT", "_CONTABILE");		
			C1 = C1.replaceAll("_PAGAM", "_PAGAMENTO");	
			C1 = C1.replaceAll("_COMP_", "_COMPETENZA_");
			C1 = C1.replaceAll("NUM_", "NUMERO_");	
			
			if (C1.equals("FLG_ELAB")){
				C1 = "AUDIT_FLG_ELAB";	
			}



			if (!C1.equals("DATA_INSERIMENTO")){
						
			V1 = tmp.getValueAsString();
			
			V1 = V1.replace("'", "''");
			V1 = V1.replace("&", "'||'&'||'");

			
			// Serve per eseguire il caricamento. Da avvisare sia DWH che CNRG
			
			if (C1.equals("SETTORE_UNBUNDLING")) {
			V1 = V1.substring(0, 19);
			}

			
			
			ColonneIns = ColonneIns.concat(C1).concat(",");
			
			if (C1.startsWith("FLG") || C1.startsWith("INTERESSI")|| C1.startsWith("IMPORTO")) {
				
				if (V1.endsWith("-")){
					V1 = V1.substring(0, V1.length()-1);
					V1 = "-".concat(V1);
				}	
					
				ValueIns = ValueIns.substring(0, ValueIns.length()-1);
				ValueIns = ValueIns.concat(V1).concat(",'");
				ValueUpdate = ValueUpdate.concat(C1).concat(" = ").concat(V1).concat(",");
			} else if (C1.equals("ID_SISTEMA_PROVENIENZA_DATI") ||C1.equals("ID_SOCIETA_COMPETENZA") || C1.equals("ID_FORNITURA")|| C1.equals("ID_FATTURA") || C1.equals("ID_RATA") ){
				ValueIns = ValueIns.concat(V1).concat("','");
				ValueQuery = ValueQuery.concat(C1).concat(" = '").concat(V1).concat("'AND ");				
			}	 else if (C1.startsWith("DATA") ){
				V1 = V1.substring(0, 10);
				ValueIns = ValueIns.substring(0, ValueIns.length()-1);
				ValueIns = ValueIns.concat("TO_DATE('").concat(V1).concat("','yyyy-MM-dd') ,'");
				ValueUpdate = ValueUpdate.concat(C1).concat(" = TO_DATE('").concat(V1).concat("','yyyy-MM-dd') ,");
			}else {
				ValueIns = ValueIns.concat(V1).concat("','");
				ValueUpdate = ValueUpdate.concat(C1).concat(" = '").concat(V1).concat("',");				
			}
			
			}
			
			tmp = tmp.getNextSibling();
			}
			
			// Per effettuaree l'insetimento......
			
			ColonneIns = ColonneIns.concat("TIPO_RATA").concat(","); //Da Dire a DWH
			ValueIns = ValueIns.concat("TIPO_RATA").concat("','");

			
			ColonneIns = ColonneIns.substring(0, ColonneIns.length()-1);
			ValueIns = ValueIns.substring(0, ValueIns.length()-2);
			ValueUpdate = ValueUpdate.substring(0, ValueUpdate.length()-1);
			ValueQuery = ValueQuery.substring(0, ValueQuery.length()-4);
			

			try {
				
				Insert = "INSERT INTO STAGING_DWH.CNRGDIS_IN_PIANO_RATE ("+ColonneIns+") VALUES ('" + ValueIns +")";
	
			csVer = conn.prepareCall(Insert);
			rs = csVer.executeQuery();
			rs.next();
			rs.close();
			csVer.close();
			
			}  catch(Exception e) {
				
			csVer.close();
			
			Insert = "UPDATE STAGING_DWH.CNRGDIS_IN_PIANO_RATE  SET "+ValueUpdate+" WHERE " + ValueQuery +"";
			
			csVer = conn.prepareCall(Insert);
			rs = csVer.executeQuery();
			rs.next();
			rs.close();
			csVer.close();

			}
			
			record = record.getNextSibling();
			}


			String SemaforoUP = "UPDATE STAGING_DWH.CNRGDIS_SEMAFORO SET STATO_ELABORAZIONE = 1 WHERE NOME_TABELLA = 'CNRGDIS_IN_PIANO_RATE' and STATO_ELABORAZIONE = 0";
			csVer = conn.prepareCall(SemaforoUP);
			rs = csVer.executeQuery();
			rs.close();
			csVer.close();
	
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
