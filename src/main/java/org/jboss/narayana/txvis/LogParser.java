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
    private final Map<String, String> threadList = new HashMap<String, String>();
    private final TransactionDAO txDAO;
    private final ParticipantDAO participantDAO;

    /*
     *
     */
    private LogParser(String filePath, TransactionDAO txDAO, ParticipantDAO participantDAO)
            throws NullPointerException, InvalidPathException, NoSuchFileException {

        if (txDAO == null)
            throw new NullPointerException("Expected a TransactionDAO, received null");

        if (participantDAO == null)
            throw new NullPointerException("Excpected a ParticipantDAO, received null");

        Path inputPath = Paths.get(filePath);
//        if (!Files.exists(inputPath))
//            throw new NoSuchFileException("Unable to locate file: " + inputPath);

        this.txDAO = txDAO;
        this.participantDAO = participantDAO;
        this.logPath = inputPath;
    }

    /**
     *
     * @param filePath
     * @return
     */
    public static LogParser getInstance(String filePath, TransactionDAO txBean,
            ParticipantDAO participantDAO) throws NullPointerException,
            InvalidPathException, NoSuchFileException {
        return new LogParser(filePath, txBean, participantDAO);
    }


    public void run() {
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
        // MATCH BEGIN TRANSACTION
        Matcher matcher = Patterns.TX_BEGIN.matcher(line);

        if (matcher.find()) {
           txDAO.create(matcher.group(2));
           logger.trace("txDAO.create( " + matcher.group(2) + " )");
           threadList.put(matcher.group(1), matcher.group(2));
           logger.trace("threadList.put( " + matcher.group(1) + " , " + matcher.group(2) + " )");
        }

        // MATCH ENLIST PARTICIPANT
        matcher = Patterns.TX_ENLIST.matcher(line);

        if (matcher.find()) {
            String txID = threadList.get(matcher.group(1));
            if (txID == null)
                logger.error("Thread: " + matcher.group(1)
                        + " does not have an associated transaction");

            Transaction tx = txDAO.get(txID);
            Participant participant = participantDAO.getParticipant(matcher.group(2));

            tx.addParticipant(participant);
        }
    }


    public void printThreadList() {
        for (String thread : threadList.keySet())
            System.out.println(thread + " -> " + threadList.get(thread));
    }

    // Suppress default constructor.
    private LogParser() {
        throw new AssertionError("Cannot instantiate without file path");
    }
}