import inspect
import json
import os
import sys

# Add python env to path to find user modules
PYTHON_ENV_PATH = os.path.abspath("src/bridges/python-env")
sys.path.append(PYTHON_ENV_PATH)

try:

    from app.decorators import FUNCTION_REGISTRY

    from app.loader import load_user_script

    

    # Simulation de la logique de lecture de config pour le générateur (si loader ne le fait pas via env)

    # Note: loader.load_user_script le fait déjà, mais on veut s'assurer que le path est bon

    if "BRIDGE_USER_FILE" not in os.environ:

        if os.path.exists("bridge.config"):

             os.environ["BRIDGE_CONFIG_FOUND"] = "true" # Juste un marqueur



    load_user_script()

except ImportError as e:


    print(f"Error loading python environment: {e}")
    sys.exit(1)

JAVA_OUTPUT_DIR = "src/bridges/java-bridge/src/main/java/fr/lirmm/bridge/generated"
PACKAGE_NAME = "fr.lirmm.bridge.generated"


def generate_java_class(func_name, func):
    """
    Generates a Java class that wraps the Python function call transparently via the BridgeService.
    """
    class_name = f"Func_{func_name}"

    # Simple docstring parsing for return type hint (very basic)
    # Default to Object/void
    return_type = "Object"

    java_code = f"""package {PACKAGE_NAME};

import fr.lirmm.bridge.core.BridgeService;
import java.util.Arrays;
import java.util.List;

/**
 * AUTO-GENERATED WRAPPER FOR PYTHON FUNCTION '{func_name}'
 * Source: @user_func in Python
 */
public class {class_name} {{

    private static BridgeService bridge;

    public static void setBridge(BridgeService b) {{
        bridge = b;
    }}

    /**
     * Executes the python function '{func_name}' transparently.
     * @param args Arguments to pass to Python
     * @return Result from Python (typed as Object, cast as needed)
     */
    public static Object run(Object... args) {{
        if (bridge == null) {{
            throw new RuntimeException("Bridge not initialized! Call {class_name}.setBridge(service) first.");
        }}
        try {{
            return bridge.callPython("{func_name}", args);
        }} catch (Exception e) {{
            throw new RuntimeException("Failed to execute python function '{func_name}'", e);
        }}
    }}
    
    /**
     * Convenience method expecting a List, matching the old interface.
     */
    public static Object run(List<Object> args) {{
        return run(args.toArray());
    }}
}}
"""
    return class_name, java_code


def main():
    if not os.path.exists(JAVA_OUTPUT_DIR):
        os.makedirs(JAVA_OUTPUT_DIR)

    print(f"🔍 Scanning for @user_func in {PYTHON_ENV_PATH}...")
    print(
        f"📝 Found {len(FUNCTION_REGISTRY)} functions: {list(FUNCTION_REGISTRY.keys())}"
    )

    for name, func in FUNCTION_REGISTRY.items():
        class_name, code = generate_java_class(name, func)
        file_path = os.path.join(JAVA_OUTPUT_DIR, class_name + ".java")

        # PROTECTION : Ne pas écraser si une implémentation native existe
        if os.path.exists(file_path):
            with open(file_path, "r") as f:
                content = f.read()
                if "// @NATIVE_IMPLEMENTATION" in content:
                    print(
                        f"⏩ Skipping {class_name}.java (Native Implementation detected)"
                    )
                    continue

        with open(file_path, "w") as f:
            f.write(code)

        print(f"✅ Generée {class_name}.java")


if __name__ == "__main__":
    main()
