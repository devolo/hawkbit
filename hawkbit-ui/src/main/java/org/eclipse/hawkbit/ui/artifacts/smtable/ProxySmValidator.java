/**
 * Copyright (c) 2020 Bosch.IO GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.ui.artifacts.smtable;

import java.util.function.BooleanSupplier;

import org.eclipse.hawkbit.ui.common.CommonUiDependencies;
import org.eclipse.hawkbit.ui.common.EntityValidator;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxySoftwareModule;
import org.springframework.util.StringUtils;

/**
 * Validator used in SoftwareModule window controllers to validate
 * {@link ProxySoftwareModule}.
 */
public class ProxySmValidator extends EntityValidator {

    /**
     * Constructor
     *
     * @param uiDependencies
     *            {@link CommonUiDependencies}
     */
    public ProxySmValidator(final CommonUiDependencies uiDependencies) {
        super(uiDependencies);
    }

    boolean isEntityValid(final ProxySoftwareModule entity, final BooleanSupplier duplicateCheck) {
        if (!StringUtils.hasText(entity.getName()) || !StringUtils.hasText(entity.getVersion())
                || entity.getTypeInfo() == null) {
            displayValidationError("message.error.missing.nameorversionortype");
            return false;
        }

        if (duplicateCheck.getAsBoolean()) {
            displayValidationError("message.duplicate.softwaremodule", entity.getName(), entity.getVersion());
            return false;
        }

        return true;
    }
}
