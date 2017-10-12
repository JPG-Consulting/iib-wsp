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
import com.ibm.broker.plugin.MbXMLNSC;

public class CARICA_MISURE_getFilesFTP extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		String ftpHost = inAssembly.getGlobalEnvironment().getRootElement()
				.getFirstElementByPath("/Variables/ftphost")
				.getValueAsString();
		String ftpUsername = inAssembly.getGlobalEnvironment().getRootElement()
				.getFirstElementByPath("/Variables/ftpusername")
				.getValueAsString();
		String ftpPassword = inAssembly.getGlobalEnvironment().getRootElement()
				.getFirstElementByPath("/Variables/ftppassword")
				.getValueAsString();
		String inputPathFtp = inAssembly.getGlobalEnvironment().getRootElement()
				.getFirstElementByPath("/Variables/inputpathftp")
				.getValueAsString();
		String okPathFtp = inAssembly.getGlobalEnvironment().getRootElement()
				.getFirstElementByPath("/Variables/okPathFtp")
				.getValueAsString();
		String koPathFtp = inAssembly.getGlobalEnvironment()
				.getRootElement()
				.getFirstElementByPath("/Variables/koPathFtp")
				.getValueAsString();

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
			

			// lists files and directories in the current working directory
			FTPFile[] files = ftpClient.listFiles(inputPathFtp);
			for (FTPFile file : files) {
				String fileName = null;
				outMessage = new MbMessage();
				outAssembly = new MbMessageAssembly(outAssembly, outMessage);
				outAssembly.getLocalEnvironment().getRootElement().delete();
					fileName = file.getName();
					outAssembly.getLocalEnvironment()
							.getRootElement()
							.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Destination", null)
							.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "File", null)
							.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Name", fileName)
							//.createElementAfter(MbElement.TYPE_NAME_VALUE, "Directory", inputPathFtp)
							.createElementAfter(MbElement.TYPE_NAME_VALUE, "Remote", null)
							.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "ServerDirectory", inputPathFtp);
					out.propagate(outAssembly);

			}
			ftpClient.logout();
			ftpClient.disconnect();
			outMessage = new MbMessage();
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			outMessage.getRootElement().createElementAsFirstChild(MbXMLNSC.PARSER_NAME).createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "Stats", null).createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "Totale", files.length);
			alt.propagate(outAssembly);
			
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
