package pikater.logging;

/**
 * User: Kuba
 * Date: 31.10.13
 * Time: 12:04
 */
public class ConsoleLogger implements Logger {
    @Override
    public void println(String text) {
        System.out.println(text);
    }

    @Override
    public void print(String text) {
        System.out.print(text);
    }
}
