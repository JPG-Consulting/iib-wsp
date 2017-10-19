import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;


public class FTP_CopyFileFTP extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate"); 
		MbOutputTerminal fail = getOutputTerminal("failure"); 
		
		String ftpPath = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PATHOUT").getValueAsString();
		String ftpHost = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/Server").getValueAsString();
		String ftpUsername = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/Utente").getValueAsString();
		String ftpPassword = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PWD").getValueAsString();
		String ftpPort = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PORT").getValueAsString();


		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			
			MbElement	tmp = inMessage.getRootElement().getLastChild().getLastChild().getFirstChild(); //getFirstElementByPath("/JSON/Data/Item");
			MbElement rec ;
			
			while (tmp != null) {
				
			rec = tmp.getFirstChild();			 
			 
			String FileName = rec.getValueAsString();
			String LocalPath =	rec.getNextSibling().getValueAsString();	
			
			String LocFile = LocalPath +"/"+ FileName; 
			String NewFile = ftpPath +"/"+ FileName; 
				
			File  theFile = new File (LocFile);
	         
	         byte[] bFile = new byte[(int) theFile.length()];
	         
			  FileInputStream fis = new FileInputStream(theFile);
			  fis.read(bFile); //read file into bytes[]
			  fis.close();
			  
			  ByteArrayInputStream BAI = new ByteArrayInputStream(bFile); 

			  
				FTPClient ftpClient = new FTPClient();
				ftpClient.connect(ftpHost, 21);
				ftpClient.login(ftpUsername, ftpPassword);
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				alt.propagate(outAssembly);

		
		        
		        boolean OKfile = ftpClient.storeFile(NewFile, BAI);

				ftpClient.sendSiteCommand("chmod " + "666 " + NewFile); 
				
				
			    ftpClient.logout();
				ftpClient.disconnect();

				boolean deleteFile = theFile.delete();
				 tmp = tmp.getNextSibling();
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
