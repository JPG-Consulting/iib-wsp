import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbMessageAssembly;

import java.util.*;

public class Controlli {

	private String errorMessage;
	private MbMessageAssembly inAssembly;

	/**
	 *  
	 * 
	 */
	public Controlli(MbMessageAssembly inAssembly) {
		this.inAssembly = inAssembly;
		this.errorMessage = "";
	}

	// CONTROLLI FORMALI URL PASSATO
	public boolean getStatusUrlLink() {
		// Controlli URL http passato

		if (checkDbName() == false) {
			return false;
		}

		if (checkDbValue() == false) {
			return false;
		}

		if (checkProcName() == false) {
			return false;
		}

		if (checkProcValue() == false) {
			return false;
		}
		return true;

	}

	// CONTROLLI FORMALI JSON PASSATO
	public boolean getStatusParametri() {
		List<HashMap<String, String>> lhmap = new  ArrayList<HashMap<String, String>>();
		
		String direction ;
    	String type  ;
    	String name  ;
    	String value ;

        MbElement parameter;
		try {
			parameter = inAssembly.getMessage().getRootElement().getFirstElementByPath("/JSON/Data/Parameters/Item");
		
        
        if (parameter != null) {
        	HashMap<String, String> hmap = new HashMap<String, String>();
        	   
        	try {
				direction = parameter.getFirstElementByPath("direction").getValueAsString();
			} catch (Exception e) {
				setErrorMessage("Parametro direction non presente");
				return false;
			}
        	try {
				type = parameter.getFirstElementByPath("type").getValueAsString();
			} catch (Exception e) {
				setErrorMessage("Parametro type non presente");
				return false;
			}
        	try {
				name = parameter.getFirstElementByPath("name").getValueAsString();
			} catch (Exception e) {
				setErrorMessage("Parametro name non presente");
				return false;
			}
        	
        	if(direction.equalsIgnoreCase("in")) {
        		try {
					value = parameter.getFirstElementByPath("value").getValueAsString();
				} catch (Exception e) {
					setErrorMessage("Parametro value non presente per parametro di input");
					return false;
				}
        		hmap.put("value", value);
        	}
         
        	hmap.put("direction", direction);
         	hmap.put("type", type);
         	hmap.put("name", name);   
         	   
         	   
         	lhmap.add(hmap);
        	
            while((parameter = parameter.getNextSibling()) != null) {
            	
            	hmap = new HashMap<String, String>();
            	   
            	try {
    				direction = parameter.getFirstElementByPath("direction").getValueAsString();
    			} catch (Exception e) {
    				setErrorMessage("Parametro direction non presente");
    				return false;
    			}
            	try {
    				type = parameter.getFirstElementByPath("type").getValueAsString();
    			} catch (Exception e) {
    				setErrorMessage("Parametro type non presente");
    				return false;
    			}
            	try {
    				name = parameter.getFirstElementByPath("name").getValueAsString();
    			} catch (Exception e) {
    				setErrorMessage("Parametro name non presente");
    				return false;
    			}
            	
            	if(direction.equalsIgnoreCase("in")) {
            		try {
    					value = parameter.getFirstElementByPath("value").getValueAsString();
    				} catch (Exception e) {
    					setErrorMessage("Parametro value non presente per parametro di input");
    					return false;
    				}
            		hmap.put("value", value);
            	}
             
            	hmap.put("direction", direction);
             	hmap.put("type", type);
             	hmap.put("name", name);
            	   
            	lhmap.add(hmap);
               }
            
           
        }
        
		// Controlli JSON passato
		// Se i controlli falliscono ritorna false
		if (!checkParamDirection(lhmap))
			return false;

		if (!checkParamType(lhmap))
			return false;

		if (!checkParamName(lhmap))
			return false;

		if (!checkParamDirectionValue(lhmap))
			return false;

		} catch (Exception e) {
			setErrorMessage("JSON formalmente errato");
			return false;
		}
		
		return true;

	}

	private void setErrorMessage(String s) {
		this.errorMessage = s;
	}

	public String getMessage() {
		return this.errorMessage;
	}

