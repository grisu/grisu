package grisu.frontend.view.cli;

import grisu.jcommons.configuration.CommonGridProperties;
import grisu.jcommons.configuration.CommonGridProperties.Property;
import grisu.jcommons.constants.Enums.LoginType;
import grisu.jcommons.exceptions.CredentialException;
import grith.gridsession.GridSessionCred;
import grith.jgrith.certificate.CertificateHelper;
import grith.jgrith.cred.AbstractCred;
import grith.jgrith.cred.Cred;
import grith.jgrith.cred.X509Cred;
import grith.jgrith.cred.callbacks.AbstractCallback;
import grith.jgrith.cred.callbacks.NoCallback;
import grith.jgrith.cred.details.IdPDetail;
import grith.jgrith.cred.details.PasswordDetail;
import grith.jgrith.cred.details.StringDetail;
import grith.jgrith.credential.Credential.PROPERTY;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;

public class GridLoginParameters {

	public static Logger myLogger = LoggerFactory
			.getLogger(GridLoginParameters.class);

	public static GridLoginParameters createFromCommandlineArgs(String[] args) {

		GridLoginParameters glp = new GridLoginParameters();
		try {
			GridCliParameters settings = new GridCliParameters();
			new JCommander(settings, args);


			return createFromGridCliParameters(settings);

		} catch (ParameterException pe) {
			throw new CredentialException("Can't parse cli parameters: "
					+ pe.getLocalizedMessage());
		}

	}

	public static GridLoginParameters createFromGridCliParameters(GridCliParameters settings) {
		GridLoginParameters glp = new GridLoginParameters();
		try {

			myLogger.debug("Using non-grid params: {}",
					StringUtils.join(settings.getOtherParams(), ", "));
			glp.setOtherParameters(settings.getOtherParams());

			if (settings.isNologin()) {
				glp.setNologin(true);
				return glp;
			} else if (settings.isLogout()) {
				glp.setLogout(true);
				return glp;
			} else {
				if (settings.useX509Login()) {
					// x509
					char[] x509pw = settings.getPassword();
					glp.setLoginType(LoginType.X509_CERTIFICATE);
					glp.setPassword(x509pw);
				} else if (settings.useMyProxyLogin()) {
					glp.setLoginType(LoginType.MYPROXY);
					glp.setPassword(settings.getPassword());
					glp.setUsername(settings.getUsername());
				} else if (settings.useIdPLogin()) {
					// shib
					glp.setLoginType(LoginType.SHIBBOLETH);
					String institution = settings.getInstitution();
					glp.setInstitution(institution);
					glp.setUsername(settings.getUsername());
					glp.setPassword(settings.getPassword());
				}

				String backend = settings.getBackend();
				if (StringUtils.isNotBlank(backend)) {
					glp.setBackend(backend);
				}

				String myProxyHost = settings.getMyproxy_host();
				if (StringUtils.isNotBlank(myProxyHost)) {
					glp.setMyproxyHost(myProxyHost);
				}
			}
		} catch (ParameterException pe) {
			throw new CredentialException("Can't parse cli parameters: "
					+ pe.getLocalizedMessage());
		}

		return glp;
	}

	public static void main(String[] args) {

		GridLoginParameters p = createFromCommandlineArgs(args);

		X509Cred c = new X509Cred();
		c.init(p.getCredProperties());

		System.out.println(c.getDN());

	}

	private StringDetail backend = new StringDetail("backend",
			"Please specify the grisu backend to login to");
	private boolean nologin = false;
	private boolean logout = false;
	private StringDetail username = new StringDetail("username",
			"Please enter your username");
	private IdPDetail institution = new IdPDetail();
	private StringDetail myproxyHost = new StringDetail("myproxy_host",
			"Please enter the MyProxy host");

	private PasswordDetail password = new PasswordDetail();

	private StringDetail loginType = new StringDetail("login_type",
			"Please choose your login type");

	private List<String> otherParams;

	private AbstractCallback callback = new NoCallback();

	public static boolean useGridSession = CommonGridProperties.getDefault()
			.useGridSession();

	public GridLoginParameters() {

		institution.assignGridProperty(Property.SHIB_IDP);
		myproxyHost.assignGridProperty(Property.MYPROXY_HOST);
	}

	public Cred createCredential() {

		if (useGridSession) {
			Cred cred = new GridSessionCred();
			cred.init(getCredProperties());

			return cred;
		} else {
			Cred cred = AbstractCred.loadFromConfig(getCredProperties());
			return cred;
		}
	}

	public String getBackend() {
		return backend.getValue();
	}

	public Map<PROPERTY, Object> getCredProperties() {
		Map<PROPERTY, Object> result = Maps.newHashMap();

		if (loginType.isSet()) {
			result.put(PROPERTY.LoginType,
					LoginType.fromString(loginType.getValue()));
			if (LoginType.fromString(loginType.getValue()).equals(
					LoginType.MYPROXY)) {
				result.put(PROPERTY.MyProxyUsername, username.getValue());
				result.put(PROPERTY.MyProxyPassword, password.getValue());
			} else {
				result.put(PROPERTY.Username, username.getValue());
				result.put(PROPERTY.Password, password.getValue());
			}
		} else {
			result.put(PROPERTY.Username, username.getValue());
			result.put(PROPERTY.Password, password.getValue());
		}

		result.put(PROPERTY.IdP, institution.getValue());
		result.put(PROPERTY.MyProxyHost, myproxyHost.getValue());

		return result;
	}

