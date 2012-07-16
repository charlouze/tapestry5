package org.charlouze.tapestry5.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.internal.util.AbstractResource;
import org.charlouze.tapestry5.DirectoryAssetConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryAssetResource extends AbstractResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryAssetResource.class);

    private final File parentFolder;
    private URL url;

    public DirectoryAssetResource(String parentFolder, String path) {
        super(path);

        this.parentFolder = new File(parentFolder);

        String realPath = parentFolder + File.separator + getPath();

        File file = new File(realPath);

        url = null;
        if (file != null && file.exists()) {
            try {
                url = file.toURI().toURL();
            } catch (MalformedURLException e) {
                LOGGER.error("Unable to create URL.", e);
            }
        }
    }

    @Override
    public String toString() {
        return String.format(DirectoryAssetConstants.DIRECTORY_ASSET_FOLDER + ":%s", getPath());
    }

    @Override
    protected Resource newResource(String path) {
        return new DirectoryAssetResource(this.parentFolder.getAbsolutePath(), path);
    }

    @Override
    public URL toURL() {
        return url;
    }

    @Override
    public int hashCode() {
        return getPath().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        final DirectoryAssetResource other = (DirectoryAssetResource) obj;

        return getPath().equals(other.getPath());
    }

}
