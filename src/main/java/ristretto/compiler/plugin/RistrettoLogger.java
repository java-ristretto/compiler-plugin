package ristretto.compiler.plugin;

import com.sun.tools.javac.util.Log;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

abstract class RistrettoLogger {

  abstract void summary(String msg);

  abstract void diagnostic(String msg);

  static RistrettoLogger stderr(Log log) {
    return new StdErrLogger(log);
  }

  static RistrettoLogger javaUtilLogging() {
    return new JavaUtilLogger();
  }

  private static final class JavaUtilLogger extends RistrettoLogger {

    final Logger logger;

    JavaUtilLogger() {
      try {
        FileHandler fileHandler = new FileHandler("ristretto.log");
        fileHandler.setFormatter(new MessageOnlyFormatter());
        fileHandler.setLevel(Level.ALL);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new MessageOnlyFormatter());
        consoleHandler.setLevel(Level.INFO);

        Logger logger = Logger.getLogger("ristretto");
        logger.setUseParentHandlers(false);
        logger.addHandler(consoleHandler);
        logger.addHandler(fileHandler);
        logger.setLevel(Level.ALL);
        this.logger = logger;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    @Override
    public void summary(String msg) {
      logger.info(msg);
    }

    @Override
    public void diagnostic(String msg) {
      logger.fine(msg);
    }
  }

  private static final class MessageOnlyFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
      return String.format("%s%n", record.getMessage());
    }
  }

  private static final class StdErrLogger extends RistrettoLogger {

    final Log log;

    StdErrLogger(Log log) {
      this.log = log;
    }

    @Override
    public void summary(String msg) {
      log.printRawLines(msg);
    }

    @Override
    public void diagnostic(String msg) {
      log.printRawLines(msg);
    }
  }

}
