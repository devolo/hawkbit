/**
 * Copyright (c) 2022 devolo GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.autorolloutcleanup;

import com.google.common.collect.Lists;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.eclipse.hawkbit.repository.QuotaManagement;
import org.eclipse.hawkbit.repository.jpa.AbstractJpaIntegrationTest;
import org.eclipse.hawkbit.repository.jpa.autocleanup.AutoActionCleanup;
import org.eclipse.hawkbit.repository.jpa.model.JpaRollout;
import org.eclipse.hawkbit.repository.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link AutoActionCleanup}.
 *
 */
@Feature("Component Tests - Repository")
@Story("Rollout cleanup handler")
public class AutoRolloutCleanupTest extends AbstractJpaIntegrationTest {

    @Autowired
    private AutoRolloutCleanup autoRolloutCleanup;

    @Test
    @Description("Verifies that rollout groups for rollouts that are not deleted in UI are not cleaned up.")
    public void rolloutGroupsForRolloutsNotDeletedAreNotCleanedUp() {
        testdataFactory.createTargets(20, "test");
        testdataFactory.createTargets(20, "controller");

        final SoftwareModule module = softwareModuleManagement.create(
                entityFactory.softwareModule().create().name("swm").version("2").description("desc").type("os"));
        final DistributionSet ds = distributionSetManagement
                .create(entityFactory.distributionSet().create().name("complete").version("2").description("complete")
                        .type("os").modules(Collections.singletonList(module.getId())));

        assertThat(ds.isComplete()).isTrue();

        final Rollout rollout1 = rolloutManagement.create(entityFactory.rollout().create().name("exampleRollout").targetFilterQuery("name==test*").set(ds), quotaManagement.getMaxRolloutGroupsPerRollout(), false, new RolloutGroupConditionBuilder().withDefaults()
                .successCondition(RolloutGroup.RolloutGroupSuccessCondition.THRESHOLD, "10").build());

        final Rollout rollout2 = rolloutManagement.create(entityFactory.rollout().create().name("exampleRollout2").targetFilterQuery("name==controller*").set(ds), quotaManagement.getMaxRolloutGroupsPerRollout(), false, new RolloutGroupConditionBuilder().withDefaults()
                .successCondition(RolloutGroup.RolloutGroupSuccessCondition.THRESHOLD, "10").build());

        assertThat(rolloutRepository.count()).isEqualTo(2);
        assertThat(rolloutGroupRepository.count()).isEqualTo(quotaManagement.getMaxRolloutGroupsPerRollout()* 2L);

        // Mark rollout 1 as deleted
        final JpaRollout rolloutToDelete = rolloutRepository.findById(rollout1.getId()).get();
        assertThat(rolloutToDelete.isDeleted()).isEqualTo(false);
        rolloutToDelete.setDeleted(true);
        assertThat(rolloutToDelete.isDeleted()).isEqualTo(true);

        assertThat(rolloutRepository.count()).isEqualTo(2);
        rolloutRepository.save(rolloutToDelete);

        // Run cleanup
        autoRolloutCleanup.run();

        // Check that rollout groups are deleted
        assertThat(rolloutRepository.count()).isEqualTo(2);
        assertThat(rolloutGroupManagement.countByRollout(rollout1.getId())).isEqualTo(0);
        assertThat(rolloutGroupManagement.countByRollout(rollout2.getId())).isEqualTo(quotaManagement.getMaxRolloutGroupsPerRollout());
    }
}
