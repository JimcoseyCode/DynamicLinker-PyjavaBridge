package fr.lirmm.bridge.core.impl.jep;

import fr.lirmm.bridge.core.PythonBridge;
import fr.lirmm.bridge.core.IPythonConnector;

public class JEPBridge extends PythonBridge {

// path du script de scannage du workign directory avec ses focntions user 
    private static final String INTERNAL_DISCOVERY_SCRIPT = "discovery_user_func.py";

    public JEPBridge(IPythonConnector connector) throws Exception {
        super(connector);
        this.connector.connect(INTERNAL_DISCOVERY_SCRIPT);
    }
}