package be.vsol.dicom.util;

import java.rmi.dgc.VMID;
import java.rmi.server.UID;
import java.util.StringTokenizer;

public class DicomUidGenerator {
    public static final String uidRoot = "1.3.6.1.4.1.5962";
    public static final String uidQualifierForThisToolkit = "99";
    public static final String uidQualifierForUIDGenerator = "1";

    private static final String UidGen = "0";

    private static String longStamp;

    private static final String root = uidRoot + "." + uidQualifierForThisToolkit + "." + uidQualifierForUIDGenerator;

    private static final int maxStampComponentLength =
            64
                    - root.length() - 1	// root .
                    - 3			// . in generated stamp
                    - 2			// . UidGen_XXX (would need to be three if > 9)
                    - 5 - 5 - 5;		// . SOP Instance UID study# (4) . series# (4) . instance# (4)

    private static final int maxLongStampComponentLength =
            64
                    - root.length() - 1	// root .
                    - 3			// . in generated stamp
                    - 2			// . UidGen_XXX (would need to be three if > 9)
            ;			// no study#, series# or instance#

    private static final VMID vmid = new VMID();									// virtual machine ID
    private static final long machineAddress = (((long)vmid.hashCode())& 0x0ffffffffL);

    private static void newStamp() {
        long ourMachine = Math.abs(machineAddress);		// don't mess with the real machine address; need it unchanged for next time

        String string = new UID().toString();	// e.g. "19c082:fb77ce774a:-8000"
        StringTokenizer st = new StringTokenizer(string,":");
        int  ourUnique = Math.abs(Integer.valueOf(st.nextToken(), 16));
        long   ourTime = Math.abs(Long.valueOf(st.nextToken(), 16));
        int   ourCount = Math.abs(Short.valueOf(st.nextToken(), 16) + 0x8000);	// why add 0x8000 ? because usually starts at -8000, which wastes 4 digits

        String machineString = Long.toString(ourMachine);
        String vmString = Integer.toString(ourUnique);
        String timeString = Long.toString(ourTime);
        String countString = Integer.toString(ourCount);

        while (ourUnique > 10000 && machineString.length()+vmString.length()+timeString.length()+countString.length() > maxLongStampComponentLength) {
            ourUnique = ourUnique / 10;
            vmString = Integer.toString(ourUnique);
        }
        while (ourMachine > 0 && machineString.length()+vmString.length()+timeString.length()+countString.length() > maxLongStampComponentLength) {
            ourMachine = ourMachine / 10;
            machineString = Long.toString(ourMachine);
        }

        longStamp = machineString + "." + vmString + "." + timeString + "." + countString;

        while (ourUnique > 10000 && machineString.length()+vmString.length()+timeString.length()+countString.length() > maxStampComponentLength) {
            ourUnique = ourUnique / 10;
            vmString = Integer.toString(ourUnique);
        }
        while (ourMachine > 0 && machineString.length()+vmString.length()+timeString.length()+countString.length() > maxStampComponentLength) {
            ourMachine = ourMachine / 10;
            machineString = Long.toString(ourMachine);
        }
    }

    private static String getNewUID() {
        return root + "." + longStamp + "." + UidGen;
    }

    public static String get() {
        newStamp();
        return getNewUID();
    }

}