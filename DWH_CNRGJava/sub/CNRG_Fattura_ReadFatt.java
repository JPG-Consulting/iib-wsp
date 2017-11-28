package sub;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;

public class CNRG_Fattura_ReadFatt extends MbJavaComputeNode {

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
			Connection connDWH = null;

			CallableStatement csVer = null;
			CallableStatement csVerC = null;
			CallableStatement csVerUPP = null; //Serve per Update su CNRG
			ResultSet rs = null;
			ResultSet rsC = null;
			ResultSet rsUPP = null;  //Serve per Update su CNRG

			String C1 = null;
			String V1 = null;
			String CU = null;   //Serve per Update su CNRG
			String VU = null;   //Serve per Update su CNRG

			
			String ColonneIns;
			String ValueIns;
			String ValueUpdate;
			String ValueQuery;
			String UPPQuery ;	//Serve per Update su CNRG
			String Insert = null;

			
			conn = getJDBCType4Connection("C3",JDBC_TransactionType.MB_TRANSACTION_AUTO); 	
			String sel = "Select * from CNRG.B2111_IN_FATTURA  where  B2111_DATA_ELAB_DWH is null and  ROWNUM < 1000000 ";

			connDWH = getJDBCType4Connection("DWH_CREDITO",JDBC_TransactionType.MB_TRANSACTION_AUTO); 	
			
			
			
			csVerC = conn.prepareCall(sel);
			rsC = csVerC.executeQuery();
			
			
			int Rec = rsC.getFetchSize();
			
