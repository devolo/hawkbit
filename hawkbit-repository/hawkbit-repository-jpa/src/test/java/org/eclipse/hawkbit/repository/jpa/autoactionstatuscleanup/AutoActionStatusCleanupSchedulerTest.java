/**
 * Copyright (c) 2022 devolo AG and others.
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
import org.eclipse.hawkbit.repository.jpa.autocleanup.AutoCleanupScheduler;
import org.eclipse.hawkbit.repository.jpa.autocleanup.CleanupTask;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Feature("Component Tests - Repository")
@Story("Auto action status cleanup scheduler")
public class AutoActionStatusCleanupSchedulerTest extends AbstractJpaIntegrationTest {
    private final AtomicInteger counter = new AtomicInteger();

    @Autowired
    private LockRegistry lockRegistry;

    @Before
    public void setUp() {
        counter.set(0);
    }

    @Test
    @Description("Verifies that all cleanup handlers are executed regardless if one of them throws an error")
    public void executeHandlerChain() {
        new AutoActionStatusCleanupScheduler(systemManagement, systemSecurityContext, lockRegistry, Arrays.asList(
                new org.eclipse.hawkbit.repository.jpa.autoactionstatuscleanup.AutoActionStatusCleanupSchedulerTest.SuccessfulCleanup(), new org.eclipse.hawkbit.repository.jpa.autoactionstatuscleanup.AutoActionStatusCleanupSchedulerTest.SuccessfulCleanup(), new org.eclipse.hawkbit.repository.jpa.autoactionstatuscleanup.AutoActionStatusCleanupSchedulerTest.FailingCleanup(), new org.eclipse.hawkbit.repository.jpa.autoactionstatuscleanup.AutoActionStatusCleanupSchedulerTest.SuccessfulCleanup())).run();

        assertThat(counter.get()).isEqualTo(4);

    }

    private class SuccessfulCleanup implements CleanupTask {

        @Override
        public void run() {
            counter.incrementAndGet();
        }

        @Override
        public String getId() {
            return "success";
        }

    }

    private class FailingCleanup implements CleanupTask {

        @Override
        public void run() {
            counter.incrementAndGet();
            throw new RuntimeException("cleanup failed");
        }

        @Override
        public String getId() {
            return "success";
        }

    }

}
