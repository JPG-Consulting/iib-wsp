package sub;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;

public class CNRGMT_Forniture_InserimentoFornitori extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			
			String SistemaCNRG = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/SistemaCNRG").getValueAsString();
			String UserCNRG = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/UserCNRG").getValueAsString();


			// ----------------------------------------------------------
			// Add user code below
			Connection conn = null;
			Connection connDWH = null;

			CallableStatement csVerC = null;
			CallableStatement csVerUPP = null; //Serve per Update su CNRG
			CallableStatement csVer = null;
			
			ResultSet rs = null;
			ResultSet rsUPP = null;  //Serve per Update su CNRG
			ResultSet rsD = null;
			String C1 = null;
			String V1 = null;
			String CU = null;   //Serve per Update su CNRG
			String VU = null;   //Serve per Update su CNRG

			String ColonneIns;
			String ValueIns;
			String ValueUpdate ;
			String ValueQuery ;	
			String UPPQuery ;	//Serve per Update su CNRG
			String Insert = null;

			conn = getJDBCType4Connection(SistemaCNRG,JDBC_TransactionType.MB_TRANSACTION_AUTO); 	
			String sel = "Select * from "+ UserCNRG + ".B2074_TO_IN_CONTRATTO where AUDIT_DATA_ELAB_DWH is null AND DATA_INIZIO_FORNITURA is not null";
			connDWH = getJDBCType4Connection("DWH_CREDITO",JDBC_TransactionType.MB_TRANSACTION_AUTO); 	


			csVerC = conn.prepareCall(sel);
			rs = csVerC.executeQuery();
			
			
			int Rec = rs.getFetchSize();
			
			if (Rec == 0 ) {
				csVerC.close();
				rs.close();
				alt.propagate(outAssembly);		
			}
			else {
			
	        while (rs.next()) {
				
				ResultSetMetaData rsmd = rs.getMetaData();
				ColonneIns = "";
				ValueIns = "";
				ValueUpdate = "";
				ValueQuery  = "";
				UPPQuery = "";
							
				int nr = rsmd.getColumnCount();
				
		         for(int i=1; i<=nr; i++){
			       	 if (rs.getString(i) != null && !rsmd.getColumnLabel(i).equals("DATA_CARICAMENTO") && !rsmd.getColumnLabel(i).equals("EXTERNAL_ID") && !rsmd.getColumnLabel(i).equals("STATO") 
			       			 	&& !rsmd.getColumnLabel(i).equals("NUM_TEL_RAPPRES_LEGALE") && !rsmd.getColumnLabel(i).equals("TIPO_SERVIZIO")  && !rsmd.getColumnLabel(i).equals("USO_RESIDENTE")
			       			 && !rsmd.getColumnLabel(i).equals("ID_CONTRATTO_PADRE") )
			       	 {
			       		 
		     			C1 = rsmd.getColumnLabel(i);
		     			CU = C1;
		     			
		    			C1 = C1.replaceAll("DISALIMENTABILITA", "FLG_DISABILIMENTA_FORNITURA");
		    			C1 = C1.replaceAll("POD_PDR_TLR", "PUNTO_EROGAZIONE");
		    			C1 = C1.replaceAll("STATO_SERVIZIO", "STATO_FORNITURA");
		    			C1 = C1.replaceAll("FLG_ELAB", "AUDIT_FLG_ELAB");
		    			C1 = C1.replaceAll("ID_FORNITURA", "CONTRATTO_COLLEGATO");
		    			C1 = C1.replaceAll("ID_CONTRATTO", "ID_FORNITURA");
		    			C1 = C1.replaceAll("CODICE_ISTAT", "CODICE_ISTAT_FORNITURA");
		    			C1 = C1.replaceAll("NIITURA", "NITURA");
		    			C1 = C1.replaceAll("CAUSALE_CHIUSURA_CONTRATTO", "CAUSALE_CESSAZIONE_CONTRATTO");

		    			
		    			V1 = rs.getString(i);
		    			VU = V1;
		    			
		    			V1 = V1.replace("'", "''");
		    			V1 = V1.replace("&", "'||'&'||'");
		    			
		    			
 		    			ColonneIns = ColonneIns.concat(C1).concat(",");
		    			
		    			if ((C1.startsWith("FLG") && !C1.equals("FLG_DISABILIMENTA_FORNITURA"))|| C1.startsWith("IMPORTO") ) {
		    				ValueIns = ValueIns.substring(0, ValueIns.length()-1);
		    				ValueIns = ValueIns.concat(V1).concat(",'");
		    				ValueUpdate = ValueUpdate.concat(C1).concat(" = ").concat(V1).concat(",");
		    			} else if (C1.equals("ID_SISTEMA_PROVENIENZA_DATI") ||C1.equals("ID_SOCIETA_COMPETENZA") || C1.equals("ID_FORNITURA") ){
		    				ValueIns = ValueIns.concat(V1).concat("','");
		    				ValueQuery = ValueQuery.concat(C1).concat(" = '").concat(V1).concat("'AND ");		
		    				UPPQuery = UPPQuery.concat(CU).concat(" = '").concat(VU).concat("'AND ");
		    			} else if (C1.startsWith("AUDIT_DATA") || C1.startsWith("DATA")  ){
			    				V1 = V1.substring(0, 10);
			    				ValueIns = ValueIns.substring(0, ValueIns.length()-1);
			    				ValueIns = ValueIns.concat("TO_DATE('").concat(V1).concat("','yyyy-MM-dd') ,'");
			    				ValueUpdate = ValueUpdate.concat(C1).concat(" = TO_DATE('").concat(V1).concat("','yyyy-MM-dd') ,");
		    			} 	else {
		    				ValueIns = ValueIns.concat(V1).concat("','");
		    				ValueUpdate = ValueUpdate.concat(C1).concat(" = '").concat(V1).concat("',");				
		    				}
		    			
		    			}			    				  
			       	 }
		         
			       	 
		    			ColonneIns = ColonneIns.substring(0, ColonneIns.length()-1);
		    			ValueIns = ValueIns.substring(0, ValueIns.length()-2);
		    			ValueUpdate = ValueUpdate.substring(0, ValueUpdate.length()-1);
		    			ValueQuery = ValueQuery.substring(0, ValueQuery.length()-4);
						UPPQuery = UPPQuery.substring(0, UPPQuery.length()-4);

		    			try {
		    				
		    			Insert = "INSERT INTO STAGING_DWH.CNRGLM_IN_FORNITURA ("+ColonneIns+") VALUES ('" + ValueIns +")";
		    	
		    			csVer = connDWH.prepareCall(Insert);
		    			rsD = csVer.executeQuery();
		    			rsD.next();
		    			rsD.close();
		    			csVer.close();
		    			
		    			}  catch(Exception e) {
		    				
		    				csVer.close();
		    			
		    			Insert = "UPDATE STAGING_DWH.CNRGLM_IN_FORNITURA  SET "+ValueUpdate+" WHERE " + ValueQuery +"";
		    			
		    			csVer = connDWH.prepareCall(Insert);
		    			rsD = csVer.executeQuery();
		    			rsD.next();
		    			rsD.close();
		    			csVer.close();

						}
		    			
						String Update = "UPDATE "+ UserCNRG + ".B2074_TO_IN_CONTRATTO SET AUDIT_DATA_ELAB_DWH = to_date (sysdate)  where " + UPPQuery;
						
						csVerUPP = conn.prepareCall(Update);
						rsUPP = csVerUPP.executeQuery();
						rsUPP.next();
						rsUPP.close();
						csVerUPP.close();
	       
			}
		
	    	rs.close();
			csVerC.close();
	        
			String SemaforoUP = "UPDATE STAGING_DWH.CNRGLM_SEMAFORO SET STATO_ELABORAZIONE = 1, TS_FINE = SYSDATE WHERE NOME_TABELLA = 'CNRGLM_IN_FORNITURA' and STATO_ELABORAZIONE = 0 and TIPO_ELABORAZIONE = 'C'";
			csVer = connDWH.prepareCall(SemaforoUP);
			rsD = csVer.executeQuery();
			rsD.close();
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
