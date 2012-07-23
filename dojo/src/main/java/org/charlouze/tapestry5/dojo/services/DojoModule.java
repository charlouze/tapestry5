package org.charlouze.tapestry5.dojo.services;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.Environment;
import org.apache.tapestry5.services.EnvironmentalShadowBuilder;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.MarkupRenderer;
import org.apache.tapestry5.services.MarkupRendererFilter;
import org.apache.tapestry5.services.PartialMarkupRenderer;
import org.apache.tapestry5.services.PartialMarkupRendererFilter;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.services.meta.MetaWorker;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.charlouze.tapestry5.dojo.DojoSymbolConstants;
import org.charlouze.tapestry5.dojo.services.impl.DojoSupportImpl;

public class DojoModule {
    private final EnvironmentalShadowBuilder environmentalBuilder;

    private final Environment environment;

    public DojoModule(EnvironmentalShadowBuilder environmentalBuilder, Environment environment) {
        this.environmentalBuilder = environmentalBuilder;
        this.environment = environment;
    }

    public static void bind(ServiceBinder binder) {
        binder.bind(DojoRequireWorker.class);
    }

    @Contribute(ComponentClassTransformWorker2.class)
    public static void provideTransformWorkers(OrderedConfiguration<ComponentClassTransformWorker2> configuration,
            MetaWorker metaWorker, ComponentClassResolver resolver) {
        configuration.addInstance("DojoRequire", DojoRequireWorker.class);
    }

    public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
        configuration.add(new LibraryMapping("dojo", "org.charlouze.tapestry5.dojo"));
    }

    @Contribute(SymbolProvider.class)
    @FactoryDefaults
    public static void contributeFactoryDefaults(MappedConfiguration<String, Object> configuration) {
        configuration.add(DojoSymbolConstants.DOJO, "classpath:${tapestry.dojo.path}");
        configuration.add("tapestry.dojo.path", "org/charlouze/tapestry5/dojo/dojo-release-1.7.3/");
        configuration.add(DojoSymbolConstants.DOJO_COMPRESSED, SymbolConstants.PRODUCTION_MODE_VALUE);
        configuration.add(DojoSymbolConstants.DOJO_DEFAULT_THEME, "claro");
    }

    public DojoSupport buildDojoSupport() {
        return environmentalBuilder.build(DojoSupport.class);
    }

    public void contributeMarkupRenderer(OrderedConfiguration<MarkupRendererFilter> configuration,
            final SymbolSource symbolSource, final AssetSource assetSource) {

        MarkupRendererFilter dojoSupport = new MarkupRendererFilter() {
            public void renderMarkup(MarkupWriter writer, MarkupRenderer renderer) {
                JavaScriptSupport javaScriptSupport = environment.peekRequired(JavaScriptSupport.class);

                DojoSupportImpl support = new DojoSupportImpl(javaScriptSupport, assetSource, symbolSource, false);

                environment.push(DojoSupport.class, support);

                renderer.renderMarkup(writer);

                environment.pop(DojoSupport.class);

                support.commit(writer);
            }
        };

        configuration.add("DojoSupport", dojoSupport, "after:JavaScriptSupport");
    }

    public void contributePartialMarkupRenderer(OrderedConfiguration<PartialMarkupRendererFilter> configuration,
            final SymbolSource symbolSource, final AssetSource assetSource) {

        PartialMarkupRendererFilter dojoSupport = new PartialMarkupRendererFilter() {
            public void renderMarkup(MarkupWriter writer, JSONObject reply, PartialMarkupRenderer renderer) {
                JavaScriptSupport javaScriptSupport = environment.peekRequired(JavaScriptSupport.class);

                DojoSupportImpl support = new DojoSupportImpl(javaScriptSupport, assetSource, symbolSource, true);

                environment.push(DojoSupport.class, support);

                renderer.renderMarkup(writer, reply);

                environment.pop(DojoSupport.class);

                support.commit(writer);
            }
        };

        configuration.add("DojoSupport", dojoSupport, "after:JavaScriptSupport");
    }
}
