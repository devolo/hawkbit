/**
 * Copyright (c) 2020 Bosch.IO GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.ui.common.data.aware;

import org.eclipse.hawkbit.ui.common.data.proxies.ProxyTypeInfo;

/**
 * Element provides type info.
 */
public interface TypeInfoAware {
    void setTypeInfo(ProxyTypeInfo typeInfo);

    ProxyTypeInfo getTypeInfo();
}
