package org.jboss.narayana.txvis.test.input;

import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.jboss.narayana.txvis.parser.Patterns;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 18/04/2013
 * Time: 12:55
 */
public class DummyLogParser implements TailerListener {

    private final List<String> log = Collections.synchronizedList(new ArrayList<String>());

    private FileWriter fw;
    private BufferedWriter bw;



    public DummyLogParser(String filePath) {
        try {
            File file = new File(filePath);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file.getAbsoluteFile());
            bw = new BufferedWriter(fw);
        }
        catch (IOException e) {}

    }

    public void handle(String line) {
        if (line.matches(Patterns.BEGIN) && !(line.matches("BasicAction::Begin"))) {
            write("[MISMATCH] " + line);
        }
        else if (line.matches(Patterns.BEGIN)) {
            write(line);
        }

    }

    private void write(String line) {
        try {
            bw.write(line);
            bw.newLine();
            bw.flush();
        } catch (IOException e) {}
    }

    public String getLine(int index) {
        return this.log.get(index);
    }

    public int getSize() {
        return this.log.size();
    }

    public void close() {
        try {
            bw.close();
        } catch (IOException e) {}
    }

    public void init(Tailer tailer) {}
    public void fileNotFound() {}
    public void fileRotated() {}
    public void handle(Exception ex) {}
}
