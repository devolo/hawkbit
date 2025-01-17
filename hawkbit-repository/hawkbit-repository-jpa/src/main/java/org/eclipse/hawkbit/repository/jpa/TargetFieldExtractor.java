/**
 * Copyright (c) 2020 devolo GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa;

import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.Target;
import org.eclipse.hawkbit.repository.model.TargetMetadata;
import org.eclipse.hawkbit.repository.model.TargetTag;
import org.eclipse.hawkbit.repository.model.TargetUpdateStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.eclipse.hawkbit.repository.TargetFields.*;

@Service
public class TargetFieldExtractor {
    private String controllerId;
    private String name;
    private String description;
    private String updateStatus;
    private String address;
    private String lastQuery;
    private List<TargetTag> targetTagList;
    private List<TargetMetadata> metadata;
    private DistributionSet assignedDs;
    private DistributionSet installedDs;
    private String createdAt;
    private String lastModifiedAt;
    private String targetType;

    private final static String EMPTY_STRING = "";

    private TargetFieldData fieldData;

    public TargetFieldData extractData(Target target, final Map<String, String> controllerAttributes, final String targetType){

        fieldData = new TargetFieldData();

        fetchTargetFieldValues(target);

        fieldData.add(ID, controllerId);
        fieldData.add(CONTROLLERID, controllerId);
        fieldData.add(NAME, name);
        fieldData.add(DESCRIPTION, description);
        fieldData.add(CREATEDAT, createdAt);
        fieldData.add(LASTMODIFIEDAT, lastModifiedAt);
        fieldData.add(UPDATESTATUS, updateStatus);
        fieldData.add(IPADDRESS, address);
        fieldData.add(LASTCONTROLLERREQUESTAT, lastQuery);
        fieldData.add(TARGETTYPE, NAME.getFieldName(), targetType);

        addMetadata();
        addAttributes(controllerAttributes);
        addAssignedDsData();
        addInstalledDsData();
        addTagData();

        return fieldData;
    }

    private void fetchTargetFieldValues(Target target){
        controllerId = target.getControllerId() == null ? EMPTY_STRING : target.getControllerId();
        name = target.getName() == null ? EMPTY_STRING : target.getName();
        description = target.getDescription() == null ? EMPTY_STRING : target.getDescription();
        updateStatus = target.getUpdateStatus() == null ? TargetUpdateStatus.UNKNOWN.name(): target.getUpdateStatus().name();
        address = target.getAddress() == null ? EMPTY_STRING : target.getAddress().toString();
        lastQuery = (target.getLastTargetQuery() == null) ? EMPTY_STRING : target.getLastTargetQuery().toString();
        targetTagList = new ArrayList<>(target.getTags());
        metadata = target.getMetadata();
        assignedDs = target.getAssignedDistributionSet();
        installedDs = target.getInstalledDistributionSet();
        createdAt = Long.toString(target.getCreatedAt());
        lastModifiedAt = Long.toString(target.getLastModifiedAt());
    }

    private void addTagData(){
        if(targetTagList.isEmpty())
            return;

        targetTagList.forEach(tag -> fieldData.add(TAG, tag.getName()));
    }

    private void addAttributes(final Map<String, String> attributes){
        attributes.forEach((key, value) -> fieldData.add(ATTRIBUTE, key, value));
    }

    private void addMetadata(){
        metadata.forEach(data -> fieldData.add(METADATA, data.getKey(), data.getValue()));
    }

    private void addAssignedDsData(){
        if(assignedDs == null)
            return;

        fieldData.add(ASSIGNEDDS, "name", assignedDs.getName());
        fieldData.add(ASSIGNEDDS, "version", assignedDs.getVersion());
    }

    private void addInstalledDsData(){
        if(installedDs == null)
            return;

        fieldData.add(INSTALLEDDS, "name", installedDs.getName());
        fieldData.add(INSTALLEDDS, "version", installedDs.getVersion());
    }
}
