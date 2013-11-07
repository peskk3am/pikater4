package pikater.logging;

/**
 * User: Kuba
 * Date: 31.10.13
 * Time: 11:55
 */
public interface Logger {
    void log(String source, String text);
    void logError(String source,String errorDescription);
    void logError(String source,String errorDescription,Severity severity);
}
