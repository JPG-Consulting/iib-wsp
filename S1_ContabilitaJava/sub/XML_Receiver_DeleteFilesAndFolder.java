package sub;

import java.io.File;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class XML_Receiver_DeleteFilesAndFolder extends MbJavaComputeNode {
	private Boolean deletedFolder;
	private Boolean deletedZip;

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		//MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			MbElement leafFolder = null;
			leafFolder = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Variables/Cartella");
			if (leafFolder != null) {
				String folderName = leafFolder.getValueAsString();
				File folder = new File(folderName);
				if (folder.isDirectory()) {
					File[] files = folder.listFiles();
					for(File f : files) {
						deletedFolder = f.delete();
					}
					
					deletedFolder = folder.delete();
				}
			}
			File zipFile = new File(inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Variables/NomeFile").getValueAsString());
			if (zipFile.exists()) deletedFolder = zipFile.delete();
			
			if (deletedFolder = false) throw new Exception("Non è stato possibile eliminare la cartella temporanea di unzip "+ leafFolder.getValueAsString());
			if (deletedZip = false) throw new Exception("Non è stato possibile eliminare il file "+ zipFile.getAbsolutePath());

			

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
