/*
 * Copyright (c) 2001-2007, JCatapult.org, All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 */
package org.jcatapult.user.service;

import java.util.Map;
import java.util.List;

import org.jcatapult.user.domain.Role;
import org.jcatapult.user.domain.User;

import com.google.inject.ImplementedBy;
import net.java.error.ErrorList;

/**
 * <p>
 * This interface defines the interactions with user objects and the business
 * rules around them.
 * </p>
 *
 * @author  Brian Pontarelli
 */
@ImplementedBy(DefaultUserService.class)
public interface UserService {

    /**
     * Gets all of the Users sorted using the given column name.
     *
     * @param   sortProperty (Optional) The sort property on the {@link User} object.
     * @return  The List of Users.
     */
    List<User> find(String sortProperty);

    /**
     * Gets a page of the Users sorted using the given column name.
     *
     * @param   page The page of Users to fetch (1 based).
     * @param   numberPerPage The number of Users to fetch (1 based).
     * @param   sortProperty (Optional) The sort property on the {@link User} object.
     * @return  The List of Users.
     */
    List<User> find(int page, int numberPerPage, String sortProperty);

    /**
     * @return  The total number of Users.
     */
    int getNumberOfUsers();

    /**
     * @param   id The ID of the user.
     * @return  A Map that contains all of the ids for associated objects of the given User.
     */
    Map<String, int[]> getAssociationIds(int id);

    /**
     * Locates the User with the given id.
     *
     * @param   id The ID of the User.
     * @return  The User or null if it doesn't exist.
     */
    User findById(int id);

    /**
     * Locates the User with the given login.
     *
     * @param   login The login.
     * @return  The User or null.
     */
    User findByLogin(String login);

    /**
     * Retrieves the user whose GUID field equals the GUID given.
     *
     * @param   guid The guid to match.
     * @return  The user or null.
     */
    User findByGUID(String guid);
    
    /**
     * Creates a new User object. This is used by the actions to create an instance of the correct
     * User implementation for use on the forms.
     *
     * @return  A new instance of the User implementation and never null.
     */
    User createUser();

    /**
     * Validates the user object has all the correct values prior to inserting or updating.
     *
     * @param   user The user to validate
     * @param   associations The map of associated IDs for the User.
     * @param   existing Determines if the validation is for an existing user or a new user.
     * @param   password (Optional) The password to validate.
     * @param   passwordConfirm (Optional) The password confirmation to validate.
     * @return  Any errors found.
     */
    ErrorList validate(User user, Map<String, int[]> associations, boolean existing, String password,
        String passwordConfirm);

    /**
     * Registers a new user account.
     *
     * @param   user The user information.
     * @param   password (Optional) The password.
     * @param   roles An optional list of roles for the user. If this is null, the default roles are
     *          pull from the UserHandler.
     * @return  The result of the registration.
     */
    RegisterResult register(User user, String password, Role... roles);

    /**
     * Registers a partial new user account. This account will have a password that is impossible to
     * log in with and the temporary flag set to true.
     *
     * @param   user The user information.
     * @param   roles An optional list of roles for the user. If this is null, the default roles are
     *          pull from the UserHandler.
     * @return  The result of the registration.
     */
    RegisterResult registerPartial(User user, Role... roles);

    /**
     * <p>
     * This updates the curernt user's information. The User object passed in MUST be a managed entity
     * Object that is fetched in an {@code @ActionPrepareMethod}. This is the only safe way to allow forms
     * to update only parts of a User object without clobbering other parts. For example, if the form
     * only updates the password, then unless this method either copies only the updated parts of the
     * given User into the existing User, associations will get deleted.
     * </p>
     *
     * <p>
     * This style of programming opens up a HUGE security risk that anyone implementing a front-end
     * for this API must consider. If you do not verify that the data sent in from the form contains
     * the correct ID against the currently logged in User, a hacker could update anyones User data
     * simply by adding a hidden field to the form like this:
     * </p>
     *
     * <pre>
     * &lt;input type="hidden" name="user.id" value="82"/>
     * </pre>
     *
     * <p>
     * Therefore, you should always delete any ID passed in from the form with the ID of the currently
     * logged in user like this:
     * </p>
     *
     * <pre>
     * {@code @ActionPrepareMethod}
     * public void prepare() {
     *   this.user = userService.findByLogin(SecurityContext.getCurrentUsername());
     * }
     *
     * public String post() {
     *   User currentUser = (User) SecurityContext.getCurrentUser();
     *   this.user.setId(currentUser.getId());
     *   ...
     * }
     * </pre>
     *
     * <p>
     * This code ensures that the User is prepared correctly and that hackers can't mess with the
     * IDs.
     * </pre>
     *
     * @param   user The user to be updated. This MUST be a managed entity Object that is setup in
     *          a action prepare method. An exception will be thrown if it isn't.
     * @param   password (Optional) A new password. Any password pulled off the form must be passed
     *          in here so that it can be encrypted and placed into the User. If you set the new
     *          password directly into the User from the form, it will go into the database without
     *          being encrypted, which means the User will be unable to login at all. Furthermore,
     *          this method is safe from hacking, because is some hacker is dumb enough to add a
     *          hidden field on the form and that value is passed in here, all that will happen is
     *          that their account will be locked down.
     * @return  This should return a result object for the update. This result contains any errors
     *          that might have occurred.
     */
    UpdateResult update(User user, String password);

    /**
     * Saves or updates the given User. This User instance must be completely filled out in order to
     * be inserted or updated. All foreign key references need to be correct prior to any updates
     * otherwise Hibernate will truncate the data.
     *
     * @param   user The User to save or update.
     * @param   associations The map of associated IDs for the User.
     * @param   password (Optional) The password.
     * @return  True if the user was saved, false otherwise.
     */
    boolean persist(User user, Map<String, int[]> associations, String password);

    /**
     * Deletes the User with the given ID.
     *
     * @param   id The ID of the User to delete.
     */
    void delete(int id);

    /**
     * Deletes the Users with the given IDs.
     *
     * @param   ids The IDs of the Users to delete.
     */
    void deleteMany(int[] ids);

    /**
     * Resets the password for the given user to the given password.
     *
     * @param   login The login to reset the password for.
     * @param   password The new password.
     * @return  The result of the reset.
     */
    UpdateResult updatePassword(String login, String password);

    /**
     * Sets up the user account for a password reset by setting a GUID and then emailing the user a
     * link that they can use to reset their password. This method uses a number of different
     * configuration options in order to correctly configure the emails.
     *
     * <p>
     * <strong>jcatapult.user.password.email.template</strong> - A String configuration
     * parameter that sets the name of the email template that is executed to generate the reset
     * password email. Defaults to <strong>reset-password</strong>
     * </p>
     * <p>
     * <strong>jcatapult.user.password.email.subject</strong> - A String configuration
     * parameter that sets the subject of the email. Defaults to <strong>Password Reset</strong>.
     * </p>
     * <p>
     * <strong>jcatapult.user.password.email.from-address</strong> - A String configuration
     * parameter that sets the from address of the email. This must be configured because it has no
     * default.
     * </p>
     * <p>
     * <strong>jcatapult.user.password.email.from-address-display</strong>  -A String
     * configuration parameter that sets the display name of the from address of the email. This
     * must be configured because it has no default.
     * </p>
     *
     * @param   login The login to setup for password reset.
     * @param   url The URL to include in the email to the user with a link to reset their password.
     * @return  The result of the reset.
     */
    UpdateResult resetPassword(String login, String url);
}