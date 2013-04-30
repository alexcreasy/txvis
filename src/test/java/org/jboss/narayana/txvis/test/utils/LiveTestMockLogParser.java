package org.jboss.narayana.txvis.test.utils;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.jboss.narayana.txvis.logprocessing.LogParser;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 30/04/2013
 * Time: 15:09
 */
public class LiveTestMockLogParser implements TailerListener {

    private final LogParser logParser;

    private boolean started = false;

    public LiveTestMockLogParser(LogParser logparser) {
        this.logParser = logparser;
    }

    public boolean hasStarted(){
        return started;
    }

    @Override
    public void init(Tailer tailer) {
        logParser.init(tailer);
    }

    @Override
    public void fileNotFound() {
        logParser.fileNotFound();
    }

    @Override
    public void fileRotated() {
        logParser.fileRotated();
    }

    @Override
    public void handle(String s) {
        if (!started)
            started = true;
        logParser.handle(s);
    }

    @Override
    public void handle(Exception e) {
        logParser.handle(e);
    }
}
