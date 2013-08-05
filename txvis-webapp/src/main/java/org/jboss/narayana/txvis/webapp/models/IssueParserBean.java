package org.jboss.narayana.txvis.webapp.models;

import org.jboss.narayana.txvis.plugins.Issue;
import org.jboss.narayana.txvis.plugins.PluginService;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 25/07/2013
 * Time: 16:48
 */
@SessionScoped
@Named
public class IssueParserBean implements Serializable {

    public static final String TXUID_LINK_FORMAT = "<a href=\"/txvis/txinfo.jsf?includeViewParams=true&amp;txid={0}\">{1}</a>";
    public static final String FORUM_LINK_FORMAT = "<a href=\"https://community.jboss.org/search.jspa?q={0}&containerType=14" +
            "&container=2040\">Search JBoss forums for help</a>";


    @Inject
    private PluginService pluginService;

    private Set<Issue> unreadIssues = new HashSet<>();
    private Set<Issue> readIssues = new HashSet<>();

    public Collection<Issue> getUnreadIssues() {
        pluginService.scanForIssues();
        updateIssues();

        // facelets don't support iterating over a set
        return new LinkedList<>(unreadIssues);
    }

    public void markAllIssuesUnread() {
        //unreadIssues.addAll(readIssues);
    }

    private void updateIssues() {
        Set<Issue> latestIssues = pluginService.getIssues();

        // Ditch any issues which are no longer valid
        unreadIssues.retainAll(latestIssues);
        // Don't parse any issues a second time.
        latestIssues.removeAll(unreadIssues);

        for (Issue issue : latestIssues)
            parseIssue(issue);

        unreadIssues.addAll(latestIssues);
    }

    private void parseIssue(Issue issue) {
        final String txuid = issue.getCause().getTxuid();
        final String shortTxuid = MessageFormat.format("...{0}", txuid.substring(txuid.length() - 5, txuid.length()));

        Pattern p = Pattern.compile(txuid);
        Matcher m = p.matcher(issue.getBody());
        StringBuilder sb = new StringBuilder();

        if (m.find())
            sb.append(m.replaceAll(MessageFormat.format(TXUID_LINK_FORMAT, issue.getCause().getId(), shortTxuid)));
        else
            sb.append(issue.getBody());

        sb.append("<p>- ").append(produceForumLink(issue)).append("</p>");
        issue.setBody(sb.toString());
    }

    private String produceForumLink(Issue issue) {
        final StringBuilder sb = new StringBuilder();

        for (String tag : issue.getTags())
            sb.append(tag).append("%20");

        return MessageFormat.format(FORUM_LINK_FORMAT, sb.toString());
    }
}
