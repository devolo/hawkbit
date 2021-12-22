package org.eclipse.hawkbit.app;

import org.eclipse.hawkbit.repository.DistributionSetManagement;
import org.eclipse.hawkbit.repository.RolloutManagement;
import org.eclipse.hawkbit.repository.TargetManagement;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

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
        builder.withDetail("sampleData", "Hello World!");
    }
}