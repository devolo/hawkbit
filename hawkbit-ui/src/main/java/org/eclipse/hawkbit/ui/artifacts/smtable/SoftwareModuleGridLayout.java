/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.artifacts.smtable;

import java.util.Arrays;
import java.util.List;

import org.eclipse.hawkbit.repository.EntityFactory;
import org.eclipse.hawkbit.repository.SoftwareModuleManagement;
import org.eclipse.hawkbit.repository.SoftwareModuleTypeManagement;
import org.eclipse.hawkbit.ui.SpPermissionChecker;
import org.eclipse.hawkbit.ui.artifacts.upload.FileUploadProgress;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxySoftwareModule;
import org.eclipse.hawkbit.ui.common.detailslayout.SoftwareModuleDetails;
import org.eclipse.hawkbit.ui.common.detailslayout.SoftwareModuleDetailsHeader;
import org.eclipse.hawkbit.ui.common.event.EventLayout;
import org.eclipse.hawkbit.ui.common.event.EventLayoutViewAware;
import org.eclipse.hawkbit.ui.common.event.EventTopics;
import org.eclipse.hawkbit.ui.common.event.EventView;
import org.eclipse.hawkbit.ui.common.event.EventViewAware;
import org.eclipse.hawkbit.ui.common.layout.AbstractGridComponentLayout;
import org.eclipse.hawkbit.ui.common.layout.MasterEntityAwareComponent;
import org.eclipse.hawkbit.ui.common.layout.listener.EntityModifiedListener;
import org.eclipse.hawkbit.ui.common.layout.listener.EntityModifiedListener.EntityModifiedAwareSupport;
import org.eclipse.hawkbit.ui.common.layout.listener.FilterChangedListener;
import org.eclipse.hawkbit.ui.common.layout.listener.GenericEventListener;
import org.eclipse.hawkbit.ui.common.layout.listener.SelectGridEntityListener;
import org.eclipse.hawkbit.ui.common.layout.listener.SelectionChangedListener;
import org.eclipse.hawkbit.ui.common.layout.listener.support.EntityModifiedGridRefreshAwareSupport;
import org.eclipse.hawkbit.ui.common.layout.listener.support.EntityModifiedSelectionAwareSupport;
import org.eclipse.hawkbit.ui.common.state.GridLayoutUiState;
import org.eclipse.hawkbit.ui.common.state.TypeFilterLayoutUiState;
import org.eclipse.hawkbit.ui.utils.UINotification;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;
import org.vaadin.spring.events.EventBus.UIEventBus;

/**
 * Software module table layout. (Upload Management)
 */
public class SoftwareModuleGridLayout extends AbstractGridComponentLayout {
    private static final long serialVersionUID = 1L;

    private final SoftwareModuleGridHeader softwareModuleGridHeader;
    private final SoftwareModuleGrid softwareModuleGrid;
    private final SoftwareModuleDetailsHeader softwareModuleDetailsHeader;
    private final SoftwareModuleDetails softwareModuleDetails;

    private final transient FilterChangedListener<ProxySoftwareModule> smFilterListener;
    private final transient SelectionChangedListener<ProxySoftwareModule> masterSmChangedListener;
    private final transient SelectGridEntityListener<ProxySoftwareModule> selectSmListener;
    private final transient EntityModifiedListener<ProxySoftwareModule> smModifiedListener;
    private final transient GenericEventListener<FileUploadProgress> fileUploadChangedListener;

