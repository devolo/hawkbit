package org.eclipse.hawkbit.app;

import org.eclipse.hawkbit.repository.DistributionSetManagement;
import org.eclipse.hawkbit.repository.OffsetBasedPageRequest;
import org.eclipse.hawkbit.repository.RolloutManagement;
import org.eclipse.hawkbit.repository.TargetManagement;
import org.eclipse.hawkbit.repository.model.Rollout;
import org.eclipse.hawkbit.repository.model.TargetUpdateStatus;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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

        Map<String, Object> rolloutsByState = new HashMap<String, Object>();

        final Page<Rollout> rolloutPage = rolloutManagement.findAllWithDetailedStatus(new OffsetBasedPageRequest(0, 100, Sort.by(Sort.Direction.ASC, "name")), false);
        final List<Rollout> rolloutList = rolloutPage.getContent();

        rolloutsByState.put("creating", rolloutList.stream().filter(rollout -> Rollout.RolloutStatus.CREATING.equals(rollout.getStatus())).count());
        rolloutsByState.put("approval_denied", rolloutList.stream().filter(rollout -> Rollout.RolloutStatus.APPROVAL_DENIED.equals(rollout.getStatus())).count());
        rolloutsByState.put("deleted", rolloutList.stream().filter(rollout -> Rollout.RolloutStatus.DELETED.equals(rollout.getStatus())).count());
        rolloutsByState.put("deleting", rolloutList.stream().filter(rollout -> Rollout.RolloutStatus.DELETING.equals(rollout.getStatus())).count());
        rolloutsByState.put("finished", rolloutList.stream().filter(rollout -> Rollout.RolloutStatus.FINISHED.equals(rollout.getStatus())).count());
        rolloutsByState.put("paused", rolloutList.stream().filter(rollout -> Rollout.RolloutStatus.PAUSED.equals(rollout.getStatus())).count());
        rolloutsByState.put("ready", rolloutList.stream().filter(rollout -> Rollout.RolloutStatus.READY.equals(rollout.getStatus())).count());
        rolloutsByState.put("running", rolloutList.stream().filter(rollout -> Rollout.RolloutStatus.RUNNING.equals(rollout.getStatus())).count());
        rolloutsByState.put("starting", rolloutList.stream().filter(rollout -> Rollout.RolloutStatus.STARTING.equals(rollout.getStatus())).count());
        rolloutsByState.put("stopped", rolloutList.stream().filter(rollout -> Rollout.RolloutStatus.STOPPED.equals(rollout.getStatus())).count());
        rolloutsByState.put("waiting_for_approval", rolloutList.stream().filter(rollout -> Rollout.RolloutStatus.WAITING_FOR_APPROVAL.equals(rollout.getStatus())).count());

        builder.withDetail("rollouts_by_state", rolloutsByState);

        builder.withDetail("total_targets", targetManagement.count());
        builder.withDetail("offline_targets", targetManagement.countByFilters(null, Boolean.TRUE, null, null, Boolean.FALSE));
        builder.withDetail("total_distribution_sets", distributionSetManagement.count());
        builder.withDetail("total_rollouts", rolloutManagement.count());
    }
}