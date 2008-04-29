package org.jcatapult.example.webapp.action.guestBook;

import org.apache.struts2.convention.annotation.Result;
import org.jcatapult.struts.action.BaseAction;

import com.google.inject.Inject;
import org.jcatapult.example.webapp.service.GuestBookEntryService;

/**
 * <p>
 * This class is the action that deletes one or more GuestBookEntry(s)
 * </p>
 *
 * @author Scaffolder
 */
@Result(name = "success", location = "index", type = "redirectAction")
public class Delete extends BaseAction {
    private final GuestBookEntryService guestBookEntryService;
    private int[] ids;

    @Inject
    public Delete(GuestBookEntryService guestBookEntryService) {
        this.guestBookEntryService = guestBookEntryService;
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
            guestBookEntryService.deleteMany(ids);
        }

        return SUCCESS;
    }
}