
import java.io.File;
import java.io.FileWriter;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.sun.xml.internal.bind.v2.runtime.output.DOMOutput;


public class IME_EE_CNRG_MT_AE1_JavaCompute extends MbJavaComputeNode {

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
			String seq     = inAssembly.getGlobalEnvironment().getRootElement().getFirstElementByPath("/Variables/sequence/VALUE").getValueAsString();			
			/*chiamata alla procedura oracle FLUSSI_SII_XML.FLUSSO_AE1_DELTA*/			
			Connection conn = null;
			String sql = "call FLUSSI_SII_XML.FLUSSO_AE1_DELTA(?,?)";			
			CallableStatement cs = null;
			PreparedStatement stmt = null;
			String result ="";
			//String dbmsoutFileName = "/ibmdata/test/out.txt";
			try{
				conn = getJDBCType4Connection("C1",JDBC_TransactionType.MB_TRANSACTION_AUTO); 	//C1 --> MERCATO MAGGIOR TUTELA			
				cs = conn.prepareCall(sql);
				cs.registerOutParameter(1, Types.VARCHAR);
				cs.setString(1, seq);
				cs.registerOutParameter(2, Types.VARCHAR);				
				//DbmsOutput dbmsOutput = new DbmsOutput(conn);
				//dbmsOutput.enable( 10000000 );
				cs.execute();
				//dbmsOutput.printFile(dbmsoutFileName);
			    //dbmsOutput.close();				
				result = cs.getString(2);						
				cs.close();
				// 255 errore generico della procedura non gestito 
				if (result.equals("255")){
					alt.propagate(outAssembly);
				}else{
					MbMessage env = outAssembly.getGlobalEnvironment();
					if (result.equals("254"))  env.getRootElement().createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "WARNING", "S");
					else env.getRootElement().createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "WARNING", "N");
					// produco il file
					sql = "SELECT CONTENUTO_FILE, NOME_FILE FROM REPORT_SII WHERE CHIAVE= '" + seq +"'"; 
					stmt = conn.prepareStatement(sql);
					ResultSet resultSet = stmt.executeQuery();
					// Metto il file in una directory di appoggio
					String path ="/ibmdata/data/SII/CNRG_MT/OUTBOUND/"; 
					String fileOut = "";
					String fileName = "";
					while (resultSet.next()) {
						 fileName = resultSet.getString("NOME_FILE");
						 fileOut =  path + fileName;
						 Reader reader = resultSet.getCharacterStream("CONTENUTO_FILE");
						 FileWriter writer = new FileWriter(fileOut);
						 char[] buffer = new char[1];
						 while (reader.read(buffer) > 0) {
						   writer.write(buffer);
						 }
						 writer.close();
						 }						
					// Sposto il file nella cartella dove leggerà l'applicazione verso hub (IME_EE_HUB_CNRG)
					String dest ="/ibmdata/WINSHARE/CNRG_MT/SII/AE1.0050/IN";
					Runtime.getRuntime().exec("mv -f "+fileOut+ " "+dest);
					
					env.getRootElement().createElementAsFirstChild(MbElement.TYPE_NAME_VALUE, "FileName", dest+"/"+fileName);

					out.propagate(outAssembly);
				}	
			}catch(Exception e ){				
				throw new MbUserException(this, "evaluate()", "", "", e.toString(),null);			
			}finally {
				if (stmt!= null) stmt.close();
				if (conn != null && !conn.isClosed()) {
					 conn.close();
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

		
	}

}
