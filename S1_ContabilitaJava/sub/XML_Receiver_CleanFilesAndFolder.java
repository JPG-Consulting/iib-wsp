package sub;

import java.io.File;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class XML_Receiver_CleanFilesAndFolder extends MbJavaComputeNode {

	private Boolean deletedZip;
	private Boolean deletedFolder;

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();

		// create new empty message
		MbMessage outMessage = new MbMessage(inMessage);
		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly,
				outMessage);
		
		try {
			// optionally copy message headers
			//copyMessageHeaders(inMessage, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			deletedZip = true;
			deletedFolder = true;
			String path = inMessage.getRootElement().getFirstElementByPath("/JSON/Data/percorso").getValueAsString();
			String fileName = inMessage.getRootElement().getFirstElementByPath("/JSON/Data/nomeFile").getValueAsString();
			File d = new File(path+File.separator+fileName);
			if (d.exists()) {
				deletedZip = d.delete();
			}
			File unzipFolder = new File(path + "unzip" + fileName.substring(0, fileName.indexOf(".zip")));
			if (unzipFolder.exists() && unzipFolder.isDirectory()) {
				String[] files = unzipFolder.list();
				for (String s : files) {
					File del = new File(unzipFolder + File.separator + s);
					deletedFolder = del.delete();
				}
				deletedFolder = unzipFolder.delete();
			}
			if (deletedFolder = false) throw new Exception("Non è stato possibile eliminare la cartella temporanea di unzip "+ unzipFolder);
			if (deletedZip = false) throw new Exception("Non è stato possibile eliminare il file "+ d);

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

	public void copyMessageHeaders(MbMessage inMessage, MbMessage outMessage)
			throws MbException {
		MbElement outRoot = outMessage.getRootElement();

		// iterate though the headers starting with the first child of the root
		// element
		MbElement header = inMessage.getRootElement().getFirstChild();
		while (header != null && header.getNextSibling() != null) // stop before
																	// the last
																	// child
																	// (body)
		{
			// copy the header and add it to the out message
			outRoot.addAsLastChild(header.copy());
			// move along to next header
			header = header.getNextSibling();
		}
	}

}