			if (Rec == 0 ) {
				csVerC.close();
				rsC.close();
				alt.propagate(outAssembly);		
			}
			else {
			
	        while (rsC.next()) {
				
				ResultSetMetaData rsmd = rsC.getMetaData();
				 ColonneIns = "";
				 ValueIns = "";
				 ValueUpdate = "";
				 ValueQuery  = "";
				UPPQuery = "";

							
				int nr = rsmd.getColumnCount();
				
		         for(int i=1; i<=nr; i++){
		        	 if (rsC.getString(i) != null ) {
		        		 
		        		C1 = rsmd.getColumnLabel(i);
			     		CU = C1;

		        		 

		        			// Elimino dal nome della colonna il codice della tabella Ossia B2110_
		        			C1 = C1.substring(6);
		        			
		        			// Modifico il nome dei campi come quelli della tabella DWH
		        			C1 = C1.replaceAll("SIST_PROV", "SISTEMA_PROVENIENZA");
		        			C1 = C1.replaceAll("SOC_", "SOCIETA_");	
		        			C1 = C1.replaceAll("_FATT", "_FATTURA");
		        			C1 = C1.replaceAll("IMP", "IMPORTO");	
		        			if (!C1.equals("INTERESSI_MORA_PARZIALI")){
		        				C1 = C1.replaceAll("_PARZ", "_PARZIALE");	
		        			}
		        			C1 = C1.replaceAll("_CONT", "_CONTABILE");		
		        			C1 = C1.replaceAll("_SCAD", "_SCADENZA");
		        			C1 = C1.replaceAll("SETT_", "SETTORE_");
		        			C1 = C1.replaceAll("_PAGAM", "_PAGAMENTO");		
		        			C1 = C1.replaceAll("NUM_CONT", "NUMERO_CONT");
		        			C1 = C1.replaceAll("_COMP", "_COMPETENZA");
		        			
		        			if (C1.equals("FLG_ELAB")){
		        				C1 = "AUDIT_FLG_ELAB";	
		        			}

		        			if (!C1.equals("DATA_INSERIMENTO")){	        		 
		        						
		        			V1 = rsC.getString(i);
			    			VU = V1;
		        			
		        			V1 = V1.replace("'", "''");
		        			V1 = V1.replace("&", "'||'&'||'");

		        						
		        			ColonneIns = ColonneIns.concat(C1).concat(",");
		        			
		        			if (C1.startsWith("FLG") || C1.startsWith("INTERESSI")|| C1.startsWith("IMPORTO")) {
		        				
		        				if (V1.endsWith("-")){
		        					V1 = V1.substring(0, V1.length()-1);
		        					V1 = "-".concat(V1);
		        				}	
		        					
		        				ValueIns = ValueIns.substring(0, ValueIns.length()-1);
		        				ValueIns = ValueIns.concat(V1).concat(",'");
		        				ValueUpdate = ValueUpdate.concat(C1).concat(" = ").concat(V1).concat(",");
		        			} else if (C1.equals("ID_SISTEMA_PROVENIENZA_DATI") ||C1.equals("ID_SOCIETA_COMPETENZA") || C1.equals("ID_FORNITURA")|| C1.equals("ID_FATTURA") ){
		        				ValueIns = ValueIns.concat(V1).concat("','");
		        				ValueQuery = ValueQuery.concat(C1).concat(" = '").concat(V1).concat("'AND ");
			    				UPPQuery = UPPQuery.concat(CU).concat(" = '").concat(VU).concat("'AND ");
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
		        			
		        		 
	//	        	  output.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,  rsmd.getColumnLabel(i),rs.getString(i));
		          	 }

       			}
	        			
	        			
	        			// Per effettuaree l'insetimento......
	        			
	        			ColonneIns = ColonneIns.concat("FLG_CEDUTA").concat(","); //Da Dire a DWH
	        			ValueIns = ValueIns.substring(0, ValueIns.length()-1);
	        			ValueIns = ValueIns.concat("0").concat(",'");

	        			
	        					
	        			ColonneIns = ColonneIns.substring(0, ColonneIns.length()-1);
	        			ValueIns = ValueIns.substring(0, ValueIns.length()-2);
	        			ValueUpdate = ValueUpdate.substring(0, ValueUpdate.length()-1);
	        			ValueQuery = ValueQuery.substring(0, ValueQuery.length()-4);
						UPPQuery = UPPQuery.substring(0, UPPQuery.length()-4);
	        			

	        			try {
	        				
	        				Insert = "INSERT INTO STAGING_DWH.CNRGDIS_IN_FATTURA ("+ColonneIns+") VALUES ('" + ValueIns +")";
	        	
	        			csVer = connDWH.prepareCall(Insert);
	        			rs = csVer.executeQuery();
	        			rs.next();
	        			rs.close();
	        			csVer.close();
	        			
	        			}  catch(Exception e) {
	        				
	        			csVer.close();
	        			
	        			Insert = "UPDATE STAGING_DWH.CNRGDIS_IN_FATTURA  SET "+ValueUpdate+" WHERE " + ValueQuery +"";
	        			
	        			csVer = connDWH.prepareCall(Insert);
	        			rs = csVer.executeQuery();
	        			rs.next();
	        			rs.close();
	        			csVer.close();

	        			}
	        			
	    				String Update = "UPDATE CNRG.B2111_IN_FATTURA SET B2111_DATA_ELAB_DWH = to_date (sysdate)  where " + UPPQuery;
	    				
	    				csVerUPP = conn.prepareCall(Update);
	    				rsUPP = csVerUPP.executeQuery();
	    				rsUPP.next();
	    				rsUPP.close();
	    				csVerUPP.close();

	        			
	        			}


	        			String SemaforoUP = "UPDATE STAGING_DWH.CNRGDIS_SEMAFORO SET STATO_ELABORAZIONE = 1, TS_FINE = SYSDATE  WHERE NOME_TABELLA = 'CNRGDIS_IN_FATTURA' and STATO_ELABORAZIONE = 0 and TIPO_ELABORAZIONE = 'C'";
	        			csVer = connDWH.prepareCall(SemaforoUP);
	        			rs = csVer.executeQuery();
	        			rs.close();
	        			csVer.close();      		 
	        		 
	        		 
		         }

			
		
	        
			rsC.close();
			csVerC.close();
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
