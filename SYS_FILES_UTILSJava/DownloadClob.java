import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;


public class DownloadClob extends MbJavaComputeNode {
	
	
	private String           errorMessage;
	private MbMessage        inMessage;
	private Connection       conn; 
	private ResultSet        rs;
	private Statement        s;
	
	
	public  MbMessageAssembly inAssembly;
	
	private String           nometabella;
	private String           nomechiave;
	private String           valorechiave;
	private String           nomefile;
	private String           posizione;
	private String           nomeclob;
	 
	
	 

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		 

		this.inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		
		this.inAssembly =inAssembly;	
		
		conn = null;
		s    = null;
	    rs   = null;
		 	
		try {
			// create new message as a copy of the input	
			
			boolean flag_continue = true;
			
			MbMessage outMessage = new MbMessage();
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			
			//boolean filejson = checkFileJson(this.inMessage);
			
			
           
	    	if (checkConnectionDb())  {
	    		setErrorMessage("URL ... connessione ok");

	    		 // controllo i parametri presenti nel json ..
	             try {	

	            	    MbElement parameter =      this.inMessage.getRootElement().getFirstElementByPath("/JSON/Data/Parameters/Item");
	             	
	            	   	
	             	 if (parameter != null) {
	 
						             		 try {	
												             	   nometabella = parameter.getFirstElementByPath("nometabella").getValueAsString();
												             	   nomechiave = parameter.getFirstElementByPath("nomechiave").getValueAsString();
												             	   
												             	   valorechiave  = parameter.getFirstElementByPath("valorechiave").getValueAsString();
												               	   nomefile = parameter.getFirstElementByPath("nomefile").getValueAsString();
												               	   nomeclob = parameter.getFirstElementByPath("nomeclob").getValueAsString();
												               	   posizione = parameter.getFirstElementByPath("posizione").getValueAsString();
						             		 }	 
						             		 catch (RuntimeException e) {
						              			setErrorMessage("Errore  nella lettura dei singoli parametri json ...  verificare !!!");
						              			flag_continue = false;
						              			 
						              		} 
	
	                  }else
	                   {
	              			setErrorMessage("Json non valido !!!");
	              			flag_continue = false;
	              			 
	              		} 
	                	  
	                	  
	                	  
	             		// ----------------------------------------------------------
	         		} catch (MbException e) {
	         			setErrorMessage("Errore  nel parameter !!! ");
	         			flag_continue = false;
	         			
	 
	         		} 
	             
	             if (flag_continue){

                   
                     String result = nomefile.substring(nomefile.indexOf("%") + 1, nomefile.lastIndexOf("%"));

	            	 s = conn.createStatement();

		             String Query1 = "Select count(*) from "+nometabella  +" where "+nomechiave+" = "+valorechiave;
		             
		             String Query2 = "Select "+ result  +","+nomeclob+" from "+nometabella  +" where "+nomechiave+" = "+valorechiave;
		             
		
		             try {
		             
						             rs = s.executeQuery(Query1);
						             
						             
						             
						             if ( !rs.next()  ){
						            	 
						            	   
						            		setErrorMessage("La query non ha prodotto nessun risultato !!! ");
						            		
								    		 
								    } else{
						
								    	rs = s.executeQuery(Query2);
								    	
										String path =posizione; 
										String fileOut = "";
										
										String f = "";
										
									
										
										while (rs.next()) {
											
											// fileName = rs.getString(nomefile);
											 
											 
											 
											 f = rs.getString(result);
											 
											 
											 fileOut =  path +  nomefile.replace(nomefile.substring(nomefile.indexOf("%"), nomefile.lastIndexOf("%") +1), f); //fileName;
											 Reader reader = rs.getCharacterStream(nomeclob);
											 FileWriter writer = new FileWriter(fileOut);
											 char[] buffer = new char[1];
											 while (reader.read(buffer) > 0) {
											   writer.write(buffer);
											 }
											 writer.close();
											 }	
										
										
									 
										
										 
										
										
										setErrorMessage("debug file è stato scaricato con successo , "+posizione+""+fileOut);
										flag_continue = true;
								    }
		             
		              
					          } catch (SQLException e) {
						    		    setErrorMessage("Errore nella query...");
						    		    flag_continue = false;
					              
					           
							    		 
							  }
	            	 
	            	 
	             }
	             
	    	 
	    		
	    		
	    	}
	    	else {
	    		 
		    	 
		    	if (checkDbName())  {
		    		//setErrorMessage("URL ... Parametro db  ok");
		    	}
		    	 
		    	if (checkDbValue())  {
		    		//setErrorMessage("URL ... Valore Parametro db ok");
		    	}
		    	if (checkDbName()&& checkDbValue())  {
		    		setErrorMessage("Parametri di connessione presenti , ma nonnessione non riuscita al db ... verificare  [db giu o istanza non valida ]");
		    	}
		    	
		    	
	
	    	}
	    	
	    	 
	    	
	    	
	    	
	    	 MbElement result = outMessage.getRootElement().createElementAsLastChild("JSON").createElementAsLastChild(MbJSON.OBJECT,MbJSON.DATA_ELEMENT_NAME, null).createElementAsLastChild(MbJSON.OBJECT, "result", null);
			 MbElement info = result.createElementAfter(MbJSON.OBJECT, "info", null);
		     info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "status", flag_continue);
		     info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "description", getMessage());
		     
	
		        
			
		      

			
			
			

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
	
	// VERIFICO CHE ESISTA IL PARAMETRO DB, PASSATO NELL'URL
    private boolean checkDbName() {

        	    try{
        	    	if (this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString") == null) {
        				setErrorMessage("URL ... Parametro db mancante");
        				return false;
        			}
        	    	
        	    	if (this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/db") == null) {
        				setErrorMessage("URL ... Parametro db mancante");
        				return false;
        			}
        	    	 
        	    	 
		    	return true;
		    	
				} catch (Exception e) { 
						return false; 
				}
		   
    	
    }
    
    // VERIFICO CHE ESISTA IL PARAMETRO DB, PASSATO NELL'URL
    // in pratica non serve perche gestito nel passo debug00
    private boolean checkDbValue() {

        	    try{
        	    	if (this.inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/db").getValueAsString() == null) {
        				setErrorMessage("URL ... Valore Parametro db mancante");
        				return false;
        			}
        	    	 
		    	return true;
		    	
				} catch (Exception e) { 
						return false; 
				}
		   
    	
    }
    
    private boolean checkConnectionDb() {

	    try{
	    	conn = getJDBCType4Connection(inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/db").getValueAsString(), JDBC_TransactionType.MB_TRANSACTION_AUTO);

    	return true;
    	
		} catch (Exception e) { 
			    setErrorMessage("connessione non valida ");			     
				return false; 
		} 
	    
   

}
    private String getMessage(){
    	return this.errorMessage;
    } 
    
    private void setErrorMessage(String s){
    	this.errorMessage = s;
    }
    
    
 // VERIFICO CHE ESISTA IL PARAMETRO DB, PASSATO NELL'URL
    private boolean checkFileJson(MbMessage inMessage) {

    	try{
    		
    	   MbElement parameter =      inMessage.getRootElement().getFirstElementByPath("/JSON");
    	   if (parameter == null) {
    		    setErrorMessage("File Mancante");
				return false;
    	    
    	   }
    	 
    	   return true;
        }
    	catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
    		return false; 
		}

    }

}
