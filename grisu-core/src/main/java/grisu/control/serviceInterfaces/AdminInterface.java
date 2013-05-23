package grisu.control.serviceInterfaces;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import grisu.backend.hibernate.UserDAO;
import grisu.backend.model.User;
import grisu.backend.utils.LocalTemplatesHelper;
import grisu.jcommons.constants.Constants;
import grisu.jcommons.interfaces.InformationManager;
import grisu.model.info.dto.DtoStringList;
import grisu.model.info.dto.VO;
import grisu.settings.Environment;
import grisu.settings.ServerPropertiesManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        if (StringUtils.isBlank(userdn) || Constants.ALL_USERS.equals(userdn)) {

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
                    .fromSingleString(Constants.ALL_USERS);

        } else {


            // clear cache for all users
            //User user = userdao.findUserByDN(userdn);
            Collection<User> users = getUsersMatch(userdn);
            List<String> userDns = Lists.newArrayList();

            for (User user : users ) {
                    myLogger.debug("Clearing mountpointcache for user " + user.getDn());
                    user.clearMountPointCache(null);
                    userDns.add(user.getDn());
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

            if (userDns.size() == 0 ) {
                userDns.add("n/a");
            }

            return DtoStringList.fromStringList(userDns);

        }

    }

    public DtoStringList execute(String command, Map<String, String> config) {

        if (admin_dns == null) {
            return DtoStringList
                    .fromSingleString("No valid grisu-admins.config found. Not doing anything.");
        }

        userdao.findAllUsersThatContain("markus");

        if (Constants.REFRESH_GRID_INFO.equals(command)) {
            return refreshGridInfo();
        } else if (Constants.REFRESH_CONFIG.equals(command)) {
            return refreshConfig();
        } else if (Constants.CLEAR_USER_CACHE.equals(command)) {
            return clearUserCache(config);
        } else if (Constants.LIST_USERS.equals(command)) {
            String u = Constants.ALL_USERS;
            if ( config != null && StringUtils.isNotBlank(config.get(Constants.USER))) {
                u = config.get(Constants.USER);
            }
            return listUsers(u);
        } else if (Constants.REFRESH_TEMPLATES.equals(command)) {
            return refreshTemplates();
        }

        return DtoStringList.fromSingleString("\"" + command
                + "\" is not a valid admin command.");
    }

    public boolean isAdmin(String dn) {
        if (admin_dns == null) {
            return false;
        }
        return admin_dns.contains(dn);
    }

    private Collection<String> getUserDNsMatch(String user) {

        List<User> users = getUsersMatch(user);

        return Collections2.transform(users, new Function<User, String>() {
            public String apply(User input) {
                return input.getDn();
            }
        });
    }

    private List<User> getUsersMatch(String user) {
        List<User> users = userdao.findAllUsers();

        if ( StringUtils.isBlank(user) || Constants.ALL_USERS.equals(user) ) {
            user = null;
        } else {
            user.toLowerCase();
        }

        List<User> dns = Lists.newLinkedList();

        for (User u : users) {
            if ( user == null || u.getDn().toLowerCase().contains(user) ) {
                dns.add(u);
            }
        }

        return dns;
    }

    private DtoStringList listUsers(String user) {
        return DtoStringList.fromStringColletion(getUserDNsMatch(user));
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
        List<String> msg = ServerPropertiesManager.refreshConfig();
        return DtoStringList.fromStringList(msg);
    }

    private DtoStringList refreshGridInfo() {

        List<String> temp = Lists.newArrayList();
        String msg = "n/a";
        try {
            refreshConfig();
            temp.add("Config refreshed successfully.");
        } catch (Exception e) {
            temp.add("Refreshing config failed: " + e.getLocalizedMessage());
        }
        try {
            msg = im.refresh();
            Set<VO> vos = im.getAllVOs();
        } catch (Exception e) {
            msg = "Refreshing grid info failed: " + e.getLocalizedMessage();
        }


        return DtoStringList.fromSingleString(msg);


    }

    private DtoStringList refreshTemplates() {
        StringBuffer temp = new StringBuffer();

        try {
            LocalTemplatesHelper.prepareTemplates();
            temp.append("Templates refreshed successfully.\n");
        } catch (Exception e) {
            temp.append("Failed refreshing templates: " + e.getLocalizedMessage() + "\n");
        }

        return DtoStringList.fromSingleString(temp.toString());
    }

}
