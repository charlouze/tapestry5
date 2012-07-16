package org.charlouze.tapestry5.services;

import java.io.IOException;

import org.apache.tapestry5.internal.services.ResourceStreamer;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.assets.AssetRequestHandler;

public class DirectoryAssetRequestHandler implements AssetRequestHandler {

    private final ResourceStreamer streamer;

    private final Resource rootResource;

    public DirectoryAssetRequestHandler(ResourceStreamer streamer, Resource rootResource) {
        this.streamer = streamer;
        this.rootResource = rootResource;
    }

    @Override
    public boolean handleAssetRequest(Request request, Response response, String extraPath) throws IOException {
        Resource resource = rootResource.forFile(extraPath);

        streamer.streamResource(resource);

        return true;
    }

}
