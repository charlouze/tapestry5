package org.charlouze.tapestry5.services;

import org.apache.tapestry5.internal.services.ResourceStreamer;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.AssetFactory;
import org.apache.tapestry5.services.AssetPathConverter;
import org.apache.tapestry5.services.assets.AssetPathConstructor;
import org.apache.tapestry5.services.assets.AssetRequestHandler;
import org.charlouze.tapestry5.DirectoryAssetConstants;
import org.charlouze.tapestry5.annotations.DirectoryAssetProvider;

public class DirectoryAssetModule {
    public void contributeAssetSource(MappedConfiguration<String, AssetFactory> configuration,
            @DirectoryAssetProvider AssetFactory directoryAssetFactory) {
        configuration.add(DirectoryAssetConstants.DIRECTORY_ASSET_FOLDER, directoryAssetFactory);
    }

    public static void contributeAssetDispatcher(MappedConfiguration<String, AssetRequestHandler> configuration,
            @DirectoryAssetProvider AssetFactory directoryAssetFactory, ResourceStreamer streamer) {

        configuration.add(DirectoryAssetConstants.DIRECTORY_ASSET_FOLDER,
                new DirectoryAssetRequestHandler(streamer, directoryAssetFactory.getRootResource()));

    }

    @Marker(DirectoryAssetProvider.class)
    public AssetFactory buildExternalMediaAssetFactory(
            @Symbol(DirectoryAssetConstants.DIRECTORY_ASSET_PARENT_FOLDER) String parentFolder,
            AssetPathConstructor assetPathConstructor, AssetPathConverter converter) {
        return new DirectoryAssetFactory(assetPathConstructor, parentFolder, converter);
    }
}
