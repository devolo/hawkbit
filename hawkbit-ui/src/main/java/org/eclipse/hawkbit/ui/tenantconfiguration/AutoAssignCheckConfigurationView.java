package org.eclipse.hawkbit.ui.tenantconfiguration;

import com.vaadin.data.Property;
import com.vaadin.ui.*;
import org.eclipse.hawkbit.repository.TenantConfigurationManagement;
import org.eclipse.hawkbit.ui.components.SPUIComponentProvider;
import org.eclipse.hawkbit.ui.tenantconfiguration.autoassign.AutoAssignCheckConfigurationItem;
import org.eclipse.hawkbit.ui.utils.UIComponentIdProvider;
import org.eclipse.hawkbit.ui.utils.VaadinMessageSource;

public class AutoAssignCheckConfigurationView extends BaseConfigurationView
        implements Property.ValueChangeListener, ConfigurationItem.ConfigurationItemChangeListener {

    private static final long serialVersionUID = 1L;

    private final AutoAssignCheckConfigurationItem autoAssignCheckConfigurationItem;
    private final VaadinMessageSource i18n;
    private CheckBox autoAssignCheckbox;

    AutoAssignCheckConfigurationView(final VaadinMessageSource i18n,
            final TenantConfigurationManagement tenantConfigurationManagement){
        this.i18n = i18n;
        this.autoAssignCheckConfigurationItem = new AutoAssignCheckConfigurationItem(tenantConfigurationManagement, i18n);
        this.init();
    }

    private void init(){

        final Panel rootPanel = new Panel();
        rootPanel.setSizeFull();

        rootPanel.addStyleName("config-panel");

        final VerticalLayout vLayout = new VerticalLayout();
        vLayout.setMargin(true);
        vLayout.setSizeFull();

        final Label header = new Label(i18n.getMessage("configuration.autoassign.check.title"));
        header.addStyleName("config-panel-header");
        vLayout.addComponent(header);

        final GridLayout gridLayout = new GridLayout(3, 1);
        gridLayout.setSpacing(true);
        gridLayout.setImmediate(true);
        gridLayout.setColumnExpandRatio(1, 1.0F);
        gridLayout.setSizeFull();

        autoAssignCheckbox = SPUIComponentProvider.getCheckBox("", "", null, false, "");
        autoAssignCheckbox.setId(UIComponentIdProvider.TRIGGER_AUTOASSIGN_CHECK_BY_TARGET);
        autoAssignCheckbox.setValue(autoAssignCheckConfigurationItem.isConfigEnabled());
        autoAssignCheckbox.addValueChangeListener(this);
        autoAssignCheckConfigurationItem.addChangeListener(this);
        gridLayout.addComponent(autoAssignCheckbox, 0, 0);
        gridLayout.addComponent(autoAssignCheckConfigurationItem, 1, 0);

        vLayout.addComponent(gridLayout);
        rootPanel.setContent(vLayout);
        setCompositionRoot(rootPanel);
    }

    @Override
    public void save() {
        this.autoAssignCheckConfigurationItem.save();
    }

    @Override
    public void undo() {
        this.autoAssignCheckConfigurationItem.undo();
        this.autoAssignCheckbox.setValue(autoAssignCheckConfigurationItem.isConfigEnabled());
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        if (autoAssignCheckbox.equals(event.getProperty())) {
            if (autoAssignCheckbox.getValue()) {
                autoAssignCheckConfigurationItem.configEnable();
            } else {
                autoAssignCheckConfigurationItem.configDisable();
            }
            notifyConfigurationChanged();
        }
    }

    @Override
    public void configurationHasChanged() {
        notifyConfigurationChanged();
    }
}
