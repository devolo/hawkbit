/**
 * Copyright (c) 2020 devolo AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.common.data.proxies;

public class ProxySystemConfigTargetSearch extends ProxySystemConfigWindow {
    private static final long serialVersionUID = 1L;

    private boolean attributeSearchEnabled;

    public boolean isAttributeSearchEnabled() {
        return this.attributeSearchEnabled;
    }

    public void setAttributeSearch(final boolean enabled){
        this.attributeSearchEnabled = enabled;
    }
}
