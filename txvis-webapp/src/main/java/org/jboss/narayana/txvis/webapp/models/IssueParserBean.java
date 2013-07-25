package org.jboss.narayana.txvis.webapp.models;

import org.jboss.narayana.txvis.plugins.Issue;
import org.jboss.narayana.txvis.plugins.PluginService;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
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

    public static final String URL_FORMAT = "<a href=\"/txvis/txinfo.jsf?includeViewParams=true&amp;txid={0}\">{1}</a>";

    @Inject
    private PluginService pluginService;

    private Set<Issue> unreadIssues = new HashSet<>();
    private Set<Issue> readIssues = new HashSet<>();

    public Collection<Issue> getUnreadIssues() {
        pluginService.scanForIssues();
        updateIssues();
        // JSF doesn't support iterating over a set
        return new LinkedList<>(unreadIssues);
    }

    public void markAllIssuesUnread() {
        //unreadIssues.addAll(readIssues);
    }

    private void updateIssues() {
        Set<Issue> latestIssues = pluginService.getIssues();

        // Perform relative complement of set readIssues in latestIssues to get unread issues.
        //latestIssues.removeAll(readIssues);
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
        //System.err.println("txuid="+txuid+", shortTxuid="+shortTxuid+", issue.getBody="+issue.getBody());
        StringBuilder sb = new StringBuilder();

        if (m.find())
            sb.append(m.replaceAll(MessageFormat.format(URL_FORMAT, issue.getCause().getId(), shortTxuid)));

        //System.err.println(sb.toString());
        issue.setBody(sb.toString());
    }
}
