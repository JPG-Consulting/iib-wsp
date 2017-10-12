import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import static java.util.Calendar.*;
import com.ibm.broker.javacompute.MbJavaComputeNode;
import com.ibm.broker.plugin.MbException;
import com.ibm.broker.plugin.MbMessage;
import com.ibm.broker.plugin.MbMessageAssembly;
import com.ibm.broker.plugin.MbOutputTerminal;
import com.ibm.broker.plugin.MbUserException;


public class MoveFile_moveFile extends MbJavaComputeNode {

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
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			
			String netaPath = "/ibmdata/WINSHARE/NETA/SII/HUB/scambi_xml/SII/OUTBOUND/csv/AE1_0050/ACQUISITE/RESPONSE/AE1_0100_"+ cal.get(YEAR)+new DecimalFormat("00").format(cal.get(MONTH)+1)+cal.get(DAY_OF_MONTH)+".csv";
			String destNetaPath = "/ibmdata/test/NETA/INBOUND";
			Runtime.getRuntime().exec("mv -f "+netaPath+ " "+destNetaPath);

			String sapPath = "/ibmdata/WINSHARE/NETA/SII/HUB/scambi_xml/SII/OUTBOUND/csv/AE1_0050/ACQUISITE/RESPONSE/AE1_0100_"+ cal.get(YEAR)+new DecimalFormat("00").format(cal.get(MONTH)+1)+cal.get(DAY_OF_MONTH)+".csv";
			String destSapPath = "/ibmdata/test/NETA/INBOUND";
			Runtime.getRuntime().exec("mv -f "+sapPath+ " "+destSapPath);

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