	// VERIFICO CHE ESISTA IL PARAMETRO DB, PASSATO NELL'URL
	private boolean checkDbName() {

		try {
			if (this.inAssembly.getLocalEnvironment().getRootElement()
					.getFirstElementByPath("/HTTP/Input/QueryString/db") == null) {
				setErrorMessage("URL ... Nome Parametro db mancante ");
				return false;
			}

		} catch (Exception e) {
			setErrorMessage(e.toString());
			return false;
		}
		return true;
	}

	// VERIFICO CHE ESISTA IL VALORE DEL PARAMETRO DB , PASSATO NELL'URL
	private boolean checkDbValue() {
		try {

			if (this.inAssembly.getLocalEnvironment().getRootElement()
					.getFirstElementByPath("/HTTP/Input/QueryString/db")
					.getValueAsString() == null) {
				setErrorMessage("URL ... Valore Parametro db mancante");
				return false;
			}

		} catch (Exception e) {
			setErrorMessage(e.toString());
			return false;
		}
		return true;
	}

	// VERIFICO CHE ESISTA IL PARAMETRO PROC , PASSATO NELL'URL
	private boolean checkProcName() {

		try {

			if (this.inAssembly.getLocalEnvironment().getRootElement()
					.getFirstElementByPath("/HTTP/Input/QueryString/proc") == null) {
				setErrorMessage("URL ... Nome Parametro procedura mancante ");
				return false;
			}

		} catch (Exception e) {
			setErrorMessage(e.toString());
			return false;
		}
		return true;
	}

	// VERIFICO CHE ESISTA IL VALORE DEL PARAMETRO PROC , PASSATO NELL'URL
	private boolean checkProcValue() {

		try {

			if (this.inAssembly.getLocalEnvironment().getRootElement()
					.getFirstElementByPath("/HTTP/Input/QueryString/proc")
					.getValueAsString() == null) {
				setErrorMessage("URL ... Valore Parametro procedura mancante");
				return false;
			}

		} catch (Exception e) {
			setErrorMessage(e.toString());
			return false;
		}
		return true;
	}

	// VERIFICO CHE IL PASAMETRO DIRECTION ABBIA I VALORI CORRETTI , PASSATO NEL
	// JSON
	private boolean checkParamDirection(List<HashMap<String, String>> lhmap) {

		for (HashMap<String, String> m : lhmap) {

			if (!(m.get("direction").equalsIgnoreCase("in")
					|| m.get("direction").equalsIgnoreCase("out") || m.get(
							"direction").equalsIgnoreCase("inout"))) {
				setErrorMessage("Valore parametro Direction non valido");
				return false;
			}

		}

		return true;

	}

	// VERIFICO CHE IL PASAMETRO TYPE ABBIA I VALORI CORRETTI , PASSATO NEL JSON
	private boolean checkParamType(List<HashMap<String, String>> lhmap) {
		for (HashMap<String, String> m : lhmap) {

			if (!(m.get("type").equalsIgnoreCase("integer")
					|| m.get("type").equalsIgnoreCase("string") || m
					.get("type").equalsIgnoreCase("double"))) {
				setErrorMessage("Valore parametro type non valido");
				return false;
			}

		}

		return true;
	}

	// VERIFICO CHE IL PASAMETRO NAME ABBIA I VALORI CORRETTI , PASSATO NEL JSON
	private boolean checkParamName(List<HashMap<String, String>> lhmap) {

		for (HashMap<String, String> m : lhmap) {

			if ((m.get("name").equalsIgnoreCase("null") || m.get("name") == null)) {
				setErrorMessage("Valore parametro nome non valido");
				return false;
			}
		}

		return true;

	}

	// VERIFICO CHE L'ACCOPPIATA DIRECTION VALORE SIA CORRETTO
	private boolean checkParamDirectionValue(List<HashMap<String, String>> lhmap) {

		for (HashMap<String, String> m : lhmap) {

			if ((m.get("direction").equalsIgnoreCase("in") && m.get("value") == null)) {
				setErrorMessage("Parametro con direzione IN non valorizzato");
				return false;
			}

		}

		return true;

	}
	// AGGIUNGERE DI SEGUITO EVENTUALI CONTROOLI AGGIUNTIVI

}
