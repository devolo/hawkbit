package org.eclipse.hawkbit.repository.jpa.autoactionstatuscleanup;

import org.eclipse.hawkbit.repository.*;
import org.eclipse.hawkbit.repository.jpa.autocleanup.CleanupTask;
import org.eclipse.hawkbit.repository.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.eclipse.hawkbit.tenancy.configuration.TenantConfigurationProperties.TenantConfigurationKey.*;

public class AutoActionStatusCleanup implements CleanupTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoActionStatusCleanup.class);
    private static final String ID = "action-status-cleanup";
    private static final boolean ACTION_STATUS_CLEANUP_ENABLED_DEFAULT = false;
    private static final long ACTION_STATUS_CLEANUP_ACTION_EXPIRY_DEFAULT = TimeUnit.DAYS.toMillis(30);
    private static final EnumSet<Action.Status> EMPTY_STATUS_SET = EnumSet.noneOf(Action.Status.class);

    private final DeploymentManagement deploymentMgmt;
    private final TenantConfigurationManagement configMgmt;
    private final TargetManagement targetMgmt;
    private final ControllerManagement controllerMgmt;

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
                                   final TargetManagement targetMgmt) {
        this.deploymentMgmt = deploymentMgmt;
        this.configMgmt = configMgmt;
        this.controllerMgmt = controllerMgmt;
        this.targetMgmt = targetMgmt;
    }

    @Override
    public void run() {

        if (!isEnabled()) {
            LOGGER.debug("Action action status cleanup is disabled for this tenant...");
            return;
        }

        int N_TARGETS = 10;

        // 1. Get 100 targets with `requires_cleanup == 0`
        Pageable pageRef = new OffsetBasedPageRequest(0, N_TARGETS, Sort.by(Sort.Direction.ASC, "controllerId"));
        Page<Target> targetPage = targetMgmt.findByRequiresCleanupIsFalse(pageRef);
        List<Target> targetList = targetPage.getContent();

        LOGGER.warn("Fetched {} targets with requires_cleanup = 0", targetList.size());

        // 2a. Get all actions for these targets with `status == 1`
        targetList.forEach(target -> {
            LOGGER.warn("Fetching actions for target with Id: {}", target.getId());

            List<Long> actionIds = controllerMgmt.findActionsOfTargetWithId(target.getId());

            LOGGER.warn("Fetched {} actions for target with Id: {}", actionIds.size(), target.getId());

            // 2b. Remove all but recent action
            if (actionIds.size() >= 2) {
                LOGGER.warn("Excluding action {} for target with Id: {} from cleanup", actionIds.get(0), target.getId());
                actionIds.remove(0);
            }

            if (actionIds.size() >= 1) {
                actionIds.forEach(actionId -> {
                    // ToDo: Set this to max.
                    Pageable pageRefForActionStatus = new OffsetBasedPageRequest(0, 100, Sort.unsorted());
                    List<ActionStatus> actionStatuses = deploymentMgmt.findActionStatusByAction(pageRefForActionStatus, actionId).getContent();

                    LOGGER.warn("Fetched {} action statuses for action with Id: {}", actionStatuses.size(), actionId);

                    controllerMgmt.deleteByIds(actionStatuses.stream().map(ActionStatus::getId).collect(Collectors.toList()));
                });
            }
        });

        // 4. Set requires_cleanup for these targets to "1"
        targetMgmt.updateRequiresCleanupForTargetsWithIds(targetList.stream().map(Target::getId).collect(Collectors.toList()));
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
