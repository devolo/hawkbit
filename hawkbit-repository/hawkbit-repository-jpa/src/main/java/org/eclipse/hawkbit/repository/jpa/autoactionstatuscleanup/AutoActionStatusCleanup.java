package org.eclipse.hawkbit.repository.jpa.autoactionstatuscleanup;

import org.eclipse.hawkbit.repository.*;
import org.eclipse.hawkbit.repository.jpa.autocleanup.CleanupTask;
import org.eclipse.hawkbit.repository.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.eclipse.hawkbit.tenancy.configuration.TenantConfigurationProperties.TenantConfigurationKey.*;

public class AutoActionStatusCleanup implements CleanupTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoActionStatusCleanup.class);
    private static final String ID = "action-status-cleanup";
    private static final boolean ACTION_STATUS_CLEANUP_ENABLED_DEFAULT = false;
    private final DeploymentManagement deploymentMgmt;
    private final TenantConfigurationManagement configMgmt;
    private final TargetManagement targetMgmt;
    private final ControllerManagement controllerMgmt;
    private final QuotaManagement quotaMgmt;

    @Value("${hawkbit.autoactionstatuscleanup.targetsPerCleanup:100}")
    private int targetsPerCleanup;

    /**
     * Constructs the action cleanup handler.
     *
     * @param deploymentMgmt
     *            The {@link DeploymentManagement} to operate on.
     * @param configMgmt
     *            The {@link TenantConfigurationManagement} service.
     */
    public AutoActionStatusCleanup(final DeploymentManagement deploymentMgmt,
                                   final TenantConfigurationManagement configMgmt,
                                   final ControllerManagement controllerMgmt,
                                   final TargetManagement targetMgmt,
                                   final QuotaManagement quotaMgmt) {
        this.deploymentMgmt = deploymentMgmt;
        this.configMgmt = configMgmt;
        this.controllerMgmt = controllerMgmt;
        this.targetMgmt = targetMgmt;
        this.quotaMgmt = quotaMgmt;
    }

    @Override
    public void run() {

        if (!isEnabled()) {
            LOGGER.debug("Action action status cleanup is disabled for this tenant...");
            return;
        }

        LOGGER.debug("targetsPerCleanup: {} and maxStatusEntriesPerAction: {}", targetsPerCleanup, quotaMgmt.getMaxStatusEntriesPerAction());

        // 1. Get 100 targets with `is_cleaned_up == 0`
        Pageable pageRef = new OffsetBasedPageRequest(0, targetsPerCleanup, Sort.by(Sort.Direction.ASC, "controllerId"));
        Page<Target> targetPage = targetMgmt.findByIsCleanedUpIsFalse(pageRef);
        List<Target> targetList = targetPage.getContent();

        LOGGER.warn("Fetched {} targets with is_cleaned_up = 0", targetList.size());

        // 2a. Get all actions for these targets with `status == 1`
        targetList.forEach(target -> {
            LOGGER.warn("Fetching actions for target with Id: {}", target.getId());

            List<Long> actionIds = controllerMgmt.findActionsOfTargetWithId(target.getId());

            LOGGER.warn("Fetched {} actions for target with Id: {}", actionIds.size(), target.getId());

            // 2b. Remove all but recent action
            if (actionIds.size() >= 1) {
                LOGGER.warn("Excluding action {} for target with Id: {} from cleanup", actionIds.get(0), target.getId());
                actionIds.remove(0);
            }

            if (actionIds.size() >= 1) {
                actionIds.forEach(actionId -> {
                    Pageable pageRefForActionStatus = new OffsetBasedPageRequest(0, quotaMgmt.getMaxStatusEntriesPerAction(), Sort.unsorted());
                    List<ActionStatus> actionStatuses = deploymentMgmt.findActionStatusByAction(pageRefForActionStatus, actionId).getContent();

                    LOGGER.warn("Fetched {} action statuses for action with Id: {}", actionStatuses.size(), actionId);

                    controllerMgmt.deleteByIds(actionStatuses.stream().map(ActionStatus::getId).collect(Collectors.toList()));
                });
            }
        });

        // 4. Set is_cleaned_up for these targets to "1"
        if (!targetList.isEmpty()) {
            targetMgmt.updateIsCleanedUpForTargetsWithIds(targetList.stream().map(Target::getId).collect(Collectors.toList()), true);
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    private boolean isEnabled() {
        final TenantConfigurationValue<Boolean> isEnabled = getConfigValue(ACTION_CLEANUP_ENABLED, Boolean.class);
        return isEnabled != null ? isEnabled.getValue() : ACTION_STATUS_CLEANUP_ENABLED_DEFAULT;
    }

    private <T extends Serializable> TenantConfigurationValue<T> getConfigValue(final String key,
                                                                                final Class<T> valueType) {
        return configMgmt.getConfigurationValue(key, valueType);
    }
}
