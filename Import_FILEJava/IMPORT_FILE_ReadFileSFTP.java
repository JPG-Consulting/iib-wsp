import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Vector;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelSftp.LsEntry;


public class IMPORT_FILE_ReadFileSFTP extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");
		
		
		String sftpHost = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/Server").getValueAsString();
		String sftpUsername = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/Utente").getValueAsString();
		String sftpPassword = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PWD").getValueAsString();
		String sftpPath = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PathIn").getValueAsString();
		String sftpPort = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PORT").getValueAsString();
		String ServerOUT = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/ServOUT").getValueAsString();
		String LocalPath = "/ibmdata/data/tmp";
//		String sftpFileName = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/filename").getValueAsString();

		
		Session session = null;
		ChannelSftp sftp = null;
		String FILE = null;


		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage();
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
		     InputStream stream;
		     
		     if (!inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/File").getValueAsString().isEmpty() ){
		    	 FILE =  ("*");
		    	 FILE = FILE.concat(inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/File").getValueAsString());
		    	 FILE =  FILE.concat("*");
		     } else  {
		    	 FILE =  ("*");
		     }
		     
	     
		     
			
		MbElement jsonData = outMessage.getRootElement().createElementAsLastChild(MbJSON.PARSER_NAME).createElementAsLastChild(MbJSON.ARRAY,MbJSON.DATA_ELEMENT_NAME, null);

		     
			//Connessione SFTP
			JSch jsch = new JSch();
			session = jsch.getSession(sftpUsername, sftpHost,Integer.parseInt(sftpPort));
			session.setConfig("PreferredAuthentications", "password");
			session.setPassword(sftpPassword);

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect(500);
			Channel channel = session.openChannel("sftp");
			sftp = (ChannelSftp) channel;
			sftp.connect(500);
			
			 sftp.cd(sftpPath);
			 
			 Vector<LsEntry> files = sftp.ls(FILE);

			int N = 0;
			 
			 if (!files.isEmpty() ) {
			 for(ChannelSftp.LsEntry entry : files)
			 {
				 
				 if (entry.getFilename().contains(".")) {
					 
				 
				 try {	

					
		         String filename = LocalPath +"/"+ entry.getFilename();
				 stream =  sftp.get(entry.getFilename());
				 
				 //ByteArrayOutputStream Bout = new ByteArrayOutputStream();
				 FileOutputStream fos = new FileOutputStream(filename);
				 byte[] buf = new byte[4096];
				 int n = 0;
				 while (-1!=(n=stream.read(buf)))
					 {
						 fos.write(buf, 0, n);
					 }	

				 fos.close();
				
			     if (!inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PathArch").getValueAsString().isEmpty() ){
			    	 String PathArch =  inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PathArch").getValueAsString();
			    	 
			    	 try {
			    	 sftp.rename(sftpPath+"/"+entry.getFilename(), PathArch+"/"+entry.getFilename());
			    	 } catch (Exception e) {
			    		 sftp.rm(PathArch+"/"+entry.getFilename());
			    		 sftp.rename(sftpPath+"/"+entry.getFilename(), PathArch+"/"+entry.getFilename());
			    		 }
			     }

				 
				 MbElement jsonArrayItem =  jsonData.createElementAsLastChild(MbElement.TYPE_NAME, MbJSON.ARRAY_ITEM_NAME, null).createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"nomeFile",entry.getFilename()).createElementAfter(MbElement.TYPE_NAME_VALUE, "percorso", LocalPath).createElementAfter(MbElement.TYPE_NAME_VALUE, "ServerOUT", ServerOUT);		     

				 N = 1;


				 } catch (Exception e) {
						throw new MbUserException(this, "evaluate()", "", "", e.toString(),	null);
						}

				 stream.close();	
			
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
