package scout;

/**
 * Wrapper class for environment variables.
 */
public class Config {

    /**
     * Gets the value of an environment variable given the key of the environment variable.
     * @param key the key of the environment variable
     * @return the value of the  environment variable
     */
    public static String get(String key) {
        return System.getenv(key.toUpperCase());
    }
}
