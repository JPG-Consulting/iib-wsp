import java.io.File;

import org.apache.commons.net.ftp.FTPClient;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;

public class CARICA_MISURE_JavaPutftpfs extends MbJavaComputeNode {

	// LOGICA QUESTA CLASSE HA IL COMPITO DI LEGGERE LA VARIABILE GLOBALE ,
	// CONTENETE IL NOME DEL CHIAMANTE ED IN BASE AD ESSO SPOSTA IL FILE NELLA
	// DIR APPROPRIATA.

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;

		String path = "";
		String chiamante = "";
		String fileName = "";

		String ftpHost = inAssembly.getMessage().getRootElement()
				.getFirstElementByPath("/XMLNSC/File/ftphost")
				.getValueAsString();

		String ftpUsername = inAssembly.getMessage().getRootElement()
				.getFirstElementByPath("/XMLNSC/File/ftpusername")
				.getValueAsString();
		String ftpPassword = inAssembly.getMessage().getRootElement()
				.getFirstElementByPath("/XMLNSC/File/ftppassword")
				.getValueAsString();
		String inputPathFtp = inAssembly.getMessage().getRootElement()
				.getFirstElementByPath("/XMLNSC/File/InputDir")
				.getValueAsString();

		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);

			// ----------------------------------------------------------
			// ----------------------------------------------------------
			// Add user code below
			FTPClient ftpClient = new FTPClient();
			ftpClient.connect(ftpHost, 21);
			ftpClient.login(ftpUsername, ftpPassword);

			chiamante = inAssembly.getGlobalEnvironment().getRootElement()
					.getFirstElementByPath("/Variables/chiamante")
					.getValueAsString();
			fileName = inAssembly.getMessage().getRootElement()
					.getFirstElementByPath("/XMLNSC/File/Name")
					.getValueAsString();

			if (chiamante.equals("call_procedure")) {

				path = inAssembly.getMessage().getRootElement()
						.getFirstElementByPath("/XMLNSC/File/OkFilePath")
						.getValueAsString();

			} else {

				path = inAssembly.getMessage().getRootElement()
						.getFirstElementByPath("/XMLNSC/File/KoFilePath")
						.getValueAsString();

			}

			ftpClient.rename(inputPathFtp + File.separator + fileName, path
					+ File.separator + fileName);

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
			// Example handling ensures all exceptions are re-thrown to be
			// handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

}
