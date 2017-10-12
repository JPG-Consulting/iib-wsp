import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;


public class ALEAUD_FtpZMRESCR01 extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");
		MbOutputTerminal fail = getOutputTerminal("failure");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		String archive = null;
		String errorSAP = null;
		String ftpHost = null;
		String ftpUsername = null;
		String ftpPassword = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage(inMessage);
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			Connection conn = null;
			PreparedStatement s = null;
			conn = getJDBCType4Connection("INT_ENGINE", JDBC_TransactionType.MB_TRANSACTION_AUTO);
			s = conn.prepareStatement("SELECT VARIABILE, VALORE FROM IIB_VAR_CFG WHERE FLUSSO = 'NETA_SAP_IMPEGNI_MAGAZZINO' ");
			ResultSet rs = s.executeQuery();
			while(rs.next()) {
				switch (rs.getString("VARIABILE")) {
				case "FTP_HOST" : ftpHost = rs.getString("VALORE");
				break;
				case "FTP_USERNAME" : ftpUsername = rs.getString("VALORE");
				break;
				case "FTP_PASSWORD" : ftpPassword = rs.getString("VALORE");
				break;
				case "ARCHIVEFOLDER" : archive = rs.getString("VALORE");
				break;
				case "SAP_ERROR_FOLDER" : errorSAP = rs.getString("VALORE");
				break;
				}
			}
			
			FTPClient ftpClient = new FTPClient();
			ftpClient.connect(ftpHost, 21);
			if (ftpClient.login(ftpUsername, ftpPassword)) {
				ftpClient.enterLocalPassiveMode();			
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				String idFlusso = inMessage.getRootElement().getFirstElementByPath("/XMLNSC/SapAleaud01/SapAleaud01IDocBO/SapAleaud01DataRecord/SapAleaud01E2adhdr001/SapAleaud01E2state002/STAPA1_LNG").getValueAsString();
				String remoteFile = archive + idFlusso;
				String errorFile = errorSAP + idFlusso.substring(idFlusso.indexOf("_")+1);
				try {
					if(!ftpClient.rename(remoteFile, errorFile)){
						throw new Exception("Invio alla directory di errore SAP del file " + remoteFile + " fallito\n");
					}
				} finally {
					ftpClient.logout();
					
				}
			}
			
			ftpClient.disconnect();

			// End of user code
			// ----------------------------------------------------------
		} catch (MbUserException e) {
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
