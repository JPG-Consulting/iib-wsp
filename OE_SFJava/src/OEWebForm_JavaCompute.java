package src;

import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbElement;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbJSON;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;


public class OEWebForm_JavaCompute extends MbJavaComputeNode {

	public void evaluate(MbMessageAssembly inAssembly) throws MbException {
		MbOutputTerminal out = getOutputTerminal("out");

		MbMessageAssembly outAssembly = null;
		try {
			// create new message as a copy of the input
			MbMessage outMessage = new MbMessage();
			outAssembly = new MbMessageAssembly(inAssembly, outMessage);
			// ----------------------------------------------------------
			// Add user code below
			MbElement DBInput = inAssembly.getMessage().getRootElement().getFirstElementByPath("/DataObject/V_EE_DATI_PRESTAZIONI");
			MbElement inboundReq = outMessage.getRootElement().createElementAsLastChild(MbJSON.PARSER_NAME).createElementAsLastChild(MbJSON.OBJECT, MbJSON.DATA_ELEMENT_NAME, null);

			String cliente = "{" +
					"\"CF_Straniero__c\""                     + ":" +   notNullPointer(DBInput.getFirstElementByPath("CF_STRANIERO__C")                    , "0")+","+
					"\"CIG__c\""                              + ":" +   notNullPointer(DBInput.getFirstElementByPath("CIG__C")                             , "0")+","+
					"\"Codice_Fiscale__c\""                   + ":" +   notNullPointer(DBInput.getFirstElementByPath("CODICE_FISCALE__C")                  , "0")+","+
					"\"Cognome__c\""                          + ":" +   notNullPointer(DBInput.getFirstElementByPath("COGNOME__C")                         , "0")+","+
					"\"Cliente_Finale_Consorzio__c\""         + ":" +   notNullPointer(DBInput.getFirstElementByPath("CLIENTE_FINALE_CONSORZIO__C")        , "0")+","+
					"\"Data_di_Nascita__c\""                  + ":" +   notNullPointer(DBInput.getFirstElementByPath("DATA_DI_NASCITA__C")                 , "0")+","+
					//"\"Email__c\""                            + ":" +   notNullPointer(DBInput.getFirstElementByPath("EMAIL__C")                           , "0")+","+
					//"\"Fax\""                                 + ":" +   notNullPointer(DBInput.getFirstElementByPath("FAX")                                , "0")+","+
					"\"Luogo_di_Nascita__c\""                 + ":" +   notNullPointer(DBInput.getFirstElementByPath("LUOGO_DI_NASCITA__C")                , "0")+","+
					"\"Nome__c\""                             + ":" +   notNullPointer(DBInput.getFirstElementByPath("NOME__C")                            , "0")+","+
					//"\"Pec__c\""                              + ":" +   notNullPointer(DBInput.getFirstElementByPath("PEC__C")                             , "0")+","+
					"\"Partita_IVA__c\""                      + ":" +   notNullPointer(DBInput.getFirstElementByPath("PARTITA_IVA__C")                     , "0")+","+
					"\"Ragione_Sociale__c\""                  + ":" +   notNullPointer(DBInput.getFirstElementByPath("RAGIONE_SOCIALE__C")                 , "0")+","+
					"\"Cliente_Finale_Segmentazione__c\""     + ":" +   notNullPointer(DBInput.getFirstElementByPath("CLIENTE_FIN_SEGMENTAZIONE__C")                   , "0")+","+
					"\"Phone\""                               + ":" +   notNullPointer(DBInput.getFirstElementByPath("PHONE")                              , "0")+","+
					//"\"Telefono_2__c\""                       + ":" +   notNullPointer(DBInput.getFirstElementByPath("TELEFONO_2__C")                      , "0")+","+
					"\"Tipo_Soggetto__c\""                    + ":" +   notNullPointer(DBInput.getFirstElementByPath("TIPO_SOGGETTO__C")                   , "0")+","+
					"\"Codice_attributo_Tipo_Mail__c\""       + ":" +   notNullPointer(DBInput.getFirstElementByPath("COD_ATTR_TIPO_MAIL__C")      , "0")+","+
					"\"Codice_attributo_Tipo_telefono_1__c\"" + ":" +   notNullPointer(DBInput.getFirstElementByPath("COD_ATTR_TIPO_TELEFONO_1__C"), "0")+","+
					"\"Codice_attributo_Tipo_telefono_2__c\"" + ":" +   notNullPointer(DBInput.getFirstElementByPath("COD_ATTR_TIPO_TELEFONO_2__C"), "0")+","+
					//"\"Codice_Fiscale_SWITCH__c\""            + ":" +   notNullPointer(DBInput.getFirstElementByPath("CODICE_FISCALE_SWITCH__C")           , "0")+","+
					"\"Codice_Tipo_Mail__c\""                 + ":" +   notNullPointer(DBInput.getFirstElementByPath("CODICE_TIPO_MAIL__C")                , "0")+","+
					"\"Codice_Tipo_telefono_1__c\""           + ":" +   notNullPointer(DBInput.getFirstElementByPath("CODICE_TIPO_TELEFONO_1__C")          , "0")+","+
					"\"Codice_Tipo_telefono_2__c\""           + ":" +   notNullPointer(DBInput.getFirstElementByPath("CODICE_TIPO_TELEFONO_2__C")          , "0")+","+
					"\"Codice_Ufficio__c\""                   + ":" +   notNullPointer(DBInput.getFirstElementByPath("CODICE_UFFICIO__C")                  , "0")+","+
					"\"CUP__c\"" 							  + ":" +   notNullPointer(DBInput.getFirstElementByPath("CUP__C")                             , "0")+","+
					//"\"Data_Documento_Identita__c\""          + ":" +   notNullPointer(DBInput.getFirstElementByPath("DATA_DOCUMENTO_IDENTITA__C")         , "0")+","+
					//"\"Documento_Identita_Numero__c\""        + ":" +   notNullPointer(DBInput.getFirstElementByPath("DOCUMENTO_IDENTITA_NUMERO__C")       , "0")+","+
					//"\"ID_Cliente_Finale_SAP__c\""            + ":" +   notNullPointer(DBInput.getFirstElementByPath("ID_CLIENTE_FINALE_SAP__C")           , "0")+","+
					"\"Intestatario_CAP__c\""                 + ":" +   notNullPointer(DBInput.getFirstElementByPath("INTESTATARIO_CAP__C")                , "0")+","+
					"\"Intestatario_Civico__c\""              + ":" +   notNullPointer(DBInput.getFirstElementByPath("INTESTATARIO_CIVICO__C")             , "0") +","+
					"\"Intestatario_Comune__c\""              + ":" +   notNullPointer(DBInput.getFirstElementByPath("INTESTATARIO_COMUNE__C")             , "0")+","+
					"\"Intestatario_Indirizzo__c\""           + ":" +   notNullPointer(DBInput.getFirstElementByPath("INTESTATARIO_INDIRIZZO__C")          , "0")+","+
					//"\"Intestatario_Interno__c\""             + ":" +   notNullPointer(DBInput.getFirstElementByPath("INTESTATARIO_INTERNO__C")            , "0")+","+
					"\"Intestatario_ISTAT__c\""               + ":" +   notNullPointer(DBInput.getFirstElementByPath("INTESTATARIO_ISTAT__C")              , "0")+","+
					//"\"Intestatario_Piano__c\""               + ":" +   notNullPointer(DBInput.getFirstElementByPath("INTESTATARIO_PIANO__C")              , "0")+","+
					//"\"Intestatario_Provincia__c\""           + ":" +   notNullPointer(DBInput.getFirstElementByPath("INTESTATARIO_PROVINCIA__C")          , "0")+","+
					"\"Intestatario_Provincia_Codice__c\""    + ":" +   notNullPointer(DBInput.getFirstElementByPath("INTESTATARIO_PROVINCIA__C")   , "0")+","+
					//"\"Intestatario_Scala__c\""               + ":" +   notNullPointer(DBInput.getFirstElementByPath("INTESTATARIO_SCALA__C")              , "0")+","+
					//"\"Intestatario_Suffisso__c\""            + ":" +   notNullPointer(DBInput.getFirstElementByPath("INTESTATARIO_SUFFISSO__C")           , "0")+","+
					"\"Intestatario_Toponimo__c\""            + ":" +   notNullPointer(DBInput.getFirstElementByPath("INTESTATARIO_TOPONIMO__C")           , "0")+","+
					//"\"Piva_SWITCH__c\""                      + ":" +   notNullPointer(DBInput.getFirstElementByPath("PIVA_SWITCH__C")                     , "0")+","+
					"\"Progressivo_Codice_Ufficio__c\""       + ":" +   notNullPointer(DBInput.getFirstElementByPath("PROGRESSIVO_CODICE_UFFICIO__C")      , "0")+","+
					//"\"Pubblica_Amministrazione__c\""         + ":" +   notNullPointer(DBInput.getFirstElementByPath("PUBBLICA_AMMINISTRAZIONE__C")        , "0")+","+
					"\"Recapito_CAP__c\""                     + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_CAP__C")                    , "0")+","+
					"\"Recapito_Civico__c\""                  + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_CIVICO__C")                 , "0") +","+
					"\"Recapito_Cognome__c\""                 + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_COGNOME__C")                , "0")+","+
					"\"Recapito_Comune__c\""                  + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_COMUNE__C")                 , "0")+","+
					"\"Recapito_Indirizzo__c\""     	      + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_INDIRIZZO__C")              , "0")+","+
					//"\"Recapito_Interno__c\""                 + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_INTERNO__C")                , "0")+","+
					"\"Recapito_ISTAT__c\""   				  + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_ISTAT__C")                  , "0")+","+
					//"\"Recapito_Mail__c\"" 				      + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_MAIL__C")                   , "0")+","+
					"\"Recapito_Nome__c\"" 				      + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_NOME__C")                   , "0")+","+
					//"\"Recapito_Piano__c\"" 				  + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_PIANO__C")                  , "0")+","+
					"\"Recapito_Provincia__c\"" 			  + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_PROVINCIA__C")              , "0")+","+
					//"\"Recapito_Provincia_Codice__c\"" 	      + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_PROVINCIA_COD__C")       , "0")+","+
					"\"Recapito_Ragione_Sociale__c\""         + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_RAGIONE_SOCIALE__C")        , "0")+","+
					//"\"Recapito_Scala__c\""                   + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_SCALA__C")                  , "0")+","+
					//"\"Recapito_Suffisso__c\""                + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_SUFFISSO__C")               , "0")+","+
					"\"Recapito_Tipo_Civico__c\""             + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_TIPO_CIVICO__C")            , "0") +","+
					"\"Recapito_Toponimo__c\""                + ":" +   notNullPointer(DBInput.getFirstElementByPath("RECAPITO_TOPONIMO__C")               , "0")+","+
					//"\"Regola_recapito__c\""                  + ":" +   notNullPointer(DBInput.getFirstElementByPath("REGOLA_RECAPITO__C")                 , "0")+","+
					"\"Tipo_Attivita_Economica__c\""          + ":" +   notNullPointer(DBInput.getFirstElementByPath("TIPO_ATTIVITA_ECONOMICA__C")         , "0")+
					//"\"Tipo_Documento_Identita__c\""          + ":" +   notNullPointer(DBInput.getFirstElementByPath("TIPO_DOCUMENTO_IDENTITA__C")         , "0")+*/
					"}";
			
			String opportunity = "{" +
					//"\"Addizionale_Provinciale__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("ADDIZIONALE_PROVINCIALE__C")        , "0")+","+
					"\"Agente__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("AGENTE__C")        , "0")+","+
					//"\"Agenzia__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("AGENZIA__C")        , "0")+","+
					"\"Amministratore_Cap__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_CAP__C"), "0") + "," +
					"\"Amministratore_CF__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_CF__C"), "0") + "," +
					"\"Amministratore_Civico__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_CIVICO__C"), "0") + "," +
					"\"Amministratore_Cognome__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_COGNOME__C"), "0") + "," +
					"\"Amministratore_Comune__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_COMUNE__C"), "0") + "," +
					"\"Amministratore_Denominazione__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMM_DENOMINAZIONE__C"), "0") + "," +
					"\"Amministratore_Indirizzo__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_INDIRIZZO__C"), "0") + "," +
					//"\"Amministratore_Interno__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_INTERNO__C"), "0") + "," +
					"\"Amministratore_Istat__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_ISTAT__C"), "0") + "," +
					"\"Amministratore_Nome__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_NOME__C"), "0") + "," +
					//"\"Amministratore_Piano__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_PIANO__C"), "0") + "," +
					"\"Amministratore_PIVA__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_PIVA__C"), "0") + "," +
					"\"Amministratore_Provincia__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_PROVINCIA__C"), "0") + "," +
					//"\"Amministratore_Provincia_Codice__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMM_PROVINCIA_CODICE__C"), "0") + "," +
					//"\"Amministratore_Scala__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_SCALA__C"), "0") + "," +
					//"\"Amministratore_Suffisso__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_SUFFISSO__C"), "0") + "," +
					"\"Amministratore_Toponimo__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("AMMINISTRATORE_TOPONIMO__C"), "0") + "," +
					"\"Bolletta_On_Line__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("BOLLETTA_ON_LINE__C")        , "0")+","+
					"\"Canale_Acquisizione__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("CANALE_ACQUISIZIONE__C")        , "0")+","+
					"\"Codice_attributo_tipo_canale_cell_deleg__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("TIPO_CELL_DELEG__C")        , "0")+","+
					"\"Codice_attributo_tipo_Mail_Delegato__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("CD_ATTR_TP_MAIL_DELEG__C")        , "0")+","+
					"\"Codice_attributo_tipo_canale_tel_deleg__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("TIPO_TEL_DELEG__C")        , "0")+","+
					//"\"Codice_Pratica_Portale_Sviluppo__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("CP_PORTALE_SVILUPPO__C")        , "0")+","+
					//"\"Codice_tipo_canale_comunicazione_cell__c\""		+ ":" +	notNullPointer(DBInput.getFirstElementByPath("CANALE_COMUNICAZIONE_CELL__C")        , "0")+","+
					"\"Codice_Tipo_Mail_Delegato__c\""		+ ":" +	notNullPointer(DBInput.getFirstElementByPath("CODICE_TP_MAIL_DELEGATO__C")        , "0")+","+
					"\"Codice_tipo_canale_comunicazione_telef__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("CANALE_COMUNICAZIONE_TELEF__C")        , "0")+","+
					"\"Data_Bolletta_On_Line__c\""		+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DATA_BOLLETTA_ON_LINE__C")        , "0")+","+
					//"\"Data_Estrazione__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DATA_ESTRAZIONE__C")        , "0")+","+
					"\"Data_Firma_Contratto__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DATA_FIRMA_CONTRATTO__C")        , "0")+","+
					//"\"Data_Invio__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DATA_INVIO__C")        , "0")+","+
					"\"Delegato_Cellulare__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_CELLULARE__C")        , "0")+","+
					//"\"Delegato_Citta__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_CITTA__C")        , "0")+","+
					"\"Delegato_Codice_Fiscale__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_CODICE_FISCALE__C")        , "0")+","+
					"\"Delegato_Cognome__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_COGNOME__C")        , "0")+","+
					"\"Delegato_Data_Nascita__c\""		+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_DATA_NASCITA__C")        , "0")+","+
					//"\"Delegato_Documento_Identita_Data__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_DOCUMENTO_DATA__C")        , "0")+","+
					//"\"Delegato_Documento_Identita_Ente__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_DOCUMENTO_ENTE__C")        , "0")+","+
					//"\"Delegato_Documento_Numero__c\""		+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_DOCUMENTO_NUMERO__C")        , "0")+","+
					//"\"Delegato_Indirizzo__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_INDIRIZZO__C")        , "0")+","+
					"\"Delegato_Luogo_Nascita__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_LUOGO_NASCITA__C")        , "0")+","+
					"\"Delegato_mail__c\""		+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_MAIL__C")        , "0")+","+
					"\"Delegato_Nome__c\""		+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_NOME__C")        , "0")+","+
					"\"Delegato_PIVA__c\""		+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_PIVA__C")        , "0")+","+
					"\"Delegato_Qualifica__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_QUALIFICA__C")        , "0")+","+
					"\"Delegato_Ragione_Sociale__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_RAGIONE_SOCIALE__C")        , "0")+","+
					//"\"Delegato_Telefono__c\""		+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_TELEFONO__C")        , "0")+","+
					//"\"Delegato_Tipo_Documento_Identita__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_TIPO_DOCUMENTO__C")        , "0")+","+
					"\"Delegato_Tipo_Soggetto__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DELEGATO_TIPO_SOGGETTO__C")        , "0")+","+
					//"\"Domiciliazione_Banca__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DOMICILIAZIONE_BANCA__C")        , "0")+","+
					"\"Domiciliazione_CF__c\""		+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DOMICILIAZIONE_CF__C")        , "0")+","+
					"\"Domiciliazione_Cognome__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DOMICILIAZIONE_COGNOME__C")        , "0")+","+
					"\"Domiciliazione_Data_Stipula__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DOMICILIAZIONE_DATA_STIPULA__C")        , "0")+","+
					"\"Domiciliazione_IBAN__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DOMICILIAZIONE_IBAN__C")        , "0")+","+
					"\"Domiciliazione_Intestatario__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DOMICILIAZIONE_INTESTATARIO__C")        , "0")+","+
					"\"Domiciliazione_Intestatario_CF__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DOMICILIAZIONE_INTESTAT_CF__C")        , "0")+","+
					//"\"Domiciliazione_Intestatario_Sottoscritto__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DOMICILIAZIONE_INT_SOTTOSCR__C")        , "0")+","+
					"\"Domiciliazione_Nome__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DOMICILIAZIONE_NOME__C")        , "0")+","+
					//"\"Domiciliazione_SWIFT__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("DOMICILIAZIONE_SWIFT__C")        , "0")+","+
					"\"Fatturato_MONO__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("FATTURATO_MONO__C")        , "0")+","+
					"\"Fatturato_MULTI__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("FATTURATO_MULTI__C")        , "0")+","+
					"\"Garanzia__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("GARANZIA__C")        , "0")+","+
					//"\"Id_Amministratore__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("ID_AMMINISTRATORE__C"), "0") + "," +
					//"\"Imposte_Erariali__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("IMPOSTE_ERARIALI__C")        , "0")+","+
					//"\"Inserimento_Delegato__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("INSERIMENTO_DELEGATO__C")        , "0")+","+
					"\"Mail_Notifica_Bolletta_On_Line__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("NOTIFICA_BOLLETTA_ON_LINE__C")        , "0")+","+
					//"\"Modalita_Spedizione__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("MODALITA_SPEDIZIONE__C")        , "0")+","+
					"\"Operatore__c\""		+ ":" +	notNullPointer(DBInput.getFirstElementByPath("OPERATORE__C")        , "0")+","+
					//"\"Origine_Caricamento__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("ORIGINE_CARICAMENTO__C")        , "0")+","+
					"\"Periodicita_fatturazione__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("PERIODICITA_FATTURAZIONE__C")        , "0")+","+
					//"\"Presenza_Amministratore__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("PRESENZA_AMMINISTRATORE__C"), "0") + "," +
					//"\"Referente_Amministrativo_Fax__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("REF_AMMINISTRATIVO_FAX__C"), "0") + "," +
					//"\"Referente_Amministrativo_Mail__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("REF_AMMINISTRATIVO_MAIL__C"), "0") + "," +
					//"\"Referente_Amministrativo_Nominativo__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("REF_AMMINISTRATIVO_NOMIN__C"), "0") + "," +
					//"\"Referente_Amministrativo_Telefono__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("REF_AMMINISTRATIVO_TELEFONO__C"), "0") + "," +
					//"\"Referente_Commerciale_Fax__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("REF_COMMERCIALE_FAX__C"), "0") + "," +
					//"\"Referente_Commerciale_Mail__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("REF_COMMERCIALE_MAIL__C"), "0") + "," +
					//"\"Referente_Commerciale_Nominativo__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("REF_COMMERCIALE_NOMIN__C"), "0") + "," +
					//"\"Referente_Commerciale_Telefono__c\"" + ":" + notNullPointer (DBInput.getFirstElementByPath("REF_COMMERCIALE_TELEFONO__C"), "0") + "," +
					"\"Regime_IVA__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("REGIME_IVA__C")        , "0")+","+
					"\"Ruolo_Amministratore__c\"" + ":" + notNullPointer(DBInput.getFirstElementByPath("RUOLO_AMMINISTRATORE__C")        , "0")+","+
					//"\"Sconto__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("SCONTO__C")        , "0")+","+
					//"\"Sconto_WEB_FORM__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("SCONTO_WEB_FORM__C")        , "0")+","+
					//"\"Sistema_Origine__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("SISTEMA_ORIGINE__C")        , "0")+","+
					"\"Split_Payment__c\""		+ ":" +	notNullPointer(DBInput.getFirstElementByPath("SPLIT_PAYMENT__C")        , "0")+","+
					//"\"Stato_Proposta_Portale_Sviluppo__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("STATO_PROP_PORTALE_SVILUPPO__C")        , "0")+","+
					//"\"Tempo_di_pagamento__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("TEMPO_DI_PAGAMENTO__C")        , "0")+","+
					//"\"Tipo_Bilancio__c\""		+ ":" +	notNullPointer(DBInput.getFirstElementByPath("TIPO_BILANCIO__C")        , "0")+","+
					//"\"Tipo_calcolo_interessi_di_mora__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("CALCOLO_INTERESSI_DI_MORA__C")        , "0")+","+
					"\"Tipo_Pagamento__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("TIPO_PAGAMENTO__C")        , "0")+","+
					"\"Tipo_Stampa_Dematerializzazione__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("TIPO_STAMPA__C")        , "0")+","+
					"\"Tipo_tolleranza_mora__c\""	+ ":" +	notNullPointer(DBInput.getFirstElementByPath("TIPO_TOLLERANZA_MORA__C")        , "0")+
					"}";
			
			String pdf = "{" +
					"\"Acquisto_Credito__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("ACQUISTO_CREDITO__C")        , "0")+","+
					//"\"Autocertificazione_Contratto_Connessione__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("AUTOCERT_CONTR_CONN__C "), "0")+","+
					"\"Certificazione_Rinnovabile__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("CERTIFICAZIONE_RINNOVABILE__C")        , "0")+","+
					"\"Codice_contratto_dispacciamento__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("CODICE_CONTRATTO_DISP__C")        , "0")+","+
					//"\"Codice_Coppia__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("CODICE_COPPIA__C")        , "0")+","+
					//"\"Codice_Flusso__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("CODICE_FLUSSO__C")        , "0")+","+
					//"\"Codice_Fornitura__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("CODICE_FORNITURA__C")        , "0")+","+
					//"\"Codice_Pratica_Web_Form__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("SF_COD_PRATICA_WF")        , "0")+","+
					//"\"Codice_Pratica_Hub__c\""  + ":" +  notNullPointer(DBInput.getFirstElementByPath("CODICE_PRATICA_HUB__C")      , "0") +","+
					"\"Codice_Servizio__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("CODICE_SERVIZIO__C")        , "0")+","+
					"\"Commodity__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("COMMODITY__C")        , "0")+","+
					"\"Condizioni_in_deroga__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("CONDIZIONI_IN_DEROGA__C")        , "0") +","+
					"\"Consumo_Annuo_EE__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("CONSUMO_ANNUO_EE__C")        , "0")+","+
					"\"Data_FineFornitura__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("DATA_FINEFORNITURA__C")        , "0")+","+
					"\"Data_Inizio_Fornitura__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("DATA_INIZIO_FORNITURA__C")        , "0")+","+
					//"\"Data_Invio_Stampatore__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("DATA_INVIO_STAMPATORE__C")        , "0")+","+
					"\"Data_riferimento_prezzi__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("DATA_RIFERIMENTO_PREZZI__C")        , "0")+","+
					//"\"Disalimentabilita__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("DISALIMENTABILITA__C")        , "0")+","+
					"\"Distributore__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("DISTRIBUTORE__C")        , "0")+","+
					"\"Durata_Fornitura__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("DURATA_FORNITURA__C")        , "0")+","+
					"\"Fornitura_Cap__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("FORNITURA_CAP__C")        , "0")+","+
					"\"Fornitura_Civico__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("FORNITURA_CIVICO__C")        , "0")+","+
					"\"Fornitura_Comune__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("FORNITURA_COMUNE__C")        , "0")+","+
					"\"Fornitura_Indirizzo__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("FORNITURA_INDIRIZZO__C")        , "0")+","+
					//"\"Fornitura_Interno__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("FORNITURA_INTERNO__C")        , "0")+","+
					"\"Fornitura_ISTAT__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("FORNITURA_ISTAT__C")        , "0")+","+
					//"\"Fornitura_Piano__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("FORNITURA_PIANO__C")        , "0")+","+
					//"\"Fornitura_Provincia__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("FORNITURA_PROVINCIA__C")        , "0")+","+
					"\"Fornitura_Provincia_Codice__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("FORNITURA_PROVINCIA__C")        , "0")+","+
					//"\"Fornitura_Scala__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("FORNITURA_SCALA__C")        , "0")+","+
					//"\"Fornitura_Suffisso__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("FORNITURA_SUFFISSO__C")        , "0")+","+
					//"\"Fornitura_Tipo_Civico__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("FORNITURA_TIPO_CIVICO__C")        , "0")+","+
					"\"Fornitura_Toponimo__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("FORNITURA_TOPONIMO__C")        , "0")+","+
					"\"Gestionale__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("GESTIONALE__C")        , "0")+","+
					//"\"ID_Trasmissione__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("ID_TRASMISSIONE__C")        , "0")+","+
					"\"Indice_I0__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("INDICE_I0__C")        , "0")+","+
					//"\"Indicizzazione_Costante__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("INDICIZZAZIONE_COSTANTE__C")        , "0")+","+
					//"\"Indicizzazione_Percentuale__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("INDICIZZAZIONE_PERCENTUALE__C")        , "0")+","+
					"\"Livello_Tensione_da_Cliente__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("LIVELLO_TENSIONE_DA_CLIENTE__C")        , "0")+","+
					//"\"Livello_Tensione_da_distributore__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("LIVELLO_TENSIONE_DISTR__C")        , "0")+","+
					"\"Mercato_di_Provenienza__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("MERCATO_DI_PROVENIENZA__C")        , "0")+","+
					//"\"Mercato_di_Rientro__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("MERCATO_DI_RIENTRO__C")        , "0")+","+
					//"\"Mesi_Recesso__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("MESI_RECESSO__C")        , "0")+","+
					//"\"Note__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("NOTE__C")        , "0")+","+
					"\"Prodotto__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("PRODOTTO__C")        , "0")+","+
					//"\"Opzione_tariffaria_1__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("OPZIONE_TARIFFARIA_1__C")        , "0")+","+
					"\"Opzione_tariffaria_10__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("OPZIONE_TARIFFARIA_10__C")        , "0")+","+
					//"\"Opzione_tariffaria_2__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("OPZIONE_TARIFFARIA_2__C")        , "0")+","+
					//"\"Opzione_tariffaria_3__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("OPZIONE_TARIFFARIA_3__C")        , "0")+","+
					//"\"Opzione_tariffaria_4__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("OPZIONE_TARIFFARIA_4__C")        , "0")+","+
					//"\"Opzione_tariffaria_5__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("OPZIONE_TARIFFARIA_5__C")        , "0")+","+
					//"\"Opzione_tariffaria_6__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("OPZIONE_TARIFFARIA_6__C")        , "0")+","+
					//"\"Opzione_tariffaria_7__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("OPZIONE_TARIFFARIA_7__C")        , "0")+","+
					//"\"Opzione_tariffaria_8__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("OPZIONE_TARIFFARIA_8__C")        , "0")+","+
					//"\"Opzione_tariffaria_9__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("OPZIONE_TARIFFARIA_9__C")        , "0")+","+
					//"\"Opzione_tariffaria_trasporto__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("OPZIONE_TARIFFARIA_TRASP__C")        , "0")+","+
					"\"Pod__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("POD__C")        , "0")+","+
					//"\"Potenza_Contrattuale_Da_Distributore__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("POTENZA_CONTRATTUALE_DISTR__C")        , "0")+","+
					//"\"Potenza_Disponibile_Da_Distributore__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("POTENZA_DISPONIBILE_DISTR__C")        , "0")+","+
					"\"Potenza_EE__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("POTENZA_EE__C")        , "0")+","+
					//"\"Pre_Winback__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("PRE_WINBACK__C")        , "0")+","+
					"\"Prezzo_di_picco__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("PREZZO_DI_PICCO__C")        , "0")+","+
					"\"Prezzo_fascia_F1__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("PREZZO_FASCIA_F1__C")        , "0")+","+
					"\"Prezzo_fascia_F2__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("PREZZO_FASCIA_F2__C")        , "0")+","+
					"\"Prezzo_fascia_F3__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("PREZZO_FASCIA_F3__C")        , "0")+","+
					"\"Prezzo_fuori_picco__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("PREZZO_FUORI_PICCO__C")        , "0")+","+
					"\"Prezzo_mono__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("PREZZO_MONO__C")        , "0")+","+
					//"\"Pricing_Combinata__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("PRICING_COMBINATA__C")        , "0")+","+
					"\"Pricing_EE__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("PRICING_EE__C")        , "0")+","+
					"\"Privacy__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("PRIVACY__C")        , "0")+","+
					//"\"Recapito__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("RECAPITO__C")        , "0")+","+
					"\"Residente_da_Cliente__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("RESIDENTE_DA_CLIENTE__C")        , "0")+","+
					//"\"Residente_Da_Distributore__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("RESIDENTE_DA_DISTRIBUTORE__C")        , "0")+","+
					"\"Revoca_Timoe__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("REVOCA_TIMOE__C")        , "0")+","+
					"\"Riferimento_in_fattura__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("RIFERIMENTO_IN_FATTURA__C")        , "0")+","+
					"\"Rinnovo_tacito__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("RINNOVO_TACITO__C")        , "0")+","+
					"\"Tensione_da_Cliente__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("TENSIONE_DA_CLIENTE__C")        , "0")+","+
					//"\"Tensione_da_distributore__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("TENSIONE_DA_DISTRIBUTORE__C")        , "0")+","+
					//"\"Tipo_Contatore__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("TIPO_CONTATORE__C")        , "0")+","+
					"\"Tipo_Feel__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("TIPO_FEEL__C")        , "0")+","+
					//"\"Tipo_processo_gestionale__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("TIPO_PROCESSO_GESTIONALE__C")        , "0")+","+
					//"\"Tipo_societa__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("TIPO_SOCIETA__C")        , "0")+","+
					"\"Tipo_tariffa__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("TIPO_TARIFFA__C")        , "0")+","+
					"\"Tipo_USO__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("TIPO_USO__C")        , "0")+","+
					//"\"Tipo_USO_da_distributore__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("TIPO_USO_DA_DISTRIBUTORE__C")        , "0")+","+
					"\"Titolo_Occupazione_Immobile_Cliente_Fina__c\""	 + ":" +  notNullPointer(DBInput.getFirstElementByPath("TITOLO_IMMOBILE_CLIENTE__C")        , "0")+","+
					"\"Venditore_Uscente__c\""		 + ":" +  notNullPointer(DBInput.getFirstElementByPath("VENDITORE_USCENTE__C")        , "0")+
					"}";		
			
			inboundReq.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Cliente", cliente);
			inboundReq.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "Pratica", opportunity);
			inboundReq.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "PdF", pdf);
			MbElement tracking = inboundReq.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "tracking", null);
			
			if (DBInput.getFirstElementByPath("ID_EE_COMMERCIALE_TRACKING") != null) 
			   tracking.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "codiceFlusso", "C");
			else tracking.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "codiceFlusso", null);
			// se ci sono anche i dati commerciali 'C' --> pratica completa 
			tracking.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "codicePratica", DBInput.getFirstElementByPath("ID_EE_PRESTAZIONE_TRACKING").getValueAsString());
			tracking.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "codicePraticaDistributore", null);
			if (DBInput.getFirstElementByPath("SF_COD_PRATICA_WF") != null) tracking.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "codicePraticaOrigine", DBInput.getFirstElementByPath("SF_COD_PRATICA_WF").getValueAsString());
			tracking.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "idMessaggio", null);
			if (DBInput.getFirstElementByPath("SEGMENTAZIONE__C") != null) tracking.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "segmentoCliente", DBInput.getFirstElementByPath("SEGMENTAZIONE__C").getValueAsString());
			tracking.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "servizio", DBInput.getFirstElementByPath("CODICE_SERVIZIO__C").getValueAsString());
			tracking.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "sistemaFatturazione", DBInput.getFirstElementByPath("GESTIONALE__C").getValueAsString());
			tracking.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "sistemaOrigine", DBInput.getFirstElementByPath("SISTEMA_ORIGINE__C").getValueAsString());
			tracking.createElementAsLastChild(MbElement.TYPE_NAME_VALUE, "societaVendita", "IREN");
			
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

	public static String notNullPointer(MbElement pointer, String defaultValue) {
		try {
			String s = pointer.getValueAsString().replace("\\", "\\\\");
			s = s.replace("\"", "\\\"");
			//s = s.replace("," , "\\,");
			s = s.trim();
			String value = "\""+ s +"\"";
			return value;
		} catch (Exception ex) {
			return null;
		}
	}
	
}
