package org.charlouze.tapestry5.dojo.menubar;

import java.util.ArrayList;
import java.util.List;

import org.charlouze.tapestry5.dojo.DataDojoProps;

public class MenuBarItem extends MenuItem {
    private final List<MenuItem> items;

    public MenuBarItem() {
        this(null);
    }

    public MenuBarItem(String label) {
        this(label, null);
    }

    public MenuBarItem(String label, DataDojoProps dojoProps) {
        super(label, null, dojoProps);
        items = new ArrayList<MenuItem>();
    }

    public void add(MenuItem item) {
        items.add(item);
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public boolean hasChildren() {
        return items.size() > 0;
    }
}
