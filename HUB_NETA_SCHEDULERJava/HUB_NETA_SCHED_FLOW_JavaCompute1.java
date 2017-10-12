import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbBLOB;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXMLNSC;

public class HUB_NETA_SCHED_FLOW_JavaCompute1 extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");
		MbOutputTerminal fal = getOutputTerminal("failure");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		String obj ="";
		String fileUnzip   = "\t";
		MbMessage outMessage = new MbMessage();
		try {
			// create new message as a copy of the input
			
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			String dirInput  = "/ibmdata/WINSHARE/NETA/SII/OUTBOUND/Packages/";
			String dirOutput   = "/ibmdata/WINSHARE/NETA/SII/HUB";
			//String dirInput  = "/ibmdata/tmp/NETA/Packages/"; // dir test
			//String dirOutput = "/ibmdata/tmp/NETA/Shared";// dir test
			
					
			int    flgUpzip = 0;
			int    flgExc   = 0;
			String exc		= "";
			int    c 		= 0;
			File dir = new File(dirInput);
			
			final Connection hubConn = getJDBCType4Connection("HUB_EAI",JDBC_TransactionType.MB_TRANSACTION_AUTO);
			
			String listaFile =" select x.name " +
							  " from hub_eai.file_neta x";
			
			Statement ins = hubConn.createStatement();
			ResultSet rs = ins.executeQuery(listaFile);
			HashSet lf = new HashSet<String>();
			while (rs.next()){
				lf.add(rs.getString("name"));				
			}	
			rs.close();
			if (dir.isDirectory()) {
				
				String[] files = dir.list(new FilenameFilter() {					
					@Override
					public boolean accept(File dir, String name) {
						if (name.endsWith(".zip")) {												
							return true;
						} else {
						return false;
						}
					}					
				});		
				for (String name : files) {		
					if (!lf.contains(name)){
						String insertSql =  " INSERT INTO HUB_EAI.FILE_NETA(NAME) values ('" + name+ "')";							
						try {											   
							   if (ins.executeUpdate(insertSql)!=0){
								   String fileSingoli = "\t";
								   // Se fa l'insert vuol dire che è da unzippare
							       byte[] buffer = new byte[1024];									      
							      	try{										      		
								       	//create output directory is not exists
								       	File folder = new File(dirOutput);								    
								       	if(!folder.exists()){
								       		folder.mkdir();
								       	}	       
								       	//get the zip file content
								       	ZipInputStream zis = new ZipInputStream(new FileInputStream(dirInput + name));
								       	//get the zipped file list entry
								       	ZipEntry ze = zis.getNextEntry();							   
								       	while(ze!=null){								    
								       	      String fileName = ze.getName();
								              File newFile = new File(dirOutput + File.separator + fileName);							    							   
								               //create all non exists folders
								               //else you will hit FileNotFoundException for compressed folder
								               new File(newFile.getParent()).mkdirs();							    
								               FileOutputStream fos = new FileOutputStream(newFile);             							    
								               int len;
								               while ((len = zis.read(buffer)) > 0) {
								          		fos.write(buffer, 0, len);
								               }							    
								               fos.close();   
								               ze = zis.getNextEntry();
								               fileSingoli = fileSingoli + " " + fileName+ " \n\t";
								       	}								    
								        zis.closeEntry();
								       	zis.close();
								       	String updateSql = "UPDATE HUB_EAI.FILE_NETA T" +
								       						  " SET T.TMS_UNZIP = sysdate" +
								       			              " WHERE T.NAME = '"+ name +"'";
									 	Statement upd = hubConn.createStatement();
									 	try {
									 	 upd.executeUpdate(updateSql);
									 	 flgUpzip  = 1; 
									 	 fileUnzip =  fileUnzip + " " + name + " \n" + fileSingoli;										
									 	}catch(Exception e){
									 		flgExc = 1;
									 		exc    = exc + " " + e.toString();
									 	} 
							       }catch(IOException ex){
								 		flgExc = 1;
								 		exc    = exc + " " + ex.toString();
							       }  									
							   }	
						} catch (Exception e) {
							String excIns = e.toString();						
								ins.close();		
						 		flgExc = 1;
						 		exc    = exc + " " + e.toString();																	
						}						
					}				
				}
	
			}
			ins.close();			
			MbElement mb = outMessage.getRootElement().createElementAsFirstChild(MbXMLNSC.PARSER_NAME).createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"Params",null);
			mb.setNamespace("www.gruppoiren.it/schemas");
			
			
			// End of user code
			// ----------------------------------------------------------
			if (flgUpzip == 1 && flgExc != 1){
				obj ="Schedulazione terminata";
				mb.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"flusso","HUB_NETA_SCHEDULER").createElementAfter(MbElement.TYPE_NAME_VALUE,"obj1",obj).createElementAfter(MbElement.TYPE_NAME_VALUE,"body1",fileUnzip);
				out.propagate(outAssembly);
			}	
			if (flgExc == 1){
			    // MAIL di eccezione
				obj ="Schedulazione terminata CON ERRORI!";
				String testoMail = "";
				if (fileUnzip.equals("")){
				   testoMail = "ECCEZIONE: " + exc;				   
				}else{
				   testoMail ="File unzippati: " +fileUnzip + "\n ECCEZIONE: " + exc;	
				}
				mb.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"flusso","HUB_NETA_SCHEDULER").createElementAfter(MbElement.TYPE_NAME_VALUE,"obj1",obj).createElementAfter(MbElement.TYPE_NAME_VALUE,"body1",testoMail);
				fal.propagate(outAssembly);				
			}
	 		
			// END MAIL-------------------------			
		} catch (MbException e) {
			// Re-throw to allow Broker handling of MbException
			throw e;
		} catch (RuntimeException e) {
			// Re-throw to allow Broker handling of RuntimeException
			throw e;
		} catch (Exception e) {
			// Consider replacing Exception with type(s) thrown by user code
			// Example handling ensures all exceptions are re-thrown to be handled in the flow
			obj ="Schedulazione terminata CON ERRORI!";
			String testoMail = "";
			if (fileUnzip.equals("")){
			   testoMail = "ECCEZIONE: " + e;				   
			}else{
			   testoMail ="File unzippati: " +fileUnzip + "\n ECCEZIONE: " + e;	
			}
			MbElement mb = outMessage.getRootElement().createElementAsFirstChild(MbXMLNSC.PARSER_NAME).createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"Params",null);
			mb.setNamespace("www.gruppoiren.it/schemas");
			mb.createElementAsLastChild(MbElement.TYPE_NAME_VALUE,"flusso","HUB_NETA_SCHEDULER").createElementAfter(MbElement.TYPE_NAME_VALUE,"obj1",obj).createElementAfter(MbElement.TYPE_NAME_VALUE,"body1",testoMail);
			fal.propagate(outAssembly);				
			
			throw new MbUserException(this, "evaluate()", "", "", e.toString(),
					null);
		}
		// The following should only be changed
		// if not propagating message to the 'out' terminal
		//out.propagate(outAssembly);		
	}

}
