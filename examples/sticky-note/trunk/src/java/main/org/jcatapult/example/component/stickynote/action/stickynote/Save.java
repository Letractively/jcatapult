package org.jcatapult.example.component.stickynote.action.stickynote;

import java.util.logging.Logger;

import org.apache.commons.configuration.Configuration;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.jcatapult.struts.action.BaseAction;

import com.google.inject.Inject;
import org.jcatapult.example.component.stickynote.service.StickyNoteService;
import org.jcatapult.example.component.stickynote.domain.StickyNote;

/**
 * <p>
 * This class is the action that persists the StickyNotes.
 * </p>
 *
 * @author  Scaffolder
 */
@Results({
    @Result(name = "success", location = "index", type = "redirectAction")
})
public class Save extends BaseAction {
    private static final Logger logger = Logger.getLogger(Save.class.getName());
    private final StickyNoteService stickyNoteService;
    private final Configuration configuration;
    private StickyNote stickyNote;

    @Inject
    public Save(StickyNoteService stickyNoteService, Configuration configuration) {
        this.stickyNoteService = stickyNoteService;
        this.configuration = configuration;
    }

    public StickyNote getStickyNote() {
        return stickyNote;
    }


    public void setStickyNote(StickyNote stickyNote) {
        this.stickyNote = stickyNote;
    }


    @Override
    @Actions({
        @Action(value = "save", results = {@Result(name = "input", location = "add.jsp")}),
        @Action(value = "update", results = {@Result(name = "input", location = "edit.jsp")})
    })
    public String execute() {
        stickyNoteService.persist(stickyNote);
        return SUCCESS;
    }
}