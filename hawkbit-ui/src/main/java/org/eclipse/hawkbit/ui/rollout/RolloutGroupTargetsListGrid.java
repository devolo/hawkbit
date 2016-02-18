package org.eclipse.hawkbit.ui.rollout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.eclipse.hawkbit.repository.model.Action.Status;
import org.eclipse.hawkbit.repository.model.RolloutGroup;
import org.eclipse.hawkbit.repository.model.RolloutGroup.RolloutGroupStatus;
import org.eclipse.hawkbit.ui.rollout.event.RolloutEvent;
import org.eclipse.hawkbit.ui.rollout.state.RolloutUIState;
import org.eclipse.hawkbit.ui.utils.I18N;
import org.eclipse.hawkbit.ui.utils.SPUIComponetIdProvider;
import org.eclipse.hawkbit.ui.utils.SPUIDefinitions;
import org.eclipse.hawkbit.ui.utils.SPUILabelDefinitions;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.addons.lazyquerycontainer.BeanQueryFactory;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventScope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.data.Container;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.renderers.HtmlRenderer;

@SpringComponent
@ViewScope
public class RolloutGroupTargetsListGrid extends AbstractSimpleGrid {

    private static final long serialVersionUID = -2244756637458984597L;

    @Autowired
    private I18N i18n;

    @Autowired
    private transient EventBus.SessionEventBus eventBus;

    @Autowired
    private transient RolloutUIState rolloutUIState;

    @Override
    @PostConstruct
    protected void init() {
        super.init();
        eventBus.subscribe(this);
    }

    @PreDestroy
    void destroy() {
        eventBus.unsubscribe(this);
    }

    @EventBusListenerMethod(scope = EventScope.SESSION)
    void onEvent(final RolloutEvent event) {
        if (event == RolloutEvent.SHOW_ROLLOUT_GROUP_TARGETS) {
            ((LazyQueryContainer) getContainerDataSource()).refresh();
            eventBus.publish(this, RolloutEvent.SHOW_ROLLOUT_GROUP_TARGETS_COUNT);
        }
    }

    @Override
    protected Container createContainer() {
        final BeanQueryFactory<RolloutGroupTargetsBeanQuery> rolloutgrouBeanQueryFactory = new BeanQueryFactory<>(
                RolloutGroupTargetsBeanQuery.class);
        return new LazyQueryContainer(
                new LazyQueryDefinition(true, SPUIDefinitions.PAGE_SIZE, SPUILabelDefinitions.VAR_ID),
                rolloutgrouBeanQueryFactory);
    }

    @Override
    protected void addContainerProperties() {
        final LazyQueryContainer rolloutGroupTargetGridContainer = (LazyQueryContainer) getContainerDataSource();
        rolloutGroupTargetGridContainer.addContainerProperty(SPUILabelDefinitions.VAR_NAME, String.class, "", false,
                true);
        rolloutGroupTargetGridContainer.addContainerProperty(SPUILabelDefinitions.VAR_STATUS, Status.class,
                Status.RETRIEVED, false, false);
        rolloutGroupTargetGridContainer.addContainerProperty(SPUILabelDefinitions.VAR_CREATED_BY, String.class, null,
                false, true);
        rolloutGroupTargetGridContainer.addContainerProperty(SPUILabelDefinitions.VAR_LAST_MODIFIED_BY, String.class,
                null, false, true);
        rolloutGroupTargetGridContainer.addContainerProperty(SPUILabelDefinitions.VAR_CREATED_DATE, String.class, null,
                false, true);
        rolloutGroupTargetGridContainer.addContainerProperty(SPUILabelDefinitions.VAR_LAST_MODIFIED_DATE, String.class,
                null, false, true);
        rolloutGroupTargetGridContainer.addContainerProperty(SPUILabelDefinitions.VAR_DESC, String.class, "", false,
                true);
        rolloutGroupTargetGridContainer.addContainerProperty(SPUILabelDefinitions.ASSIGNED_DISTRIBUTION_NAME_VER,
                String.class, "", false, true);
    }

    @Override
    protected void setColumnExpandRatio() {
        getColumn(SPUILabelDefinitions.VAR_NAME).setExpandRatio(1);
        getColumn(SPUILabelDefinitions.VAR_NAME).setMaximumWidth(300);

        getColumn(SPUILabelDefinitions.VAR_STATUS).setExpandRatio(0);
        getColumn(SPUILabelDefinitions.VAR_STATUS).setMinimumWidth(75);

        getColumn(SPUILabelDefinitions.VAR_CREATED_DATE).setExpandRatio(0);
        getColumn(SPUILabelDefinitions.VAR_CREATED_BY).setExpandRatio(0);
        getColumn(SPUILabelDefinitions.VAR_LAST_MODIFIED_DATE).setExpandRatio(0);
        getColumn(SPUILabelDefinitions.VAR_LAST_MODIFIED_BY).setExpandRatio(0);
        getColumn(SPUILabelDefinitions.VAR_DESC).setExpandRatio(0);
        getColumn(SPUILabelDefinitions.ASSIGNED_DISTRIBUTION_NAME_VER).setExpandRatio(0);

    }

