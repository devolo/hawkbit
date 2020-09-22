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
import java.util.Map;

import org.eclipse.hawkbit.ui.common.data.aware.DescriptionAware;
import org.eclipse.hawkbit.ui.common.data.aware.DsIdAware;

/**
 * Proxy entity representing rollout popup window bean.
 */
public class ProxyBulkUploadWindow implements Serializable, DescriptionAware, DsIdAware {
    private static final long serialVersionUID = 1L;

    private ProxyDistributionSetInfo dsInfo;
    private Map<Long, String> tagIdsWithNameToAssign;
    private String description;

    /**
     * Gets the distribution set info
     *
     * @return dsInfo
     */
    @Override
    public ProxyDistributionSetInfo getDistributionSetInfo() {
        return dsInfo;
    }

    /**
     * Sets the distribution set info
     *
     * @param dsInfo
     *            Info of distribution set
     */
    @Override
    public void setDistributionSetInfo(final ProxyDistributionSetInfo dsInfo) {
        this.dsInfo = dsInfo;
    }

    /**
     * Gets the list of tagIds WithNameToAssign
     *
     * @return tagIdsWithNameToAssign
     */
    public Map<Long, String> getTagIdsWithNameToAssign() {
        return tagIdsWithNameToAssign;
    }

    /**
     * Sets the tagIdsWithNameToAssign
     *
     * @param tagIdsWithNameToAssign
     *            list of tagIds WithNameToAssign
     */
    public void setTagIdsWithNameToAssign(final Map<Long, String> tagIdsWithNameToAssign) {
        this.tagIdsWithNameToAssign = tagIdsWithNameToAssign;
    }

    /**
     * Gets the entity description
     *
     * @return description
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description
     *
     * @param description
     *            entity description
     */
    @Override
    public void setDescription(final String description) {
        this.description = description;
    }
}
