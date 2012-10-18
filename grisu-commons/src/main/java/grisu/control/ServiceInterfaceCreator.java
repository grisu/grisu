package grisu.control;

import grisu.control.exceptions.ServiceInterfaceException;
import grith.jgrith.cred.Cred;

/**
 * Interface that must be implemented if you want to provide a different means
 * of providing an object that implements the {@link ServiceInterface}
 * interface.
 * 
 * It is used by the ServiceInterfaceFactory to determine which implementation
 * to use.
 * 
 * @author Markus Binsteiner
 * 
 */
public interface ServiceInterfaceCreator {

	/**
	 * Checks whether this creator supports the creation of a serviceInterface
	 * object for the specified url.
	 * 
	 * @param url
	 *            the url (e.g. "https://grisu.vpac.org/grisu-ws/services/grisu"
	 *            or "Local")
	 * @return true if the url is supported - false if not
	 */
	boolean canHandleUrl(String url);

	/**
	 * Creates the serviceinterface object with the specified parameters.
	 * 
	 * @param interfaceUrl
	 *            the url of the serviceInterface backend. Can be a real url or
	 *            a virtual one like "Local" for the LocalServiceInterface.
	 * @param cred
	 * 			  the credential of the user
	 * @param otherOptions
	 *            other (optional) options that may be needed for this
	 *            serviceInterface to be created
	 * @return a {@link ServiceInterface} object
	 * @throws ServiceInterfaceException
	 *             if the serviceInterface couldn't be created
	 */
	ServiceInterface create(String interfaceUrl, Cred cred, Object[] otherOptions) throws ServiceInterfaceException;

}
