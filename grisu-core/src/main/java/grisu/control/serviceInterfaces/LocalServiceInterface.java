package grisu.control.serviceInterfaces;

import grisu.backend.hibernate.HibernateSessionFactory;
import grisu.backend.model.User;
import grisu.control.ServiceInterface;
import grisu.control.exceptions.NoSuchTemplateException;
import grisu.control.exceptions.NoValidCredentialException;
import grisu.settings.ServiceTemplateManagement;
import grith.jgrith.cred.AbstractCred;
import grith.jgrith.cred.Cred;

import java.util.Arrays;
import java.util.Date;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang.StringUtils;

public class LocalServiceInterface extends AbstractServiceInterface implements
ServiceInterface {

	private Cred credential = null;

	private User user;


	// @Override
	protected final Cred getCredential() {

		if ( credential == null) {
			throw new NoValidCredentialException("No credential set.");
		}
		
		if ( !credential.isValid() ) {
			throw new NoValidCredentialException("Credential set but not valid.");
		}

		return credential;

	}



	public final long getCredentialEndTime() {

		return new Date().getTime() + getCredential().getRemainingLifetime()*1000L;
	}

	@Override
	public String getInterfaceType() {

		return "Local";
	}

	public final String getTemplate(final String application)
			throws NoSuchTemplateException {
		final String temp = ServiceTemplateManagement.getTemplate(application);

		if (StringUtils.isBlank(temp)) {
			throw new NoSuchTemplateException(
					"Could not find template for application: " + application
					+ ".");
		}
		return temp;
	}

	@Override
	protected final synchronized User getUser() {

		if (user == null) {
			this.user = User.createUser(getCredential(), this);
		}

		user.setCredential(getCredential());

		return user;
	}

	public final String[] listHostedApplicationTemplates() {
		return ServiceTemplateManagement.getAllAvailableApplications();
	}

	public final void init(final Cred cred) {

		try {
			// init database and make sure everything is all right
			HibernateSessionFactory.getSessionFactory();
		} catch (final Throwable e) {
			throw new RuntimeException("Could not initialize database.", e);
		}

		try {
			this.credential = cred;
		} catch (final RuntimeException re) {
			throw re;
		} catch (final Exception e) {
			// e.printStackTrace();
			throw new NoValidCredentialException("No valid credential: "
					+ e.getLocalizedMessage());
		}

	}

	public final String logout() {
		//Arrays.fill(passphrase, 'x');
		return null;
	}



	@POST
	@Path("/user/login")
	public void login(@QueryParam("username") String username,
			@QueryParam("password") String password,
			@QueryParam("host") String host, @QueryParam("port") int port) {
		// TODO Auto-generated method stub
		
	}



}
