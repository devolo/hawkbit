/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.distributions.smtable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.hawkbit.repository.ArtifactManagement;
import org.eclipse.hawkbit.repository.EntityFactory;
import org.eclipse.hawkbit.repository.SoftwareModuleManagement;
import org.eclipse.hawkbit.repository.SoftwareModuleTypeManagement;
import org.eclipse.hawkbit.ui.SpPermissionChecker;
import org.eclipse.hawkbit.ui.artifacts.smtable.SmMetaDataWindowBuilder;
import org.eclipse.hawkbit.ui.artifacts.smtable.SmWindowBuilder;
import org.eclipse.hawkbit.ui.artifacts.smtable.SoftwareModuleGrid;
import org.eclipse.hawkbit.ui.artifacts.smtable.SoftwareModuleGridHeader;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyDistributionSet;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxySoftwareModule;
import org.eclipse.hawkbit.ui.common.detailslayout.SoftwareModuleDetails;
import org.eclipse.hawkbit.ui.common.detailslayout.SoftwareModuleDetailsHeader;
import org.eclipse.hawkbit.ui.common.event.EventLayout;
import org.eclipse.hawkbit.ui.common.event.EventLayoutViewAware;
import org.eclipse.hawkbit.ui.common.event.EventView;
import org.eclipse.hawkbit.ui.common.event.EventViewAware;
import org.eclipse.hawkbit.ui.common.layout.AbstractGridComponentLayout;
import org.eclipse.hawkbit.ui.common.layout.MasterEntityAwareComponent;
import org.eclipse.hawkbit.ui.common.layout.listener.EntityModifiedListener;
import org.eclipse.hawkbit.ui.common.layout.listener.EntityModifiedListener.EntityModifiedAwareSupport;
import org.eclipse.hawkbit.ui.common.layout.listener.FilterChangedListener;
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
 * Implementation of software module Layout on the Distribution View
 */
public class SwModuleGridLayout extends AbstractGridComponentLayout {
    private static final long serialVersionUID = 1L;

    private final SoftwareModuleGridHeader swModuleGridHeader;
    private final SoftwareModuleGrid swModuleGrid;
    private final SoftwareModuleDetailsHeader softwareModuleDetailsHeader;
    private final SoftwareModuleDetails swModuleDetails;

    private final transient FilterChangedListener<ProxySoftwareModule> smFilterListener;
    private final transient SelectionChangedListener<ProxyDistributionSet> masterDsChangedListener;
    private final transient SelectionChangedListener<ProxySoftwareModule> masterSmChangedListener;
    private final transient SelectGridEntityListener<ProxySoftwareModule> selectSmListener;
    private final transient EntityModifiedListener<ProxySoftwareModule> smModifiedListener;

