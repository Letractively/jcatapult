package org.jcatapult.example.component.stickynote.action.stickynote;

import org.apache.struts2.convention.annotation.Result;
import org.jcatapult.struts.action.BaseAction;

import com.google.inject.Inject;
import org.jcatapult.example.component.stickynote.service.StickyNoteService;

/**
 * <p>
 * This class is the action that deletes one or more StickyNote(s)
 * </p>
 *
 * @author Scaffolder
 */
@Result(name = "success", location = "index", type = "redirectAction")
public class Delete extends BaseAction {
    private final StickyNoteService stickyNoteService;
    private int[] ids;

    @Inject
    public Delete(StickyNoteService stickyNoteService) {
        this.stickyNoteService = stickyNoteService;
    }

    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    @Override
    public String execute() {
        if (ids != null && ids.length > 0) {
            stickyNoteService.deleteMany(ids);
        }

        return SUCCESS;
    }
}