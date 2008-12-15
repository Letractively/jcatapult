/*
 * Copyright (c) 2001-2006, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.service;

import java.net.InetAddress;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import static java.util.Arrays.asList;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;

import org.jcatapult.commerce.service.CommerceService;
import org.jcatapult.commerce.service.VerifyResult;
import org.jcatapult.config.Configuration;
import org.jcatapult.domain.commerce.Money;
import org.jcatapult.domain.contact.EmailAddress;
import org.jcatapult.email.service.EmailService;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.security.PasswordEncryptor;
import org.jcatapult.security.SecurityContext;

import com.google.inject.Inject;
import net.java.error.ErrorList;
import net.java.lang.StringTools;
import static net.java.lang.StringTools.*;

import com.inversoft.module.user.domain.AuditableCreditCard;
import com.inversoft.module.user.domain.Role;
import com.inversoft.module.user.domain.User;

/**
 * <p>
 * This is the implementation of the UserService.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class DefaultUserService implements UserService {
    private final PersistenceService persistenceService;
    private final EmailService emailService;
    private final Configuration configuration;
    private final PasswordEncryptor passwordEncryptor;
    private final UserHandler<User, Role> userHandler;
    private final CommerceService commerceService;

    @Inject
    @SuppressWarnings("unchecked")
    public DefaultUserService(PersistenceService persistenceService, EmailService emailService,
            Configuration configuration, PasswordEncryptor passwordEncryptor, UserHandler userHandler,
            CommerceService commerceService) {
        this.persistenceService = persistenceService;
        this.emailService = emailService;
        this.configuration = configuration;
        this.passwordEncryptor = passwordEncryptor;
        this.userHandler = userHandler;
        this.commerceService = commerceService;
    }

    /**
     * {@inheritDoc}
     */
    public User createUser() {
        return userHandler.createUser();
    }

    /**
     * {@inheritDoc}
     */
    public ErrorList validate(User user, Map<String, Integer[]> associations, boolean existing,
            String password, String passwordConfirm) {
        return userHandler.validate(user, associations, existing, password, passwordConfirm);
    }

    /**
     * {@inheritDoc}
     */
    public RegisterResult register(User user, String password, Role... roles) {
        User partial = findByLogin(user.getLogin());
        if (partial != null) {
            if (partial.isPartial()) {
                // Make this an update
                user.setId(partial.getId());
                user.setPartial(false);
            } else {
                return RegisterResult.EXISTS;
            }
        }

        if (roles.length != 0) {
            user.setRoles(new HashSet<Role>(asList(roles)));
        } else {
            user.setRoles(userHandler.getDefaultRoles());
        }

        Map<String, Integer[]> associations = userHandler.getDefaultAssociations();
        if (!persist(user, associations, password)) {
            return RegisterResult.EXISTS;
        }

        return RegisterResult.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public RegisterResult registerPartial(User user, Role... roles) {
        user.setPassword("not-hashed");
        user.setPartial(true);

        if (roles.length != 0) {
            user.setRoles(new HashSet<Role>(asList(roles)));
        } else {
            user.setRoles(userHandler.getDefaultRoles());
        }

        Map<String, Integer[]> associations = userHandler.getDefaultAssociations();
        if (!persist(user, associations, null)) {
            return RegisterResult.EXISTS;
        }

        return RegisterResult.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public UpdateResult update(User user, String password) {
        if (user == null) {
            return UpdateResult.MISSING;
        }

        if (persistenceService.contains(user)) {
            throw new PersistenceException("Don't pass managed User entities to the update method");
        }

        // Transfer the ID and roles over to ensure no hacking
        User current = currentUser();
        user.setId(current.getId());
        user.setRoles(current.getRoles());

        if (!StringTools.isTrimmedEmpty(password)) {
            String encodedPassword = passwordEncryptor.encryptPassword(password);
            user.setPassword(encodedPassword);
        } else {
            user.setPassword(current.getPassword());
        }

        // Create any additional associations
        userHandler.prepare(user);

        try {
            persistenceService.persist(user);
            return UpdateResult.SUCCESS;
        } catch (PersistenceException e) {
            return UpdateResult.ERROR;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean persist(User user, Map<String, Integer[]> associations, String password) {
        // Updates and encrypts the password if it is specified
        if (user.getId() != null && isTrimmedEmpty(password)) {
            // Get the old user for their password because they left it blank and we need to preserve it
            User existing = persistenceService.findById(userHandler.getUserType(), user.getId());
            if (existing != null) {
                user.setPassword(existing.getPassword());
            }
        } else if (!isTrimmedEmpty(password)) {
            String encodedPassword = passwordEncryptor.encryptPassword(password);
            user.setPassword(encodedPassword);
        }

        // Add the associations
        userHandler.associate(user, associations);

        // Create any additional associations
        userHandler.prepare(user);

        try {
            persistenceService.persist(user);
            return true;
        } catch (EntityExistsException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public UpdateResult updatePassword(String login, String password) {
        User user = findByLogin(login);
        if (user == null || user.isPartial()) {
            return UpdateResult.MISSING;
        }

        String encodedPassword = passwordEncryptor.encryptPassword(password);
        user.setPassword(encodedPassword);

        try {
            persistenceService.persist(user);
            return UpdateResult.SUCCESS;
        } catch (PersistenceException e) {
            return UpdateResult.ERROR;
        }
    }

    /**
     * {@inheritDoc}
     */
    public UpdateResult resetPassword(String login) {
        User user = findByLogin(login);
        if (user == null || user.isPartial()) {
            return UpdateResult.MISSING;
        }

        // Create/update the GUID and save it
        Random rand = new SecureRandom();
        StringBuilder build = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            int r = rand.nextInt(16);
            build.append(Integer.toHexString(r));
        }

        user.setGuid(build.toString());
        persistenceService.persist(user);

        String template = configuration.getString("inversoft.modules.user.password.email.template", "reset-password");
        emailService.sendEmail(template).
            to(new EmailAddress(user.getLogin())).
            withTemplateParam("user", user).
            later();
        return UpdateResult.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public void delete(int id) {
        persistenceService.delete(userHandler.getUserType(), id);
    }

    /**
     * {@inheritDoc}
     */
    public void deleteMany(int[] ids) {
        for (int id : ids) {
            delete(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    public User currentUser() {
        String username = SecurityContext.getCurrentUsername();
        if (username == null) {
            return null;
        }

        return findByLogin(username);
    }

    /**
     * {@inheritDoc}
     */
    public List<User> find(int page, int number, String sortProperty) {
        if (sortProperty == null) {
            sortProperty = getDefaultSortProperty();
        }

        page--;

        Class<User> userType = userHandler.getUserType();
        return persistenceService.query(userType, "select obj from " + userType.getSimpleName() +
            " obj where obj.deleted = false order by obj." + sortProperty, page * number, number);
    }

    /**
     * {@inheritDoc}
     */
    public List<User> find(String sortProperty) {
        if (sortProperty == null) {
            sortProperty = getDefaultSortProperty();
        }

        Class<User> userType = userHandler.getUserType();
        return persistenceService.queryAll(userType, "select obj from " + userType.getSimpleName() +
            " obj where obj.deleted = false order by obj." + sortProperty);
    }

    /**
     * {@inheritDoc}
     */
    public User findById(Integer id) {
        return persistenceService.findById(userHandler.getUserType(), id);
    }

    /**
     * {@inheritDoc}
     */
    public User findByGUID(String guid) {
        Class<? extends User> userType = userHandler.getUserType();
        return persistenceService.queryFirst(userType,
            "select u from " + userType.getSimpleName() + " u where u.guid = ?1 and u.deleted = false", guid);
    }

    /**
     * {@inheritDoc}
     */
    public AuditableCreditCard findCreditCard(Integer id, String username) {
        return persistenceService.queryFirst(AuditableCreditCard.class,
            "select acc from AuditableCreditCard acc where acc.id = ?1 and acc.user.login = ?2", id, username);
    }

    /**
     * {@inheritDoc}
     */
    public CreditCardUpdateResult updateCreditCard(AuditableCreditCard creditCard, String username, InetAddress ip) {
        if (creditCard == null) {
            return new CreditCardUpdateResult(null, UpdateResult.MISSING);
        }

        AuditableCreditCard cc = findCreditCard(creditCard.getId(), username);
        if (cc == null) {
            return new CreditCardUpdateResult(null, UpdateResult.MISSING);
        }

        // Setup the user
        creditCard.setUser(cc.getUser());

        // Verify the credit card using the commerce service
        VerifyResult result = commerceService.verifyCard(creditCard, Money.valueOfUSD("1.00"), Money.ZERO_USD, ip);
        if (result.isError()) {
            return new CreditCardUpdateResult(result.getError(), UpdateResult.ERROR);
        }

        try {
            persistenceService.persist(creditCard);
            return new CreditCardUpdateResult(null, UpdateResult.SUCCESS);
        } catch (PersistenceException e) {
            return new CreditCardUpdateResult(null, UpdateResult.ERROR);
        }
    }

    /**
     * {@inheritDoc}
     */
    public UpdateResult deleteCreditCard(Integer id, String username) {
        AuditableCreditCard cc = findCreditCard(id, username);
        if (cc == null) {
            return UpdateResult.MISSING;
        }

        User user = findByLogin(username);
        user.removeCreditCard(cc);
        cc.setUser(null);

        persistenceService.delete(cc);
        return UpdateResult.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public User findByLogin(String login) {
        Class<? extends User> userType = userHandler.getUserType();
        return persistenceService.queryFirst(userType,
            "select u from " + userType.getSimpleName() + " u where u.login = ?1 and u.deleted = false", login);
    }

    /**
     * @return  The default sort property.
     */
    protected String getDefaultSortProperty() {
        return "login";
    }

    /**
     * {@inheritDoc}
     */
    public int getNumberOfUsers() {
        return (int) persistenceService.count(userHandler.getUserType(), true);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, List<?>> getAssociationObjects() {
        Map<String, List<?>> values = new HashMap<String, List<?>>();
        values.put("roles", persistenceService.findAllByType(userHandler.getRoleType()));
        return values;
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Integer[]> getAssociationIds(User user) {
        return userHandler.getAssociationIds(user);
    }
}