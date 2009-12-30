package mt.com.go.rule.engine.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;

public class RuleEngineLogger {

    private static Logger logger = null;

    public static void logDebug(Object obj, String message, Throwable t) {

        initLogger();

        if (obj != null && !logger.getName().equalsIgnoreCase(obj.getClass().getSimpleName())) {
            logger = Logger.getLogger(obj.getClass().getSimpleName());
        }

        if (t == null) {
            logger.debug(message);
        } else {
            //logger.debug(message, t);
            logger.debug(message + " " + getStackTrace(t));
        }

    }

    public static void logDebug(Object obj, String message) {
        logDebug(obj, message, null);
    }

    private static void initLogger() {

        if (logger == null) {

            logger = Logger.getRootLogger();

            Layout layout = new Layout() {

                @Override
                public String format(LoggingEvent le) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                    return sdf.format(new Date(le.getTimeStamp())) + " [" + le.getLevel().toString() + "] [" + le.getThreadName() + "] [" + le.getLoggerName() + "] " + le.getMessage() + "\n";
                }

                @Override
                public boolean ignoresThrowable() {
                    return false;
                }

                public void activateOptions() {
                }
            };

            RollingFileAppender fileAppender = null;
            try {
                fileAppender = new RollingFileAppender(layout, "C:\\decisionEngine.log", true);
                fileAppender.setMaxFileSize("10MB");
                fileAppender.setMaxBackupIndex(1000);

            } catch (Exception e) {
                e.printStackTrace();
            }

            ConsoleAppender consoleAppender = new ConsoleAppender(layout);

            AsyncAppender asyncAppender = new AsyncAppender();
            asyncAppender.addAppender(fileAppender);
            asyncAppender.addAppender(consoleAppender);

            logger.addAppender(asyncAppender);
            
            logger.setLevel((Level) Level.ALL);

            logger.debug("Log file initialised...");

        }
    }

    public static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        
        Throwable x = t.getCause();
        while (x != null) {
            x.printStackTrace(pw);
            x = x.getCause();
        }


        pw.flush();
        sw.flush();
        return sw.toString();
    }


}
