package org.charlouze.tapestry5.dojo.components;

import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ClientElement;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.runtime.Event;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.charlouze.tapestry5.dojo.annotations.DojoRequire;
import org.charlouze.tapestry5.dojo.menubar.MenuBarItem;
import org.charlouze.tapestry5.dojo.menubar.MenuItem;
import org.charlouze.tapestry5.dojo.services.DojoSupport;

@DojoRequire(components = { "dijit.MenuBar", "dijit.PopupMenuBarItem", "dijit.DropDownMenu", "dijit.MenuItem" })
public class MenuBar implements ClientElement {
    @Environmental
    private JavaScriptSupport javaScriptSupport;

    @Environmental
    private DojoSupport dojoSupport;

    @Parameter(value = "prop:componentResources.id", defaultPrefix = BindingConstants.LITERAL)
    private String id;

    @Property
    @Parameter(required = true)
    private List<MenuBarItem> source;

    @Property
    private MenuBarItem barItem;

    @Property
    private MenuItem item;

    @Property(read = false)
    private String clientId;

    public void setupRender(MarkupWriter writer, Event event) {
        clientId = javaScriptSupport.allocateClientId(id);
        dojoSupport.addParserForElements(clientId);
    }

    @Override
    public String getClientId() {
        return clientId;
    }
}