	public String getInstitution() {
		return institution.getValue();
	}

	public LoginType getLoginType() {
		if (loginType == null) {
			return null;
		}
		return LoginType.fromString(loginType.getValue());
	}

	public String getMyProxyHost() {
		return myproxyHost.getValue();
	}

	public List<String> getOtherParameters() {
		return otherParams;
	}

	public char[] getPassword() {
		return password.getValue();
	}

	public String getUsername() {
		return username.getValue();
	}

	public boolean isLogout() {
		return logout;
	}

	public boolean isNologin() {
		return nologin;
	}

	public void populate() {

		LoginType lt = getLoginType();


		if ( lt == null ) {
			String idp = institution.getValue();
			if (StringUtils.isBlank(idp)) {
				idp = CommonGridProperties.getDefault().getLastShibIdp();
			}
			List<String> choices = Lists.newLinkedList();
			choices.add("Institution login");
			if (StringUtils.isNotBlank(idp)) {
				choices.add("Institution login (using: '" + idp + "')");
			}
			if (CertificateHelper.userCertExists()) {
				choices.add("Certificate login");
			}
			choices.add("MyProxy login");

			loginType.setChoices(choices);

			callback.fill(loginType);

			String ltString = loginType.getValue();
			if ("Institution login".equals(ltString)) {
				lt = LoginType.SHIBBOLETH;
				username.assignGridProperty(Property.SHIB_USERNAME);
			} else if (ltString.startsWith("Institution login (using")) {
				lt = LoginType.SHIBBOLETH;
				username.assignGridProperty(Property.SHIB_USERNAME);
				if (StringUtils.isNotBlank(idp)) {
					institution.set(idp);
				}
			} else if ("Certificate login".equals(ltString)) {
				lt = LoginType.X509_CERTIFICATE;
			} else if ("MyProxy login".equals(ltString)) {
				lt = LoginType.MYPROXY;
				username.assignGridProperty(Property.MYPROXY_USERNAME);
			} else {
				throw new CredentialException("LoginType " + ltString
						+ " not supported.");
			}

			loginType.set(lt.toString());

			switch(lt) {
			case SHIBBOLETH:
				String idpToUse = institution.getValue();
				if (StringUtils.isBlank(idpToUse)) {
					String answer = callback.getStringValue(institution);
					institution.set(answer);
				}
				if (! username.isSet() ) {
					String answer = callback.getStringValue(username);
					username.set(answer);
				}

				if (! password.isSet()) {
					char[] answer = callback.getPasswordValue(password);
					password.set(answer);
				}
				break;
			case X509_CERTIFICATE:
				if (!password.isSet()) {
					char[] answer = callback.getPasswordValue(password);
					password.set(answer);
				}
				break;
			case MYPROXY:
				if (!username.isSet()) {
					String answer = callback.getStringValue(username);
					username.set(answer);
				}
				if (!password.isSet()) {
					char[] answer = callback.getPasswordValue(password);
					password.set(answer);
				}
				break;

			default:
				throw new CredentialException("Login type: " + lt.toString()
						+ " not supported.");
			}

		}

		if (!validConfig()) {
			throw new CredentialException(
					"No valid credential after callbacks.");
		}


	}

	public void setBackend(String backend) {
		this.backend.set(backend);
	}

	public void setCallback(AbstractCallback c) {
		this.callback =c;
	}

	public void setInstitution(String institution) {
		this.institution.set(institution);
	}

	public void setLoginType(LoginType loginType) {
		this.loginType.set(loginType.toString());
	}

	public void setLogout(boolean logout) {
		this.logout = logout;
	}

	public void setMyproxyHost(String myproxyHost) {
		this.myproxyHost.set(myproxyHost);
	}

	public void setNologin(boolean nologin) {
		this.nologin = nologin;
	}

	public void setOtherParameters(List<String> other) {
		this.otherParams = other;
	}

	public void setPassword(char[] password) {
		this.password.set(password);
	}

	public void setUsername(String username) {
		this.username.set(username);
	}

	public boolean validConfig() {

		if (isNologin() || isLogout()) {
			return false;
		}

		LoginType lt = getLoginType();

		if (lt == null) {
			return false;
		}

		switch (lt) {
		case MYPROXY:
			if (!institution.isSet() && username.isSet() && password.isSet()) {
				return true;
			} else {
				return false;
			}
		case SHIBBOLETH:
			if (institution.isSet() && username.isSet() && password.isSet()) {
				return true;
			} else {
				return false;
			}
		case X509_CERTIFICATE:
			if (password.isSet()) {
				return true;
			} else {
				return false;
			}

		default:
			return false;
		}

	}

}
