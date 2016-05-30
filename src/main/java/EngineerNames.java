import exceptions.InfoException;
import properties.PropertyUtils;

import java.util.*;

/**
 * Created by Levsha on 25.04.2016.
 */
public class EngineerNames {
    public static Set<String> readEngineerNames() {
        Set<String> set = new HashSet<>();
        try {
            Properties properties = PropertyUtils.getProperties();
            int count = Integer.parseInt(properties.getProperty("ENGINEERS_COUNT"));
            for (int i = 1; i <= count; i++) {
                set.add(properties.getProperty("ENGINEER" + i));
            }
            return set;
        } catch (Exception ex) {
            //ex.printStackTrace();
            throw new InfoException("Не могу прочитать фамилии инженеров.");
        }
    }

}
