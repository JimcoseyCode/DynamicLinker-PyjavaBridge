package fr.lirmm.bridge.core;

import java.util.List;

/**
 * Standard interface for executing Python functions from Java.
 */
public interface PythonExecutor {
    
    void initialize();

    Object execute(String functionName, List<Object> args) throws Exception;

    /**
     * Discover functions exposed by the Python side.
     */
    List<String> getAvailableFunctions() throws Exception;

    void close();
    
    String getStrategyName();
}