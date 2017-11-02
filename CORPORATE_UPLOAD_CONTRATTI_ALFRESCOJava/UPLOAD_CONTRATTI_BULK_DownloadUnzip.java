import java.io.File;
import java.io.FilenameFilter;
import java.net.URLEncoder;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbBLOB;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;


public class UPLOAD_CONTRATTI_BULK_DownloadUnzip extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below

			String host = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Variables/Host").getValueAsString();
			String user = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Variables/User").getValueAsString();
			String password = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Variables/Password").getValueAsString();
			String path = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Variables/Path").getValueAsString();
			String url = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/Variables/URL").getValueAsString();
			
			String ftpurl = "ftp://"+host+path+"/*";
			ProcessBuilder wget = new ProcessBuilder(
			"wget", "-q", ftpurl, "--user", user, "--password", password, "-nc", "-P", "/ibmdata/WINSHARE/ALFRESCO/BULKIMPORT/IREN Mercato/Retail/Contratti"+path+"/"
			);
					
			Process downloadFiles = wget.start();
			if (downloadFiles.waitFor() != 0){
				MbElement result = outMessage.getRootElement().createElementAsLastChild("JSON").createElementAsLastChild(MbJSON.OBJECT,MbJSON.DATA_ELEMENT_NAME, null).createElementAsLastChild(MbJSON.OBJECT, "result", null);
				MbElement info = result.createElementAfter(MbJSON.OBJECT, "info", null);
		        info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "status", false);
		        String message = "";
		        if (downloadFiles.exitValue() == 8) message="No file found for specified date - exit code: "+downloadFiles.exitValue();
		        else message = "FTP download failed, exit code: "+downloadFiles.exitValue();
		        info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "description", message);
		        
		        alt.propagate(outAssembly);
		        return;
			} else {
				File[] zipFiles = new File("/ibmdata/WINSHARE/ALFRESCO/BULKIMPORT/IREN Mercato/Retail/Contratti"+path+"/").listFiles(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".zip");
					}
				});
				if (zipFiles.length != 0) {
					Process unzip = new ProcessBuilder("unzip", "-o", "/ibmdata/WINSHARE/ALFRESCO/BULKIMPORT/IREN Mercato/Retail/Contratti"+path+"/*.zip", "-d", "/ibmdata/WINSHARE/ALFRESCO/BULKIMPORT/IREN Mercato/Retail/Contratti"+path+"/").start();
					
					if (unzip.waitFor() != 0) {
						MbElement result = outMessage.getRootElement().createElementAsLastChild("JSON").createElementAsLastChild(MbJSON.OBJECT,MbJSON.DATA_ELEMENT_NAME, null).createElementAsLastChild(MbJSON.OBJECT, "result", null);
						MbElement info = result.createElementAfter(MbJSON.OBJECT, "info", null);
				        info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "status", false);
				        info.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "description", "Unzip failed, exit code: "+unzip.exitValue());
				        
				        alt.propagate(outAssembly);
				        return;
					} else {
						for (File f : zipFiles){
							f.delete();
						}
					}
				}
				outAssembly.getMessage().getRootElement().getFirstElementByPath("HTTPRequestHeader").createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Content-Type", "application/x-www-form-urlencoded");
				outAssembly.getMessage().getRootElement().createElementAsLastChild(MbBLOB.PARSER_NAME).createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "BLOB", ("sourceDirectory="+URLEncoder.encode("/alfresco/bulkimport/IREN Mercato/Retail/Contratti"+path, "UTF-8")+"&"+"targetPath="+URLEncoder.encode("/Company Home/Sites/IrenEnergia/documentLibrary/DOCUMENTAZIONE COMMERCIALE/Retail/Contratti", "UTF-8")).replace("+", "%20").getBytes());
				outAssembly.getLocalEnvironment().getRootElement().createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Destination", null).createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "HTTP", null).createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "RequestURL", url );
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
