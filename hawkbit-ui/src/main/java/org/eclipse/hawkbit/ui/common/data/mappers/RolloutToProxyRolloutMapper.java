/**
 * Copyright (c) 2020 Bosch.IO GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.ui.common.data.mappers;

import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.Rollout;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyDistributionSetInfo;
import org.eclipse.hawkbit.ui.common.data.proxies.ProxyRollout;

/**
 * Maps {@link Rollout} entities, fetched from backend, to the
 * {@link ProxyRollout} entities.
 */
public class RolloutToProxyRolloutMapper extends AbstractNamedEntityToProxyNamedEntityMapper<ProxyRollout, Rollout> {

    @Override
    public ProxyRollout map(final Rollout rollout) {
        final ProxyRollout proxyRollout = new ProxyRollout();

        mapNamedEntityAttributes(rollout, proxyRollout);

        final DistributionSet ds = rollout.getDistributionSet();
        proxyRollout.setDsInfo(new ProxyDistributionSetInfo(ds.getId(), ds.getName(), ds.getVersion(), ds.isValid()));
        proxyRollout
                .setNumberOfGroups(rollout.getRolloutGroupsCreated() > 0 ? rollout.getRolloutGroupsCreated() : null);
        proxyRollout.setForcedTime(rollout.getForcedTime() > 0 ? rollout.getForcedTime() : null);
        proxyRollout.setStatus(rollout.getStatus());
        proxyRollout.setStatusTotalCountMap(rollout.getTotalTargetCountStatus().getStatusTotalCountMap());
        proxyRollout.setTotalTargets(rollout.getTotalTargets());
        proxyRollout.setApprovalDecidedBy(rollout.getApprovalDecidedBy());
        proxyRollout.setApprovalRemark(rollout.getApprovalRemark());
        proxyRollout.setActionType(rollout.getActionType());
        proxyRollout.setTargetFilterQuery(rollout.getTargetFilterQuery());
        proxyRollout.setStartAt(rollout.getStartAt());

        return proxyRollout;
    }
}
