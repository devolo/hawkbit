package org.eclipse.hawkbit.app;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.eclipse.hawkbit.repository.*;
import org.eclipse.hawkbit.repository.model.Rollout;
import org.eclipse.hawkbit.repository.model.TargetUpdateStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class CustomInfoContributor implements InfoContributor {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomInfoContributor.class);

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
        // Get timestamp
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        Map<String, Object> targetsByState = new HashMap<>();
        Map<String, Object> rolloutsByState = new HashMap<>();
        Map<String, Object> rolloutCleanupState = new HashMap<>();
        Map<String, Object> actionStatusCleanupState = new HashMap<>();

        // Setup timer to record elapsed time for fetching statistics
        SimpleMeterRegistry registry = new SimpleMeterRegistry();
        Timer timer = registry.timer("elapsedTimeForInfoEndpoint");

        timer.record(() -> {
            List<Long> targetCountByUpdateStatus = targetManagement.countByUpdateStatus();
            final long pendingTargetsCount = targetCountByUpdateStatus.get(2);
            final long pendingOfflineTargetsCount = targetManagement.countByFilters(
                    new FilterParams(Collections.singletonList(TargetUpdateStatus.PENDING),
                    Boolean.TRUE,
                    null,
                    null,
                    Boolean.FALSE));

            targetsByState.put("pending", pendingTargetsCount);
            targetsByState.put("unknown", targetCountByUpdateStatus.get(0));
            targetsByState.put("error", targetCountByUpdateStatus.get(3));
            targetsByState.put("in_sync", targetCountByUpdateStatus.get(1));
            targetsByState.put("registered", targetCountByUpdateStatus.get(4));
            targetsByState.put("ready_for_update", pendingTargetsCount - pendingOfflineTargetsCount);

            final Slice<Rollout> rolloutPage = rolloutManagement.findAllWithDetailedStatus(new OffsetBasedPageRequest(0, 100, Sort.by(Sort.Direction.ASC, "name")), false);
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

            rolloutCleanupState.put("deleted_in_ui", rolloutManagement.countRolloutsMarkedAsDeleted());
            rolloutCleanupState.put("cleaned_up", rolloutManagement.countByIsCleanUp());

            final long cleanedUpTargets = targetManagement.countByIsCleanedUp();
            final long totalTargets = targetManagement.count();

            actionStatusCleanupState.put("cleaned_up", cleanedUpTargets);
            actionStatusCleanupState.put("not_cleaned_up", totalTargets - cleanedUpTargets);

            builder.withDetail("targets_by_state", targetsByState);
            builder.withDetail("rollouts_by_state", rolloutsByState);
            builder.withDetail("rollouts_by_cleanup_state", rolloutCleanupState);
            builder.withDetail("targets_by_cleanup_state", actionStatusCleanupState);
            builder.withDetail("total_targets", targetManagement.count());
            builder.withDetail("offline_targets", targetManagement.countByFilters(new FilterParams(null, Boolean.TRUE, null, null, Boolean.FALSE)));
            builder.withDetail("total_distribution_sets", distributionSetManagement.count());
            builder.withDetail("total_rollouts", rolloutManagement.count());
        });
        
        builder.withDetail("timestamp", dateFormat.format(System.currentTimeMillis()));
        builder.withDetail("elapsed_time_in_s", timer.totalTime(TimeUnit.SECONDS));
    }
}