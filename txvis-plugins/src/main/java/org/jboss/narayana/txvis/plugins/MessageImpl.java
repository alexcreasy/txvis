package org.jboss.narayana.txvis.plugins;

/**
 * @Author Alex Creasy &lt;a.r.creasy@newcastle.ac.uk$gt;
 * Date: 17/07/2013
 * Time: 17:53
 */
public class MessageImpl implements Message {

    private final String title;
    private final String body;

    public MessageImpl(String title, String body) {
        this.title = title;
        this.body = body;
    }


    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getBody() {
        return body;
    }
}