    @Override
    protected void setColumnHeaderNames() {
        getColumn(SPUILabelDefinitions.VAR_NAME).setHeaderCaption(i18n.get("header.name"));
        getColumn(SPUILabelDefinitions.VAR_STATUS).setHeaderCaption(i18n.get("header.status"));
        getColumn(SPUILabelDefinitions.VAR_CREATED_DATE).setHeaderCaption(i18n.get("header.createdDate"));
        getColumn(SPUILabelDefinitions.VAR_CREATED_BY).setHeaderCaption(i18n.get("header.createdBy"));
        getColumn(SPUILabelDefinitions.VAR_LAST_MODIFIED_DATE).setHeaderCaption(i18n.get("header.modifiedDate"));
        getColumn(SPUILabelDefinitions.VAR_LAST_MODIFIED_BY).setHeaderCaption(i18n.get("header.modifiedBy"));
        getColumn(SPUILabelDefinitions.VAR_DESC).setHeaderCaption(i18n.get("header.description"));
        getColumn(SPUILabelDefinitions.ASSIGNED_DISTRIBUTION_NAME_VER).setHeaderCaption(i18n.get("header.assigned.ds"));

    }

    @Override
    protected String getTableId() {
        return SPUIComponetIdProvider.ROLLOUT_GROUP_TARGETS_LIST_TABLE_ID;
    }

    @Override
    protected void setColumnProperties() {
        List<Object> columnList = new ArrayList<>();
        columnList.add(SPUILabelDefinitions.VAR_NAME);
        columnList.add(SPUILabelDefinitions.VAR_CREATED_DATE);
        columnList.add(SPUILabelDefinitions.VAR_CREATED_BY);
        columnList.add(SPUILabelDefinitions.VAR_LAST_MODIFIED_DATE);
        columnList.add(SPUILabelDefinitions.VAR_LAST_MODIFIED_BY);
        columnList.add(SPUILabelDefinitions.VAR_DESC);
        columnList.add(SPUILabelDefinitions.VAR_STATUS);
        setColumnOrder(columnList.toArray());
        alignColumns();
    }

    private void alignColumns() {
        setCellStyleGenerator(new CellStyleGenerator() {
            private static final long serialVersionUID = 5573570647129792429L;

            @Override
            public String getStyle(final CellReference cellReference) {
                if (SPUILabelDefinitions.VAR_STATUS.equals(cellReference.getPropertyId())) {
                    return "centeralign";
                }
                return null;
            }
        });
    }

    @Override
    protected void addColumnRenderes() {
        addStatusCoulmn();
    }

    @Override
    protected void setHiddenColumns() {
            getColumn(SPUILabelDefinitions.ASSIGNED_DISTRIBUTION_NAME_VER).setHidden(true);
    }

    private void addStatusCoulmn() {
        getColumn(SPUILabelDefinitions.VAR_STATUS).setRenderer(new HtmlRenderer(), new Converter<String, Status>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Status convertToModel(final String value, final Class<? extends Status> targetType,
                    final Locale locale) {
                return null;
            }

            @Override
            public String convertToPresentation(final Status status, final Class<? extends String> targetType,
                    final Locale locale) {
                String result = null;
                if (status == null) {
                    //Actions are not created for targets when rollout's status is READY and when duplicate assignment is done.
                    //In these cases display a appropriate status with description
                    return getStatus(status);
                } else {
                    switch (status) {
                    case FINISHED:
                        result = "<div class=\"statusIconGreen\">" + FontAwesome.CHECK_CIRCLE.getHtml() + "</div>";
                        break;
                    case SCHEDULED:
                        result = "<div class=\"statusIconBlue\">" + FontAwesome.BULLSEYE.getHtml() + "</div>";
                        break;
                    case RUNNING:
                    case RETRIEVED:
                    case WARNING:
                    case DOWNLOAD:
                        result = "<div class=\"statusIconYellow\">" + FontAwesome.ADJUST.getHtml() + "</div>";
                        break;
                    case CANCELED:
                        result = "<div class=\"statusIconPending\">" + FontAwesome.TIMES_CIRCLE.getHtml() + "</div>";
                        break;
                    case CANCELING:
                        result = "<div class=\"statusIconGreen\">" + FontAwesome.TIMES_CIRCLE.getHtml() + "</div>";
                        break;
                    case ERROR:
                        result = "<div class=\"statusIconRed\">" + FontAwesome.EXCLAMATION_CIRCLE.getHtml() + "</div>";
                        break;
                    default:
                        break;
                    }
                    return result;
                }
            }

            @Override
            public Class<Status> getModelType() {
                return Status.class;
            }

            @Override
            public Class<String> getPresentationType() {
                return String.class;
            }
        });
    }

    private String getStatus(Status status) {
        final RolloutGroup rolloutGroup = rolloutUIState.getRolloutGroup().isPresent()
                ? rolloutUIState.getRolloutGroup().get() : null;
        if (rolloutGroup != null && rolloutGroup.getStatus() == RolloutGroupStatus.READY) {
            return "<div class=\"statusIconLightBlue\">" + FontAwesome.DOT_CIRCLE_O.getHtml() + "</div>";
        } else if (rolloutGroup != null && rolloutGroup.getStatus() == RolloutGroupStatus.FINISHED) {
            return "<div class=\"statusIconBlue\">" + FontAwesome.MINUS_CIRCLE.getHtml() + "</div>";
            // TODO set descriptionf or status icons
            // final String dsNameVersion = (String) item.getItemProperty(
            // SPUILabelDefinitions.ASSIGNED_DISTRIBUTION_NAME_VER).getValue();
            // statusLabel.setDescription(i18n
            // .get("message.dist.already.assigned", new Object[] {
            // dsNameVersion }));
        }
        return null;
    }
}
