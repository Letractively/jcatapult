package org.jcatapult.filemgr.action;

import java.io.InputStream;

import org.jcatapult.filemgr.domain.Connector;
import org.jcatapult.filemgr.service.FileManagerService;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.convention.annotation.Result;

import com.google.inject.Inject;
import net.java.lang.StringTools;

/**
 * <p>
 * This class extends the {@link FileManager} action and provides the
 * JavaScript response for file uploads.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Results({
    @Result(name = "error", location = "", params = {"status", "500"}, type = "httpheader"),
    @Result(name = "success", location = "", params = {"contentType", "text/xml"}, type = "stream"),
    @Result(name = "upload", location = "file-mgr/fck-file-manager.ftl")
})
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
        connector = fileManagerService.upload(getNewFile(), getNewFileFileName(), getNewFileContentType(),
            getType(), getCurrentFolder());
        return "upload";
    }
}