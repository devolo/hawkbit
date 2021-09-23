/**
 * Copyright (c) 2020 Bosch.IO GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.management.miscs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.hawkbit.repository.DeploymentManagement;
import org.eclipse.hawkbit.repository.MaintenanceScheduleHelper;
import org.eclipse.hawkbit.repository.exception.InvalidMaintenanceScheduleException;
import org.eclipse.hawkbit.repository.exception.MultiAssignmentIsNotEnabledException;
import org.eclipse.hawkbit.repository.model.Action.ActionType;
import org.eclipse.hawkbit.repository.model.DeploymentRequest;
import org.eclipse.hawkbit.repository.model.DeploymentRequestBuilder;
import org.eclipse.hawkbit.repository.model.DistributionSetAssignmentResult;
import org.eclipse.hawkbit.repository.model.RepositoryModelConstants;
import org.eclipse.hawkbit.ui.UiProperties;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyAssignmentWindow;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyDistributionSet;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyTarget;
import org.eclipse.hawkbit.ui.common.event.EntityModifiedEventPayload;
import org.eclipse.hawkbit.ui.common.event.EntityModifiedEventPayload.EntityModifiedEventType;
import org.eclipse.hawkbit.ui.common.event.EventTopics;
import org.eclipse.hawkbit.ui.utils.SPDateTimeUtil;
import org.eclipse.hawkbit.ui.utils.UIMessageIdProvider;
import org.eclipse.hawkbit.ui.utils.UINotification;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.spring.events.EventBus.UIEventBus;

/**
 * Controller for populating data and performing assignment of targets to
 * distribution sets.
 */
public class DeploymentAssignmentWindowController {
    private static final Logger LOG = LoggerFactory.getLogger(DeploymentAssignmentWindowController.class);

    private final VaadinMessageSource i18n;
    private final UIEventBus eventBus;
    private final UINotification notification;
    private final DeploymentManagement deploymentManagement;

    private final AssignmentWindowLayout assignmentWindowLayout;

    private ProxyAssignmentWindow proxyAssignmentWindow;

    /**
     * Constructor for DeploymentAssignmentWindowController
     *
     * @param i18n
     *          VaadinMessageSource
     * @param uiProperties
     *          UiProperties
     * @param eventBus
     *          UIEventBus
     * @param notification
     *          UINotification
     * @param deploymentManagement
     *          DeploymentManagement
     */
    public DeploymentAssignmentWindowController(final VaadinMessageSource i18n, final UiProperties uiProperties,
            final UIEventBus eventBus, final UINotification notification,
            final DeploymentManagement deploymentManagement) {
        this.i18n = i18n;
        this.eventBus = eventBus;
        this.notification = notification;
        this.deploymentManagement = deploymentManagement;

        this.assignmentWindowLayout = new AssignmentWindowLayout(i18n, uiProperties);
    }

    /**
     * @return Assignment window layout
     */
    public AssignmentWindowLayout getLayout() {
        return assignmentWindowLayout;
    }

    /**
     * Populate assignment window with data
     */
    public void populateWithData() {
        proxyAssignmentWindow = new ProxyAssignmentWindow();

        proxyAssignmentWindow.setActionType(ActionType.FORCED);
        proxyAssignmentWindow.setForcedTime(SPDateTimeUtil.twoWeeksFromNowEpochMilli());
        proxyAssignmentWindow.setMaintenanceTimeZone(SPDateTimeUtil.getClientTimeZoneOffsetId());

        assignmentWindowLayout.getProxyAssignmentBinder().setBean(proxyAssignmentWindow);
    }

