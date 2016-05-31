import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

/**
 * Created by Levsha on 31.05.2016.
 */
public class EngineerNamesTest {
    @Test
    public void readEngineerNamesTest(){
        Set<String> names = EngineerNames.readEngineerNames();
        Assert.assertTrue(names.contains("Горишняя"));
    }
}
