package grisu.control.serviceInterfaces;

import grisu.backend.hibernate.UserDAO;
import grisu.backend.model.User;
import grisu.backend.utils.LocalTemplatesHelper;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.interfaces.InformationManager;
import grisu.model.info.dto.DtoStringList;
import grisu.model.info.dto.VO;
import grisu.settings.Environment;
import grisu.settings.ServerPropertiesManager;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class AdminInterface {

	private static Logger myLogger = LoggerFactory
			.getLogger(AdminInterface.class.getName());

	private final UserDAO userdao;

	private List<String> admin_dns;

	final private String adminUserFile;

	private final InformationManager im;

	public AdminInterface(String adminUserFile, InformationManager im,
			UserDAO userdao) {

		this.userdao = userdao;
		this.adminUserFile = adminUserFile;
		this.im = im;

		readAdminConfigFile();

	}

	private DtoStringList clearUserCache(Map<String, String> config) {

		String userdn = config.get(Constants.USER);

		if (StringUtils.isNotBlank(userdn)) {

			if (Constants.ALL_USERS.equals(userdn)) {

				List<User> allUsers = userdao.findAllUsers();

				for (User user : allUsers) {
					myLogger.debug("Clearing mountpointcache for user "
							+ user.getDn());
					user.clearMountPointCache(null);
				}
				
				// CacheManager.getInstance();
				myLogger.debug("clearing all user cache sessions...");
				for (CacheManager cm : CacheManager.ALL_CACHE_MANAGERS) {
					if (cm.getName().equals("grisu")) {
						Cache usercache = cm.getCache("userCache");
						if (usercache != null) {
							// for (Object key : usercache.getKeys()) {
							// System.out.println("KEY: " + key);
							// }
							// keys are myproxyUsername[@<myproxyHost>]
							usercache.removeAll();
						}
						break;
					}
				}
				
				return DtoStringList
						.fromSingleString("All user mountpointchaches cleared.");
			}
		

			// clear cache for all users
			User user = userdao.findUserByDN(userdn);
			myLogger.debug("Clearing mountpointcache for user " + user.getDn());
			user.clearMountPointCache(null);
			

			// CacheManager.getInstance();
			myLogger.debug("clearing all user cache sessions...");
			for (CacheManager cm : CacheManager.ALL_CACHE_MANAGERS) {
				if (cm.getName().equals("grisu")) {
					Cache usercache = cm.getCache("userCache");
					if (usercache != null) {
						// for (Object key : usercache.getKeys()) {
						// System.out.println("KEY: " + key);
						// }
						// keys are myproxyUsername[@<myproxyHost>]
						usercache.removeAll();
					}
					break;
				}
			}
			
			return DtoStringList
					.fromSingleString("Mountpointchache cleared for user "
							+ user.getDn());
		} else {
			return DtoStringList
					.fromSingleString("No user specified. Doing nothing.");
		}

	}

	public DtoStringList execute(String command, Map<String, String> config) {

		if (admin_dns == null) {
			return DtoStringList
					.fromSingleString("No valid grisu-admins.config found. Not doing anything.");
		}

		if (Constants.REFERSH_GRID_INFO.equals(command)) {
			return refreshGridInfo();
		} else if (Constants.REFRESH_CONFIG.equals(command)) {
			return refreshConfig();
		} else if (Constants.CLEAR_USER_CACHE.equals(command)) {
			return clearUserCache(config);
		} else if (Constants.LIST_USERS.equals(command)) {
			return listUsers();
		} else if ( Constants.REFRESH_TEMPLATES.equals(command)) {
			return refreshTemplates();
		}

		return DtoStringList.fromSingleString("\"" + command
				+ "\" is not a valid admin command.");
	}

	public boolean isAdmin(String dn) {
		if ( admin_dns == null) {
			return false;
		}
		return admin_dns.contains(dn);
	}

	private DtoStringList listUsers() {
		List<User> users = userdao.findAllUsers();

		List<String> dns = Lists.newLinkedList();

		for (User user : users) {
			dns.add(user.getDn());
		}

		return DtoStringList.fromStringColletion(dns);
	}

	private void readAdminConfigFile() {

		String temp;
		if (StringUtils.isBlank(adminUserFile)) {
			temp = Environment.getGrisuDirectory() + File.separator
					+ "grisu-admins.config";
		} else {
			temp = adminUserFile;
		}

		File adminConfig = new File(temp);
		if (!adminConfig.exists() || !adminConfig.canRead()) {
			admin_dns = null;
		} else {
			try {
				admin_dns = FileUtils.readLines(adminConfig);
			} catch (IOException e) {
				admin_dns = null;
			}
		}

	}

	private DtoStringList refreshConfig() {
		readAdminConfigFile();
		ServerPropertiesManager.refreshConfig();
		return DtoStringList.fromSingleString("Config refreshed.");
	}

	private DtoStringList refreshGridInfo() {

		StringBuffer temp = new StringBuffer();
		try {
			refreshConfig();
			temp.append("Config refreshed successfully.\n");
		} catch (Exception e) {
			temp.append("Refreshing config failed: "+e.getLocalizedMessage()+"\n");
		}
		try {
			im.refresh();
			Set<VO> vos = im.getAllVOs();
			temp.append("Grid info refreshed successfully.\n");
		} catch (Exception e) {
			temp.append("Refreshing grid info failed: "+e.getLocalizedMessage()+"\n");
		}


		return DtoStringList.fromSingleString(temp.toString());


	}
	
	private DtoStringList refreshTemplates() {
		StringBuffer temp = new StringBuffer();
		
		try {
			LocalTemplatesHelper.prepareTemplates();
			temp.append("Templates refreshed successfully.\n");
		} catch (Exception e) {
			temp.append("Failed refreshing templates: "+e.getLocalizedMessage()+"\n");
		}
		
		return DtoStringList.fromSingleString(temp.toString());
	}

}
