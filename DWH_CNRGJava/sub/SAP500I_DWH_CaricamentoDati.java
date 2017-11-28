package sub;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;
import com.sun.jmx.snmp.Timestamp;

public class SAP500I_DWH_CaricamentoDati extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");
		
		Connection conn = null;
		CallableStatement csVer = null;
		ResultSet rs = null;
		String sistema = "DWH_CREDITO";



		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			MbElement tmp ;
		
			
			String VerifError = "SELECT * FROM (Select STATO_ELABORAZIONE from STAGING_DWH.SAPSD5_SEMAFORO where NOME_TABELLA = 'SAPSD5_IN_INCASSO' and TIPO_ELABORAZIONE = 'R' order by TS_INIZIO desc ) WHERE ROWNUM = 1";
			String SemaforoIns = "INSERT INTO STAGING_DWH.SAPSD5_SEMAFORO (NOME_TABELLA , TIPO_ELABORAZIONE , TS_INIZIO, STATO_ELABORAZIONE ) VALUES ('SAPSD5_IN_INCASSO','C', sysdate,'0')";
			String ColonneIns = null;
			String ValueIns = null;
			String C1 = null;
			String V1 = null;
			String Insert = null;
			String CountClient = null;
			String ValueQuery = null;
			String ValueUpdate = null;
			String result = null;
			String Error = null;
			String numberRow= "";

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
			
			if (!numberRow.equals("1") ) {
				alt.propagate(outAssembly);		
			} else {
				csVer = conn.prepareCall(SemaforoIns);
				rs = csVer.executeQuery();
				rs.close();
				csVer.close();

						
				
				MbElement param = inMessage.getRootElement();	
				MbElement record = param.getLastChild().getLastChild().getLastChild().getLastChild().getFirstChild();

				while (record != null) {
				
				tmp = record.getFirstChild(); //getFirstElementByPath("/SapZidwhcliente/SapZcocskszIDocBO/SapZcocskszDataRecord/SapZcocskszZcoscsksz000/Record");

				C1 = tmp.getName();
				V1 = tmp.getValueAsString();
				
				ColonneIns = C1.concat(",");
				ValueIns = V1.concat("','");
				
				ValueUpdate = C1.concat(" = '").concat(V1).concat("', ");
				
				if (C1.equals("ID_SISTEMA_PROVENIENZA_DATI") ||C1.equals("ID_SOCIETA_COMPETENZA") || C1.equals("ID_FATTURA") || C1.equals("ID_INCASSO") || C1.equals("ID_RATA")){
					ValueQuery = C1.concat(" = '").concat(V1).concat("' AND ");
				}

				
				tmp = tmp.getNextSibling();

				
				while (tmp != null) {
				

				C1 = tmp.getName();
				V1 = tmp.getValueAsString();
				
				ColonneIns = ColonneIns.concat(C1).concat(",");
				
				if (C1.equals("ID_RATA") && V1.length() == 0 ){
					V1 = "-1";
				}
				
				
				if (C1.startsWith("IMPORTO") ){
					ValueIns = ValueIns.substring(0, ValueIns.length()-1);
					
					if (V1.endsWith("-")){
						V1 = V1.substring(0, V1.length()-1);
						V1 = "-".concat(V1);
					}
					
					ValueIns = ValueIns.concat(V1).concat(",'");
					ValueUpdate = ValueUpdate.concat(C1).concat(" = ").concat(V1).concat(",");
				} else if (C1.startsWith("DATA") ){
						ValueIns = ValueIns.substring(0, ValueIns.length()-1);
						ValueIns = ValueIns.concat("TO_DATE('").concat(V1).concat("','mmddyyyy') ,'");
						ValueUpdate = ValueUpdate.concat(C1).concat(" = TO_DATE('").concat(V1).concat("','mmddyyyy') ,");
				} else if (C1.equals("ID_SISTEMA_PROVENIENZA_DATI") ||C1.equals("ID_SOCIETA_COMPETENZA") || C1.equals("ID_FATTURA") || C1.equals("ID_INCASSO") || C1.equals("ID_RATA") ){
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
				
				try{
					Insert = "INSERT INTO STAGING_DWH.SAPSD5_IN_INCASSO ("+ColonneIns+") VALUES ('" + ValueIns +"')";
					csVer = conn.prepareCall(Insert);
					rs = csVer.executeQuery();
					rs.next();
					rs.close();
					csVer.close();

				}  catch(Exception e) {
					
					rs.close();
					csVer.close();
					
					Insert = "UPDATE STAGING_DWH.SAPSD5_IN_INCASSO  SET "+ValueUpdate+" WHERE " + ValueQuery +"";

					csVer = conn.prepareCall(Insert);
					rs = csVer.executeQuery();
					rs.next();
					rs.close();
					csVer.close();

				}

				
				record = record.getNextSibling();
				}


				String SemaforoUP = "UPDATE STAGING_DWH.SAPSD5_SEMAFORO SET STATO_ELABORAZIONE = 1, TS_FINE = SYSDATE WHERE NOME_TABELLA = 'SAPSD5_IN_INCASSO' and STATO_ELABORAZIONE = 0 and TIPO_ELABORAZIONE = 'C'";
				csVer = conn.prepareCall(SemaforoUP);
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
