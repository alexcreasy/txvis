package org.jboss.narayana.txvis;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogReader {

    public static final String DEF_CHARSET = "UTF-8";

    private static final Logger logger = Logger.getLogger(LogReader.class.getName());

    private final Path logPath;

    /*
     *
     */
    private LogReader(String filePath)
            throws InvalidPathException, NoSuchFileException {
        Path inputPath = Paths.get(filePath);

        if (!Files.exists(inputPath))
            throw new NoSuchFileException("Unable to locate file: " + inputPath);

        this.logPath = inputPath;
    }



    public List<Transaction> getTx() {
        List<Transaction> result = new LinkedList<>();

        Charset charset = Charset.forName(DEF_CHARSET);

        try (BufferedReader reader = Files.newBufferedReader(logPath, charset)) {
            String line = null;
            while ((line = reader.readLine()) != null) {

                Pattern pattern = Pattern.compile(Patterns.CREATE_TX);
                Matcher matcher = pattern.matcher(line);

                if (matcher.find())
                {
                    result.add(new Transaction(matcher.group(2)));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("IOException while attempting to read file", e);
        }

        return result;
    }

    /**
     *
     * @param filePath
     * @return
     */
    public static LogReader getInstance(String filePath)
            throws InvalidPathException, NoSuchFileException {
        return new LogReader(filePath);
    }


    // Suppress default constructor.
    private LogReader() {
        throw new AssertionError("Cannot instantiate without file path");
    }

}
