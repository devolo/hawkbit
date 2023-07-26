/**
 * Copyright (c) 2020 devolo GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.ui.tenantconfiguration.search;

import com.vaadin.ui.HorizontalLayout;
import org.eclipse.hawkbit.ui.components.SPUIComponentProvider;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;

public class TargetSearchConfigurationItem extends HorizontalLayout {

    private static final long serialVersionUID = 1L;

    public TargetSearchConfigurationItem(VaadinMessageSource i18n) {
        setSpacing(false);
        setMargin(false);
        addComponent(SPUIComponentProvider.generateLabel(i18n, "configuration.targetsearch.attributes.label"));
    }
}
