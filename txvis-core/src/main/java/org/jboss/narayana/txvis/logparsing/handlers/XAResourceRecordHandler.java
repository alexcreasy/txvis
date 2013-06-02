package org.jboss.narayana.txvis.logparsing.handlers;

import org.jboss.narayana.txvis.Utils;
import org.jboss.narayana.txvis.persistence.entities.Participant;

import java.util.regex.Matcher;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 28/04/2013
 * Time: 22:43
 */
public class XAResourceRecordHandler extends AbstractHandler {

    public static final String REGEX =
            PATTERN_TIMESTAMP +
            ".*?XAResourceRecord\\.(?<RECORDACTION>XAResourceRecord|topLevelPrepare).*?tx_uid="
            + PATTERN_TXID +
            ".*?productName=(?<PRODUCTNAME>.*?)\\sproductVersion=(?<PRODUCTVERSION>.*?)\\sjndiName=(?<JNDINAME>java:[\\w/]+)";


    public XAResourceRecordHandler() {
        super(REGEX);
    }

    @Override
    public void handle(Matcher matcher, String line) {

        switch (matcher.group("RECORDACTION")) {
            case "XAResourceRecord":
                enlistResourceManger(matcher);
                break;
            case "topLevelPrepare":
                break;
        }
    }


    void enlistResourceManger(Matcher matcher) {
        Participant rm = dao.retrieveResourceManagerByJndiName(matcher.group("JNDINAME"));

        if (rm == null)
            rm = new Participant(matcher.group("JNDINAME"),
                    matcher.group("PRODUCTNAME"), matcher.group("PRODUCTVERSION"));

         dao.createParticipantRecord(matcher.group(TXID), rm, Utils.parseTimestamp(matcher.group(TIMESTAMP)));
    }
}
