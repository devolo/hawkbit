/**
 * Copyright (c) 2020 devolo AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.tenantconfiguration;

import com.vaadin.data.Binder;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import org.eclipse.hawkbit.ui.UiProperties;
import org.eclipse.hawkbit.ui.common.builder.FormComponentBuilder;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxySystemConfigWindow;
import org.eclipse.hawkbit.ui.tenantconfiguration.search.TargetSearchConfigurationItem;
import org.eclipse.hawkbit.ui.utils.UIComponentIdProvider;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;


/**
 * Provides configuration for target search option
 * to include/exclude target attributes for the search.
 */
public class TargetSearchConfigurationView extends CustomComponent {

    private static final long serialVersionUID = 1L;

    private final VaadinMessageSource i18n;
    private final UiProperties uiProperties;
    private final Binder<ProxySystemConfigWindow> binder;
    private final TargetSearchConfigurationItem targetSearchConfigurationItem;

    TargetSearchConfigurationView(final VaadinMessageSource i18n, final UiProperties uiProperties,
            final Binder<ProxySystemConfigWindow> binder) {
        this.i18n = i18n;
        this.targetSearchConfigurationItem = new TargetSearchConfigurationItem(i18n);
        this.uiProperties = uiProperties;
        this.binder = binder;
        this.init();
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

        CheckBox attributeSearchCheckbox = FormComponentBuilder.getCheckBox(
                UIComponentIdProvider.TARGET_SEARCH_ATTRIBUTES, binder,
                ProxySystemConfigWindow::isAttributeSearchEnabled, ProxySystemConfigWindow::setAttributeSearch);

        gridLayout.addComponent(attributeSearchCheckbox, 0, 0);
        gridLayout.addComponent(targetSearchConfigurationItem, 1, 0);

        vLayout.addComponent(gridLayout);
        rootPanel.setContent(vLayout);
        setCompositionRoot(rootPanel);
    }
}
