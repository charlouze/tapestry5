package org.charlouze.tapestry5.dojo.services;

import org.apache.tapestry5.json.JSONArray;

public interface DojoSupport {
    public String getTheme();

    public void setTheme(String theme);

    public void addRequiredComponents(String... components);

    public void addParserForElements(String... elementIds);

    public void addInitializerCall(String functionName, JSONArray parameter);
}
