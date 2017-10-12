import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;
import com.ibm.broker.plugin.MbNode.JDBC_TransactionType;
import static com.ibm.broker.plugin.MbElement.*;
import java.sql.*;

public class Sort_ElaboraBAPI extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");
		getOutputTerminal("alternate");

		MbMessage inMessage = inAssembly.getMessage();
		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage();
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			outAssembly.getLocalEnvironment().getRootElement().createElementAsLastChild(TYPE_NAME, "Adapter", null).createElementAsLastChild(TYPE_NAME_VALUE, "MethodName", "executeSapZIdocReadCompletely");

			if (outAssembly.getGlobalEnvironment().getRootElement().getFirstElementByPath("/Variables/Stato") == null) {
				outAssembly.getGlobalEnvironment().getRootElement().createElementAsLastChild(TYPE_NAME, "Variables", null).createElementAsLastChild(TYPE_NAME_VALUE, "Stato", null).createElementAfter(TYPE_NAME_VALUE, "Rif", null).createElementAfter(TYPE_NAME_VALUE, "IdFlusso", null);
			} else {
				outAssembly.getGlobalEnvironment().getRootElement().getFirstElementByPath("/Variables/Stato").setValue(null);
				outAssembly.getGlobalEnvironment().getRootElement().getFirstElementByPath("/Variables/Rif").setValue(null);
				outAssembly.getGlobalEnvironment().getRootElement().getFirstElementByPath("/Variables/IdFlusso").setValue(null);
			}

			MbElement idocData = inMessage.getRootElement().getFirstElementByPath("/XMLNSC/SapAleaud01/SapAleaud01IDocBO/SapAleaud01DataRecord/SapAleaud01E2adhdr001/SapAleaud01E2state002");
			String idFlusso = null;
			String stato  = null;
			String numeroIdoc = null;
			String descrizione = null;
			String esito = null;
			String tipoMessaggio = null;
			String idLancio = null;
			String societa = null;
			String tipoFlusso = null;
			String sistema = null;
			String descrizioneStato = null;
			String codEsito = null;
			String descrElab = null;
			String file = null;

			do {
				MbMessage m2 = new MbMessage();
				MbElement STAPA1 = idocData.getFirstElementByPath("STAPA1_LNG");
				if (STAPA1 != null) {
					Object STAPA1_VAL = STAPA1.getValue();
					if (!STAPA1_VAL.toString().isEmpty()) {
						idFlusso = idocData.getFirstElementByPath("STAPA1_LNG").getValueAsString();


						if (idocData.getFirstElementByPath("STATUS") != null) stato = idocData.getFirstElementByPath("STATUS").getValueAsString();
						else stato = null;
						if (idocData.getFirstElementByPath("SapAleaud01E2prtob/DOCNUM") != null) numeroIdoc = idocData.getFirstElementByPath("SapAleaud01E2prtob/DOCNUM").getValueAsString();
						else numeroIdoc = null;
						if (idocData.getFirstElementByPath("STAPA2_LNG") != null) descrizione = idocData.getFirstElementByPath("STAPA2_LNG").getValueAsString();
						else descrizione = null;
						if (idocData.getFirstElementByPath("SapAleaud01E2prtob/OBJKEY") != null) esito = idocData.getFirstElementByPath("SapAleaud01E2prtob/OBJKEY").getValueAsString();
						else esito = null;
						if (inMessage.getRootElement().getFirstElementByPath("/XMLNSC/SapAleaud01/SapAleaud01IDocBO/SapAleaud01DataRecord/SapAleaud01E2adhdr001/MESTYP_LNG") != null) tipoMessaggio = idocData.getFirstElementByPath("/XMLNSC/SapAleaud01/SapAleaud01IDocBO/SapAleaud01DataRecord/SapAleaud01E2adhdr001/MESTYP_LNG").getValueAsString();
						else tipoMessaggio = null;
						String[] tokens = idFlusso.split("-");
						sistema = tokens[0];
						if (sistema.equals("N1")||sistema.equals("N2")||sistema.equals("N3")||sistema.equals("C1")||sistema.equals("C2")||sistema.equals("C3")||sistema.equals("I1")) {
							if (tokens[tokens.length -1].equals("SIM")) {
								idLancio = idFlusso.substring(18, 23);
							} else {
								idLancio = tokens[tokens.length-1];
							}
							societa = tokens[1];
							tipoFlusso = tokens[2];
						} else {
							if(sistema.equals("E1")||sistema.equals("E2")||sistema.equals("E3")) {
								societa = " ";
								idLancio = tokens[1];
							} else {
								if(sistema.equals("HR")) {
									societa = tokens[1];
									tipoFlusso = tokens[2];
								}
								if(sistema.equals("ZU")) {
									societa = tokens[1];
									tipoFlusso = tokens[2];
								}
							}
						}

						if (stato.equals("53")) {
							stato = "S";
							descrizioneStato = esito;
							codEsito = "000";
						} else
						{
							stato = "K";
							descrizioneStato = descrizione;
							codEsito = "999";
						}
						if(stato.equals("S")){
							descrElab = esito.substring(6,10); 
						} else 
							descrElab = descrizioneStato;

						//se contabile update sul database
						if(sistema.equals("N1")||sistema.equals("N2")||sistema.equals("N3")||sistema.equals("C1")||sistema.equals("C2")||sistema.equals("C3")) {
							try {
								Connection conn = null;

								conn = getJDBCType4Connection(sistema,
										JDBC_TransactionType.MB_TRANSACTION_AUTO);

								PreparedStatement s = conn.prepareStatement("UPDATE EXT_SAP_FLUSSO set ELABORATO = ?,COD_ELAB = ?, " +
										"DESCR_ELAB = ? , DATA_ELAB = sysdate , ID_ESTERNO = ?  where ID_LANCIO = ? and TIPO_FLUSSO = ? and SOC_SAP = ?");
								s.setString(1, stato);
								s.setString(2, codEsito);
								s.setString(3, descrElab);
								s.setString(4, numeroIdoc);
								s.setInt(5, Integer.valueOf(idLancio));
								s.setString(6, tipoFlusso);
								s.setString(7, societa);

								s.executeUpdate();

							} catch (Exception e) {
								throw new MbUserException("Errore nell'aggiornamento dello stato", "ID lancio : " + idLancio.toString() + " - Tipo flusso : " + tipoFlusso, "Sistema : " + sistema + " Società : " + societa, e.toString(), this.getClass().getName(), null);
							}

						}
						if (sistema.equals("I1")) {
							try {
								Connection conn = null;

								conn = getJDBCType4Connection(sistema,
										JDBC_TransactionType.MB_TRANSACTION_AUTO);

								PreparedStatement s = conn.prepareStatement("UPDATE GLCAE.EXT_SAP_FLUSSO set ELABORATO = ?,COD_ELAB = ?, " +
										"DESCR_ELAB = ? , DATA_ELAB = sysdate , ID_ESTERNO = ?  where ID_LANCIO = ? and TIPO_FLUSSO = ? and SOC_SAP = ?");
								s.setString(1, stato);
								s.setString(2, codEsito);
								s.setString(3, descrElab);
								s.setString(4, numeroIdoc);
								s.setInt(5, Integer.valueOf(idLancio));
								s.setString(6, tipoFlusso);
								s.setString(7, societa);

								s.executeUpdate();

							} catch (Exception e) {
								throw new MbUserException("Errore nell'aggiornamento dello stato", "ID lancio : " + idLancio.toString() + " - Tipo flusso : " + tipoFlusso, "Sistema : " + sistema + " Società : " + societa, e.toString(), this.getClass().getName(), null);
							}
						}

						m2.getRootElement().createElementAsLastChild(TYPE_NAME, "DataObject", null).createElementAsLastChild(TYPE_NAME, "SapZIdocReadCompletely", null).createElementAsLastChild(TYPE_NAME_VALUE, "DOCUMENT_NUMBER", numeroIdoc);

						outAssembly.getGlobalEnvironment().getRootElement().getFirstElementByPath("/Variables/Stato").setValue(stato);
						outAssembly.getGlobalEnvironment().getRootElement().getFirstElementByPath("/Variables/Rif").setValue(esito);
						outAssembly.getGlobalEnvironment().getRootElement().getFirstElementByPath("/Variables/IdFlusso").setValue(idFlusso);
						out.propagate(new MbMessageAssembly(outAssembly, m2));
					}
				}
				idocData = idocData.getNextSibling();
			} while (idocData != null);


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
		//out.propagate(outAssembly);

	}

}
