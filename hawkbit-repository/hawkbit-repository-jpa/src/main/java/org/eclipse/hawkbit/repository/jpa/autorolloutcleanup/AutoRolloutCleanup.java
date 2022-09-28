/**
 * Copyright (c) 2022 devolo AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.autorolloutcleanup;

import org.eclipse.hawkbit.repository.*;
import org.eclipse.hawkbit.repository.jpa.autocleanup.CleanupTask;
import org.eclipse.hawkbit.repository.model.Rollout;
import org.eclipse.hawkbit.repository.model.RolloutGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

public class AutoRolloutCleanup implements CleanupTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoRolloutCleanup.class);

    private static final String ID = "rollout-cleanup";
    private static final boolean ROLLOUT_CLEANUP_ENABLED_DEFAULT = true;

    private final DeploymentManagement deploymentMgmt;
    private final TenantConfigurationManagement configMgmt;
    private final RolloutManagement rolloutMgmt;

    private final RolloutGroupManagement rolloutGroupMgmt;

    @Value("${hawkbit.autorolloutcleanup.rolloutsPerCleanup:100}")
    private int rolloutsPerCleanup;
    @Value("${hawkbit.autorolloutcleanup.rolloutGroupsPerRolloutCleanup:500}")
    private int rolloutGroupsPerRolloutCleanup;

    public AutoRolloutCleanup(final DeploymentManagement deploymentMgmt, final TenantConfigurationManagement configMgmt, final RolloutManagement rolloutMgmt, RolloutGroupManagement rolloutGroupMgmt) {
        this.deploymentMgmt = deploymentMgmt;
        this.configMgmt = configMgmt;
        this.rolloutMgmt = rolloutMgmt;
        this.rolloutGroupMgmt = rolloutGroupMgmt;
    }

    @Override
    public void run() {
        if (!isEnabled()) {
            LOGGER.debug("Rollout cleanup is disabled for this tenant...");
            return;
        }
        LOGGER.debug("rolloutsPerCleanup: {}; rolloutGroupsPerRolloutCleanup: {}", rolloutsPerCleanup, rolloutGroupsPerRolloutCleanup);

        List<Rollout> rolloutsToCleanUp = getRolloutsNotYetCleanedUp();
        LOGGER.debug("Fetched {} rollouts that were deleted in UI and not yet cleaned up", rolloutsToCleanUp.size());

        if (rolloutsToCleanUp.isEmpty()) { return; }

        // Get rolloutGroups that match
        rolloutsToCleanUp.forEach(rollout -> {
            Page<RolloutGroup> rolloutGroupPage = rolloutGroupMgmt.findByRollout(new OffsetBasedPageRequest(0, rolloutGroupsPerRolloutCleanup, Sort.unsorted()), rollout.getId());
            List<RolloutGroup> rolloutGroupList = rolloutGroupPage.getContent();

            LOGGER.debug("Found {} rollout groups for rollout with ID {}", rolloutGroupList.size(), rollout.getId());

            if (rolloutGroupList.size() == 0) {
                rolloutMgmt.setRolloutAsCleanedUp(rollout);
                return;
            }

            List<Long> rolloutGroupIds = rolloutGroupList.stream().map(RolloutGroup::getId).collect(Collectors.toList());
            rolloutGroupMgmt.deleteByIds(rolloutGroupIds);

            LOGGER.debug("Deleted {} rollout groups with ids: {}", rolloutGroupIds.size(), rolloutGroupIds);

            rolloutMgmt.setRolloutAsCleanedUp(rollout);
        });
    }

    @Override
    public String getId() {
        return ID;
    }

    private List<Rollout> getRolloutsNotYetCleanedUp() {
        final Page<Rollout> rolloutPage = rolloutMgmt.findByIsCleanedUpIsFalse(new OffsetBasedPageRequest(0, rolloutsPerCleanup, Sort.unsorted()));
        final List<Rollout> rolloutList = rolloutPage.getContent();

        return rolloutList;
    }

    private boolean isEnabled() {
        return ROLLOUT_CLEANUP_ENABLED_DEFAULT;
    }
}
