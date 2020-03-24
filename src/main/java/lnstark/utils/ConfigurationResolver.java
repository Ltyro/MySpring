package lnstark.utils;

import lnstark.entity.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * 解析配置文件
 */
public class ConfigurationResolver extends Configuration {

    private Log log = LogFactory.getLog(ConfigurationResolver.class);

    private Map config = null;

    public ConfigurationResolver() {
        loadConfigurationFile();
    }

    public void loadConfigurationFile() {
        // 配置文件路径
        String prjPath = System.getProperty("user.dir");
        String resourcePath = prjPath + "\\src\\main\\resources";
        File rscFolder = new File(resourcePath);
        File[] resources = rscFolder.listFiles((file) -> file.getName().endsWith(".yml"));
        if (resources.length == 0) {
            log.error("configuration file not found");
            return;
        }
        Yaml yaml = new Yaml();
        Map m = null;
        try {
            m = yaml.load(new FileInputStream(resources[0]));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println(m);
        config = m;

        setValues();
    }

    private void setValues() {
        setPort((Integer) getValue("server.port"));
        setServletPath((String) getValue("server.servlet.context-path"));
    }

    public static void main(String[] args) {
        ConfigurationResolver configurationResolver = new ConfigurationResolver();
        Object value = configurationResolver.getValue("server.servlet.context-path");
        System.out.println(value);
    }

    public Object getValue(String key) {
        if (key == null || key.isEmpty())
            return null;
        String keys[] = key.split("\\.");
        String keyTemp;
        Map map = config;
        for (int i = 0; i < keys.length; i++) {
            keyTemp = keys[i];
            if (i == keys.length - 1)
                return map.get(keyTemp);
            map = (Map) map.get(keyTemp);
            if (map == null) {
                log.error("propertie not found!");
                return null;
            }
        }
        return null;
    }

}
