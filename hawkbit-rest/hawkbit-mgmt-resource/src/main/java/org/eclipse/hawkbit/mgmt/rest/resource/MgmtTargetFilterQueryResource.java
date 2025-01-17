/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.hawkbit.mgmt.rest.resource;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.distributionset.MgmtDistributionSet;
import org.eclipse.hawkbit.mgmt.json.model.targetfilter.MgmtDistributionSetAutoAssignment;
import org.eclipse.hawkbit.mgmt.json.model.targetfilter.MgmtTargetFilterQuery;
import org.eclipse.hawkbit.mgmt.json.model.targetfilter.MgmtTargetFilterQueryRequestBody;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtRepresentationMode;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtRestConstants;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtTargetFilterQueryRestApi;
import org.eclipse.hawkbit.repository.EntityFactory;
import org.eclipse.hawkbit.repository.OffsetBasedPageRequest;
import org.eclipse.hawkbit.repository.TargetFilterQueryManagement;
import org.eclipse.hawkbit.repository.TargetManagement;
import org.eclipse.hawkbit.repository.TenantConfigurationManagement;
import org.eclipse.hawkbit.repository.builder.AutoAssignDistributionSetUpdate;
import org.eclipse.hawkbit.repository.exception.EntityNotFoundException;
import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.Target;
import org.eclipse.hawkbit.repository.model.TargetFilterQuery;
import org.eclipse.hawkbit.security.SystemSecurityContext;
import org.eclipse.hawkbit.utils.TenantConfigHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Resource handling target CRUD operations.
 */
@RestController
public class MgmtTargetFilterQueryResource implements MgmtTargetFilterQueryRestApi {
    private static final Logger LOG = LoggerFactory.getLogger(MgmtTargetFilterQueryResource.class);

    private final TargetFilterQueryManagement filterManagement;
    private final TargetManagement targetManagement;

    private final EntityFactory entityFactory;
    
    private final TenantConfigHelper tenantConfigHelper;

    MgmtTargetFilterQueryResource(final TargetFilterQueryManagement filterManagement,
            final EntityFactory entityFactory, final TargetManagement targetManagement,
            final SystemSecurityContext systemSecurityContext,
            final TenantConfigurationManagement tenantConfigurationManagement) {
        this.filterManagement = filterManagement;
        this.entityFactory = entityFactory;
        this.targetManagement = targetManagement;
        this.tenantConfigHelper = TenantConfigHelper.usingContext(systemSecurityContext, tenantConfigurationManagement);
    }

