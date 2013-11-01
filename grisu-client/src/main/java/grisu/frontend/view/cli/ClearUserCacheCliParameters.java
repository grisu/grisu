package grisu.frontend.view.cli;

import com.beust.jcommander.Parameter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import grisu.jcommons.constants.Constants;
import grisu.model.info.dto.DtoStringList;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClearUserCacheCliParameters extends AbstractAdminCliCommand {

    @Parameter(description = "list of users")
    private List<String> users;

    public List<String> getUsers() {
        return users;
    }

    @Override
    public void execute() throws Exception {

        List<String> temp = getUsers();
        if (temp == null || temp.size() == 0 || temp.contains(Constants.ALL_USERS)) {
            temp = Lists.newArrayList(Constants.ALL_USERS);
        }

        for (String user : temp) {

            System.out.println("Clearing user(s) for: " + user);

            DtoStringList allUsers = execute(Constants.LIST_USERS);
            Set<String> toClear = Sets.newTreeSet();
            for (String u : allUsers.asArray()) {
                if (u.toLowerCase().contains(user.toLowerCase())) {
                    toClear.add(u);
                }
            }

            if (toClear.isEmpty()) {
                System.out.println("No matching user for: " + user);
                continue;
            }

            for (String u : toClear) {
                Map<String, String> config = ImmutableMap.of(Constants.USER, u);
                System.out.println("\tClearing: " + u);

                DtoStringList result = execute(Constants.CLEAR_USER_CACHE,
                        config);

                for (String line : result.asArray()) {
                    if (!"n/a".equalsIgnoreCase(line)) {
                        System.out.println("\t" + line);
                    }
                }
            }

        }


    }
}
