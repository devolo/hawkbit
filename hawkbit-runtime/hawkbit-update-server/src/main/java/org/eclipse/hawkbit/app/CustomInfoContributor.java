package org.eclipse.hawkbit.app;

import org.eclipse.hawkbit.repository.DistributionSetManagement;
import org.eclipse.hawkbit.repository.RolloutManagement;
import org.eclipse.hawkbit.repository.TargetManagement;
import org.eclipse.hawkbit.repository.model.TargetUpdateStatus;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CustomInfoContributor implements InfoContributor {

    private final TargetManagement targetManagement;
    private final DistributionSetManagement distributionSetManagement;
    private final RolloutManagement rolloutManagement;

    public CustomInfoContributor(TargetManagement targetManagement, DistributionSetManagement distributionSetManagement, RolloutManagement rolloutManagement) {
        this.targetManagement = targetManagement;
        this.distributionSetManagement = distributionSetManagement;
        this.rolloutManagement = rolloutManagement;
    }
    @Override
    public void contribute(Info.Builder builder) {
        Map<String, Object> targetsByState = new HashMap<String, Object>();

        final List<TargetUpdateStatus> pendingTargets = Collections.singletonList(TargetUpdateStatus.PENDING);
        final List<TargetUpdateStatus> unknownTargets = Collections.singletonList(TargetUpdateStatus.UNKNOWN);
        final List<TargetUpdateStatus> errorTargets = Collections.singletonList(TargetUpdateStatus.ERROR);
        final List<TargetUpdateStatus> inSyncTargets = Collections.singletonList(TargetUpdateStatus.IN_SYNC);
        final List<TargetUpdateStatus> registeredTargets = Collections.singletonList(TargetUpdateStatus.REGISTERED);

        targetsByState.put("pending", targetManagement.countByFilters(pendingTargets, null, null, null, Boolean.FALSE));
        targetsByState.put("unknown", targetManagement.countByFilters(unknownTargets, null, null, null, Boolean.FALSE));
        targetsByState.put("error", targetManagement.countByFilters(errorTargets, null, null, null, Boolean.FALSE));
        targetsByState.put("in_sync", targetManagement.countByFilters(inSyncTargets, null, null, null, Boolean.FALSE));
        targetsByState.put("registered", targetManagement.countByFilters(registeredTargets, null, null, null, Boolean.FALSE));

        builder.withDetail("targets_by_state", targetsByState);
        builder.withDetail("total_targets", targetManagement.count());
        builder.withDetail("offline_targets", targetManagement.countByFilters(null, Boolean.TRUE, null, null, Boolean.FALSE));
    }
}