    @Override
    public ResponseEntity<MgmtTargetFilterQuery> getFilter(@PathVariable("filterId") final Long filterId) {
        final TargetFilterQuery findTarget = findFilterWithExceptionIfNotFound(filterId);
        // to single response include poll status
        final MgmtTargetFilterQuery response = MgmtTargetFilterQueryMapper.toResponse(findTarget,
                tenantConfigHelper.isConfirmationFlowEnabled(), true);
        MgmtTargetFilterQueryMapper.addLinks(response);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PagedList<MgmtTargetFilterQuery>> getFilters(
            @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_PAGING_OFFSET, defaultValue = MgmtRestConstants.REQUEST_PARAMETER_PAGING_DEFAULT_OFFSET) final int pagingOffsetParam,
            @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_PAGING_LIMIT, defaultValue = MgmtRestConstants.REQUEST_PARAMETER_PAGING_DEFAULT_LIMIT) final int pagingLimitParam,
            @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_SORTING, required = false) final String sortParam,
            @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_SEARCH, required = false) final String rsqlParam,
            @RequestParam(value = MgmtRestConstants.REQUEST_PARAMETER_REPRESENTATION_MODE, defaultValue = MgmtRestConstants.REQUEST_PARAMETER_REPRESENTATION_MODE_DEFAULT) String representationModeParam) {


        final int sanitizedOffsetParam = PagingUtility.sanitizeOffsetParam(pagingOffsetParam);
        final int sanitizedLimitParam = PagingUtility.sanitizePageLimitParam(pagingLimitParam);
        final Sort sorting = PagingUtility.sanitizeTargetFilterQuerySortParam(sortParam);

        final Pageable pageable = new OffsetBasedPageRequest(sanitizedOffsetParam, sanitizedLimitParam, sorting);
        final Slice<TargetFilterQuery> findTargetFiltersAll;
        final Long countTargetsAll;
        if (rsqlParam != null) {
            final Page<TargetFilterQuery> findFilterPage = filterManagement.findByRsql(pageable, rsqlParam);
            countTargetsAll = findFilterPage.getTotalElements();
            findTargetFiltersAll = findFilterPage;
        } else {
            findTargetFiltersAll = filterManagement.findAll(pageable);
            countTargetsAll = filterManagement.count();
        }

        final boolean isRepresentationFull = parseRepresentationMode(representationModeParam) == MgmtRepresentationMode.FULL;

        final List<MgmtTargetFilterQuery> rest = MgmtTargetFilterQueryMapper
                .toResponse(findTargetFiltersAll.getContent(), tenantConfigHelper.isConfirmationFlowEnabled(), isRepresentationFull);
        return ResponseEntity.ok(new PagedList<>(rest, countTargetsAll));
    }

    @Override
    public ResponseEntity<MgmtTargetFilterQuery> createFilter(
            @RequestBody final MgmtTargetFilterQueryRequestBody filter) {
        final TargetFilterQuery createdTarget = filterManagement
                .create(MgmtTargetFilterQueryMapper.fromRequest(entityFactory, filter));

        final MgmtTargetFilterQuery response = MgmtTargetFilterQueryMapper.toResponse(createdTarget,
                tenantConfigHelper.isConfirmationFlowEnabled(), false);
        MgmtTargetFilterQueryMapper.addLinks(response);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<MgmtTargetFilterQuery> updateFilter(@PathVariable("filterId") final Long filterId,
            @RequestBody final MgmtTargetFilterQueryRequestBody targetFilterRest) {
        LOG.debug("updating target filter query {}", filterId);

        final TargetFilterQuery updateFilter = filterManagement
                .update(entityFactory.targetFilterQuery().update(filterId).name(targetFilterRest.getName())
                        .query(targetFilterRest.getQuery()));

        final MgmtTargetFilterQuery response = MgmtTargetFilterQueryMapper.toResponse(updateFilter,
                tenantConfigHelper.isConfirmationFlowEnabled(), false);
        MgmtTargetFilterQueryMapper.addLinks(response);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteFilter(@PathVariable("filterId") final Long filterId) {
        filterManagement.delete(filterId);
        LOG.debug("{} target filter query deleted, return status {}", filterId, HttpStatus.OK);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<MgmtTargetFilterQuery> postAssignedDistributionSet(
            @PathVariable("filterId") final Long filterId,
            @RequestBody final MgmtDistributionSetAutoAssignment autoAssignRequest) {

        final boolean confirmationRequired = autoAssignRequest.isConfirmationRequired() == null
                ? tenantConfigHelper.isConfirmationFlowEnabled()
                : autoAssignRequest.isConfirmationRequired();

        final AutoAssignDistributionSetUpdate update = MgmtTargetFilterQueryMapper
                .fromRequest(entityFactory, filterId, autoAssignRequest).confirmationRequired(confirmationRequired);

        final TargetFilterQuery updateFilter = filterManagement.updateAutoAssignDS(update);

        final MgmtTargetFilterQuery response = MgmtTargetFilterQueryMapper.toResponse(updateFilter,
                tenantConfigHelper.isConfirmationFlowEnabled(), false);
        MgmtTargetFilterQueryMapper.addLinks(response);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MgmtDistributionSet> getAssignedDistributionSet(
            @PathVariable("filterId") final Long filterId) {
        final TargetFilterQuery filter = findFilterWithExceptionIfNotFound(filterId);
        final DistributionSet autoAssignDistributionSet = filter.getAutoAssignDistributionSet();

        if (autoAssignDistributionSet == null) {
            return ResponseEntity.noContent().build();
        }

        final MgmtDistributionSet response = MgmtDistributionSetMapper.toResponse(autoAssignDistributionSet);
        MgmtDistributionSetMapper.addLinks(autoAssignDistributionSet, response);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteAssignedDistributionSet(@PathVariable("filterId") final Long filterId) {
        filterManagement.updateAutoAssignDS(entityFactory.targetFilterQuery().updateAutoAssign(filterId).ds(null));

        return ResponseEntity.noContent().build();
    }

    private TargetFilterQuery findFilterWithExceptionIfNotFound(final Long filterId) {
        return filterManagement.get(filterId)
                .orElseThrow(() -> new EntityNotFoundException(TargetFilterQuery.class, filterId));
    }

    @Override
    public ResponseEntity<List<String>> getResults(@PathVariable("filterId") final Long filterId) {
        final List<Target> findAll = this.targetManagement.findAllByTargetFilterQuery(filterId);
        return ResponseEntity.ok(findAll.stream().map(target -> target.getControllerId()).collect(Collectors.toList()));
    }

    private static MgmtRepresentationMode parseRepresentationMode(final String representationModeParam) {
        return MgmtRepresentationMode.fromValue(representationModeParam).orElseGet(() -> {
            // no need for a 400, just apply a safe fallback
            LOG.warn("Received an invalid representation mode: {}", representationModeParam);
            return MgmtRepresentationMode.COMPACT;
        });
    }

}
