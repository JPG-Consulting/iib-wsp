package sub;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

public class Sap500F_DWH_InserFattDWH extends MbJavaComputeNode {

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
			Connection connIntEng = null;
			
			CallableStatement csVer = null;
			ResultSet rs = null;
		
			String sistema = "DWH_CREDITO";
			String VerifError = "SELECT * FROM (Select STATO_ELABORAZIONE from STAGING_DWH.SAPSD5_SEMAFORO where NOME_TABELLA = 'SAPSD5_IN_FATTURA' and TIPO_ELABORAZIONE = 'R' order by TS_INIZIO desc ) WHERE ROWNUM = 1";
			String SemaforoIns = "INSERT INTO STAGING_DWH.SAPSD5_SEMAFORO (NOME_TABELLA , TIPO_ELABORAZIONE , TS_INIZIO, STATO_ELABORAZIONE ) VALUES ('SAPSD5_IN_FATTURA','C', sysdate,'0')";
			String INIZIOElab = "INSERT INTO IIB_DWH_CREDITO_LOG (FLUSSO,DATA_INIZIO) VALUES ('SAP500',SYSDATE)";

			String numberRow= "";
			
			int NInsert = 0;
			int NUpdate = 0;
			int NError = 0;
			int NElab = 0;
			
			conn = getJDBCType4Connection(sistema, JDBC_TransactionType.MB_TRANSACTION_AUTO);
			connIntEng = getJDBCType4Connection("INT_ENGINE", JDBC_TransactionType.MB_TRANSACTION_AUTO);
			
			
			
			
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
				csVer.execute();
				csVer.close();						
				
				MbElement record = inMessage.getRootElement().getFirstElementByPath("/DataObject/SapZidwhfattura/SapZidwhfatturaIDocBO/SapZidwhfatturaDataRecord/SapZidwhfatturaZsdwhfattura000");
				
				String preparedInsert = "Insert into STAGING_DWH.SAPSD5_IN_FATTURA                                                           "+
						"(ID_SISTEMA_PROVENIENZA_DATI, ID_SOCIETA_COMPETENZA, ID_FATTURA, TIPO_COMMODITY,                    "+
						"ID_CLIENTE, NUM_FATTURA, STATO_DUNNING, TIPO_FATTURA, INTERESSI_MORA, INTERESSI_MORA_PARZIALI,      "+
						"CONDIZIONE_FATTURA, IMPORTO_FATTURA, IMPORTO_IVA, IMPORTO_FATTURA_CONTABILE, IMPORTO_IVA_CONTABILE, "+
						"IMPORTO_FATTURA_PARZIALE, IMPORTO_IVA_PARZIALE, IMPORTO_FATT_CONT_PARZIALE,                         "+
						"DATA_EMISSIONE, DATA_SCADENZA_IN, DATA_SCADENZA_FIN, CONTO_COGE, SETTORE_UNBUNDLING,                "+
						"FLG_CEDUTA, CODE_LINE, AUDIT_FLG_ELAB)                                                              "+
						"Values                                                                                              "+
						"(?, ?, ?, ?,                                                                                        "+
						"?, ?, ?, ?, ?, ?,                                                                                   "+
						"?, ?, ?, ?, ?,                                                                                      "+
						"?, ?, ?,                                                                                            "+
						"TO_DATE(?, 'mmddyyyy'), TO_DATE(?, 'mmddyyyy'), TO_DATE(?, 'mmddyyyy'), ?, ?,                       "+
						"?, ?, ?)                                                                                            ";
				String preparedUpdate = "UPDATE STAGING_DWH.SAPSD5_IN_FATTURA" +  
						" SET                                " +
						"ID_CLIENTE                  = ?,    " +
						"NUM_FATTURA                 = ?,    " +
						"STATO_DUNNING               = ?,    " +
						"TIPO_FATTURA                = ?,    " +
						"INTERESSI_MORA              = ?,    " +
						"INTERESSI_MORA_PARZIALI     = ?,    " +
						"CONDIZIONE_FATTURA          = ?,    " +
						"IMPORTO_FATTURA             = ?,    " +
						"IMPORTO_IVA                 = ?,    " +
						"IMPORTO_FATTURA_CONTABILE   = ?,    " +
						"IMPORTO_IVA_CONTABILE       = ?,    " +
						"IMPORTO_FATTURA_PARZIALE    = ?,    " +
						"IMPORTO_IVA_PARZIALE        = ?,    " +
						"IMPORTO_FATT_CONT_PARZIALE  = ?,    " +
						"DATA_EMISSIONE      = TO_DATE(?,'mmddyyyy'),    " +
						"DATA_SCADENZA_IN    = TO_DATE(?,'mmddyyyy'),    " +
						"DATA_SCADENZA_FIN   = TO_DATE(?,'mmddyyyy'),    " +
						"CONTO_COGE                  = ?,    " +
						"SETTORE_UNBUNDLING          = ?,    " +
						"FLG_CEDUTA                  = ?,    " +
						"CODE_LINE                   = ?,    " +
						"AUDIT_FLG_ELAB              = ?     " +
						"WHERE                               " +
						"ID_SISTEMA_PROVENIENZA_DATI = ? AND " +
						"ID_SOCIETA_COMPETENZA       = ? AND " +
						"ID_FATTURA                  = ? AND " +
						"TIPO_COMMODITY              = ?     ";

