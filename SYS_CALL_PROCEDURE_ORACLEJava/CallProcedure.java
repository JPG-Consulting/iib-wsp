import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbJSON;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import java.util.*;


public class CallProcedure extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		//MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		// inserimento chiamata alla classe controlli formali !!!
	
		try {			
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage();
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			
			//CONTROLLI SULL'URL PASSATO !!!
			Controlli st = new Controlli(inAssembly);
			if (st.getStatusUrlLink()){

		    Connection conn = getJDBCType4Connection(inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/db").getValueAsString(), JDBC_TransactionType.MB_TRANSACTION_AUTO);			
		    CallableStatement cs;

			//COMMENTO
		    List<HashMap <String, String>> lhmap = new  ArrayList<HashMap<String, String>>();
     
			String direction ;
        	String type  ;
        	String name  ;
        	String value ;
        	
        	String n_Interrogativi ="";
            
           //CONTROLLI SUI PARAMETRI PASSATI, NEL JSON !!!           
            if (!st.getStatusParametri()){

            	System.out.println("in fase di debug!!!");
            	MbElement result = outMessage.getRootElement().createElementAsLastChild("JSON").createElementAsLastChild(MbJSON.OBJECT,MbJSON.DATA_ELEMENT_NAME, null).createElementAsLastChild(MbJSON.OBJECT, "result", null);
				MbElement info = result.createElementAfter(MbJSON.OBJECT, "info", null);
		        info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "status", false);
		        info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "description", st.getMessage());

            } else {
            	MbElement parameter = inMessage.getRootElement().getFirstElementByPath("/JSON/Data/Parameters/Item");
                
                if (parameter != null) {
                	HashMap<String, String> hmap = new HashMap<String, String>();
                	   
                	direction = parameter.getFirstElementByPath("direction").getValueAsString();
                	type = parameter.getFirstElementByPath("type").getValueAsString();
                	name = parameter.getFirstElementByPath("name").getValueAsString();
                	
                	if(direction.equalsIgnoreCase("in")) {
                		value = parameter.getFirstElementByPath("value").getValueAsString();
                		hmap.put("value", value);
                	}
                 
                	hmap.put("direction", direction);
    	         	hmap.put("type", type);
    	         	hmap.put("name", name);   
    	         	   
    	         	lhmap.add(hmap);
    	         	   
    	         	n_Interrogativi = n_Interrogativi + "?";
                	
    	            while((parameter = parameter.getNextSibling()) != null) {
    	            	
    	            	hmap = new HashMap<String, String>();
    	            	   
    	               	direction = parameter.getFirstElementByPath("direction").getValueAsString();
    	               	type = parameter.getFirstElementByPath("type").getValueAsString();
    	               	name = parameter.getFirstElementByPath("name").getValueAsString();
    	               	
    	               	if(direction.equalsIgnoreCase("in")) {
    	               		value = parameter.getFirstElementByPath("value").getValueAsString();
    	               		hmap.put("value", value);
    	               	}
    	                
    	               	hmap.put("direction", direction);
    	   	         	hmap.put("type", type);
    	   	         	hmap.put("name", name);
    	            	   
    	            	lhmap.add(hmap);
    	            	   
    	            	n_Interrogativi = n_Interrogativi+",?";
    	               
    	            }
    	           
                }
                
            	cs=conn.prepareCall("{call "+ inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/proc").getValueAsString() +"("+n_Interrogativi+")}");
						        
		        for(HashMap<String, String> m : lhmap){
						
		        	switch (m.get("direction")) {
	
		        	case "in" :
		        		 
		        	     System.out.println("direction in.");
		        	     if (m.get("type").equalsIgnoreCase("string")) {
		        	    	 cs.setString(m.get("name"),m.get("value"));	                     
		                 }
		        	     if (m.get("type").equalsIgnoreCase("integer")) {
		        	    	 cs.setInt(m.get("name"),Integer.parseInt(m.get("value")));	                     
		                 }
		        	     if (m.get("type").equalsIgnoreCase("double")) {
		        	    	 cs.setDouble(m.get("name"),Double.parseDouble(m.get("value")));	                     
		                 }
		        	     
		        	     break;
	
		        	case "out" :
		        	 
		        		System.out.println("direction out.");	        		
		        		 if (m.get("type").equalsIgnoreCase("string")) {
		        			 cs.registerOutParameter(m.get("name"), Types.VARCHAR );                     
		                 }
		        	     if (m.get("type").equalsIgnoreCase("integer")) {
		        	    	 cs.registerOutParameter(m.get("name"), Types.NUMERIC );	        	    	 	                     
		                 }
		        	     if (m.get("type").equalsIgnoreCase("double")) {
		        	    	 cs.registerOutParameter(m.get("name"), Types.NUMERIC );    
		                 }
	
		        	     break;
	
		        	case "inout" :
		        		 System.out.println("direction in/out.");
		        		 if (m.get("type").equalsIgnoreCase("string")) {
		        			 cs.setString(m.get("name"),m.get("value"));
		        			 cs.registerOutParameter(m.get("name"),Types.VARCHAR );
		                     
		                 }
		        	     if (m.get("type").equalsIgnoreCase("integer")) {
		        	    	 cs.registerOutParameter(m.get("name"),Types.NUMERIC  );
		        	    	 cs.setInt(m.get("name"),Integer.parseInt(m.get("value")));
		        	    	                 
		                 }
		        	     if (m.get("type").equalsIgnoreCase("double")) {
		        	    	 cs.registerOutParameter(m.get("name"),Types.NUMERIC );
		        	    	 cs.setDouble(m.get("name"),Double.parseDouble(m.get("value")));         
		                 }
		        		  	        	     
		        	     break;
	
		        	default:
		        	     System.out.println("direction da definire");
		        	     
		        	    
		        	}       		        	
		        }
	
		        cs.execute();
		        
	
		         
				MbElement result = outMessage.getRootElement().createElementAsLastChild("JSON").createElementAsLastChild(MbJSON.OBJECT,MbJSON.DATA_ELEMENT_NAME, null).createElementAsLastChild(MbJSON.OBJECT, "result", null);
		        for(HashMap<String, String> m : lhmap){
		        	if(m.get("direction").equalsIgnoreCase("out") || m.get("direction").equalsIgnoreCase("inout")){
		        		if (m.get("type").equalsIgnoreCase("string")) {
		        			 result.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, m.get("name"), cs.getString(m.get("name")));	                     
		                 }
		        	     if (m.get("type").equalsIgnoreCase("integer")) {        	    	 
		        	    	 result.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, m.get("name"), cs.getInt(m.get("name")));	        	    	                 
		                 }
		        	     if (m.get("type").equalsIgnoreCase("double")) {
		        	    	 result.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, m.get("name"), cs.getDouble(m.get("name")));        
		                 }	        		
		        	}
		        }
		        MbElement info = result.createElementAfter(MbJSON.OBJECT, "info", null);
		        info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "status", true);
		        info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "description", "OK");
							        
            }
	        
			} else {
				MbElement result = outMessage.getRootElement().createElementAsLastChild("JSON").createElementAsLastChild(MbJSON.OBJECT,MbJSON.DATA_ELEMENT_NAME, null).createElementAsLastChild(MbJSON.OBJECT, "result", null);
				MbElement info = result.createElementAfter(MbJSON.OBJECT, "info", null);
		        info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "status", false);
		        info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "description", st.getMessage());
			}

			// End of user code
			// ----------------------------------------------------------
		
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



}
