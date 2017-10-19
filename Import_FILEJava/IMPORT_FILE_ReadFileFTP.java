import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;


public class IMPORT_FILE_ReadFileFTP extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");
		MbMessage inMessage = inAssembly.getMessage();
	
		String LocalPath = "/ibmdata/data/tmp";
		String ftpPath = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PathIn").getValueAsString();
		String ftpUsername = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/Utente").getValueAsString();
		String ftpPassword = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PWD").getValueAsString();
		String ftpHost = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/Server").getValueAsString();
		String ServerOUT = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/ServOUT").getValueAsString();
		String FILE = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/File").getValueAsString();
		String ServerIN = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/ServIN").getValueAsString();
		
		FTPClient ftpClient = new FTPClient();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			
			InputStream temp1 ;
			boolean okFile   = false;
			
			MbElement jsonData = outMessage.getRootElement().createElementAsLastChild(MbJSON.PARSER_NAME).createElementAsLastChild(MbJSON.ARRAY,MbJSON.DATA_ELEMENT_NAME, null);			
			ftpClient.setConnectTimeout(120000);
			ftpClient.connect(ftpHost, 21);			
			ftpClient.login(ftpUsername, ftpPassword);	
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			FTPFile[] files = ftpClient.listFiles(ftpPath);					
			int N = 0;
			 for(int i=0; i<files.length; i++){				 
				 if (files[i].isFile() && files[i].getName().contains(FILE) ){
				     OutputStream outputStream2 = null;
				     InputStream inputStream    = null;
				     try{  						 						 
						 String remoteFile2 = ftpPath + File.separator +files[i].getName();
			             File downloadFile2 = new File(LocalPath + File.separator + files[i].getName());
				         outputStream2 = new FileOutputStream(downloadFile2);
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
					     if (!inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PathArch").getValueAsString().isEmpty() ){
					    	 String PathArch =  inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PathArch").getValueAsString();
					    	 okFile = ftpClient.rename(ftpPath + File.separator + files[i].getName() , PathArch + File.separator + files[i].getName());
					         if (okFile == false){ // se è false vuol dire che il file è già presente nella cartella di backup
					            	okFile = ftpClient.deleteFile( PathArch + File.separator + files[i].getName()); // cancello il file per poterlo ricopiare
					            	if (okFile == false){
					    			  ftpClient.logout();
					     			  ftpClient.disconnect();
					            	  throw new Exception("Invio alla directory Archivio fallito ");
					            	}else
					            		okFile = ftpClient.rename(ftpPath + File.separator + files[i].getName(),  PathArch + File.separator + files[i].getName() );	            	
					          }	  
					     }
						 MbElement jsonArrayItem =  jsonData.createElementAsLastChild(MbElement.TYPE_NAME, MbJSON.ARRAY_ITEM_NAME, null).createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"nomeFile",files[i].getName()).createElementAfter(MbElement.TYPE_NAME_VALUE, "percorso", LocalPath).createElementAfter(MbElement.TYPE_NAME_VALUE, "ServerOUT", ServerOUT).createElementAfter(MbElement.TYPE_NAME_VALUE, "ServerIN", ServerIN);		     
	
						 N = 1;
					}catch(Exception e){						 
						 if (inputStream != null)
							 inputStream.close();
						 if (outputStream2 != null)
							 outputStream2.close();				 
						 throw e;
					 }  
				 }				
			 }
			 
		    ftpClient.logout();
			ftpClient.disconnect();
			 
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
			e.printStackTrace();
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			e.printStackTrace();
			throw e;
		} catch (Exception e) {			
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			e.printStackTrace();
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}finally{
			if(ftpClient.isConnected()) {
		        try {
		        	ftpClient.disconnect();
		        } catch(Exception e) {
					throw new MbUserException(this, "evaluate()", "", "", e.toString(),
							null);
		        }
			}      
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal


	}

}
