package sub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXMLNSC;


public class XML_Receiver_Unzip extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();

		// create new empty message
		MbMessage outMessage = new MbMessage();
		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly,
				outMessage);

		try {
			// optionally copy message headers
			copyMessageHeaders(inMessage, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			String fileName = inMessage.getRootElement().getFirstElementByPath("/JSON/Data/nomeFile").getValueAsString();
			String societa = inMessage.getRootElement().getFirstElementByPath("/JSON/Data/societa").getValueAsString();
			String dataRegistrazione = inMessage.getRootElement().getFirstElementByPath("/JSON/Data/dataRegistrazione").getValueAsString();
			String path = inMessage.getRootElement().getFirstElementByPath("/JSON/Data/percorso").getValueAsString();
			String InputZipFile = path + File.separator + fileName;
			String OutputFolder = path + "unzip" + File.separator + fileName.substring(0, fileName.indexOf(".zip"));
			
			byte[] buffer = new byte[1024];
			
		    //create output directory if not exists
		    File folder = new File(OutputFolder);
		    if(!folder.exists()){
		    	folder.mkdir();
		    }
		 
		    //get the zip file content
		    ZipInputStream zis = 
		    	new ZipInputStream(new FileInputStream(InputZipFile));
		    //get the zipped file list entry
		    ZipEntry ze = zis.getNextEntry();
		    while(ze!=null){
		 
		    	String name = ze.getName();
		        File newFile = new File(OutputFolder + File.separator + name);
		 
		 
		        //create all non exists folders
		        //else you will hit FileNotFoundException for compressed folder
		        new File(newFile.getParent()).mkdirs();
		 
		        FileOutputStream fos = new FileOutputStream(newFile);             
		 
		        int len;
		        while ((len = zis.read(buffer)) >= 0) {
		        	fos.write(buffer, 0, len);
		        }
		 
		        fos.close();
		        //outMessage.getRootElement().getFirstElementByPath("/MQMD/CorrelId").setValue(fileName);
		        //MbMessageAssembly tmpAssembly = new MbMessageAssembly(outAssembly, outMessage);
		        MbElement m = outAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Destination/File/Name");
		        if (m == null)
		        	outAssembly.getLocalEnvironment().getRootElement().createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Destination",null).createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"File",null).createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Directory", OutputFolder).createElementAfter(MbElement.TYPE_NAME_VALUE, "Name", name);
		        else
		        	m.setValue(name);
		        
		        out.propagate(outAssembly);
		        ze = zis.getNextEntry();
		    }
		 
		    zis.closeEntry();
		    zis.close();
		    
		    outAssembly.getLocalEnvironment().getRootElement().createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Variables", null).createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Final", "YES").createElementAfter(MbElement.TYPE_NAME_VALUE, "Societa", societa).createElementAfter(MbElement.TYPE_NAME_VALUE, "DataRegistrazione", dataRegistrazione).createElementAfter(MbElement.TYPE_NAME_VALUE, "Cartella", path + "unzip" + File.separator + fileName.substring(0, fileName.indexOf(".zip"))).createElementAfter(MbElement.TYPE_NAME_VALUE, "DataRegistrazione", dataRegistrazione).createElementAfter(MbElement.TYPE_NAME_VALUE, "NomeFile", path + File.separator + fileName);
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
