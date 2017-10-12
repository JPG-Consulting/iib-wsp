import java.io.File;
import java.io.FilenameFilter;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;


public class Neta_HUB_APR_fileLoop extends MbJavaComputeNode {

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
			// ----------------------------------------------------------
			// Add user code below
			
			File dir = new File("/ibmdata/WINSHARE/NETA/SII/HUB/scambi_xml/SII/OUTBOUND/xml/APR_0050");
			if (dir.isDirectory()) {
				String[] files = dir.list(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						String upperName = name.toUpperCase();
						if (upperName.endsWith(".XML")) {
							return true;
						} else {
						return false;
						}
					}
				});
				
				for (String s : files) {
					outAssembly.getLocalEnvironment().getRootElement().createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Destination", null).createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "File", null).createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Name", s);
					outAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Destination/File").createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "Directory", dir.getAbsolutePath());
					out.propagate(outAssembly);
				}
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