    /**
     * Constructor for SoftwareModuleGridLayout
     *
     * @param i18n
     *            VaadinMessageSource
     * @param permChecker
     *            SpPermissionChecker
     * @param uiNotification
     *            UINotification
     * @param eventBus
     *            UIEventBus
     * @param softwareModuleManagement
     *            SoftwareModuleManagement
     * @param softwareModuleTypeManagement
     *            SoftwareModuleTypeManagement
     * @param entityFactory
     *            EntityFactory
     * @param smTypeFilterLayoutUiState
     *            TypeFilterLayoutUiState
     * @param smGridLayoutUiState
     *            GridLayoutUiState
     */
    public SoftwareModuleGridLayout(final VaadinMessageSource i18n, final SpPermissionChecker permChecker,
            final UINotification uiNotification, final UIEventBus eventBus,
            final SoftwareModuleManagement softwareModuleManagement,
            final SoftwareModuleTypeManagement softwareModuleTypeManagement, final EntityFactory entityFactory,
            final TypeFilterLayoutUiState smTypeFilterLayoutUiState, final GridLayoutUiState smGridLayoutUiState) {
        super();

        final SmWindowBuilder smWindowBuilder = new SmWindowBuilder(i18n, entityFactory, eventBus, uiNotification,
                softwareModuleManagement, softwareModuleTypeManagement, EventView.UPLOAD);
        final SmMetaDataWindowBuilder smMetaDataWindowBuilder = new SmMetaDataWindowBuilder(i18n, entityFactory,
                eventBus, uiNotification, permChecker, softwareModuleManagement);

        this.softwareModuleGridHeader = new SoftwareModuleGridHeader(i18n, permChecker, eventBus,
                smTypeFilterLayoutUiState, smGridLayoutUiState, smWindowBuilder, EventView.UPLOAD);
        this.softwareModuleGridHeader.buildHeader();
        this.softwareModuleGrid = new SoftwareModuleGrid(eventBus, i18n, permChecker, uiNotification,
                smTypeFilterLayoutUiState, smGridLayoutUiState, softwareModuleManagement, EventView.UPLOAD);
        this.softwareModuleGrid.init();

        this.softwareModuleDetailsHeader = new SoftwareModuleDetailsHeader(i18n, permChecker, eventBus, uiNotification,
                smWindowBuilder, smMetaDataWindowBuilder);
        this.softwareModuleDetailsHeader.buildHeader();
        this.softwareModuleDetails = new SoftwareModuleDetails(i18n, eventBus, softwareModuleManagement,
                softwareModuleTypeManagement, smMetaDataWindowBuilder);
        this.softwareModuleDetails.buildDetails();

        this.smFilterListener = new FilterChangedListener<>(eventBus, ProxySoftwareModule.class,
                new EventViewAware(EventView.UPLOAD), softwareModuleGrid.getFilterSupport());
        this.masterSmChangedListener = new SelectionChangedListener<>(eventBus,
                new EventLayoutViewAware(EventLayout.SM_LIST, EventView.UPLOAD), getMasterSmAwareComponents());
        this.selectSmListener = new SelectGridEntityListener<>(eventBus,
                new EventLayoutViewAware(EventLayout.SM_LIST, EventView.UPLOAD),
                softwareModuleGrid.getSelectionSupport());
        this.smModifiedListener = new EntityModifiedListener.Builder<>(eventBus, ProxySoftwareModule.class)
                .entityModifiedAwareSupports(getSmModifiedAwareSupports()).build();
        this.fileUploadChangedListener = new GenericEventListener<>(eventBus, EventTopics.FILE_UPLOAD_CHANGED,
                this::onUploadChanged);

        buildLayout(softwareModuleGridHeader, softwareModuleGrid, softwareModuleDetailsHeader, softwareModuleDetails);
    }

    private List<MasterEntityAwareComponent<ProxySoftwareModule>> getMasterSmAwareComponents() {
        return Arrays.asList(softwareModuleDetailsHeader, softwareModuleDetails);
    }

    private List<EntityModifiedAwareSupport> getSmModifiedAwareSupports() {
        return Arrays.asList(EntityModifiedGridRefreshAwareSupport.of(softwareModuleGrid::refreshAll),
                EntityModifiedSelectionAwareSupport.of(softwareModuleGrid.getSelectionSupport(),
                        softwareModuleGrid::mapIdToProxyEntity));
    }

    /**
     * Verifies when file upload is in progress
     *
     * @param fileUploadProgress
     *          FileUploadProgress
     */
    public void onUploadChanged(final FileUploadProgress fileUploadProgress) {
        softwareModuleGrid.onUploadChanged(fileUploadProgress);
    }

    /**
     * Show software module grid header
     */
    public void showSmTypeHeaderIcon() {
        softwareModuleGridHeader.showFilterIcon();
    }

    /**
     * Hide software module grid header
     */
    public void hideSmTypeHeaderIcon() {
        softwareModuleGridHeader.hideFilterIcon();
    }

    /**
     * Maximize the software module grid
     */
    public void maximize() {
        softwareModuleGrid.createMaximizedContent();
        hideDetailsLayout();
    }

    /**
     * Minimize the software module grid
     */
    public void minimize() {
        softwareModuleGrid.createMinimizedContent();
        showDetailsLayout();
    }

    /**
     * Is called when view is shown to the user
     */
    public void restoreState() {
        softwareModuleGridHeader.restoreState();
        softwareModuleGrid.restoreState();
    }

    /**
     * Unsubscribe all the events listeners
     */
    public void unsubscribeListener() {
        smFilterListener.unsubscribe();
        masterSmChangedListener.unsubscribe();
        selectSmListener.unsubscribe();
        smModifiedListener.unsubscribe();
        fileUploadChangedListener.unsubscribe();
    }
}
