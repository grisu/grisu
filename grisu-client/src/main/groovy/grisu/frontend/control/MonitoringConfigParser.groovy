package grisu.frontend.control
import grisu.jcommons.git.GitRepoUpdater
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Project: grisu
 *
 * Written by: Markus Binsteiner
 * Date: 23/09/13
 * Time: 2:10 PM
 */
class MonitoringConfigParser {

    static final Logger log = LoggerFactory.getLogger(MonitoringConfigParser.class);

    public static Collection<MonitoringConfig> parseConfig(def pathToConfig) {

        String configString = "n/a"
        def config

        if (!pathToConfig) {
            throw new RuntimeException("No path to config specified")
        }

        if (pathToConfig instanceof File) {
            pathToConfig = pathToConfig.getAbsolutePath()
        }

        if (!pathToConfig instanceof File) {
            throw new RuntimeException("Config type not valid: " + pathToConfig.getClass().getName());
        }

        if (pathToConfig.startsWith('git://')) {
            log.debug 'Checking out/updating config from git: "' + pathToConfig + '"...'

            File gitRepoFile = GitRepoUpdater.ensureUpdated(pathToConfig)
            configString = gitRepoFile.text
            pathToConfig = gitRepoFile.getAbsolutePath()
            config = new ConfigSlurper().parse(new File(pathToConfig).toURL())

        } else if (pathToConfig.startsWith('http')) {
            log.debug 'Retrieving remote config from "' + pathToConfig + '"...'
            config = new ConfigSlurper().parse(new URL(pathToConfig))
            configString = "n/a"
        } else {
            log.debug 'Using local config from "' + pathToConfig + '"...'
            File c = new File(pathToConfig)
            configString = c.text
            config = new ConfigSlurper().parse(c.toURL())
        }

        def configs = []

        for (def e in config) {

            def name = e.key
            def object = e.value


            switch (object.class) {

                case MonitoringConfig:

                    configs.add(object);

                    break

                default:

                    break
            }
        }

        return configs
    }




}
