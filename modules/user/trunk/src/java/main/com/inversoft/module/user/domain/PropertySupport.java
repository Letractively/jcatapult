/*
 * Copyright (c) 2001-2008, Inversoft, All Rights Reserved
 */
package com.inversoft.module.user.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.FetchType;

/**
 * <p>
 * This class provides user properties support.
 * </p>
 *
 * @author Brian Pontarelli
 */
@Embeddable
public class PropertySupport implements Serializable {
    private static final long serialVersionUID = 1l;

    @OneToMany(cascade={CascadeType.ALL}, mappedBy = "user", fetch = FetchType.EAGER)
    @MapKey(name="name")
    private Map<String, UserProperty> map = new HashMap<String, UserProperty>();

    /**
     * @return  The properties.
     */
    public Map<String, UserProperty> getMap() {
        return map;
    }

    /**
     * Sets the entire properties map.
     *
     * @param   map The map of properties.
     */
    public void setProperties(Map<String, UserProperty> map) {
        this.map = map;
    }

    /**
     * Adds the given property to the user. If a property already exists with the given name, its
     * value is updated.
     *
     * @param   name The property name.
     * @param   value The property value.
     */
    public void add(String name, String value) {
        if (this.map.containsKey(name)) {
            this.map.get(name).setValue(value);
        } else {
            this.map.put(name, new UserProperty(name, value));
        }
    }

    /**
     * Retrieves the property value for the given name.
     *
     * @param   name The property name.
     * @return  The property value or null if there is no property with the given name.
     */
    public String getString(String name) {
        if (map.containsKey(name)) {
            return map.get(name).getValue();
        }

        return null;
    }

    /**
     * Delegates to {@link #add(String, String)}.
     *
     * @param   name The property name.
     * @param   value The property value.
     */
    public void setString(String name, String value) {
        add(name, value);
    }

    /**
     * Retrieves the property value for the given name as a boolean.
     *
     * @param   name The property name.
     * @return  The property value or false if there is no property with the given name.
     */
    public boolean getBoolean(String name) {
        return map.containsKey(name) && Boolean.parseBoolean(map.get(name).getValue());
    }

    /**
     * Sets the property value by delegating to {@link #add(String, String)} and passing the
     * boolean as a String ("true" or "false").
     *
     * @param   name The property name.
     * @param   value The property value
     */
    public void setBoolean(String name, boolean value) {
        add(name, value ? "true" : "false");
    }

    /**
     * Retrieves the property value for the given name as a int.
     *
     * @param   name The property name.
     * @return  The property value or 0 if there is no property with the given name.
     */
    public int getInt(String name) {
        if (map.containsKey(name)) {
            return Integer.parseInt(map.get(name).getValue());
        }

        return 0;
    }

    /**
     * Sets the property value by delegating to {@link #add(String, String)} and passing the
     * int as a String.
     *
     * @param   name The property name.
     * @param   value The property value
     */
    public void setInt(String name, int value) {
        add(name, Integer.toString(value));
    }
}