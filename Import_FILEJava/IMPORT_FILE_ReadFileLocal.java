import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;


public class IMPORT_FILE_ReadFileLocal extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		String LocalPath = "/ibmdata/data/tmp";
		String Path = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PathIn/PathIn").getValueAsString();
		String ServerOUT = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/ServOUT").getValueAsString();
		String FILE = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/File").getValueAsString();

		
		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			
			String PathArch = null;

			MbElement jsonData = outMessage.getRootElement().createElementAsLastChild(MbJSON.PARSER_NAME).createElementAsLastChild(MbJSON.ARRAY,MbJSON.DATA_ELEMENT_NAME, null);
			
			File[] filesInDirectory = new File(Path).listFiles();
	
			int d = filesInDirectory.length;
			int N = 0;
			
			 for(int i=0; i<filesInDirectory.length; i++){
				 
				 if (filesInDirectory[i].isFile() && filesInDirectory[i].getName().contains(FILE))
				 {
				 try {
					 

			         String filenameIN = Path +"/"+ filesInDirectory[i].getName();

					File  theFile = new File (filenameIN);
			         
			         byte[] bFile = new byte[(int) theFile.length()];
					  

					  FileInputStream fis = new FileInputStream(theFile);
					  fis.read(bFile); //read file into bytes[]
					  fis.close();
					  
			         String filenameOut = LocalPath +"/"+ filesInDirectory[i].getName();

					  
					 FileOutputStream fos = new FileOutputStream(filenameOut);
					 fos.write(bFile);
					 fos.close();
					 
	
				     if (!inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PathArch").getValueAsString().isEmpty() ){
				    	 PathArch =  inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PathArch").getValueAsString();
				         String filenameARCH = PathArch +"/"+ filesInDirectory[i].getName();		
				         theFile.renameTo(new File (filenameARCH));
				     }
					 
					 MbElement jsonArrayItem =  jsonData.createElementAsLastChild(MbElement.TYPE_NAME, MbJSON.ARRAY_ITEM_NAME, null).createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"nomeFile",filesInDirectory[i].getName()).createElementAfter(MbElement.TYPE_NAME_VALUE, "percorso", LocalPath).createElementAfter(MbElement.TYPE_NAME_VALUE, "ServerOUT", ServerOUT);		     

					 N = 1;
					 
				 } catch (Exception e) {
						throw new MbUserException(this, "evaluate()", "", "", e.toString(),	null);
					}
				 

			 }
				 
			 }
			 
			 if (N != 0) {
				 out.propagate(outAssembly);
			 }
			 else{
				 alt.propagate(outAssembly);
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
