package org.jcatapult.module.user.service;

import java.net.InetAddress;
import java.util.List;
import java.util.Map;

import com.google.inject.ImplementedBy;
import net.java.error.ErrorList;

import org.jcatapult.module.user.domain.AuditableCreditCard;
import org.jcatapult.module.user.domain.Role;
import org.jcatapult.module.user.domain.User;

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
    ErrorList validate(User user, Map<String, Integer[]> associations, boolean existing, String password,
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
     * This updates the curernt users information using the data in the given user. If the password
     * given is not null then it will be encrypted and used to update the current user.
     * </p>
     *
     * <p>
     * This method is for consumer facing usage only. The {@link #persist(User, Map, String)} method
     * is to be used for admins.
     * </p>
     *
     * @param   newUserData The new user data, which might be complete or incomplete, depending on the
     *          application needs. This must not be a managed entity.
     * @param   password (Optional) The password.
     * @return  This should return a result object for the update. This result contains any errors
     *          that might have occurred.
     */
    UpdateResult update(User newUserData, String password);

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
    boolean persist(User user, Map<String, Integer[]> associations, String password);

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
     * <strong>inversoft.modules.user.password.email.template</strong> - A String configuration
     * parameter that sets the name of the email template that is executed to generate the reset
     * password email. Defaults to <strong>reset-password</strong>
     * </p>
     * <p>
     * <strong>inversoft.modules.user.password.email.subject</strong> - A String configuration
     * parameter that sets the subject of the email. Defaults to <strong>Password Reset</strong>.
     * </p>
     * <p>
     * <strong>inversoft.modules.user.password.email.from-address</strong> - A String configuration
     * parameter that sets the from address of the email. This must be configured because it has no
     * default.
     * </p>
     * <p>
     * <strong>inversoft.modules.user.password.email.from-address-display</strong>  -A String
     * configuration parameter that sets the display name of the from address of the email. This
     * must be configured because it has no default.
     * </p>
     *
     * @param   login The login to setup for password reset.
     * @return  The result of the reset.
     */
    UpdateResult resetPassword(String login);

    /**
     * Locates the current user based on the username in the {@link org.jcatapult.security.SecurityContext}.
     *
     * @return  The current user or null if no one is logged in.
     */
    User currentUser();

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
     * @return  A Map that contains all of the Objects that a User can be associated with. In most
     *          implementations this will contain the Role objects.
     */
    Map<String, List<?>> getAssociationObjects();

    /**
     * @param   user The user.
     * @return  A Map that contains all of the ids for associated objects of the given User.
     */
    Map<String, Integer[]> getAssociationIds(User user);

    /**
     * Locates the User with the given id.
     *
     * @param   id The ID of the User.
     * @return  The User or null if it doesn't exist.
     */
    User findById(Integer id);

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
     * Locates the credit card with the given id for the given user.
     *
     * @param   id The ID of the credit card.
     * @param   username The username that owns the credit card.
     * @return  The credit card or null if it doesn't exist.
     */
    AuditableCreditCard findCreditCard(Integer id, String username);

    /**
     * Updates the credit card given.
     *
     * @param   creditCard The credit card.
     * @param username
     *@param   ip The IP address of the client. @return  The result of the update.
     */
    CreditCardUpdateResult updateCreditCard(AuditableCreditCard creditCard, String username, InetAddress ip);

    /**
     * Deletes the credit card for the given user and id.
     *
     * @param   id The id of the credit card.
     * @param   username The username.
     * @return  The result of the deletion.
     */
    UpdateResult deleteCreditCard(Integer id, String username);
}