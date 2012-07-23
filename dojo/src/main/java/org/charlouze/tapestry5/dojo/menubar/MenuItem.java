package org.charlouze.tapestry5.dojo.menubar;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.json.JSONObject;
import org.charlouze.tapestry5.dojo.DataDojoProps;

public class MenuItem {
    private String label;

    private DataDojoProps dojoProps;

    private Link link;

    public MenuItem() {
        this(null, null);
    }

    public MenuItem(String label) {
        this(label, null);
    }

    public MenuItem(String label, Link link) {
        this(label, link, null);
    }

    public MenuItem(String label, Link link, DataDojoProps dojoProps) {
        this.label = label;
        this.link = link;
        this.dojoProps = dojoProps;
        if (link != null) {
            if (this.dojoProps == null) {
                this.dojoProps = new DataDojoProps();
            }
            this.dojoProps.add("onClick", "function(){window.location='" + link.toURI() + "';}");
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Link getLink() {
        return link;
    }

    public DataDojoProps getDojoProps() {
        return dojoProps;
    }

    public void setDojoProps(DataDojoProps dojoProps) {
        this.dojoProps = dojoProps;
    }

    public void setOnclickFunction(String functionName, JSONObject parameter) {
        if (StringUtils.isBlank(functionName))
            throw new IllegalArgumentException("Function name must not be null");

        if (dojoProps == null) {
            dojoProps = new DataDojoProps();
        }

        if (parameter == null) {
            dojoProps.add("onClick", "function(){" + functionName + "();}");
        } else {
            dojoProps.add("onClick", "function(){" + functionName + "(" + parameter.toCompactString() + ");}");
        }
    }
}
