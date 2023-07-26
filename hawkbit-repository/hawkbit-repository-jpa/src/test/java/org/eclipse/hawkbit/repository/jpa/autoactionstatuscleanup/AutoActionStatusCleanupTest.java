/**
 * Copyright (c) 2022 devolo GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.autoactionstatuscleanup;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.eclipse.hawkbit.repository.jpa.AbstractJpaIntegrationTest;
import org.eclipse.hawkbit.repository.model.Action;
import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.Target;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.eclipse.hawkbit.tenancy.configuration.TenantConfigurationProperties.TenantConfigurationKey.*;

/**
 * Test class for {@link AutoActionStatusCleanup}
 */
@Feature("Component Tests - Repository")
@Story("Action status cleanup handler")
public class AutoActionStatusCleanupTest extends AbstractJpaIntegrationTest {
    @Autowired
    private AutoActionStatusCleanup autoActionStatusCleanup;

    @Test
    @Description("Verifies that action statuses (and subsequently action status messages) for the latest action of a target is not cleaned up")
    public void actionStatusesForLatestActionIsNotCleanedUp() {
        setupCleanupConfiguration(true);

        // Create a target
        Target target = testdataFactory.createTarget("target");
        assertThat(target.getIsCleanedUp()).isEqualTo(false);

        // Generate several actions and action statuses for this target
        final DistributionSet ds = testdataFactory.createDistributionSet("ds");
        final Long action = getFirstAssignedActionId(assignDistributionSet(ds.getId(), target.getControllerId()));
        controllerManagement.addUpdateActionStatus(entityFactory.actionStatus().create(action).status(Action.Status.FINISHED));

        assertThat(actionRepository.count()).isEqualTo(1);
        assertThat(actionStatusRepository.count()).isEqualTo(2);

        // Run cleanup
        autoActionStatusCleanup.run();

        // Check that number of actions and action statuses are still the same
        assertThat(actionRepository.count()).isEqualTo(1);
        assertThat(actionStatusRepository.count()).isEqualTo(2);
    }

    @Test
    @Description("Verifies that action statuses (and subsequently action status messages) for all but latest action of a target are cleaned up")
    public void actionStatusesForAllButLatestActionsAreCleanedUp() {
        setupCleanupConfiguration(true);

        // Create a target
        Target target = testdataFactory.createTarget("target");
        assertThat(target.getIsCleanedUp()).isEqualTo(false);

        // Generate several actions and action statuses for this target
        final DistributionSet ds = testdataFactory.createDistributionSet("ds");
        final Long action = getFirstAssignedActionId(assignDistributionSet(ds.getId(), target.getControllerId()));
        controllerManagement.addUpdateActionStatus(entityFactory.actionStatus().create(action).status(Action.Status.FINISHED));

        final DistributionSet ds2 = testdataFactory.createDistributionSet("ds2");
        final Long action2 = getFirstAssignedActionId(assignDistributionSet(ds2.getId(), target.getControllerId()));
        controllerManagement.addUpdateActionStatus(entityFactory.actionStatus().create(action2).status(Action.Status.FINISHED));

        assertThat(actionRepository.count()).isEqualTo(2);
        assertThat(actionStatusRepository.count()).isEqualTo(4);
        assertThat(autoActionStatusCleanup).isNotNull();

        autoActionStatusCleanup.run();

        target = targetRepository.findById(target.getId()).get();

        assertThat(target.getIsCleanedUp()).isEqualTo(true);
        assertThat(actionRepository.count()).isEqualTo(2);
        assertThat(actionStatusRepository.count()).isEqualTo(3); // Remove only the penultimate status only for previous action
    }

    @Test
    @Description("Verifies that action statuses (and subsequently action status messages) for actions in error status are not cleaned up")
    public void actionStatusesForActionsInErrorStateAreNotCleanedUp() {
        setupCleanupConfiguration(true);

        // Create a target
        Target target = testdataFactory.createTarget("target");
        assertThat(target.getIsCleanedUp()).isEqualTo(false);

        // Generate several actions and action statuses for this target
        final DistributionSet ds = testdataFactory.createDistributionSet("ds");
        final Long action = getFirstAssignedActionId(assignDistributionSet(ds.getId(), target.getControllerId()));
        controllerManagement.addUpdateActionStatus(entityFactory.actionStatus().create(action).status(Action.Status.ERROR));

        final DistributionSet ds2 = testdataFactory.createDistributionSet("ds2");
        final Long action2 = getFirstAssignedActionId(assignDistributionSet(ds2.getId(), target.getControllerId()));
        controllerManagement.addUpdateActionStatus(entityFactory.actionStatus().create(action2).status(Action.Status.FINISHED));

        assertThat(actionRepository.count()).isEqualTo(2);
        assertThat(actionStatusRepository.count()).isEqualTo(4);
        assertThat(autoActionStatusCleanup).isNotNull();

        autoActionStatusCleanup.run();

        target = targetRepository.findById(target.getId()).get();

        assertThat(target.getIsCleanedUp()).isEqualTo(true);
        assertThat(actionRepository.count()).isEqualTo(2);
        assertThat(actionStatusRepository.count()).isEqualTo(4);
    }

    @Test
    @Description("Verifies that `is_cleaned_up` column is set to false if target has been cleaned up before AND a new action has been created")
    public void targetsHaveIsCleanedUpSetToFalseAfterANewActionIsCreated() {
        setupCleanupConfiguration(true);

        // Create a target
        Target target = testdataFactory.createTarget("target");
        assertThat(target.getIsCleanedUp()).isEqualTo(false);

        // Generate several actions and action statuses for this target
        final DistributionSet ds = testdataFactory.createDistributionSet("ds");
        final Long action = getFirstAssignedActionId(assignDistributionSet(ds.getId(), target.getControllerId()));
        controllerManagement.addUpdateActionStatus(entityFactory.actionStatus().create(action).status(Action.Status.ERROR));

        final DistributionSet ds2 = testdataFactory.createDistributionSet("ds2");
        final Long action2 = getFirstAssignedActionId(assignDistributionSet(ds2.getId(), target.getControllerId()));
        controllerManagement.addUpdateActionStatus(entityFactory.actionStatus().create(action2).status(Action.Status.FINISHED));

        autoActionStatusCleanup.run();

        target = targetRepository.findById(target.getId()).get();
        assertThat(target.getIsCleanedUp()).isEqualTo(true);

        final DistributionSet ds3 = testdataFactory.createDistributionSet("ds3");
        final Long action3 = getFirstAssignedActionId(assignDistributionSet(ds3.getId(), target.getControllerId()));
        controllerManagement.addUpdateActionStatus(entityFactory.actionStatus().create(action3).status(Action.Status.FINISHED));

        target = targetRepository.findByControllerId("target").get();
        target = targetManagement.get(target.getId()).get();
        target = targetRepository.findById(target.getId()).get();

        assertThat(target.getIsCleanedUp()).isEqualTo(false);
    }

    private void setupCleanupConfiguration(final boolean cleanupEnabled) {
        tenantConfigurationManagement.addOrUpdateConfiguration(ACTION_CLEANUP_ENABLED, cleanupEnabled);
    }
}
