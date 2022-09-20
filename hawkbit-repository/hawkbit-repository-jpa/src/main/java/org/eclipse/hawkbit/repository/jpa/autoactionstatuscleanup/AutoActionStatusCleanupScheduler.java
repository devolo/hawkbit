package org.eclipse.hawkbit.repository.jpa.autoactionstatuscleanup;

import org.eclipse.hawkbit.repository.SystemManagement;
import org.eclipse.hawkbit.repository.jpa.autocleanup.CleanupTask;
import org.eclipse.hawkbit.security.SystemSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class AutoActionStatusCleanupScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoActionStatusCleanupScheduler.class);

    private static final String AUTO_ACTION_STATUS_CLEANUP = "auto-action-status-cleanup";
    private static final String SEP = ".";
    private static final String PROP_AUTO_ACTION_STATUS_DELAY = "${hawkbit.autoactionstatuscleanup.scheduler.fixedDelay:0}";

    private static final String PROP_AUTO_ACTION_STATUS_CLEANUP_INTERVAL = "${hawkbit.autoactionstatuscleanup.scheduler.frequency:14400000}";

    private final SystemManagement systemManagement;
    private final SystemSecurityContext systemSecurityContext;
    private final LockRegistry lockRegistry;
    private final List<CleanupTask> cleanupTasks;

    /**
     * Constructs the cleanup schedulers and initializes it with a set of
     * cleanup handlers.
     *
     * @param systemManagement
     *            Management APIs to invoke actions in a certain tenant context.
     * @param systemSecurityContext
     *            The system security context.
     * @param lockRegistry
     *            A registry for shared locks.
     * @param cleanupTasks
     *            A list of cleanup tasks.
     */
    public AutoActionStatusCleanupScheduler(final SystemManagement systemManagement,
                                final SystemSecurityContext systemSecurityContext, final LockRegistry lockRegistry,
                                final List<CleanupTask> cleanupTasks) {
        this.systemManagement = systemManagement;
        this.systemSecurityContext = systemSecurityContext;
        this.lockRegistry = lockRegistry;
        this.cleanupTasks = cleanupTasks;
    }

    /**
     * Scheduler method which kicks off the cleanup process.
     */
    @Scheduled(initialDelayString = PROP_AUTO_ACTION_STATUS_DELAY, fixedDelayString = PROP_AUTO_ACTION_STATUS_CLEANUP_INTERVAL)
    public void run() {
        LOGGER.warn("Auto action status cleanup scheduler has been triggered.");
        // run this code in system code privileged to have the necessary
        // permission to query and create entities
        if (!cleanupTasks.isEmpty()) {
            systemSecurityContext.runAsSystem(this::executeAutoCleanup);
        }
    }

    /**
     * Method which executes each registered cleanup task for each tenant.
     */
    @SuppressWarnings("squid:S3516")
    private Void executeAutoCleanup() {
        systemManagement.forEachTenant(tenant -> cleanupTasks.forEach(task -> {
            final Lock lock = obtainLock(task, tenant);
            if (!lock.tryLock()) {
                return;
            }

            LOGGER.debug("Obtained lock with key: {}", AUTO_ACTION_STATUS_CLEANUP + SEP + task.getId() + SEP + tenant);

            try {
                task.run();
            } catch (final RuntimeException e) {
                LOGGER.error("Cleanup task failed.", e);
            } finally {
                lock.unlock();
                LOGGER.debug("Unlocked lock with key: {}", AUTO_ACTION_STATUS_CLEANUP + SEP + task.getId() + SEP + tenant);
            }
        }));
        return null;
    }

    private Lock obtainLock(final CleanupTask task, final String tenant) {
        return lockRegistry.obtain(AUTO_ACTION_STATUS_CLEANUP + SEP + task.getId() + SEP + tenant);
    }
}
