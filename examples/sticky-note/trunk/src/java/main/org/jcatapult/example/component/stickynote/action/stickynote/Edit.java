package org.jcatapult.example.component.stickynote.action.stickynote;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.convention.annotation.Result;
import org.jcatapult.domain.Identifiable;
import org.jcatapult.struts.action.BaseAction;

import com.google.inject.Inject;
import org.jcatapult.example.component.stickynote.domain.StickyNote;
import org.jcatapult.example.component.stickynote.service.StickyNoteService;

/**
 * <p>
 * This class fetches an existing StickyNote for editing.
 * </p>
 *
 * @author  Scaffolder
 */
@Result(name = "error", location = "index.jsp")
public class Edit extends BaseAction {
    private final StickyNoteService stickyNoteService;
    private Integer id;
    private StickyNote stickyNote;

    @Inject
    public Edit(StickyNoteService stickyNoteService) {
        this.stickyNoteService = stickyNoteService;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public StickyNote getStickyNote() {
        return stickyNote;
    }


    @Override
    public String execute() {
        stickyNote = stickyNoteService.getById(id);
        if (stickyNote == null) {
            addActionError("That StickyNote has been deleted.");
            return ERROR;
        }

        return SUCCESS;
    }
}