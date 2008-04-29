package org.jcatapult.example.webapp.action.guestBook;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.jcatapult.example.webapp.service.GuestBookEntryService;

import com.google.inject.Inject;

/**
 * <p>
 * This class handles the addition form submit.
 * </p>
 *
 * @author  Scaffolder
 */
public class Add extends BaseForm {
    @Inject
    public Add(GuestBookEntryService guestBookEntryService) {
        super(guestBookEntryService);
    }

    @Override
    @Action(results = {@Result(name = "success", location = "index", type = "redirectAction")})
    public String execute() {
        if (isGet()) {
            return INPUT;
        }

        guestBookEntryService.persist(guestBookEntry);
        return SUCCESS;
    }
}