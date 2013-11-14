package pikater.logging;

/**
 * User: Kuba
 * Date: 31.10.13
 * Time: 12:04
 */
public class ConsoleLogger implements Logger {
    public void log(String source,String text) {
        System.out.println(source + ": " + text);
    }

    @Override
    public void logError(String source, String errorDescription) {
        logError(source,errorDescription,Severity.Normal);
    }

    @Override
    public void logError(String source, String errorDescription, Severity severity) {
        if (severity==Severity.Critical)
        {
            System.err.println("Critical Error in "+source + ": " + errorDescription);
        }
        else
        {
            System.err.println("Error in "+source + ": " + errorDescription);
        }
    }
}
