/**
 * Copyright (c) 2020 Bosch.IO GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.common.data.mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.hawkbit.repository.RolloutGroupManagement;
import org.eclipse.hawkbit.repository.TargetFilterQueryManagement;
import org.eclipse.hawkbit.repository.model.RolloutGroup;
import org.eclipse.hawkbit.repository.model.TargetFilterQuery;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyAdvancedRolloutGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.util.StringUtils;

/**
 * Maps {@link RolloutGroup} entities, fetched from backend, to the
 * {@link ProxyAdvancedRolloutGroup} entities.
 */
public class RolloutGroupToAdvancedDefinitionMapper {

    private final TargetFilterQueryManagement targetFilterQueryManagement;

    /**
     * Constructor for RolloutGroupToAdvancedDefinitionMapper
     *
     * @param targetFilterQueryManagement
     *          TargetFilterQueryManagement
     */
    public RolloutGroupToAdvancedDefinitionMapper(final TargetFilterQueryManagement targetFilterQueryManagement) {
        this.targetFilterQueryManagement = targetFilterQueryManagement;
    }

    /**
     * Map advanced Rollout Group
     *
     * @param rolloutGroup
     *          RolloutGroup
     *
     * @return ProxyAdvancedRolloutGroup
     */
    public ProxyAdvancedRolloutGroup map(final RolloutGroup rolloutGroup) {
        final ProxyAdvancedRolloutGroup advancedGroupRow = new ProxyAdvancedRolloutGroup();
        advancedGroupRow.setGroupName(rolloutGroup.getName());
        advancedGroupRow.setTargetsCount((long) rolloutGroup.getTotalTargets());

        final String groupTargetFilterQuery = rolloutGroup.getTargetFilterQuery();
        if (!StringUtils.isEmpty(groupTargetFilterQuery)) {
            advancedGroupRow.setTargetFilterQuery(groupTargetFilterQuery);
            final Page<TargetFilterQuery> filterQueries = targetFilterQueryManagement.findByQuery(PageRequest.of(0, 1),
                    groupTargetFilterQuery);
            if (filterQueries.getTotalElements() == 1) {
                advancedGroupRow.setTargetFilterId(filterQueries.getContent().get(0).getId());
            }
        }

        advancedGroupRow.setTargetPercentage(rolloutGroup.getTargetPercentage());
        advancedGroupRow.setTriggerThresholdPercentage(rolloutGroup.getSuccessConditionExp());
        advancedGroupRow.setErrorThresholdPercentage(rolloutGroup.getErrorConditionExp());

        return advancedGroupRow;
    }

    /**
     * Fetch rollout group data from the backend
     *
     * @param rolloutId
     *          Rollout id
     * @param rolloutGroupManagement
     *          RolloutGroupManagement
     * @param pageCount
     *          Total page count
     *
     * @return List of advance rollout group
     */
    public List<ProxyAdvancedRolloutGroup> loadRolloutGroupssFromBackend(final Long rolloutId,
            final RolloutGroupManagement rolloutGroupManagement, final int pageCount) {
        return rolloutGroupManagement.findByRollout(PageRequest.of(0, pageCount), rolloutId).stream().map(this::map)
                .collect(Collectors.toList());
    }
}
