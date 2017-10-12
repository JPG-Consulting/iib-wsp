package sub;

import java.io.File;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class TCR_Sender extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		
		String localFolder = inAssembly.getLocalEnvironment().getRootElement()
				.getFirstElementByPath("/XMLNSC/Variables/localFolder")
				.getValueAsString();
		String archivePathFolder = inAssembly.getLocalEnvironment()
				.getRootElement()
				.getFirstElementByPath("/XMLNSC/Variables/archivePath")
				.getValueAsString();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			File dir = new File(localFolder);

			// lists files and directories in the current working directory
			File[] files = dir.listFiles();
			if (files == null)
				throw new MbUserException(
						"Nessun file presente nella directory",
						dir.getCanonicalPath(), null, null, null, null);
			else
				alt.propagate(outAssembly);
			String fileName = null;
			outAssembly
					.getLocalEnvironment()
					.getRootElement()
					.getFirstElementByPath("Destination")
					.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
							"File", null)
					.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
							"Name", fileName)
					.createElementAfter(MbElement.TYPE_NAME_VALUE, "Directory",
							localFolder);
			outAssembly
					.getLocalEnvironment()
					.getRootElement()
					.getFirstElementByPath("Destination/File")
					.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
							"Archive", null)
					.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,
							"Directory", archivePathFolder);
			
			for (File file : files) {
				MbMessage outMessage2 = new MbMessage();
				outAssembly = new MbMessageAssembly(inAssembly, outMessage2);

				fileName = file.getName();
				outAssembly.getLocalEnvironment().getRootElement()
						.getFirstElementByPath("Destination/File/Name")
						.setValue(fileName);
				outAssembly
						.getLocalEnvironment()
						.getRootElement()
						.getFirstElementByPath(
								"Destination/File/Archive/Directory")
						.setValue(archivePathFolder);
				
				out.propagate(outAssembly);
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
			// Example handling ensures all exceptions are re-thrown to be
			// handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}

	}

}
