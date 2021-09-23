/**
 * Copyright (c) 2020 Bosch.IO GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.common.data.proxies;

import java.util.List;

/**
 * Proxy for target attributes details.
 */
public class ProxyTargetAttributesDetails {

    private final String controllerId;
    private final boolean isRequestAttributes;
    private final List<ProxyKeyValueDetails> targetAttributes;

    /**
     * dummy constructor needed for TargetAttributesDetailsComponent getValue
     */
    public ProxyTargetAttributesDetails() {
        this.controllerId = null;
        this.isRequestAttributes = false;
        this.targetAttributes = null;
    }

    /**
     * Constructor for ProxyTargetAttributesDetails
     *
     * @param controllerId
     *          Target attribute controller id
     * @param isRequestAttributes
     *          Target has request attributes
     * @param targetAttributes
 *              Target attributes
     */
    public ProxyTargetAttributesDetails(final String controllerId, final boolean isRequestAttributes,
            final List<ProxyKeyValueDetails> targetAttributes) {
        this.controllerId = controllerId;
        this.isRequestAttributes = isRequestAttributes;
        this.targetAttributes = targetAttributes;
    }

    /**
     * @return target attributes detail controllerId
     */
    public String getControllerId() {
        return controllerId;
    }

    /**
     * @return <code>true</code> if the target attribute detail has requestAttributes, otherwise
     *         <code>false</code>
     */
    public boolean isRequestAttributes() {
        return isRequestAttributes;
    }

    /**
     * @return key value pair of target attributes
     */
    public List<ProxyKeyValueDetails> getTargetAttributes() {
        return targetAttributes;
    }
}
