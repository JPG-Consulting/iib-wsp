package sub;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


public class SFTP_InsertSFTP extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		String sftpPath = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PATHOUT").getValueAsString();
		String sftpHost = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/Server").getValueAsString();
		String sftpUsername = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/Utente").getValueAsString();
		String sftpPassword = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PWD").getValueAsString();
		String sftpPort = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/PORT").getValueAsString();

		Session session = null;
		ChannelSftp sftp = null;
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
			String NewFile = sftpPath +"/"+ FileName; 
				
			File  theFile = new File (LocFile);
	         
	         byte[] bFile = new byte[(int) theFile.length()];
	         
			  FileInputStream fis = new FileInputStream(theFile);
			  fis.read(bFile); //read file into bytes[]
			  fis.close();
			  
			  ByteArrayInputStream BAI = new ByteArrayInputStream(bFile); 

        
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
         
				sftp.put(BAI, NewFile);
				
				sftp.disconnect();	
				
				 theFile.delete();
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
