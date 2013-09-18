package grisu.backend.model.fs;

import grith.jgrith.cred.Cred;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 18/09/13
 * Time: 12:37 PM
 */
public class DnSubfolderCalculator implements SharedSubfolderNameCalculator {

    private final boolean shortName;
    private final String seperator;
    private final boolean lowercase;

    public DnSubfolderCalculator() {
        this(true, ".", true);
    }

    public DnSubfolderCalculator(boolean shortName, String seperator, boolean lowercase) {
        this.shortName = shortName;
        this.seperator = seperator;
        this.lowercase = lowercase;
    }

    public String getSubfolderName(Cred cred) {

        String dn = cred.getDN();

        if ( shortName ) {
            int index = dn.lastIndexOf("CN=");
            if ( index > 0 ) {
                dn = dn.substring(index+3);
            }
        }

        String temp = dn.replace("=", seperator).replace(",", seperator).replace(" ", seperator);
        if ( lowercase ) {
            temp = temp.toLowerCase();
        }
        return temp;

    }
}
