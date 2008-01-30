package org.jcatapult.filemgr.action;

import org.jcatapult.filemgr.domain.Connector;
import org.jcatapult.filemgr.service.FileManagerService;

import com.google.inject.Inject;

/**
 * <p>
 * This class extends the {@link FileManager} action and provides the
 * JavaScript response for file uploads.
 * </p>
 *
 * @author Brian Pontarelli
 */
public class FckFileManager extends FileManager {
    // Output for file upload from the service.
    private Connector connector;

    @Inject
    public FckFileManager(FileManagerService fileManagerService) {
        super(fileManagerService);
    }

    public Connector getConnector() {
        return connector;
    }

    @Override
    protected String doUpload() {
        connector = fileManagerService.upload(getNewFile(), getNewFileFileName(), getNewFileContentType(), getType());
        return SUCCESS;
    }
}