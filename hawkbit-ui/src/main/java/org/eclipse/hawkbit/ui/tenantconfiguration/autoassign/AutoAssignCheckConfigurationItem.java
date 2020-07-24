package org.eclipse.hawkbit.ui.tenantconfiguration.autoassign;

import org.eclipse.hawkbit.repository.TenantConfigurationManagement;
import org.eclipse.hawkbit.tenancy.configuration.TenantConfigurationProperties.TenantConfigurationKey;
import org.eclipse.hawkbit.ui.tenantconfiguration.generic.AbstractBooleanTenantConfigurationItem;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;

public class AutoAssignCheckConfigurationItem extends AbstractBooleanTenantConfigurationItem {

    private static final long serialVersionUID = 1L;

    private boolean configurationEnabled;
    private boolean configurationEnabledChange;

    public AutoAssignCheckConfigurationItem(TenantConfigurationManagement tenantConfigurationManagement, VaadinMessageSource i18n){
        super(TenantConfigurationKey.TRIGGER_AUTO_ASSIGN_CHECK_BY_TARGET, tenantConfigurationManagement, i18n);

        super.init("configuration.autoassign.check.label");
        this.configurationEnabled = isConfigEnabled();
    }

    @Override
    public void configEnable() {
        if (!configurationEnabled) {
            configurationEnabledChange = true;
        }
        configurationEnabled = true;
    }

    @Override
    public void configDisable() {
        if (configurationEnabled) {
            configurationEnabledChange = true;
        }
        configurationEnabled = false;
    }

    @Override
    public void save() {
        if (!configurationEnabledChange) {
            return;
        }
        getTenantConfigurationManagement().addOrUpdateConfiguration(getConfigurationKey(), configurationEnabled);
    }

    @Override
    public void undo() {
        configurationEnabledChange = false;
        configurationEnabled = getTenantConfigurationManagement()
                .getConfigurationValue(getConfigurationKey(), Boolean.class).getValue();
    }
}
