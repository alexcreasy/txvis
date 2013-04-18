package org.jboss.narayana.txvis.test.input;

import org.jboss.narayana.txvis.input.LogFileReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 18/04/2013
 * Time: 13:11
 */
@RunWith(JUnit4.class)
public class LogFileReaderTest {

    private static final String FILE_PATH = "/Users/alex/Documents/workspace/txvis/src/test/resources/test.log";

    @Test
    public void readFromBeginningTest() throws Exception {

        File log = new File(FILE_PATH);

        if (!log.canRead())
            Assert.fail("Unable to read from test file");

        DummyLogParser parser = new DummyLogParser();
        LogFileReader reader = new LogFileReader(log, parser, true);

        Thread readerThread = new Thread(reader);
        readerThread.start();
        Thread.sleep(5000);
        reader.stop();
        Assert.assertEquals("Did not read correct number of lines", 2932, parser.getSize());
    }

}
