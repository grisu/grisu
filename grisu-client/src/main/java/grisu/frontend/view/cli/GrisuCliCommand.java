package grisu.frontend.view.cli;

import grisu.control.ServiceInterface;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 8/08/13
 * Time: 4:23 PM
 */
public abstract class GrisuCliCommand {

    protected ServiceInterface si;

    abstract public void execute() throws Exception;

    public void setServiceInterface(ServiceInterface si) {
        this.si = si;
    }
}
