package org.eclipse.hawkbit.repository.jpa.autorolloutcleanup;

import static org.eclipse.hawkbit.tenancy.configuration.TenantConfigurationProperties.TenantConfigurationKey.ROLLOUT_CLEANUP_ENABLED;

import org.eclipse.hawkbit.repository.DeploymentManagement;
import org.eclipse.hawkbit.repository.OffsetBasedPageRequest;
import org.eclipse.hawkbit.repository.RolloutManagement;
import org.eclipse.hawkbit.repository.TenantConfigurationManagement;
import org.eclipse.hawkbit.repository.jpa.autocleanup.CleanupTask;
import org.eclipse.hawkbit.repository.model.Rollout;
import org.eclipse.hawkbit.repository.model.TenantConfigurationValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

public class AutoRolloutCleanup implements CleanupTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoRolloutCleanup.class);

    private static final String ID = "rollout-cleanup";
    private static final boolean ROLLOUT_CLEANUP_ENABLED_DEFAULT = true;

    private final DeploymentManagement deploymentMgmt;
    private final TenantConfigurationManagement configMgmt;
    private final RolloutManagement rolloutMgmt;

    public AutoRolloutCleanup(final DeploymentManagement deploymentMgmt, final TenantConfigurationManagement configMgmt, final RolloutManagement rolloutMgmt) {
        this.deploymentMgmt = deploymentMgmt;
        this.configMgmt = configMgmt;
        this.rolloutMgmt = rolloutMgmt;
    }

    @Override
    public void run() {
        if (!isEnabled()) {
            LOGGER.debug("Rollout cleanup is disabled for this tenant...");
            return;
        }

        if(getRolloutDeletionStatus()){
            final int rolloutCount = rolloutMgmt.deleteRolloutsDeletedInUI();
            LOGGER.debug("Deleted {} rollouts which have been marked to be deleted in UI", rolloutCount);
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    private boolean getRolloutDeletionStatus() {
        final Page<Rollout> rolloutPage = rolloutMgmt
                .findAllWithDetailedStatus(new OffsetBasedPageRequest(0, 100, Sort.unsorted()), true);
        final List<Rollout> rolloutList = rolloutPage.getContent();

        return rolloutList.stream().anyMatch(Rollout::isDeleted);
    }

    private boolean isEnabled() {
        final TenantConfigurationValue<Boolean> isEnabled = getConfigValue(ROLLOUT_CLEANUP_ENABLED, Boolean.class);
        return isEnabled != null ? isEnabled.getValue() : ROLLOUT_CLEANUP_ENABLED_DEFAULT;
    }

    private <T extends Serializable> TenantConfigurationValue<T> getConfigValue(final String key,
                                                                                final Class<T> valueType) {
        return configMgmt.getConfigurationValue(key, valueType);
    }

}
