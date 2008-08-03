package ${pkgName};

import org.jcatapult.mvc.action.annotation.Action;
import org.jcatapult.mvc.message.MessageStore;

import com.google.inject.Inject;

/**
 * <p>
 * This class is an action that
 * </p>
 *
 * @author  Scaffolder
 */
@Action
public class ${className} {
    public final MessageStore messageStore;

    @Inject
    public ${className}(MessageStore messageStore) {
        this.messageStore = messageStore;
    }

    public String execute() {
        return "success";
    }
}
