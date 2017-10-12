import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;


public class sleepAndMove extends MbJavaComputeNode {

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
			
			MbElement localEnv = inAssembly.getLocalEnvironment().getRootElement();		
			inAssembly.getGlobalEnvironment().getRootElement().createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "codicePraticaHub", localEnv.getFirstElementByPath("/File/Read/Name").getValueAsString());
			inAssembly.getGlobalEnvironment().getRootElement().createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "codicePratica", inMessage.getRootElement().getFirstElementByPath("/XMLNSC").getFirstChild().getFirstElementByPath("IdentificativiRichiesta/CP_UTENTE").getValueAsString());
			File filePointer = new File(localEnv.getFirstElementByPath("/File/Read/Directory").getValueAsString(), localEnv.getFirstElementByPath("/File/Read/Name").getValueAsString());
			Path archiveFolder = filePointer.toPath().getParent().resolve("ACQUISITE");
			if (Files.isDirectory(archiveFolder)) {
				//Thread.sleep(120 * 1000);				
				//Thread.sleep(1 * 1000);
				//File archivedFile = archiveFolder.resolve(filePointer.getName()).toFile();
				//if(!filePointer.renameTo(archiveFolder.resolve(archivedFile.toPath())) throw new Exception();
				Files.copy(filePointer.toPath(), archiveFolder.resolve(filePointer.getName()), StandardCopyOption.REPLACE_EXISTING);
				filePointer.delete();
				//Files.move(filePointer.toPath(), archiveFolder.resolve(filePointer.getName()), StandardCopyOption.REPLACE_EXISTING);
			} else {
				throw new MbUserException("","","","", null, null);
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
