package org.jboss.narayana.txvis;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;

public class LogParser {

    private static final Charset DEF_CHARSET = Charset.forName("UTF-8");
    private static final Logger logger = Logger.getLogger(LogParser.class.getName());

    private final Path logPath;
    private final Map<String, Transaction> threadList = new HashMap<String, Transaction>();
    private final TransactionBean txBean;

    /*
     *
     */
    private LogParser(String filePath, TransactionBean txBean)
            throws NullPointerException, InvalidPathException, NoSuchFileException {

        if (txBean == null)
            throw new NullPointerException("Expected a TransactionBean, received null");

        Path inputPath = Paths.get(filePath);
        if (!Files.exists(inputPath))
            throw new NoSuchFileException("Unable to locate file: " + inputPath);

        this.txBean = txBean;
        this.logPath = inputPath;
    }

    /**
     *
     * @param filePath
     * @return
     */
    public static LogParser getInstance(String filePath, TransactionBean txBean)
            throws NullPointerException, InvalidPathException, NoSuchFileException {
        return new LogParser(filePath, txBean);
    }


    public void run() {
        List<Transaction> result = new LinkedList<>();

        try (BufferedReader reader =
                     Files.newBufferedReader(logPath, DEF_CHARSET)) {

            String line = null;
            while ((line = reader.readLine()) != null)
                parseln(line);

        } catch (IOException e) {
            throw new RuntimeException("IOException while attempting to read file", e);
        }
    }

    private void parseln(String line) {
        Matcher matcher = Patterns.TX_BEGIN.matcher(line);

        if (matcher.find())
        {
           txBean.create(matcher.group(2));
        }

    }


    // Suppress default constructor.
    private LogParser() {
        throw new AssertionError("Cannot instantiate without file path");
    }
}