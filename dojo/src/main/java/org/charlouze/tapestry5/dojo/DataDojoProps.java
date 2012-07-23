package org.charlouze.tapestry5.dojo;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DataDojoProps {
    private final Map<String, String> props;

    public DataDojoProps() {
        props = new HashMap<String, String>();
    }

    public void add(String key, String value) {
        props.put(key, value);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        String colon = ":";
        for (Entry<String, String> entry : props.entrySet()) {
            str.append(entry.getKey()).append(colon).append(entry.getValue());
        }
        return str.toString();
    }
}
