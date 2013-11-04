package grisu.backend.model.fs;

import grith.jgrith.cred.Cred;
import org.apache.commons.lang3.StringUtils;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 18/09/13
 * Time: 12:37 PM
 */
public class DnSubfolderCalculator implements SharedSubfolderNameCalculator {

    public static void main(String[] args) {

        String dn = "DC=nz,DC=org,DC=bestgrid,DC=slcs,O=The University of Auckland,CN=Markus Binsteiner _bK32o4Lh58A3vo9kKBcoKrJ7ZY";
        String dn2 = "DC=nz,DC=org,DC=bestgrid,DC=slcs,O=The University of Auckland,CN=Markus Binsteiner 125883";

        DnSubfolderCalculator c = new DnSubfolderCalculator(true, ".", true);

        System.out.println(c.getSubfolderName(dn));
        System.out.println(c.getSubfolderName(dn2));
    }

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
        return getSubfolderName(cred.getDN());
    }

    public String getSubfolderName(String dn) {

        if ( shortName ) {
            int index = dn.lastIndexOf("CN=");
            if ( index > 0 ) {
                dn = dn.substring(index+3);

                // check whether last substring is longer than 25 characters,
                // if it is, remove it since it's probably the shared token
                String[] tokens = StringUtils.split(dn);
                String lastToken = tokens[tokens.length-1];
                boolean isInteger = false;
                try {
                    Integer.parseInt(lastToken);
                    isInteger = true;
                } catch (Exception e) {

                }
                if ( tokens.length > 2 && ( lastToken.length() >= 25 || isInteger )) {
                    dn = StringUtils.join(tokens, " ", 0, tokens.length-1);
                }


            }
        }

        String temp = dn.replace("=", seperator).replace(",", seperator).replace(" ", seperator);
        if ( lowercase ) {
            temp = temp.toLowerCase();
        }
        return temp;

    }
}
