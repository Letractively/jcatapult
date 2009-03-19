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

import java.security.SecureRandom;
import static java.util.Arrays.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;

import org.jcatapult.config.Configuration;
import org.jcatapult.domain.contact.EmailAddress;
import org.jcatapult.email.service.EmailCommand;
import org.jcatapult.email.service.EmailService;
import org.jcatapult.persistence.service.PersistenceService;
import org.jcatapult.security.PasswordEncryptor;
import org.jcatapult.user.domain.Role;
import org.jcatapult.user.domain.User;
import org.jcatapult.user.domain.Usernamed;

import com.google.inject.Inject;
import net.java.error.ErrorList;
import net.java.lang.StringTools;
import static net.java.lang.StringTools.*;

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

    @Inject
    @SuppressWarnings("unchecked")
    public DefaultUserService(PersistenceService persistenceService, EmailService emailService,
            Configuration configuration, PasswordEncryptor passwordEncryptor, UserHandler userHandler) {
        this.persistenceService = persistenceService;
        this.emailService = emailService;
        this.configuration = configuration;
        this.passwordEncryptor = passwordEncryptor;
        this.userHandler = userHandler;
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
    public User findById(int id) {
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
    public User findByUsernameOrEmail(String login) {
        Class<? extends User> userType = userHandler.getUserType();
        String jpaql;
        if (Usernamed.class.isAssignableFrom(userType) && !login.contains("@")) {
            jpaql = "select u from " + userType.getSimpleName() + " u where u.username = ?1 and u.deleted = false";
        } else {
            jpaql = "select u from " + userType.getSimpleName() + " u where u.email = ?1 and u.deleted = false"; 
        }
        
        return persistenceService.queryFirst(userType, jpaql, login);
    }

    /**
     * {@inheritDoc}
     */
    public User findByUsername(String login) {
        Class<? extends User> userType = userHandler.getUserType();
        String jpaql;
        if (Usernamed.class.isAssignableFrom(userType)) {
            jpaql = "select u from " + userType.getSimpleName() + " u where u.username = ?1 and u.deleted = false";
        } else {
            jpaql = "select u from " + userType.getSimpleName() + " u where u.email = ?1 and u.deleted = false";
        }

        return persistenceService.queryFirst(userType, jpaql, login);
    }

    /**
     * {@inheritDoc}
     */
    public User findByEmail(String email) {
        Class<? extends User> userType = userHandler.getUserType();
        return persistenceService.queryFirst(userType, "select u from " + userType.getSimpleName() + " u where u.email = ?1 and u.deleted = false", email);
    }

    /**
     * @return  The default sort property.
     */
    protected String getDefaultSortProperty() {
        return "email";
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
    public Map<String, int[]> getAssociationIds(int id) {
        return userHandler.getAssociationIds(id);
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
    public ErrorList validate(User user, Map<String, int[]> associations, boolean existing,
            String password, String passwordConfirm) {
        return userHandler.validate(user, associations, existing, password, passwordConfirm);
    }

    /**
     * {@inheritDoc}
     */
    public RegisterResult register(User user, String password, String url, Role... roles) {
        User partial = findByUsername(user.getEmail());
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
        
        boolean verify = configuration.getBoolean("jcatapult.user.verify-emails", false);
        if (verify) {
            user.setVerified(false);
            user.setGuid(makeGUID());
        }

        Map<String, int[]> associations = userHandler.getDefaultAssociations();
        if (!persist(user, associations, password)) {
            return RegisterResult.EXISTS;
        }

        if (verify) {
            resendVerificationEmail(user.getEmail(), url);
        }

        return verify ? RegisterResult.PENDING : RegisterResult.SUCCESS;
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

        Map<String, int[]> associations = userHandler.getDefaultAssociations();
        if (!persist(user, associations, null)) {
            return RegisterResult.EXISTS;
        }

        return RegisterResult.SUCCESS;
    }

    /**
     * {@inheritDoc}
     */
    public void resendVerificationEmail(String login, String url) throws EntityNotFoundException {
        User user = findByUsernameOrEmail(login);
        if (user == null) {
            throw new EntityNotFoundException("Invalid login [" + login + "]");
        }

        // Make a new GUID, just to be safe
        user.setGuid(makeGUID());
        persistenceService.persist(user);

        String template = configuration.getString("jcatapult.user.verify-email.template", "verify-email");
        EmailCommand command = emailService.sendEmail(template).
            to(new EmailAddress(user.getEmail())).
            withTemplateParam("user", user).
            withTemplateParam("url", url);
        if (command.getSubject() == null) {
            command.withSubject("Email verification");
        }
        command.later();
    }

    /**
     * {@inheritDoc}
     */
    public UpdateResult update(User user, String password) {
        if (user == null) {
            return UpdateResult.MISSING;
        }

        if (!persistenceService.contains(user)) {
            throw new PersistenceException("You must pass in managed User entities to the update method. " +
                "Please consult the JavaDoc for this method on the UserService to find out why.");
        }

        if (!StringTools.isTrimmedEmpty(password)) {
            String encodedPassword = passwordEncryptor.encryptPassword(password);
            user.setPassword(encodedPassword);
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
    public boolean persist(User user, Map<String, int[]> associations, String password) {
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
        } catch (PersistenceException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean persist(User user) {
        try {
            persistenceService.persist(user);
            return true;
        } catch (PersistenceException e) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    public UpdateResult updatePassword(int id, String password) {
        User user = findById(id);
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
    public UpdateResult resetPassword(int id, String url) {
        User user = findById(id);
        if (user == null || user.isPartial()) {
            return UpdateResult.MISSING;
        }

        user.setGuid(makeGUID());
        persistenceService.persist(user);

        String template = configuration.getString("jcatapult.user.password-reset.template", "password-reset");
        EmailCommand command = emailService.sendEmail(template).
            to(new EmailAddress(user.getEmail())).
            withTemplateParam("user", user).
            withTemplateParam("url", url);
        if (command.getSubject() == null) {
            command.withSubject("Password reset");
        }
        command.later();
        
        return UpdateResult.SUCCESS;
    }

    private String makeGUID() {
        // Create/update the GUID and save it
        Random rand = new SecureRandom();
        StringBuilder build = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            int r = rand.nextInt(16);
            build.append(Integer.toHexString(r));
        }
        return build.toString();
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
}