				String updateIntEng = "INSERT INTO IIB_DWH_CREDITO_SAP500  (Data_inserimento,ID_FATTURA,STATO) VALUES (SYSDATE,?,'E')";

				PreparedStatement insert = conn.prepareStatement(preparedInsert);
				PreparedStatement update = conn.prepareStatement(preparedUpdate);
				PreparedStatement updIntEng = connIntEng.prepareStatement(updateIntEng);
				
				while (record != null) {
					insert.clearParameters();
					update.clearParameters();
					
					insert.setString(1,(record.getFirstElementByPath("ID_SISTEMA_PROVENIENZA_DATI")                   != null) ? record.getFirstElementByPath("ID_SISTEMA_PROVENIENZA_DATI").getValueAsString(): "");
					insert.setString(2,(record.getFirstElementByPath("ID_SOCIETA_COMPETENZA")                         != null) ? record.getFirstElementByPath("ID_SOCIETA_COMPETENZA")     .getValueAsString(): "");
					insert.setString(3,(record.getFirstElementByPath("ID_FATTURA")                                    != null) ? record.getFirstElementByPath("ID_FATTURA")                .getValueAsString(): "");
					insert.setString(4,(record.getFirstElementByPath("TIPO_COMMODITY")                                != null) ? record.getFirstElementByPath("TIPO_COMMODITY")            .getValueAsString(): "");
					insert.setString(5,(record.getFirstElementByPath("ID_CLIENTE")                                    != null) ? record.getFirstElementByPath("ID_CLIENTE")                .getValueAsString(): "");
					insert.setString(6,(record.getFirstElementByPath("NUM_FATTURA")                                   != null) ? record.getFirstElementByPath("NUM_FATTURA")               .getValueAsString(): "");
					insert.setString(7,(record.getFirstElementByPath("STATO_DUNNING")                                 != null) ? record.getFirstElementByPath("STATO_DUNNING")             .getValueAsString(): "");
					insert.setString(8,(record.getFirstElementByPath("TIPO_FATTURA")                                  != null) ? record.getFirstElementByPath("TIPO_FATTURA")              .getValueAsString(): "");
					insert.setDouble(9,Double.parseDouble((record.getFirstElementByPath("INTERESSI_MORA")             != null) ? (record.getFirstElementByPath("INTERESSI_MORA")            .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("INTERESSI_MORA").getValueAsString()            .replace("-", "") : (!record.getFirstElementByPath("INTERESSI_MORA").getValueAsString().equals("")) ? record.getFirstElementByPath("INTERESSI_MORA").getValueAsString() : "0.0" : "0.0"));
					insert.setDouble(10,Double.parseDouble((record.getFirstElementByPath("INTERESSI_MORA_PARZIALI")    != null) ? (record.getFirstElementByPath("INTERESSI_MORA_PARZIALI")   .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("INTERESSI_MORA_PARZIALI").getValueAsString()   .replace("-", "") : (!record.getFirstElementByPath("INTERESSI_MORA_PARZIALI").getValueAsString().equals(""))? record.getFirstElementByPath("INTERESSI_MORA_PARZIALI").getValueAsString() : "0.0" : "0.0"));
					insert.setString(11,(record.getFirstElementByPath("CONDIZIONE_FATTURA")                            != null) ? record.getFirstElementByPath("CONDIZIONE_FATTURA")        .getValueAsString(): "");                                                                                              
					insert.setDouble(12,Double.parseDouble((record.getFirstElementByPath("IMPORTO_FATTURA")           != null) ? (record.getFirstElementByPath("IMPORTO_FATTURA")           .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_FATTURA").getValueAsString()           .replace("-", "") : (!record.getFirstElementByPath("IMPORTO_FATTURA").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_FATTURA").getValueAsString():"0.0" : "0.0"));
					insert.setDouble(13,Double.parseDouble((record.getFirstElementByPath("IMPORTO_IVA")               != null) ? (record.getFirstElementByPath("IMPORTO_IVA")               .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_IVA").getValueAsString()               .replace("-", "") : (!record.getFirstElementByPath("IMPORTO_IVA").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_IVA").getValueAsString():"0.0" : "0.0"));
					insert.setDouble(14,Double.parseDouble((record.getFirstElementByPath("IMPORTO_FATTURA_CONTABILE") != null) ? (record.getFirstElementByPath("IMPORTO_FATTURA_CONTABILE") .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_FATTURA_CONTABILE").getValueAsString() .replace("-", "") : (!record.getFirstElementByPath("IMPORTO_FATTURA_CONTABILE").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_FATTURA_CONTABILE").getValueAsString():"0.0" : "0.0"));
					insert.setDouble(15,Double.parseDouble((record.getFirstElementByPath("IMPORTO_IVA_CONTABILE")     != null) ? (record.getFirstElementByPath("IMPORTO_IVA_CONTABILE")     .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_IVA_CONTABILE").getValueAsString()     .replace("-", "") : (!record.getFirstElementByPath("IMPORTO_IVA_CONTABILE").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_IVA_CONTABILE").getValueAsString():"0.0" : "0.0"));
					insert.setDouble(16,Double.parseDouble((record.getFirstElementByPath("IMPORTO_FATTURA_PARZIALE")  != null) ? (record.getFirstElementByPath("IMPORTO_FATTURA_PARZIALE")  .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_FATTURA_PARZIALE").getValueAsString()  .replace("-", "") : (!record.getFirstElementByPath("IMPORTO_FATTURA_PARZIALE").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_FATTURA_PARZIALE").getValueAsString():"0.0" : "0.0"));
					insert.setDouble(17,Double.parseDouble((record.getFirstElementByPath("IMPORTO_IVA_PARZIALE")      != null) ? (record.getFirstElementByPath("IMPORTO_IVA_PARZIALE")      .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_IVA_PARZIALE").getValueAsString()      .replace("-", "") : (!record.getFirstElementByPath("IMPORTO_IVA_PARZIALE").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_IVA_PARZIALE").getValueAsString():"0.0" : "0.0"));
					insert.setDouble(18,Double.parseDouble((record.getFirstElementByPath("IMPORTO_FATT_CONT_PARZIALE")!= null) ? (record.getFirstElementByPath("IMPORTO_FATT_CONT_PARZIALE").getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_FATT_CONT_PARZIALE").getValueAsString().replace("-", "") : (!record.getFirstElementByPath("IMPORTO_FATT_CONT_PARZIALE").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_FATT_CONT_PARZIALE").getValueAsString():"0.0" : "0.0")); 
					insert.setString(19,(record.getFirstElementByPath("DATA_EMISSIONE")                               != null) ? record.getFirstElementByPath("DATA_EMISSIONE")            .getValueAsString(): "");  
					insert.setString(20,(record.getFirstElementByPath("DATA_SCADENZA_IN")                             != null) ? record.getFirstElementByPath("DATA_SCADENZA_IN")          .getValueAsString(): "");    
					insert.setString(21,(record.getFirstElementByPath("DATA_SCADENZA_FIN")                            != null) ? record.getFirstElementByPath("DATA_SCADENZA_FIN")         .getValueAsString(): "");
					insert.setString(22,(record.getFirstElementByPath("CONTO_COGE")                                   != null) ? record.getFirstElementByPath("CONTO_COGE")                .getValueAsString(): "");
					insert.setString(23,(record.getFirstElementByPath("SETTORE_UNBUNDLING")                           != null) ? record.getFirstElementByPath("SETTORE_UNBUNDLING")        .getValueAsString(): "");
					insert.setInt(24,Integer.parseInt((record.getFirstElementByPath("FLG_CEDUTA")                     != null) ? (!record.getFirstElementByPath("FLG_CEDUTA")                .getValueAsString().equals(""))? record.getFirstElementByPath("FLG_CEDUTA").getValueAsString() : "0": "0"));
					insert.setString(25,(record.getFirstElementByPath("CODE_LINE")                                    != null) ? record.getFirstElementByPath("CODE_LINE")                 .getValueAsString(): "");
					insert.setInt(26,Integer.parseInt((record.getFirstElementByPath("AUDIT_FLG_ELAB")                 != null) ? (!record.getFirstElementByPath("AUDIT_FLG_ELAB")            .getValueAsString().equals(""))? record.getFirstElementByPath("AUDIT_FLG_ELAB").getValueAsString() :"0" : "0"));
					
					try{
						
						insert.execute();
						
						NInsert = NInsert + 1;
						
					} catch(SQLException e) {
	
						try {
							update.setString(23,(record.getFirstElementByPath("ID_SISTEMA_PROVENIENZA_DATI")                         != null) ? record.getFirstElementByPath("ID_SISTEMA_PROVENIENZA_DATI")     .getValueAsString(): "");
							update.setString(24,(record.getFirstElementByPath("ID_SOCIETA_COMPETENZA")                         != null) ? record.getFirstElementByPath("ID_SOCIETA_COMPETENZA")     .getValueAsString(): "");
							update.setString(25,(record.getFirstElementByPath("ID_FATTURA")                                    != null) ? record.getFirstElementByPath("ID_FATTURA")                .getValueAsString(): "");
							update.setString(26,(record.getFirstElementByPath("TIPO_COMMODITY")                                != null) ? record.getFirstElementByPath("TIPO_COMMODITY")            .getValueAsString(): "");
							update.setString(1,(record.getFirstElementByPath("ID_CLIENTE")                                    != null) ? record.getFirstElementByPath("ID_CLIENTE")                .getValueAsString(): "");
							update.setString(2,(record.getFirstElementByPath("NUM_FATTURA")                                   != null) ? record.getFirstElementByPath("NUM_FATTURA")               .getValueAsString(): "");
							update.setString(3,(record.getFirstElementByPath("STATO_DUNNING")                                 != null) ? record.getFirstElementByPath("STATO_DUNNING")             .getValueAsString(): "");
							update.setString(4,(record.getFirstElementByPath("TIPO_FATTURA")                                  != null) ? record.getFirstElementByPath("TIPO_FATTURA")              .getValueAsString(): "");
							update.setDouble(5,Double.parseDouble((record.getFirstElementByPath("INTERESSI_MORA")             != null) ? (record.getFirstElementByPath("INTERESSI_MORA")            .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("INTERESSI_MORA").getValueAsString()            .replace("-", "") : (!record.getFirstElementByPath("INTERESSI_MORA").getValueAsString().equals("")) ? record.getFirstElementByPath("INTERESSI_MORA").getValueAsString() : "0.0" : "0.0"));
							update.setDouble(6,Double.parseDouble((record.getFirstElementByPath("INTERESSI_MORA_PARZIALI")    != null) ? (record.getFirstElementByPath("INTERESSI_MORA_PARZIALI")   .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("INTERESSI_MORA_PARZIALI").getValueAsString()   .replace("-", "") : (!record.getFirstElementByPath("INTERESSI_MORA_PARZIALI").getValueAsString().equals(""))? record.getFirstElementByPath("INTERESSI_MORA_PARZIALI").getValueAsString() : "0.0" : "0.0"));
							update.setString(7,(record.getFirstElementByPath("CONDIZIONE_FATTURA")                            != null) ? record.getFirstElementByPath("CONDIZIONE_FATTURA")        .getValueAsString(): "");                                                                                              
							update.setDouble(8,Double.parseDouble((record.getFirstElementByPath("IMPORTO_FATTURA")           != null) ? (record.getFirstElementByPath("IMPORTO_FATTURA")           .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_FATTURA").getValueAsString()           .replace("-", "") : (!record.getFirstElementByPath("IMPORTO_FATTURA").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_FATTURA").getValueAsString():"0.0" : "0.0"));
							update.setDouble(9,Double.parseDouble((record.getFirstElementByPath("IMPORTO_IVA")               != null) ? (record.getFirstElementByPath("IMPORTO_IVA")               .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_IVA").getValueAsString()               .replace("-", "") : (!record.getFirstElementByPath("IMPORTO_IVA").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_IVA").getValueAsString():"0.0" : "0.0"));
							update.setDouble(10,Double.parseDouble((record.getFirstElementByPath("IMPORTO_FATTURA_CONTABILE") != null) ? (record.getFirstElementByPath("IMPORTO_FATTURA_CONTABILE") .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_FATTURA_CONTABILE").getValueAsString() .replace("-", "") : (!record.getFirstElementByPath("IMPORTO_FATTURA_CONTABILE").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_FATTURA_CONTABILE").getValueAsString():"0.0" : "0.0"));
							update.setDouble(11,Double.parseDouble((record.getFirstElementByPath("IMPORTO_IVA_CONTABILE")     != null) ? (record.getFirstElementByPath("IMPORTO_IVA_CONTABILE")     .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_IVA_CONTABILE").getValueAsString()     .replace("-", "") : (!record.getFirstElementByPath("IMPORTO_IVA_CONTABILE").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_IVA_CONTABILE").getValueAsString():"0.0" : "0.0"));
							update.setDouble(12,Double.parseDouble((record.getFirstElementByPath("IMPORTO_FATTURA_PARZIALE")  != null) ? (record.getFirstElementByPath("IMPORTO_FATTURA_PARZIALE")  .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_FATTURA_PARZIALE").getValueAsString()  .replace("-", "") : (!record.getFirstElementByPath("IMPORTO_FATTURA_PARZIALE").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_FATTURA_PARZIALE").getValueAsString():"0.0" : "0.0"));
							update.setDouble(13,Double.parseDouble((record.getFirstElementByPath("IMPORTO_IVA_PARZIALE")      != null) ? (record.getFirstElementByPath("IMPORTO_IVA_PARZIALE")      .getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_IVA_PARZIALE").getValueAsString()      .replace("-", "") : (!record.getFirstElementByPath("IMPORTO_IVA_PARZIALE").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_IVA_PARZIALE").getValueAsString():"0.0" : "0.0"));
							update.setDouble(14,Double.parseDouble((record.getFirstElementByPath("IMPORTO_FATT_CONT_PARZIALE")!= null) ? (record.getFirstElementByPath("IMPORTO_FATT_CONT_PARZIALE").getValueAsString().endsWith("-")) ? "-"+record.getFirstElementByPath("IMPORTO_FATT_CONT_PARZIALE").getValueAsString().replace("-", "") : (!record.getFirstElementByPath("IMPORTO_FATT_CONT_PARZIALE").getValueAsString().equals(""))?record.getFirstElementByPath("IMPORTO_FATT_CONT_PARZIALE").getValueAsString():"0.0" : "0.0")); 
							update.setString(15,(record.getFirstElementByPath("DATA_EMISSIONE")                               != null) ? record.getFirstElementByPath("DATA_EMISSIONE")            .getValueAsString(): "");  
							update.setString(16,(record.getFirstElementByPath("DATA_SCADENZA_IN")                             != null) ? record.getFirstElementByPath("DATA_SCADENZA_IN")          .getValueAsString(): "");    
							update.setString(17,(record.getFirstElementByPath("DATA_SCADENZA_FIN")                            != null) ? record.getFirstElementByPath("DATA_SCADENZA_FIN")         .getValueAsString(): "");
							update.setString(18,(record.getFirstElementByPath("CONTO_COGE")                                   != null) ? record.getFirstElementByPath("CONTO_COGE")                .getValueAsString(): "");
							update.setString(19,(record.getFirstElementByPath("SETTORE_UNBUNDLING")                           != null) ? record.getFirstElementByPath("SETTORE_UNBUNDLING")        .getValueAsString(): "");
							update.setInt(20,Integer.parseInt((record.getFirstElementByPath("FLG_CEDUTA")                     != null) ? (!record.getFirstElementByPath("FLG_CEDUTA")                .getValueAsString().equals(""))? record.getFirstElementByPath("FLG_CEDUTA").getValueAsString() : "0": "0"));
							update.setString(21,(record.getFirstElementByPath("CODE_LINE")                                    != null) ? record.getFirstElementByPath("CODE_LINE")                 .getValueAsString(): "");
							update.setInt(22,Integer.parseInt((record.getFirstElementByPath("AUDIT_FLG_ELAB")                 != null) ? (!record.getFirstElementByPath("AUDIT_FLG_ELAB")            .getValueAsString().equals(""))? record.getFirstElementByPath("AUDIT_FLG_ELAB").getValueAsString() :"0" : "0"));
							
							int upd = update.executeUpdate();
							if(upd != 1) throw new SQLException("Aggiornate " + upd + " righe");
							
							NUpdate = NUpdate + 1;
						} catch(SQLException e1) {
							updIntEng.setString(1, (record.getFirstElementByPath("ID_FATTURA")!= null) ? record.getFirstElementByPath("ID_FATTURA").getValueAsString(): "");
							updIntEng.execute();
	
							NError = NError + 1;
						}
					}
	
					
	
					NElab = NElab + 1;
					record = record.getNextSibling();
				}


				String SemaforoUP = "UPDATE STAGING_DWH.SAPSD5_SEMAFORO SET STATO_ELABORAZIONE = 1, TS_FINE = SYSDATE WHERE NOME_TABELLA = 'SAPSD5_IN_FATTURA' and STATO_ELABORAZIONE = 0 and TIPO_ELABORAZIONE = 'C'";
				csVer = conn.prepareCall(SemaforoUP);
				csVer.execute();
				csVer.close();
						
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
