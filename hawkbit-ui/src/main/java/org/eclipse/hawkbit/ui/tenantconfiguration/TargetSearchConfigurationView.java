/**
 * Copyright (c) 2020 devolo GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.tenantconfiguration;

import org.eclipse.hawkbit.repository.TenantConfigurationManagement;
import org.eclipse.hawkbit.tenancy.configuration.TenantConfigurationProperties.TenantConfigurationKey;
import org.eclipse.hawkbit.ui.UiProperties;
import org.eclipse.hawkbit.ui.common.builder.FormComponentBuilder;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxySystemConfigWindow;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxySystemConfigTargetSearch;
import org.eclipse.hawkbit.ui.components.SPUIComponentProvider;
import org.eclipse.hawkbit.ui.tenantconfiguration.search.TargetSearchConfigurationItem;
import org.eclipse.hawkbit.ui.utils.UIComponentIdProvider;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;


/**
 * Provides configuration for target search option
 * to include/exclude target attributes for the search.
 */
public class TargetSearchConfigurationView extends BaseConfigurationView<ProxySystemConfigTargetSearch> {

    private static final long serialVersionUID = 1L;

    private final VaadinMessageSource i18n;
    private final UiProperties uiProperties;
    private final TargetSearchConfigurationItem targetSearchConfigurationItem;

    TargetSearchConfigurationView(final VaadinMessageSource i18n, final UiProperties uiProperties,
                                  final TenantConfigurationManagement tenantConfigurationManagement) {
        super(tenantConfigurationManagement);

        this.i18n = i18n;
        this.targetSearchConfigurationItem = new TargetSearchConfigurationItem(i18n);
        this.uiProperties = uiProperties;
        this.init();
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        init();
    }

    private void init() {
        final Panel rootPanel = new Panel();
        rootPanel.setSizeFull();
        rootPanel.addStyleName("config-panel");

        final VerticalLayout vLayout = new VerticalLayout();
        vLayout.setSpacing(false);
        vLayout.setMargin(true);
        vLayout.setSizeFull();

        final Label header = new Label(i18n.getMessage("configuration.targetsearch.title"));
        header.addStyleName("config-panel-header");
        vLayout.addComponent(header);

        final GridLayout gridLayout = new GridLayout(3, 1);
        gridLayout.setSpacing(true);
        gridLayout.setColumnExpandRatio(1, 1.0F);
        gridLayout.setSizeFull();

        CheckBox attributeSearchCheckbox = FormComponentBuilder.createCheckBox(
                UIComponentIdProvider.TARGET_SEARCH_ATTRIBUTES, getBinder(),
                ProxySystemConfigWindow::isAttributeSearchEnabled, ProxySystemConfigWindow::setAttributeSearch);

        gridLayout.addComponent(attributeSearchCheckbox, 0, 0);
        gridLayout.addComponent(targetSearchConfigurationItem, 1, 0);

        vLayout.addComponent(gridLayout);
        rootPanel.setContent(vLayout);
        setCompositionRoot(rootPanel);
    }

    @Override
    protected ProxySystemConfigTargetSearch populateSystemConfig() {
        ProxySystemConfigTargetSearch configBean = new ProxySystemConfigTargetSearch();
        configBean.setAttributeSearch(readConfigOption(TenantConfigurationKey.TARGET_SEARCH_ATTRIBUTES_ENABLED));
        return configBean;
    }

    @Override
    public void save() {
        writeConfigOption(TenantConfigurationKey.TARGET_SEARCH_ATTRIBUTES_ENABLED, getBinderBean().isAttributeSearchEnabled());
    }


}
