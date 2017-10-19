package sub;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbBLOB;
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
import com.jcraft.jsch.SftpException;


public class GetSftpDocument_JavaCompute extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");
		MbOutputTerminal failure = getOutputTerminal("failure");

		MbMessage inMessage = inAssembly.getMessage();
	// get values from LocalENV, previously written by ESQL module
        String sftpHost       = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/server").getValueAsString();
        String sftpUsername   = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/user").getValueAsString();
        String sftpPassword   = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/password").getValueAsString();
        String sftpinputPath  = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/fullpath").getValueAsString();
        String sftpPort       = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/port").getValueAsString();
        // fileCOPYname
        String fileCOPYname       = inAssembly.getLocalEnvironment().getRootElement().getFirstElementByPath("/XMLNSC/Variables/fileCOPYname").getValueAsString();
//        String localFolder   = "P:/Users/e-todeschini/Documents/temp/";
        boolean okFile   = false;
		MbMessageAssembly outAssembly = null;
		Session session = null;
		ChannelSftp sftp = null;
		
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			 JSch jsch = new JSch();
             session = jsch.getSession(sftpUsername, sftpHost, Integer.parseInt(sftpPort) );
             session.setConfig( "PreferredAuthentications", "password" );
             session.setPassword(sftpPassword);
             
             java.util.Properties config = new java.util.Properties(); 
             config.put("StrictHostKeyChecking", "no");
             session.setConfig(config);
             
             session.connect( 5000 );
             Channel channel = session.openChannel( "sftp" );
             sftp = ( ChannelSftp ) channel;
             sftp.connect( 5000 );

         //    try {
            	 InputStream in = sftp.get( sftpinputPath );
                 byte[] bytes = IOUtils.toByteArray(in);
              
                 MbMessage outMessage2 = new MbMessage(inMessage);      
                 outAssembly = new MbMessageAssembly(inAssembly, outMessage2);
                 
                 String parserName = MbBLOB.PARSER_NAME;
                 String messageType = "";
                 String messageSet = "";
                 String messageFormat = "";
                 int encoding = 0;
                 int ccsid = 0;
                 int options = 0;

                 final String multipart = "multipart/form-data";
                 
                 MbElement outRoot = outMessage2.getRootElement();

                 
                 MbElement outProps = outRoot.getFirstElementByPath("/Properties");
                 MbElement outPropDomain = outProps.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Domain", "MIME");
                 outPropDomain.setValue("MIME");
                 
            //     MbElement outPropContentType = outRoot.getFirstElementByPath("/Properties/ContentType");
            //     outPropContentType.setValue(multipart);
                
            //     MbElement outPropContentDisposition =  outProps.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "ContentDisposition", null);
            //     outPropContentDisposition.setValue("filename=\""+ fileCOPYname+ "\"");

                 /*
                  * questo è quello che scarica correttamente il file
                  */
                 outMessage2.getRootElement().createElementAsLastChildFromBitstream( bytes, //buffer.toByteArray(), // data,
                           parserName, messageType, messageSet, messageFormat, encoding, ccsid,
                           options);
                 /*
                  * 
                  */
                     
                 outAssembly = new MbMessageAssembly(inAssembly, outMessage2);
                 out.propagate(outAssembly);

                 
/*			} catch (Exception e) {
				// TODO: handle exception
			}  */

            	 
			// End of user code
			// ----------------------------------------------------------
		} catch (SftpException e) {
			throw new MbUserException(this, "evaluate()", "", "","NO SUCH FILE --- File not found exception ---------",	null);
			
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			 throw e;
/////			 MbMessage outMessageError = new MbMessage(inMessage);      
/////             outAssembly = new MbMessageAssembly(inAssembly, outMessageError);
/*             MbElement outRoot = outMessageError.getRootElement();

             
             MbElement outProps = outRoot.getFirstElementByPath("/Properties");
             MbElement outPropDomain = outProps.getFirstElementByPath("/Topic"); // .createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Topic", "ERROR GETTING FILE");
             if (outPropDomain == null) 
            	 outPropDomain=outProps.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Topic", "ERROR GETTING FILE");
             else
            	 outPropDomain.setValue("ERROR GETTING FILE");*/
             
//////			 failure.propagate(outAssembly);
			 
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),	null);
			//così esce dal FAILURE terminal!!!!!!
			
		// The following should only be changed
		} finally  {
			sftp.exit();
			session.disconnect();
			
		}
		// if not propagating message to the 'out' terminal
		out.propagate(outAssembly);

	}

}
