package org.jcatapult.filemgr.action.jcatapult;

import org.jcatapult.filemgr.domain.Connector;
import org.jcatapult.filemgr.service.FileManagerService;
import org.jcatapult.mvc.action.result.annotation.Forward;
import org.jcatapult.mvc.action.result.annotation.Header;
import org.jcatapult.mvc.action.result.annotation.Stream;

import com.google.inject.Inject;

/**
 * <p>
 * This class extends the {@link FileManager} action and provides the
 * JavaScript response for file uploads.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Header(code = "error", status = 500)
@Stream(type = "text/xml", name = "result")
@Forward(code = "uplodate", page = "/file-mgr/fck-file-manager.ftl")
public class FckFileManager extends FileManager {
    // Output for file upload from the service.
    public Connector connector;
    public String type;

    @Inject
    public FckFileManager(FileManagerService fileManagerService) {
        super(fileManagerService);
    }

    public Connector getConnector() {
        return connector;
    }

    @Override
    protected String doUpload() {
        connector = fileManagerService.upload(newFile.file, newFile.name, newFile.contentType,
            type, currentFolder);
        return "upload";
    }
}