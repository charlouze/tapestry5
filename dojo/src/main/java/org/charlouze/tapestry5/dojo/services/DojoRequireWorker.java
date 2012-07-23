package org.charlouze.tapestry5.dojo.services;

import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.model.MutableComponentModel;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.apache.tapestry5.plastic.PlasticClass;
import org.apache.tapestry5.plastic.PlasticMethod;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.TransformConstants;
import org.apache.tapestry5.services.transform.ComponentClassTransformWorker2;
import org.apache.tapestry5.services.transform.TransformationSupport;
import org.charlouze.tapestry5.dojo.annotations.DojoRequire;

public class DojoRequireWorker implements ComponentClassTransformWorker2 {
    private final DojoSupport dojoSupport;

    public DojoRequireWorker(DojoSupport dojoSupport, AssetSource assetSource, SymbolSource symbolSource) {
        this.dojoSupport = dojoSupport;
    }

    @Override
    public void transform(PlasticClass componentClass, TransformationSupport support, MutableComponentModel model) {
        DojoRequire annotation = componentClass.getAnnotation(DojoRequire.class);

        if (annotation != null) {
            PlasticMethod setupRender = componentClass.introduceMethod(TransformConstants.SETUP_RENDER_DESCRIPTION);

            decorateMethod(setupRender, annotation);

            model.addRenderPhase(SetupRender.class);
        }
    }

    private void decorateMethod(PlasticMethod method, DojoRequire annotation) {
        method.addAdvice(createDojoRequireMethodAdvice(annotation.components(), annotation.parseElements()));
    }

    private MethodAdvice createDojoRequireMethodAdvice(final String[] components, final String[] parseElements) {
        return new MethodAdvice() {
            @Override
            public void advise(MethodInvocation invocation) {
                dojoSupport.addRequiredComponents(components);
                dojoSupport.addParserForElements(parseElements);

                invocation.proceed();
            }
        };

    }
}
