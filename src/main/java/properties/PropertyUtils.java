package properties;

import exceptions.InfoException;

import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils {
    private static Properties properties = null;

    public static Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            try (InputStream in = //new FileInputStream("Report.properties")){
                         PropertyUtils.class.getClassLoader().getResourceAsStream("Report.properties")) {
                properties.load(in);
            } catch (Exception e) {
                e.printStackTrace();
                throw new InfoException("Не могу прочитать 'Report.properties'.");
            }
        }
        return properties;
    }
}
