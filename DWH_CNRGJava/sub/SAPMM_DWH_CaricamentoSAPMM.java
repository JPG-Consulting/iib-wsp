package sub;

import java.io.BufferedReader;
import java.io.FileReader;
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

public class SAPMM_DWH_CaricamentoSAPMM extends MbJavaComputeNode {

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
			
			String DirPath = "/ibmdata/data/DWH/SAPMM";
			String NomeFile = "IGP_LFA1.CSV";
			String record = "";     
			String cvsSplitBy = ";";  
			String numberRow= "";
			String Insert = "";
			Connection conn = null;
			
			CallableStatement csVer = null;
			ResultSet rs = null;


			conn = getJDBCType4Connection("DWH_SAPMM",JDBC_TransactionType.MB_TRANSACTION_AUTO); 	

			String VerifError = "SELECT * FROM (Select STATO_ELABORAZIONE from STAGING_DWH.SAPMM_SEMAFORO where NOME_TABELLA = 'SAPMM_IN_FORNITORE' and TIPO_ELABORAZIONE = 'R' order by TS_INIZIO desc ) WHERE ROWNUM = 1";					
			
			csVer = conn.prepareCall(VerifError);
			rs = csVer.executeQuery();

			rs.next();
			numberRow = rs.getString(1);
			rs.close();
			csVer.close();
			
			if (!numberRow.equals("1") ) {
				alt.propagate(outAssembly);		
			} else {
				
			String SemaforoIns = "INSERT INTO STAGING_DWH.SAPMM_SEMAFORO (NOME_TABELLA , TIPO_ELABORAZIONE , TS_INIZIO, STATO_ELABORAZIONE ) VALUES ('SAPMM_IN_FORNITORE','W', sysdate,'0')";
			csVer = conn.prepareCall(SemaforoIns);
 			rs = csVer.executeQuery();
 			rs.next();
 			rs.close();
 			csVer.close();


			 BufferedReader br = new BufferedReader(new FileReader(DirPath + "/"+ NomeFile));
	//		 record =  br.readLine();
			 
			 while ((record =  br.readLine()) != null) {
				 String[] Campo = record.split(cvsSplitBy); 
				 
				 Campo[6] = Campo[6].replace("'", "''");
				 Campo[6] = Campo[6].replace("&", "'||'&'||'");

				 
				try {
				 Insert = "INSERT INTO STAGING_DWH.SAPMM_IN_FORNITORE (ID_SISTEMA_PROVENIENZA_DATI, ID_SOCIETA_COMPETENZA, ID_FORNITORE, CF, PIVA, RAGIONE_SOCIALE_NOMINATIVO,DATA_ULTIMA_FATTURA, AUDIT_DATA_INS_TERR, AUDIT_FLG_ELAB  ) " +
				 		"VALUES (13, '"+Campo[2].substring(2)+"','" + Campo[3]+"','" + Campo[4]+"','" + Campo[5]+"','" + Campo[6]+"',TO_DATE('" + Campo[7]+"','yyyyMMdd'), to_date (sysdate) ,0)";
     			csVer = conn.prepareCall(Insert);
     			rs = csVer.executeQuery();
     			rs.next();
     			rs.close();
     			csVer.close();
				} catch(Exception e) {
						
						rs.close();
						csVer.close();
						
						Insert = "UPDATE STAGING_DWH.SAPMM_IN_FORNITORE SET CF = '"+Campo[4] +"', PIVA = '"+Campo[5] +"', RAGIONE_SOCIALE_NOMINATIVO = '"+Campo[6] +"', DATA_ULTIMA_FATTURA  = TO_DATE('" + Campo[7]+"','yyyyMMdd')  WHERE  ID_SISTEMA_PROVENIENZA_DATI = 13 AND ID_SOCIETA_COMPETENZA = '"+Campo[2].substring(2)+"' AND ID_FORNITORE = '"+Campo[3] +"'";

						
						csVer = conn.prepareCall(Insert);
						rs = csVer.executeQuery();
						rs.next();
						rs.close();
						csVer.close();

						}
     			

			 }

     			
    			String SemaforoUPP = "UPDATE STAGING_DWH.SAPMM_SEMAFORO SET STATO_ELABORAZIONE = 1, TS_FINE = SYSDATE WHERE NOME_TABELLA = 'SAPMM_IN_FORNITORE' and STATO_ELABORAZIONE = 0 and TIPO_ELABORAZIONE = 'W'";
    			csVer = conn.prepareCall(SemaforoUPP);
     			rs = csVer.executeQuery();
     			rs.next();
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
