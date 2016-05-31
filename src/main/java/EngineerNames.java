import exceptions.InfoException;

import java.util.Set;

/**
 * Created by Levsha on 25.04.2016.
 */
public class EngineerNames {
    public static Set<String> readEngineerNames() {
        try {
            //String fileName = PropertyUtils.getProperties().getProperty("ENGINEERS");
            //return RP_ExcelUtils.readEngineers(fileName);
            return RP_ExcelUtils.readEngineers("Engineers.xlsx");
        } catch (Exception ex) {
            //ex.printStackTrace();
            throw new InfoException("Не могу прочитать фамилии инженеров.");
        }
    }

}
