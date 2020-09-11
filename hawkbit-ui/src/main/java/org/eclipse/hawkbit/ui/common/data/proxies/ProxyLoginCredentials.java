/**
 * Copyright (c) 2020 Bosch.IO GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.common.data.proxies;

import java.io.Serializable;

/**
 * Proxy for login credentials.
 */
public class ProxyLoginCredentials implements Serializable {
    private static final long serialVersionUID = 1L;

    private String tenant;
    private String username;
    private String password;

    /**
     * Gets the tenant
     *
     * @return tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * Sets the tenant
     *
     * @param tenant
     *          Tenant of corresponding user
     */
    public void setTenant(final String tenant) {
        this.tenant = tenant;
    }

    /**
     * Gets the username
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username
     *
     * @param username
     *          Username for login
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * Gets the password
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password
     *
     * @param password
     *          password for login
     */
    public void setPassword(final String password) {
        this.password = password;
    }

}
