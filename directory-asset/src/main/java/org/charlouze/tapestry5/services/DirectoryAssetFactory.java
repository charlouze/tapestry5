package org.charlouze.tapestry5.services;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.internal.services.AbstractAsset;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.services.AssetFactory;
import org.apache.tapestry5.services.AssetPathConverter;
import org.apache.tapestry5.services.assets.AssetPathConstructor;
import org.charlouze.tapestry5.DirectoryAssetConstants;
import org.charlouze.tapestry5.util.DirectoryAssetResource;

public class DirectoryAssetFactory implements AssetFactory {

    private final AssetPathConstructor assetPathConstructor;

    private final AssetPathConverter converter;

    private final boolean invariant;

    private final Resource rootResource;

    public DirectoryAssetFactory(AssetPathConstructor assetPathConstructor, String parentFolder,
            AssetPathConverter converter) {
        this.assetPathConstructor = assetPathConstructor;
        this.converter = converter;

        this.rootResource = new DirectoryAssetResource(parentFolder, "/");
        this.invariant = converter.isInvariant();
    }

    @Override
    public Resource getRootResource() {
        return rootResource;
    }

    @Override
    public Asset createAsset(Resource resource) {
        String defaultPath = assetPathConstructor.constructAssetPath(DirectoryAssetConstants.DIRECTORY_ASSET_FOLDER,
                resource.getPath());

        if (invariant) {
            return createInvariantAsset(resource, defaultPath);
        }

        return createVariantAsset(resource, defaultPath);
    }

    private Asset createInvariantAsset(final Resource resource, final String defaultPath) {
        return new AbstractAsset(true) {
            private String clientURL;

            public Resource getResource() {
                return resource;
            }

            public synchronized String toClientURL() {
                if (clientURL == null) {
                    clientURL = converter.convertAssetPath(defaultPath);
                }

                return clientURL;
            }
        };
    }

    private Asset createVariantAsset(final Resource resource, final String defaultPath) {
        return new AbstractAsset(false) {
            public Resource getResource() {
                return resource;
            }

            public String toClientURL() {
                return converter.convertAssetPath(defaultPath);
            }
        };
    }
}
