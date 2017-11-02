import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;


public class InsertDB_InsertRows extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		
		Connection conn = null;
		Statement s = null;
		String  nometabella;
		String  formatodata;
		String  separatore;
		String	chiave = null;
		String	sequence = null;
		String	chiaveUnica = "NO";
		String	separatoreDecimali = null;
		String message;
		MbElement localEnv = inAssembly.getLocalEnvironment().getRootElement();
		
		
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage();
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			
			if (localEnv.getFirstElementByPath("/HTTP/Input/QueryString") == null) {
				throw new Exception("Parametri URL mancanti");
 			}
 	    	
 	    	if (localEnv.getFirstElementByPath("/HTTP/Input/QueryString/db") == null) {
 	    		throw new Exception("Parametro 'db' mancante");
 			}
 	    	
 	    	if (localEnv.getFirstElementByPath("/HTTP/Input/QueryString/db").getValueAsString() == null) {
 	    		throw new Exception("Valore parametro 'db' mancante");
 			}
 	    	
 	    	conn = getJDBCType4Connection(localEnv.getFirstElementByPath("/HTTP/Input/QueryString/db").getValueAsString(), JDBC_TransactionType.MB_TRANSACTION_AUTO);
 	    	
 	    	if (localEnv.getFirstElementByPath("/HTTP/Input/QueryString/nomeTabella") == null) {
 	    		throw new Exception("Parametro 'nomeTabella' mancante");
 			}
 	    	
 	    	if (localEnv.getFirstElementByPath("/HTTP/Input/QueryString/nomeTabella").getValueAsString() == null) {
 	    		throw new Exception("Valore parametro 'nomeTabella' mancante");
 			} else {
 				nometabella = localEnv.getFirstElementByPath("/HTTP/Input/QueryString/nomeTabella").getValueAsString();
 			}
 	    	
 	    	if (localEnv.getFirstElementByPath("/HTTP/Input/QueryString/formatoData") == null) {
 	    		throw new Exception("Parametro 'formatoData' mancante");
 			}
 	    	
 	    	if (localEnv.getFirstElementByPath("/HTTP/Input/QueryString/formatoData").getValueAsString() == null) {
 	    		throw new Exception("Valore parametro 'formatoData' mancante");
 			} else {
 				formatodata = localEnv.getFirstElementByPath("/HTTP/Input/QueryString/formatoData").getValueAsString();
 			}
 	    	
 	    	if (localEnv.getFirstElementByPath("/HTTP/Input/QueryString/separatore") == null) {
 	    		throw new Exception("Parametro 'separatore' mancante");
 			}
 	    	
 	    	if (localEnv.getFirstElementByPath("/HTTP/Input/QueryString/separatore").getValueAsString() == null) {
 	    		throw new Exception("Valore parametro 'separatore' mancante");
 			} else {
 				separatore = localEnv.getFirstElementByPath("/HTTP/Input/QueryString/separatore").getValueAsString();
 			}
 	    	//logica inserimento chiavi via sequence
            MbElement e = null;
            
            e = localEnv.getFirstElementByPath("/HTTP/Input/QueryString/chiave");
            if (e != null) chiave = e.getValueAsString();
            e = null;
            
            e = localEnv.getFirstElementByPath("/HTTP/Input/QueryString/sequence");
            if (e != null) sequence = e.getValueAsString();
            e = null;
            
            e = localEnv.getFirstElementByPath("/HTTP/Input/QueryString/chiaveUnica");
            if (e != null) chiaveUnica = e.getValueAsString();
            
            e = localEnv.getFirstElementByPath("/HTTP/Input/QueryString/separatoreDecimali");
            if (e != null) separatoreDecimali = e.getValueAsString();
            
            s = conn.createStatement();
            
            s.execute("alter session set nls_date_format = '"+formatodata+"'");

            if (separatoreDecimali != null) {
          	  s.execute("alter session set nls_numeric_characters = '"+separatoreDecimali+"'");
            }

            if (inAssembly.getMessage().getRootElement().getFirstElementByPath("/BLOB/BLOB") == null) {
            	throw new Exception("Nessun file allegato");
            }
            
            String[] records    = new String((byte[]) inAssembly.getMessage().getRootElement().getFirstElementByPath("/BLOB/BLOB").getValue(), StandardCharsets.UTF_8).split("\\r\\n|\\n|\\r"); 
				
			String[] cols = records[0].split(Pattern.quote(separatore));
			
	  		String intestazione ="";
	  		String[] riga;
	  		String campi         ="";
	  		String insert         ="";
	  		String valoreChiave = null;

	  		int numero_colonne = 0;
	  		int numero_valori = 0;
	  		int numero_righe_inserite = 0;
			
  	  	  	for (String i: cols ) {		
  	  			intestazione = intestazione + ","+i;		
  	  				numero_colonne=numero_colonne+1;
  	  	  	}			
  	  		
  	  	  	intestazione =	intestazione.replaceFirst(",", " ");
  	  	  	if (chiave != null ) {
  	  			if(chiaveUnica.equals("SI")){
  	  				ResultSet rs = s.executeQuery("SELECT "+ sequence+".nextval from dual");
  	  				rs.next();
  	  				valoreChiave = rs.getString("nextval");
  	  			}
  	  			if (sequence.equals("NO")){
  	  				valoreChiave = chiaveUnica;
  	  			}
  	  	  	}
  	        
  	  	  	for (int a = 1; a < records.length; a++) {
  	              	
  	              	   riga = records[a].split(Pattern.quote(separatore),-1);
  	              	   
  	              	   for (String c: riga ) {
  	         				
  	         				campi = campi + ",'"+c+"'";
  	         				numero_valori =numero_valori+1;
  	         			  
  	         			  }	
  	              	   campi =	campi.replaceFirst(",", " ");
  	              	   
  	              	 if (chiave != null) {
  	              		 	if (chiaveUnica.equals("NO")){
  	              		 		ResultSet rs = s.executeQuery("SELECT "+ sequence+".nextval from dual");
	        	  					rs.next();
  	              		 		valoreChiave = rs.getString("nextval");
  	              		 		rs.close();
  	              		 	}
  	              		 	insert = "insert into " +  nometabella +" ("+chiave+","+intestazione+") "+" values ('"+valoreChiave+"',"+campi+")";
  	              	 } else {
  	              		 insert = "insert into " +  nometabella +" ("+intestazione+") "+" values ("+campi+")";
  	              	 }
  	              	 s.execute(insert); 	        	              	
  	              	 insert="";
  	              	 campi = "";
  	              	
  	              	 numero_righe_inserite =numero_righe_inserite+1;

  	  			}
  	             
  	             
  	        message = numero_righe_inserite + " righe inserite"; 
			        
 	    	
			MbElement result = outMessage.getRootElement().createElementAsLastChild("JSON").createElementAsLastChild(MbJSON.OBJECT,MbJSON.DATA_ELEMENT_NAME, null).createElementAsLastChild(MbJSON.OBJECT, "result", null);
		    MbElement info = result.createElementAfter(MbJSON.OBJECT, "info", null);
		    if (numero_righe_inserite > 0) {
		    	info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "status", true);
		    } else {
		    	info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "status", false);
		    }
		    
		    info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "description", message);
		    
		    s.close();
		    
			// End of user code
			// ----------------------------------------------------------
		
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			Object[] obj = {e.getMessage()};
			try {
				s.close();
				conn.rollback();
			} catch (Exception e1) {
				//do nothing
			}
			
			throw new MbUserException(this, "evaluate()", "", "", "",
					obj);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

}
