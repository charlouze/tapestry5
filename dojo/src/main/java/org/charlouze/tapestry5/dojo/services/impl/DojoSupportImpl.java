package org.charlouze.tapestry5.dojo.services.impl;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.dom.Document;
import org.apache.tapestry5.dom.Element;
import org.apache.tapestry5.func.Predicate;
import org.apache.tapestry5.ioc.internal.util.CollectionFactory;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.javascript.InitializationPriority;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.charlouze.tapestry5.dojo.DojoSymbolConstants;
import org.charlouze.tapestry5.dojo.services.DojoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DojoSupportImpl implements DojoSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(DojoSupportImpl.class);

    private final JavaScriptSupport javaScriptSupport;

    private final AssetSource assetSource;

    private final StringBuilder dojoCSSThemeBuilder;

    private final Asset dojoJS;
    private final Asset tapestryDojoJS;

    private final Asset dojoCSS;

    private final Set<String> components = CollectionFactory.newSet();

    private final Set<String> elementToParse = CollectionFactory.newSet();

    private final Map<String, JSONArray> initialize = CollectionFactory.newMap();

    private final boolean partialMode;

    private String theme;

    public DojoSupportImpl(JavaScriptSupport javaScriptSupport, AssetSource assetSource, SymbolSource symbolSource,
            boolean partialMode) {
        this.javaScriptSupport = javaScriptSupport;
        this.assetSource = assetSource;
        String dojoBasePath = symbolSource.valueForSymbol(DojoSymbolConstants.DOJO);
        this.dojoCSSThemeBuilder = new StringBuilder(dojoBasePath).append("dijit/themes//.css");
        StringBuilder dojoPathBuilder = new StringBuilder(dojoBasePath);
        if (Boolean.TRUE.toString().equals(symbolSource.valueForSymbol(DojoSymbolConstants.DOJO_COMPRESSED))) {
            dojoPathBuilder.append("/dojo/dojo.js");
        } else {
            dojoPathBuilder.append("/dojo/dojo.js.uncompressed.js");
        }
        this.dojoJS = assetSource.getClasspathAsset(dojoPathBuilder.toString());
        this.dojoCSS = assetSource.getClasspathAsset(dojoBasePath + "/dojo/resources/dojo.css");
        this.tapestryDojoJS = assetSource.getClasspathAsset("org/charlouze/tapestry5/dojo/tapestry-dojo.js");
        this.partialMode = partialMode;
        this.theme = symbolSource.valueForSymbol(DojoSymbolConstants.DOJO_DEFAULT_THEME);
    }

    public void commit(MarkupWriter writer) {
        if (!partialMode) {
            Document document = writer.getDocument();
            Element body = document.getRootElement().getElement(new Predicate<Element>() {
                @Override
                public boolean accept(Element element) {
                    return "body".equalsIgnoreCase(element.getName());
                }
            });

            body.addClassName(theme);
        }

        if (components.size() != 0 || elementToParse.size() != 0) {
            javaScriptSupport.importJavaScriptLibrary(dojoJS);
            javaScriptSupport.importJavaScriptLibrary(tapestryDojoJS);
            javaScriptSupport.importStylesheet(dojoCSS);
            dojoCSSThemeBuilder.insert(dojoCSSThemeBuilder.indexOf("/.css"), theme);
            dojoCSSThemeBuilder.insert(dojoCSSThemeBuilder.indexOf(".css"), theme);
            javaScriptSupport.importStylesheet(assetSource.getClasspathAsset(dojoCSSThemeBuilder.toString()));

            Object[] parameters = new Object[components.size() + elementToParse.size() + (initialize.size() * 2)];
            int i = 0;
            for (String component : components) {
                parameters[i++] = component;
            }
            for (String element : elementToParse) {
                parameters[i++] = element;
            }
            for (Entry<String, JSONArray> entry : initialize.entrySet()) {
                parameters[i++] = entry.getKey();
                parameters[i++] = entry.getValue();
            }

            javaScriptSupport.addScript(InitializationPriority.IMMEDIATE, getScript(), parameters);
        }
    }

    private String getScript() {
        StringBuilder script = new StringBuilder();

        script.append(StringUtils.repeat("dojo.require('%s');\n", components.size()));

        if (elementToParse.size() > 0) {
            script.append("dojo.require('dojo.parser');\n");
        }
        if (initialize.size() > 0 || elementToParse.size() > 0) {
            script.append("dojo.ready(function(){\n");
            script.append(StringUtils.repeat("\tdojo.parser.parse(dojo.byId('%s'));\n", elementToParse.size()));
            script.append(StringUtils.repeat("\tTapestry.dojo.Initializer.%s(%s);\n", initialize.size()));
            script.append("});\n");
        }

        return script.toString();
    }

    @Override
    public String getTheme() {
        if (partialMode) {
            throw new UnsupportedOperationException("DojoSupport.getTheme() not supported for partial render.");
        }
        return theme;
    }

    @Override
    public void setTheme(String theme) {
        if (partialMode) {
            throw new UnsupportedOperationException("DojoSupport.setTheme(String) not supported for partial render.");
        }
        this.theme = theme;
    }

    @Override
    public void addRequiredComponents(String... components) {
        for (String component : components) {
            this.components.add(component);
        }
    }

    @Override
    public void addParserForElements(String... elementIds) {
        for (String elementId : elementIds) {
            this.elementToParse.add(elementId);
        }
    }

    @Override
    public void addInitializerCall(String functionName, JSONArray parameter) {
        this.initialize.put(functionName, parameter);
    }
}
