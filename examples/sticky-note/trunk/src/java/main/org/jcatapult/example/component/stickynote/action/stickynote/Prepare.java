package org.jcatapult.example.component.stickynote.action.stickynote;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jcatapult.struts.action.BaseAction;

import com.google.inject.Inject;
import org.jcatapult.example.component.stickynote.service.StickyNoteService;

/**
 * <p>
 * This class prepares the form that adds and edits StickyNotes.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class Prepare extends BaseAction {
    private static final Logger logger = Logger.getLogger(Prepare.class.getName());
    private final StickyNoteService stickyNoteService;

    @Inject
    public Prepare(StickyNoteService stickyNoteService) {
        this.stickyNoteService = stickyNoteService;
    }

    public String execute() {
        return SUCCESS;
    }
}