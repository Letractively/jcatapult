/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package org.jcatapult.module.user.action;

import java.util.HashMap;
import java.util.Map;

import org.jcatapult.mvc.message.MessageStore;
import org.jcatapult.mvc.message.scope.MessageScope;
import org.jcatapult.mvc.validation.annotation.ValidateMethod;

import com.google.inject.Inject;
import net.java.error.ErrorList;
import net.java.error.PropertyError;

import org.jcatapult.module.user.domain.User;
import org.jcatapult.module.user.service.UserConfiguration;
import org.jcatapult.module.user.service.UserService;

/**
 * <p>
 * This class is a base class for form actions. It provides support
 * for creating and fetching the correct {@link User} implementation
 * using the {@link UserService#createUser()}. This also handles validation.
 * </p>
 *
 * <p>
 * In order to correctly handle object associations, this class uses
 * two Maps. One of the maps contains the results from the form
 * submission and also the values from an existing User when using the
 * edit form. THe other Map contains the set of associated objects
 * to populate forms. If you have a custom User class and any associated
 * classes, you will need to use theses Maps to handle those associations
 * like this:
 * </p>
 *
 * <pre>
 * &lt;s:checkboxlist key="association[blogs]" items="items[blogs]"/>
 * </pre>
 *
 * <p>
 * The roles for the User are handled using these Maps to make things
 * consistent.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class BaseUserFormAction {
    protected MessageStore messageStore;
    protected UserConfiguration userConfiguration;
    protected UserService userService;

    public User user;

    /**
     * The Map of associated ids.
     */
    public Map<String, Integer[]> associations = new HashMap<String, Integer[]>();

    /**
     * The password field value.
     */
    public String password;

    /**
     * The password confirmation field value.
     */
    public String passwordConfirm;

    /**
     * Returns whether or not the validation routine in this class will check the password
     * field. This is nice to set to false when the user is updating their own account.
     * Defaults to true.
     */
    public boolean checkPassword = true;

    @Inject
    public void setServices(MessageStore messageStore, UserConfiguration userConfiguration,
            UserService userService) {
        this.messageStore = messageStore;
        this.userConfiguration = userConfiguration;
        this.userService = userService;
    }

    /**
     * @return  The Map of settings for the forms. These settings are boolean flags and are fetched
     *          from the {@link UserConfiguration#getDomainFlags()} method. Defaults to true.
     */
    public Map<String, Boolean> getSettings() {
        return userConfiguration.getDomainFlags();
    }

    /**
     * Performs validation on the User class by passing the User object from the form submission to
     * the {@link UserService#validate(User,Map,boolean,String,String)} method.
     */
    @SuppressWarnings("unchecked")
    @ValidateMethod
    public void validate() {
        ErrorList errors = userService.validate(user, associations, (!checkPassword || user.getId() != null),
            password, passwordConfirm);
        if (!errors.isEmpty()) {
            for (net.java.error.Error error : errors) {
                PropertyError pe = (PropertyError) error;
                messageStore.addFieldError(MessageScope.REQUEST, pe.getProperty(), pe.getMessage());
            }
        }
    }
}