    /**
     * Save the given distribution sets to targets assignments
     * 
     * @param proxyTargets
     *            to assign the given distribution sets to
     * @param proxyDistributionSets
     *            to assign to the given targets
     */
    public void assignTargetsToDistributions(final List<ProxyTarget> proxyTargets,
            final List<ProxyDistributionSet> proxyDistributionSets) {

        final ActionType actionType = proxyAssignmentWindow.getActionType();

        final Long forcedTimeStamp = actionType == ActionType.TIMEFORCED ? proxyAssignmentWindow.getForcedTime()
                : RepositoryModelConstants.NO_FORCE_TIME;

        final String maintenanceSchedule = proxyAssignmentWindow.getMaintenanceSchedule();
        final String maintenanceDuration = proxyAssignmentWindow.getMaintenanceDuration();
        final String maintenanceTimeZone = proxyAssignmentWindow.getMaintenanceTimeZone();

        final Set<Long> dsIdsToAssign = proxyDistributionSets.stream().map(ProxyDistributionSet::getId)
                .collect(Collectors.toSet());

        final List<DeploymentRequest> deploymentRequests = new ArrayList<>();
        dsIdsToAssign.forEach(dsId -> proxyTargets.forEach(t -> {
            final DeploymentRequestBuilder request = DeploymentManagement.deploymentRequest(t.getControllerId(), dsId)
                    .setActionType(actionType).setForceTime(forcedTimeStamp);
            if (proxyAssignmentWindow.isMaintenanceWindowEnabled()) {
                request.setMaintenance(maintenanceSchedule, maintenanceDuration, maintenanceTimeZone);
            }
            deploymentRequests.add(request.build());
        }));

        try {
            final List<DistributionSetAssignmentResult> assignmentResults = deploymentManagement
                    .assignDistributionSets(deploymentRequests);

            // use the last one for the notification box
            showAssignmentResultNotifications(assignmentResults.get(assignmentResults.size() - 1));

            final Set<Long> assignedTargetIds = proxyTargets.stream().map(ProxyTarget::getId)
                    .collect(Collectors.toSet());
            eventBus.publish(EventTopics.ENTITY_MODIFIED, this, new EntityModifiedEventPayload(
                    EntityModifiedEventType.ENTITY_UPDATED, ProxyTarget.class, assignedTargetIds));
        } catch (final MultiAssignmentIsNotEnabledException e) {
            notification.displayValidationError(i18n.getMessage("message.target.ds.multiassign.error"));
            LOG.error("UI allowed multiassignment although it is not enabled: {}", e);
        }
    }

    private void showAssignmentResultNotifications(final DistributionSetAssignmentResult assignmentResult) {
        notification.displaySuccess(i18n.getMessage("message.target.ds.assign.success"));

        if (assignmentResult.getAssigned() > 0) {
            notification.displaySuccess(i18n.getMessage("message.target.assignment", assignmentResult.getAssigned()));
        }

        if (assignmentResult.getAlreadyAssigned() > 0) {
            notification.displaySuccess(
                    i18n.getMessage("message.target.alreadyAssigned", assignmentResult.getAlreadyAssigned()));
        }
    }

    /**
     * Check if the maintenance window is valid or not
     * 
     * @return boolean if maintenance window is valid or not
     */
    public boolean isMaintenanceWindowValid() {
        if (proxyAssignmentWindow.isMaintenanceWindowEnabled()) {
            try {
                MaintenanceScheduleHelper.validateMaintenanceSchedule(proxyAssignmentWindow.getMaintenanceSchedule(),
                        proxyAssignmentWindow.getMaintenanceDuration(), proxyAssignmentWindow.getMaintenanceTimeZone());
            } catch (final InvalidMaintenanceScheduleException e) {
                LOG.trace("Maintenance Window is invalid in UI: {}", e.getMessage());
                notification.displayValidationError(
                        i18n.getMessage(UIMessageIdProvider.CRON_VALIDATION_ERROR) + ": " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the time-forced date is valid
     * 
     * @return boolean if time-forced date is valid or not
     */
    public boolean isForceTimeValid() {
        if (proxyAssignmentWindow.getActionType() == ActionType.TIMEFORCED
                && proxyAssignmentWindow.getForcedTime() == null) {
            notification.displayValidationError(i18n.getMessage("message.forcedTime.missing"));

            return false;
        }

        return true;
    }
}
