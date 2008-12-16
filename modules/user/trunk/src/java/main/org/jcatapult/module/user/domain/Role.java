/*
 * Copyright (c) 2007, Inversoft LLC, All Rights Reserved
 */
package com.inversoft.module.user.domain;

import org.jcatapult.persistence.domain.Identifiable;

/**
 * <p>
 * This is a marker interface for roles.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public interface Role extends Identifiable {
    /**
     * @return  The name of the role.
     */
    String getName();
}