    /**
     * Constructor for SwModuleGridLayout
     *
     * @param i18n
     *            VaadinMessageSource
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
     * @param permChecker
     *            SpPermissionChecker
     * @param artifactManagement
     *            ArtifactManagement
     * @param smTypeFilterLayoutUiState
     *            TypeFilterLayoutUiState
     * @param swModuleGridLayoutUiState
     *            GridLayoutUiState
     */
    public SwModuleGridLayout(final VaadinMessageSource i18n, final UINotification uiNotification,
            final UIEventBus eventBus, final SoftwareModuleManagement softwareModuleManagement,
            final SoftwareModuleTypeManagement softwareModuleTypeManagement, final EntityFactory entityFactory,
            final SpPermissionChecker permChecker, final ArtifactManagement artifactManagement,
            final TypeFilterLayoutUiState smTypeFilterLayoutUiState,
            final GridLayoutUiState swModuleGridLayoutUiState) {
        super();

        final SmWindowBuilder smWindowBuilder = new SmWindowBuilder(i18n, entityFactory, eventBus, uiNotification,
                softwareModuleManagement, softwareModuleTypeManagement, EventView.DISTRIBUTIONS);
        final SmMetaDataWindowBuilder smMetaDataWindowBuilder = new SmMetaDataWindowBuilder(i18n, entityFactory,
                eventBus, uiNotification, permChecker, softwareModuleManagement);

        this.swModuleGridHeader = new SoftwareModuleGridHeader(i18n, permChecker, eventBus, smTypeFilterLayoutUiState,
                swModuleGridLayoutUiState, smWindowBuilder, EventView.DISTRIBUTIONS);
        this.swModuleGridHeader.buildHeader();
        this.swModuleGrid = new SoftwareModuleGrid(eventBus, i18n, permChecker, uiNotification,
                smTypeFilterLayoutUiState, swModuleGridLayoutUiState, softwareModuleManagement,
                EventView.DISTRIBUTIONS);
        this.swModuleGrid.addDragAndDropSupport();
        this.swModuleGrid.addMasterSupport();
        this.swModuleGrid.init();

        this.softwareModuleDetailsHeader = new SoftwareModuleDetailsHeader(i18n, permChecker, eventBus, uiNotification,
                smWindowBuilder, smMetaDataWindowBuilder);
        this.softwareModuleDetailsHeader.addArtifactDetailsHeaderSupport(artifactManagement);
        this.softwareModuleDetailsHeader.buildHeader();
        this.swModuleDetails = new SoftwareModuleDetails(i18n, eventBus, softwareModuleManagement,
                softwareModuleTypeManagement, smMetaDataWindowBuilder);
        this.swModuleDetails.buildDetails();

        this.smFilterListener = new FilterChangedListener<>(eventBus, ProxySoftwareModule.class,
                new EventViewAware(EventView.DISTRIBUTIONS), swModuleGrid.getFilterSupport());
        this.masterDsChangedListener = new SelectionChangedListener<>(eventBus,
                new EventLayoutViewAware(EventLayout.DS_LIST, EventView.DISTRIBUTIONS), getMasterDsAwareComponents());
        this.masterSmChangedListener = new SelectionChangedListener<>(eventBus,
                new EventLayoutViewAware(EventLayout.SM_LIST, EventView.DISTRIBUTIONS), getMasterSmAwareComponents());
        this.selectSmListener = new SelectGridEntityListener<>(eventBus,
                new EventLayoutViewAware(EventLayout.SM_LIST, EventView.DISTRIBUTIONS),
                swModuleGrid.getSelectionSupport());
        this.smModifiedListener = new EntityModifiedListener.Builder<>(eventBus, ProxySoftwareModule.class)
                .entityModifiedAwareSupports(getSmModifiedAwareSupports()).build();

        buildLayout(swModuleGridHeader, swModuleGrid, softwareModuleDetailsHeader, swModuleDetails);
    }

    private List<MasterEntityAwareComponent<ProxyDistributionSet>> getMasterDsAwareComponents() {
        return Collections.singletonList(swModuleGrid.getMasterEntitySupport());
    }

    private List<MasterEntityAwareComponent<ProxySoftwareModule>> getMasterSmAwareComponents() {
        return Arrays.asList(softwareModuleDetailsHeader, swModuleDetails);
    }

    private List<EntityModifiedAwareSupport> getSmModifiedAwareSupports() {
        return Arrays.asList(EntityModifiedGridRefreshAwareSupport.of(swModuleGrid::refreshAll),
                EntityModifiedSelectionAwareSupport.of(swModuleGrid.getSelectionSupport(),
                        swModuleGrid::mapIdToProxyEntity));
    }

    /**
     * Show software type module header icon
     */
    public void showSmTypeHeaderIcon() {
        swModuleGridHeader.showFilterIcon();
    }

    /**
     * Hide software type module header icon
     */
    public void hideSmTypeHeaderIcon() {
        swModuleGridHeader.hideFilterIcon();
    }

    /**
     * Maximize the software module grid
     */
    public void maximize() {
        swModuleGrid.createMaximizedContent();
        hideDetailsLayout();
    }

    /**
     * Minimize the software module grid
     */
    public void minimize() {
        swModuleGrid.createMinimizedContent();
        showDetailsLayout();
    }

    /**
     * Restore the software module grid and header
     */
    public void restoreState() {
        swModuleGridHeader.restoreState();
        swModuleGrid.restoreState();
    }

    /**
     * Unsubscribe the event listener
     */
    public void unsubscribeListener() {
        smFilterListener.unsubscribe();
        masterDsChangedListener.unsubscribe();
        masterSmChangedListener.unsubscribe();
        selectSmListener.unsubscribe();
        smModifiedListener.unsubscribe();
    }
}
