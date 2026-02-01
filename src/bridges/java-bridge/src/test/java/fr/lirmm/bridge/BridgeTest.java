package fr.lirmm.bridge;

import fr.lirmm.bridge.core.PythonExecutor;
import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;

public class BridgeTest {

    // Les tests unitaires gRPC nécessitent un serveur lancé, 
    // ils sont pour l'instant gérés par l'intégration continue ou manuellement.

    private void runCommonTests(PythonExecutor executor) throws Exception {
        // Test addition
        Object addResult = executor.execute("addition", Arrays.asList(10, 20));
        Assert.assertEquals(30, ((Number) addResult).intValue());

        // Test dire_bonjour
        Object greetResult = executor.execute("dire_bonjour", Collections.singletonList("Raphael"));
        Assert.assertTrue(greetResult.toString().contains("Raphael"));

        // Test puissance
        Object powerResult = executor.execute("puissance", Arrays.asList(2, 10));
        Assert.assertEquals(1024, ((Number) powerResult).intValue());

        // Test calculer_age
        Object ageResult = executor.execute("calculer_age", Collections.singletonList(2000));
        Assert.assertTrue(((Number) ageResult).intValue() > 0);
    }
}