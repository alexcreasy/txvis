package org.jboss.narayana.txvis.test.input;

import org.jboss.narayana.txvis.input.LogParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 18/04/2013
 * Time: 12:55
 */
public class DummyLogParser implements LogParser {

    private final List<String> log = Collections.synchronizedList(new ArrayList<String>());

    public void parseln(String line) {
        this.log.add(line);
    }

    public String getLine(int index) {
        return this.log.get(index);
    }

    public int getSize() {
        return this.log.size();
    }
}
