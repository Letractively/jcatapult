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
 */
package org.example.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * <p>
 * This is a test user.
 * </p>
 *
 * @author  Brian Pontarelli
 */
public class User {
    private String name;
    private Integer age;
    private boolean male;
    private Map<String, Address> addresses = new HashMap<String, Address>();
    private List<User> siblings = new ArrayList<User>();
    private String[] securityQuestions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public Map<String, Address> getAddresses() {
        return addresses;
    }

    public Address getAddress(String type) {
        return addresses.get(type);
    }

    public void setAddresses(Map<String, Address> addresses) {
        this.addresses = addresses;
    }

    public void setAddress(String type, Address address) {
        this.addresses.put(type, address);
    }

    public List<User> getSiblings() {
        return siblings;
    }

    public User getSibling(int index) {
        if (index >= this.siblings.size()) {
            return null;
        }

        return siblings.get(index);
    }

    public void setSiblings(List<User> siblings) {
        this.siblings = siblings;
    }

    public void setSibling(int index, User sibling) {
        if (index >= this.siblings.size()) {
            for (int i = this.siblings.size(); i <= index; i++) {
                this.siblings.add(null);
            }
        }
        this.siblings.set(index, sibling);
    }

    public String[] getSecurityQuestions() {
        return securityQuestions;
    }

    public void setSecurityQuestions(String[] securityQuestions) {
        this.securityQuestions = securityQuestions;
    }
}