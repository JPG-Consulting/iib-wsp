import java.nio.charset.StandardCharsets;
import java.sql.Connection;
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


public class InsertDB extends MbJavaComputeNode {
	
	
	private Connection       conn;
	private Statement        s;
	
	private String           errorMessage;
	
	private MbMessage        outMessage;
	
	
	public  MbMessageAssembly inAssembly;
	public  MbMessageAssembly outAssembly;
	 
	
	private boolean          flag_continue;
	private String           nometabella;
	private String           formatodata;
	private String           separatore;
	 
	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		
		MbOutputTerminal out = getOutputTerminal("out");
		//MbMessage inMessage = inAssembly.getMessage();
		 //inMessage = inAssembly.getMessage();
	    outAssembly = null;
	    
		this.inAssembly =inAssembly;
				
		conn = null;
		s    = null;
	        
	    setContinue(true);
	   
		try {
			// create new message as a copy of the input
			// outMessage = new MbMessage();
			 //outMessage = new MbMessage(inMessage);
			 outMessage = new MbMessage();
			
			
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
				
		 	
			// ----------------------------------------------------------
			if (   getContinue() && checkConnectionDb() )  {
	    		setErrorMessage("URL ... connessione ok");  // posso continuare l'elaborato...
	    		setContinue(true); 
			}
			
			// 
			if (getContinue() && checkDbName())  {
	    		setErrorMessage("URL ... parametro db ok");  // posso continuare l'elaborato...
	    		setContinue(true); 
			}
			
			if (getContinue() && checkDbValue())  {
	    		setErrorMessage("URL ... valore parametro db ok");  // posso continuare l'elaborato...
	    		setContinue(true); 
			}
			
			if (getContinue() && checkNomeTabella())  {
	    		setErrorMessage("URL ... nome parametro nomeTabella ok");  // posso continuare l'elaborato...
	    		setContinue(true); 
			}
			
			if (getContinue() && checkNomeTabellaValue())  {
	    		setErrorMessage("URL ... valore parametro nomeTabella ok");  // posso continuare l'elaborato...
	    		setContinue(true); 
			}
			
			if (getContinue() && checkNomeformatoData())  {
	    		setErrorMessage("URL ... nome parametro formatoData ok");  // posso continuare l'elaborato...
	    		setContinue(true); 
			}
			
			if (getContinue() && checkformatoDataValue())  {
	    		setErrorMessage("URL ... valore parametro formatoData ok");  // posso continuare l'elaborato...
	    		setContinue(true); 
			}
			
			if (getContinue() && checkNomeSeparatore())  {
	    		setErrorMessage("URL ... nome parametro separatore ok");  // posso continuare l'elaborato...
	    		setContinue(true); 
			}
			
			if (getContinue() && checkSeparatoreValue())  {
	    		setErrorMessage("URL ... valore parametro separatore ok");  // posso continuare l'elaborato...
	    		setContinue(true); 
			}

			if (getContinue() && checkInsertRigheDb())  {
	    		setErrorMessage("Insert ... righe inserite");  // posso continuare l'elaborato...
	    		setContinue(true); 
			}
			
			if (getContinue()){
								MbElement result = outMessage.getRootElement().createElementAsLastChild("JSON").createElementAsLastChild(MbJSON.OBJECT,MbJSON.DATA_ELEMENT_NAME, null).createElementAsLastChild(MbJSON.OBJECT, "result", null);
							    MbElement info = result.createElementAfter(MbJSON.OBJECT, "info", null);
							    info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "status", true);
							    info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "description", getMessage());
			} else throw new Exception(getMessage());

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
			Object[] obj = {e.toString()};
			throw new MbUserException(this, "evaluate()", "", "", "",
					obj);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}
	
	 private boolean checkConnectionDb() {

	    try{
	    	conn = getJDBCType4Connection(
	    			
	    			outAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/db").getValueAsString(),
	    			//this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/db").getValueAsString(),
	    			JDBC_TransactionType.MB_TRANSACTION_AUTO);
	    	
	    	//conn.close();
	    	setContinue(true);                             
    	    return true;
    	
		} catch (Exception e) { 
			    setErrorMessage("connessione non valida ");		
			    setContinue(false);
				return false; 
		} 
	 }
	
	 private boolean checkDbName() {
 	    try{
 	    	if (this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString") == null) {
 				setErrorMessage("URL ... Parametro db mancante");
 				setContinue(false);  
 				return false;
 			}
 	    	
 	    	if (this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/db") == null) {
 				setErrorMessage("URL ... Parametro db mancante");
 				setContinue(false);  
 				return false;
 			}
 	    	 
 	    	 
	    	return true;
	    	
			} catch (Exception e) { 
					return false; 
			}
	   
	 }
	 		 
	 private boolean checkDbValue() {

 	    try{
 	    	if (this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/db").getValueAsString() == null) {
 				setErrorMessage("URL ... Valore Parametro db mancante");
 				setContinue(false);;
 				return false;
 			}
 	    	 
	    	return true;
	    	
			} catch (Exception e) { 
					return false; 
			}
	   
	
      }
		 
	 private boolean checkNomeTabella() {
	 	    try{
	 	    	 
	 	    	
	 	    	if (this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/nomeTabella") == null) {
	 				setErrorMessage("URL ... Parametro nomeTabella mancante");
	 				setContinue(false);  
	 				return false;
	 			}
	 	    	 
	 	    	 
		    	return true;
		    	
				} catch (Exception e) { 
						return false; 
				}
		   
		 }
	 
	 private boolean checkNomeTabellaValue() {

	 	    try{
	 	    	 
	 	    	
	 	    	if (this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/nomeTabella").getValueAsString() == null) {
	 				setErrorMessage("URL ... valore Parametro nomeTabella mancante");
	 				setContinue(false);  
	 				return false;
	 			}
	 	    	 
	 	    	setNomeTabella(this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/nomeTabella").getValueAsString());
		    	return true;
		    	
				} catch (Exception e) { 
						return false; 
				}
		   
		 }
	 
	 private boolean checkNomeformatoData() {
	 	    try{
	 	    	 
	 	    	
	 	    	if (this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/formatoData") == null) {
	 				setErrorMessage("URL ... Parametro formatoData mancante");
	 				setContinue(false);  
	 				return false;
	 			}
	 	    	 
	 	    	 
		    	return true;
		    	
				} catch (Exception e) { 
						return false; 
				}
		   
		 }
	 
	 private boolean checkformatoDataValue() {

	 	    try{
	 	    	 
	 	    	
	 	    	if (this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/formatoData").getValueAsString() == null) {
	 				setErrorMessage("URL ... valore Parametro formatoData mancante");
	 				setContinue(false);  
	 				return false;
	 			}
	 	    	
	 	    	setFormatoData(this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/formatoData").getValueAsString());

	 	    	 
		    	return true;
		    	
				} catch (Exception e) { 
						return false; 
				}
		   
		 }
	  
	 private boolean checkNomeSeparatore() {
	 	    try{
	 	    	 
	 	    	
	 	    	if (this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/separatore") == null) {
	 				setErrorMessage("URL ... Parametro separatore mancante");
	 				setContinue(false);  
	 				return false;
	 			}
	 	    	 
	 	    	 
		    	return true;
		    	
				} catch (Exception e) { 
						return false; 
				}
		   
		 }
	 
	 private boolean checkSeparatoreValue() {

	 	    try{
	 	    	 
	 	    	
	 	    	if (this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/separatore").getValueAsString() == null) {
	 				setErrorMessage("URL ... valore Parametro separatore mancante");
	 				setContinue(false);  
	 				return false;
	 			}
	 	    	 
	 	    	setSeparatore(this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/separatore").getValueAsString());

		    	return true;
		    	
				} catch (Exception e) { 
						return false; 
				}
		   
		 }
	 	 
	 private String getMessage(){
	    	return this.errorMessage;
	 } 
	    
	 private void setErrorMessage(String s){
	    	this.errorMessage = s;
	 }
	    
	 private boolean getContinue(){
	    	return this.flag_continue ;
	 } 
	    
	 private void setContinue(boolean scontinue){
	    	this.flag_continue = scontinue;
	 }
	 
	 private String getNomeTabella(){
	    	return this.nometabella;
	 } 
	    
	 private void setNomeTabella(String s){
	    	this.nometabella = s;
	 }
	 
	 private String getFormatoData(){
	    	return this.formatodata;
	 } 
	    
	 private void setFormatoData(String s){
	    	this.formatodata = s;
	 }
	 
	 private String getSeparatore(){
	    	return this.separatore;
	 } 
	    
	 private void setSeparatore(String s){
	    	this.separatore = s;
	 }
	 private boolean checkInsertRigheDb() {

         try {
        	// quando controlli la connessione al db , ricordati di campiare il parametro di sessione alter session set NLS_DATE_FORMAT = 'yyyymmdd'
        	              conn = getJDBCType4Connection(	    			
 	    			      outAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/db").getValueAsString(),
 	    			      JDBC_TransactionType.MB_TRANSACTION_AUTO);
        	              
        	              
        	              s = conn.createStatement();
        	              
        	              //s.addBatch("alter session set nls_date_format = 'yyyy/mm/dd hh24:mi:ss'");
        	              s.addBatch("alter session set nls_date_format = "+getFormatoData());
        	              s.executeBatch();
 
        	              
        	              String[] records    = new String((byte[]) inAssembly.getMessage().getRootElement().getFirstElementByPath("/BLOB/BLOB").getValue(), StandardCharsets.UTF_8).split("\r\n"); 
  		 				
        	  			  String[] cols = records[0].split(Pattern.quote(getSeparatore()));
        	  			
	        	  		  String intesatazione ="";
	        	  		  String[] riga;
	        	  		  String campi         ="";
	        	  		  String insert         ="";

	        	  		  int numero_colonne = 0;
	        	  		  int numero_valori = 0;
	        	  		  int numero_righe_inserite = 0;
        	  			
		        	  	  for (String i: cols ) {		
		        	  				intesatazione = intesatazione + ","+i;		
		        	  				numero_colonne=numero_colonne+1;
		        	  	  }			
		        	  			intesatazione =	intesatazione.replaceFirst(",", " ");
		        	  			 	
		        	             for (int a = 1; a < records.length; a++) {
		        	              	
		        	              	   riga = records[a].split(Pattern.quote(separatore),-1);
		        	              	   
		        	              	   for (String c: riga ) {
		        	         				
		        	         				campi = campi + ",'"+c+"'";
		        	         				numero_valori =numero_valori+1;
		        	         			  
		        	         			  }	
		        	              	   campi =	campi.replaceFirst(",", " ");
		        	              	   
		        	              	  
		        	              	 insert = "insert into " +  getNomeTabella() +" ("+intesatazione+") "+" values ("+campi+")";
		        	              	 s.executeQuery(insert); 	        	              	
		        	              	 insert="";
		        	              	 campi = "";
		        	              	 numero_righe_inserite =numero_righe_inserite+1;
		
		        	  			}
		        	             
		        	             
		        	             
		     			        s.close();
         			                
						        flag_continue = true;
						        return true;
		 }
         
          
		  catch (SQLException e) {
			    		    setErrorMessage("Errore SQL ..."+e.toString());
			    		    flag_continue = false;  
			    		    return false;			    		 
		 }
         catch (MbException e) {
 		    setErrorMessage("Errore nella nell'insert ...");
 		    flag_continue = false;
 		    return false;	 
         }
    	 
	 
		   
	 }


}
