package org.eclipse.hawkbit.repository.jpa.autoactionstatuscleanup;

import org.eclipse.hawkbit.repository.DeploymentManagement;
import org.eclipse.hawkbit.repository.OffsetBasedPageRequest;
import org.eclipse.hawkbit.repository.TargetManagement;
import org.eclipse.hawkbit.repository.TenantConfigurationManagement;
import org.eclipse.hawkbit.repository.jpa.autocleanup.AutoActionCleanup;
import org.eclipse.hawkbit.repository.jpa.autocleanup.CleanupTask;
import org.eclipse.hawkbit.repository.model.Action;
import org.eclipse.hawkbit.repository.model.Target;
import org.eclipse.hawkbit.repository.model.TenantConfigurationValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.time.Instant;
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
                                   final TargetManagement targetMgmt) {
        this.deploymentMgmt = deploymentMgmt;
        this.configMgmt = configMgmt;
        this.targetMgmt = targetMgmt;
    }

    @Override
    public void run() {

        if (!isEnabled()) {
            LOGGER.debug("Action action status cleanup is disabled for this tenant...");
            return;
        }

        /**
         * 1. Get the targets via targetManagement
         * 2. Do the rest step by step
         */
        Pageable pageRef = new OffsetBasedPageRequest(0, 100, Sort.by(Sort.Direction.ASC, "controllerId"));
        Page<Target> targetPage = targetMgmt.findByIsPrunedIsFalse(pageRef);
        List<Target> targetList = targetPage.getContent();

        LOGGER.warn("Fetched {} targets with is_pruned = 0", targetList.size());
    }

    @Override
    public String getId() {
        return ID;
    }

    private long getExpiry() {
        final TenantConfigurationValue<Long> expiry = getConfigValue(ACTION_CLEANUP_ACTION_EXPIRY, Long.class);
        return expiry != null ? expiry.getValue() : ACTION_STATUS_CLEANUP_ACTION_EXPIRY_DEFAULT;
    }

    private EnumSet<Action.Status> getActionStatus() {
        final TenantConfigurationValue<String> statusStr = getConfigValue(ACTION_CLEANUP_ACTION_STATUS, String.class);
        if (statusStr != null) {
            return Arrays.stream(statusStr.getValue().split("[;,]")).map(Action.Status::valueOf)
                    .collect(Collectors.toCollection(() -> EnumSet.noneOf(Action.Status.class)));
        }
        return EMPTY_STATUS_SET;
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
