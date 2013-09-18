package grisu.backend.model.fs;

import grith.jgrith.cred.Cred;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 18/09/13
 * Time: 12:36 PM
 */
public interface SharedSubfolderNameCalculator {

    public String getSubfolderName(Cred cred);
}
