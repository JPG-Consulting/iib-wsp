package sub;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTP;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import sun.net.ftp.FtpClient;

public class EDI_Sender_FTP extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out"); 
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage  = inAssembly.getMessage();
		String ftpHost       = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/ftphost").getValueAsString();
		String ftpUsername   = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/ftpusername").getValueAsString();
		String ftpPassword   = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/ftppassword").getValueAsString();
		String inputPathFtp  = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/inputpathftp_edi").getValueAsString();
		String localFolder   = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/localFolder_edi").getValueAsString();
		String okPathFolder  = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/okPathFtp_edi").getValueAsString();
		String koPathFtp_edi = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/errorPathFtp_edi").getValueAsString();
		String dataReg       = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/dataRegistrazione").getValueAsString();
		String societa       = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/HTTP/Input/QueryString/societa").getValueAsString();
		boolean okFile   = false;
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);		
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			FTPClient ftpClient = new FTPClient();
			ftpClient.connect(ftpHost, 21);
			ftpClient.login(ftpUsername, ftpPassword);
			alt.propagate(outAssembly);
			
			// lists files and directories in the current working directory									
			FTPFile[] files = ftpClient.listFiles(inputPathFtp);					
			String fileName = null;			
			for (FTPFile file : files) {
			MbMessage outMessage2 = new MbMessage();		
			outAssembly = new MbMessageAssembly(inAssembly, outMessage2);
		    OutputStream outputStream2 = null;
		    InputStream inputStream    = null;
			 try{	
				fileName =  file.getName();			    
				ftpClient.enterLocalPassiveMode();
	            String remoteFile2 = inputPathFtp + File.separator +fileName;
	            File downloadFile2 = new File(localFolder + File.separator + fileName);
	            outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
	            inputStream = ftpClient.retrieveFileStream(remoteFile2);
	            byte[] bytesArray = new byte[4096];
	            int bytesRead = -1;
	            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
	                outputStream2.write(bytesArray, 0, bytesRead);
	            }
	 
	            okFile = ftpClient.completePendingCommand();
	         
	            outputStream2.close();
	            inputStream.close();
                if (okFile == false){
                	throw new Exception("Invio alla directory Locale fallito ");
                }			                                   
                // sposto il file nella cartella di archivio                          
                okFile = ftpClient.rename(inputPathFtp + fileName, okPathFolder + fileName );
                if (okFile == false){
                	throw new Exception("Invio alla directory Archivio fallito ");
                }
			    outMessage2.getRootElement().createElementAsLastChild(MbJSON.PARSER_NAME).createElementAsLastChild(MbJSON.OBJECT,MbJSON.DATA_ELEMENT_NAME,null).createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"nomeFile",fileName).createElementAfter(MbElement.TYPE_NAME_VALUE, "percorso", localFolder).createElementAfter(MbElement.TYPE_NAME_VALUE,"dataRegistrazione",dataReg).createElementAfter(MbElement.TYPE_NAME_VALUE,"societa",societa);		     
			    out.propagate(outAssembly);
			 }catch(Exception e){
				 ftpClient.rename(inputPathFtp + fileName, koPathFtp_edi + fileName );
				 if (inputStream != null)
					 inputStream.close();
				 if (outputStream2 != null)
					 outputStream2.close();				 
				 throw new Exception("Invio alla directory Locale fallito ");
			 }  
			}
		    ftpClient.logout();
			ftpClient.disconnect();
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
		//out.propagate(outAssembly);

	}

}
