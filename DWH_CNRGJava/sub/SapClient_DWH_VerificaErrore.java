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

public class SapClient_DWH_VerificaErrore extends MbJavaComputeNode {

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
			MbElement tmp ;

			String sistema = "DWH_SAP500";
			String VerifError = "Select count(*) from STAGING_DWH.SAPSD5_SEMAFORO where STATO_ELABORAZIONE != 1 and NOME_TABELLA = 'SAPSD5_IN_CLIENTE'";
			String SemaforoIns = "INSERT INTO STAGING_DWH.SAPSD5_SEMAFORO (NOME_TABELLA , TIPO_ELABORAZIONE , TS_INIZIO, STATO_ELABORAZIONE ) VALUES ('SAPSD5_IN_CLIENTE','W', to_date(sysdate, 'dd/mm/yy'),'0')";
			String ColonneIns = null;
			String ValueIns = null;
			String C1 = null;
			String V1 = null;
			String Insert = null;
			String CountClient = null;
			String ValueQuery = null;
			String ValueUpdate = null;
			String result = null;

			
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
						
				
				MbElement param = inMessage.getRootElement();	
				MbElement record = param.getLastChild().getLastChild().getLastChild().getLastChild().getFirstChild();

				while (record != null) {
				
				tmp = record.getFirstChild(); //getFirstElementByPath("/SapZidwhcliente/SapZcocskszIDocBO/SapZcocskszDataRecord/SapZcocskszZcoscsksz000/Record");

				C1 = tmp.getName();
				V1 = tmp.getValueAsString();
				
				ColonneIns = C1.concat(",");
				ValueIns = V1.concat("','");
				
				ValueUpdate = C1.concat(" = '").concat(V1).concat("', ");
				
				if (C1.equals("ID_SISTEMA_PROVENIENZA_DATI") ||C1.equals("ID_SOCIETA_COMPETENZA") || C1.equals("ID_CLIENTE") ){
					ValueQuery = C1.concat(" = '").concat(V1).concat("' AND ");
				}

				
				tmp = tmp.getNextSibling();

				
				while (tmp != null) {
				

				C1 = tmp.getName();
				if (C1.equals("CLASSIFICAZIONE_CLIE")){
					C1 = "CLASSIFICAZIONE_CLIENTE";	
				}
							
				V1 = tmp.getValueAsString();
				
				ColonneIns = ColonneIns.concat(C1).concat(",");
				
				if (C1.equals("FLG_PA") ||C1.equals("FLG_CONDOMINIO") || C1.equals("IMPORTO_GARANZIE") ){
					ValueIns = ValueIns.substring(0, ValueIns.length()-1);
					ValueIns = ValueIns.concat(V1).concat(",'");
					ValueUpdate = ValueUpdate.concat(C1).concat(" = ").concat(V1).concat(",");
				} else if (C1.equals("ID_SISTEMA_PROVENIENZA_DATI") ||C1.equals("ID_SOCIETA_COMPETENZA") || C1.equals("ID_CLIENTE") ){
					ValueIns = ValueIns.concat(V1).concat("','");
					ValueQuery = ValueQuery.concat(C1).concat(" = '").concat(V1).concat("'AND ");				
				} else {
					ValueIns = ValueIns.concat(V1).concat("','");
					ValueUpdate = ValueUpdate.concat(C1).concat(" = '").concat(V1).concat("',");				
				}

				tmp = tmp.getNextSibling();
				}
				
				ColonneIns = ColonneIns.substring(0, ColonneIns.length()-1);
				ValueIns = ValueIns.substring(0, ValueIns.length()-3);
				ValueUpdate = ValueUpdate.substring(0, ValueUpdate.length()-1);
				ValueQuery = ValueQuery.substring(0, ValueQuery.length()-4);
				
				CountClient = "Select count (*) from STAGING_DWH.SAPSD5_IN_CLIENTE Where "+ ValueQuery ;
				csVer = conn.prepareCall(CountClient);
				rs = csVer.executeQuery();
				rs.next();
				numberRow = rs.getInt("count(*)");
				rs.close();

				if  (numberRow == 0){
					Insert = "INSERT INTO STAGING_DWH.SAPSD5_IN_CLIENTE ("+ColonneIns+") VALUES ('" + ValueIns +"')";
				} else{
					Insert = "UPDATE STAGING_DWH.SAPSD5_IN_CLIENTE  SET "+ValueUpdate+" WHERE " + ValueQuery +"";
		
				}
					
	
				Insert = "INSERT INTO STAGING_DWH.SAPSD5_IN_CLIENTE ("+ColonneIns+") VALUES ('" + ValueIns +"')";

				csVer = conn.prepareCall(Insert);
				rs = csVer.executeQuery();
				rs.next();
				rs.close();
				
				record = record.getNextSibling();
				}


				String SemaforoUP = "UPDATE STAGING_DWH.SAPSD5_SEMAFORO SET STATO_ELABORAZIONE = 1 WHERE NOME_TABELLA = 'SAPSD5_IN_CLIENTE' and STATO_ELABORAZIONE = 0";
				csVer = conn.prepareCall(SemaforoUP);
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


	}

}
