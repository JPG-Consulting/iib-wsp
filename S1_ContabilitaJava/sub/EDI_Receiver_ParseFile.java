package sub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbXML;
import com.ibm.broker.plugin.MbXMLNS;
import com.ibm.broker.plugin.MbXMLNSC;
import static com.ibm.broker.plugin.MbElement.TYPE_NAME_VALUE;

public class EDI_Receiver_ParseFile extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		MbOutputTerminal alt = getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();

		// create new empty message
		MbMessage outMessage = new MbMessage();
		MbMessageAssembly outAssembly = new MbMessageAssembly(inAssembly,
				outMessage);
		
		FileInputStream is = null;
		Scanner sc = null;

		try {
			// optionally copy message headers
			//copyMessageHeaders(inMessage, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			MbElement parser = outMessage.getRootElement().createElementAsLastChild(MbXMLNSC.PARSER_NAME);
			MbElement currentIBGM = null;
			MbElement currentILIN = null;
			MbElement currentINA1 = null;
			MbElement currentIRF1 = null;
			String fileName = inMessage.getRootElement().getFirstElementByPath("/JSON/Data/nomeFile").getValueAsString();
			String societa = inMessage.getRootElement().getFirstElementByPath("/JSON/Data/societa").getValueAsString();
			String dataRegistrazione = inMessage.getRootElement().getFirstElementByPath("/JSON/Data/dataRegistrazione").getValueAsString();
			String path = inMessage.getRootElement().getFirstElementByPath("/JSON/Data/percorso").getValueAsString();
			//String path = File.separator+"ibmdata"+File.separator+"data"+File.separator+"contabile"+File.separator+"edi"+File.separator;
			
			outAssembly.getLocalEnvironment().getRootElement().createElementAsLastChild(TYPE_NAME_VALUE, "Variables", null).createElementAsLastChild(TYPE_NAME_VALUE, "societa", societa).createElementAfter(TYPE_NAME_VALUE, "dataRegistrazione", dataRegistrazione).createElementAfter(TYPE_NAME_VALUE, "nomeFile", fileName).createElementAfter(TYPE_NAME_VALUE, "localFolder", path);
			is = new FileInputStream(path+File.separator+fileName);
			sc = new Scanner(is, "Windows-1252");
			
			String id = null;
			Boolean considerRow = false;
			parser = parser.createElementAsLastChild(TYPE_NAME_VALUE, "EDI", null);
			parser.setNamespace("http://www.gruppoiren.it/schemas");
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String currentId = line.substring(0,4);
				String type = line.substring(4, 8);
				String row = line.substring(8).trim();
				if (type.equals("IBGM")) {
					String value = row.substring(0, 3);
					if(value.equals("380") || value.equals("381")) {
						considerRow = true;
						id = currentId;
						String numFattura = row.substring(44, 59);
						row = row.substring(60).trim();
						currentIBGM = parser.createElementAsLastChild(TYPE_NAME_VALUE, type, null);
						currentIBGM.setNamespace("http://www.gruppoiren.it/schemas");
						currentIBGM.createElementAsLastChild(TYPE_NAME_VALUE, "id", currentId).createElementAfter(TYPE_NAME_VALUE, "numeroFattura", numFattura).createElementAfter(TYPE_NAME_VALUE, "row", row);
						currentILIN = null;
						currentINA1 = null;
						currentIRF1 = null;
					} else {
						considerRow = false;
					}
				} else {
					if (considerRow = true && currentId.equals(id)) {
						switch(type) {
						case "IDT1" : {
							String subtype = row.substring(0, 3).trim();
							if (subtype.equals("3")) {
								String dataFatt = row.substring(3, 11);
								row = row.substring(11).trim();
								currentIBGM.createElementAsLastChild(TYPE_NAME_VALUE, type, null).createElementAsLastChild(TYPE_NAME_VALUE, "dataFattura", dataFatt).createElementAfter(TYPE_NAME_VALUE, "row", row);
							} else if (subtype.equals("13")) {
								String dataScad = row.substring(3,11);
								row = row.substring(11).trim();
								currentIBGM.createElementAsLastChild(TYPE_NAME_VALUE, type, null).createElementAsLastChild(TYPE_NAME_VALUE, "dataScadenza", dataScad).createElementAfter(TYPE_NAME_VALUE, "row", row);
							} else {
								currentIBGM.createElementAsLastChild(TYPE_NAME_VALUE, type, null).createElementAsLastChild(TYPE_NAME_VALUE, "row", row);
							}
							break;
						}
						case "IFT1" : {
							currentIBGM.createElementAsLastChild(TYPE_NAME_VALUE, type, row);
							break;
						}
						case "ICUX" : {
							currentIBGM.createElementAsLastChild(TYPE_NAME_VALUE, type, row);
							break;
						}
						case "ICNT" : {
							currentIBGM.createElementAsLastChild(TYPE_NAME_VALUE, type, row);
							break;
						}
						case "IMO3" : {
							String codiceAddebito = row.substring(0, 3).trim();
							String segno = row.substring(3,4);
							String importo = row.substring(4,22);
							String cur = row.substring(22).trim();
							currentIBGM.createElementAsLastChild(TYPE_NAME_VALUE, type, null).createElementAsLastChild(TYPE_NAME_VALUE, "codiceAddebito", codiceAddebito).createElementAfter(TYPE_NAME_VALUE, "segno", segno).createElementAfter(TYPE_NAME_VALUE, "importo", importo).createElementAfter(TYPE_NAME_VALUE, "valuta", cur);
							break;
						}
						case "ITA2" : {
							if (row.startsWith("7  VAT")) {
								String codIVA1 = row.substring(74,77);
								String codIVA2 = row.substring(176, 179);
								String codImponibile = row.substring(145, 148);
								String segnoImponibile = row.substring(148, 149);
								String imponibile = row.substring(149, 167);
								//String cur = row.substring(167, 170);
								String importoIVA = row.substring(180, 198);
								currentIBGM.createElementAsLastChild(TYPE_NAME_VALUE, type, null).createElementAsLastChild(TYPE_NAME_VALUE, "codiceIVA1", codIVA1).createElementAfter(TYPE_NAME_VALUE, "codiceIVA2", codIVA2).createElementAfter(TYPE_NAME_VALUE, "codiceImponibile", codImponibile).createElementAfter(TYPE_NAME_VALUE, "segnoImponibile", segnoImponibile).createElementAfter(TYPE_NAME_VALUE, "imponibile", imponibile).createElementAfter(TYPE_NAME_VALUE, "importoIVA", importoIVA);
							}
							break;
						}
						case "IRF1" : {
							if (row.startsWith("ACD")) {
								String idFlusso = row.substring(3);
								currentIRF1 = currentIBGM.createElementAsLastChild(TYPE_NAME_VALUE, type, null);
								currentIRF1.createElementAsLastChild(TYPE_NAME_VALUE, "idFlusso", idFlusso);
							}
							break;
						}
						case "IDT2" : {
							currentIRF1.createElementAsLastChild(TYPE_NAME_VALUE, type, row);
							break;
						}
						case "INA1" : {
							currentINA1 = currentIBGM.createElementAsLastChild(TYPE_NAME_VALUE, type, null);
							currentINA1.createElementAsLastChild(TYPE_NAME_VALUE, "row", row);
							break;
						}
						case "IFII" : {
							currentINA1.createElementAsLastChild(TYPE_NAME_VALUE, type, row);
							break;
						}
						case "ICTA" : {
							currentINA1.createElementAsLastChild(TYPE_NAME_VALUE, type, row);
							break;
						}
						case "IRF2" : {
							currentINA1.createElementAsLastChild(TYPE_NAME_VALUE, type, row);
							break;
						}
						case "ILIN" : {
							String subtype = row.substring(53, 54);
							currentILIN = currentIBGM.createElementAsLastChild(TYPE_NAME_VALUE, type, null);
							currentILIN.createElementAsLastChild(TYPE_NAME_VALUE, "tipoIlin", subtype).createElementAfter(TYPE_NAME_VALUE, "row", row);
							break;
						}
						case "IQTY" : {
							currentILIN.createElementAsLastChild(TYPE_NAME_VALUE, type, row);
							break;
						}
						case "IDT3": {
							currentILIN.createElementAsLastChild(TYPE_NAME_VALUE, type, row);
							break;
						}
						case "IFT3" : {
							currentILIN.createElementAsLastChild(TYPE_NAME_VALUE, type, row);
							break;
						}
						case "IMO1" : {
							String codAddebito = row.substring(0,3).trim();
							String segno = row.substring(3,4);
							String importo = row.substring(4,22);
							String cur = row.substring(22, 25);
							currentILIN.createElementAsLastChild(TYPE_NAME_VALUE, type, null).createElementAsLastChild(TYPE_NAME_VALUE, "codiceAddebito", codAddebito).createElementAfter(TYPE_NAME_VALUE, "segno", segno).createElementAfter(TYPE_NAME_VALUE, "importo", importo).createElementAfter(TYPE_NAME_VALUE, "valuta", cur);
							break;
						}
						case "IPRI" : {
							currentILIN.createElementAsLastChild(TYPE_NAME_VALUE, type, row);
							break;
						}
						case "ITA1" : {
							if (row.startsWith("7  VAT")) {
							String codiceIVA = row.substring(74,77);
							currentILIN.createElementAsLastChild(TYPE_NAME_VALUE, type, null).createElementAsFirstChild(TYPE_NAME_VALUE, "codiceIVA", codiceIVA);
							}
							break;
						}
						default : break;
							
						}
						
					}
				}
			}

			is.close();

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
		} finally {
			if(sc != null) sc.close();
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
