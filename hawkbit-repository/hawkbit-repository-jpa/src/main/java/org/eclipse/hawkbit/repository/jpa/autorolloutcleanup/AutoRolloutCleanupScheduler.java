/**
 * Copyright (c) 2022 devolo AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.autorolloutcleanup;

import org.eclipse.hawkbit.repository.SystemManagement;
import org.eclipse.hawkbit.repository.jpa.autocleanup.CleanupTask;
import org.eclipse.hawkbit.security.SystemSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class AutoRolloutCleanupScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoRolloutCleanupScheduler.class);

    private static final String AUTO_ROLLOUT_CLEANUP = "auto-rollout-cleanup";
    private static final String SEP = ".";
    private static final String PROP_AUTO_ROLLOUT_CLEANUP_INTERVAL = "${hawkbit.autorolloutcleanup.scheduler.interval:2592000000}"; // 30 days
    private static final String PROP_AUTH_ROLLOUT_CLEANUP_INITIAL_DELAY = "${hawkbit.autorolloutcleanup.scheduler.initialDelay:1000}"; // 1 second

    private final SystemManagement systemManagement;
    private final SystemSecurityContext systemSecurityContext;
    private final LockRegistry lockRegistry;
    private final List<CleanupTask> cleanupTasks;

    public AutoRolloutCleanupScheduler(final SystemManagement systemManagement, final SystemSecurityContext systemSecurityContext,
                                       final LockRegistry lockRegistry, final List<CleanupTask> cleanupTasks) {
        this.systemManagement = systemManagement;
        this.systemSecurityContext = systemSecurityContext;
        this.lockRegistry = lockRegistry;
        this.cleanupTasks = cleanupTasks;
    }

    @Scheduled(initialDelayString = PROP_AUTH_ROLLOUT_CLEANUP_INITIAL_DELAY, fixedDelayString = PROP_AUTO_ROLLOUT_CLEANUP_INTERVAL)
    public void run() {
        LOGGER.debug("Auto rollout cleanup scheduler has been triggered.");

        if(!cleanupTasks.isEmpty()) {
            systemSecurityContext.runAsSystem(this::executeAutoRolloutCleanup);
        }
    }

    private Void executeAutoRolloutCleanup() {
        systemManagement.forEachTenant(tenant -> cleanupTasks.forEach(task -> {
            final Lock lock = obtainLock(task, tenant);

            if (!lock.tryLock()) {
                return;
            }

            LOGGER.debug("Obtained lock with key: {}", AUTO_ROLLOUT_CLEANUP + SEP + task.getId() + SEP + tenant);

            try {
                task.run();
            } catch (final RuntimeException e) {
                LOGGER.error("Rollout cleanup task failed.", e);
            } finally {
                lock.unlock();
                LOGGER.debug("Unlocked lock with key: {}", AUTO_ROLLOUT_CLEANUP + SEP + task.getId() + SEP + tenant);
            }
        }));
        return null;
    }

    private Lock obtainLock(final CleanupTask task, final String tenant) {
        return lockRegistry.obtain(AUTO_ROLLOUT_CLEANUP + SEP + task.getId() + SEP + tenant);
    }
}
