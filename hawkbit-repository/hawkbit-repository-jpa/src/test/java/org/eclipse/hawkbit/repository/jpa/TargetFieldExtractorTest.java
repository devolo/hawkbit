/**
 * Copyright (c) 2020 devolo AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa;

import org.eclipse.hawkbit.repository.UpdateMode;
import org.eclipse.hawkbit.repository.exception.EntityNotFoundException;
import org.eclipse.hawkbit.repository.model.Action;
import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.DistributionSetAssignmentResult;
import org.eclipse.hawkbit.repository.model.Target;
import org.eclipse.hawkbit.repository.model.TargetTag;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.eclipse.hawkbit.repository.TargetFields.ASSIGNEDDS;
import static org.eclipse.hawkbit.repository.TargetFields.ATTRIBUTE;
import static org.eclipse.hawkbit.repository.TargetFields.CONTROLLERID;
import static org.eclipse.hawkbit.repository.TargetFields.CREATEDAT;
import static org.eclipse.hawkbit.repository.TargetFields.DESCRIPTION;
import static org.eclipse.hawkbit.repository.TargetFields.ID;
import static org.eclipse.hawkbit.repository.TargetFields.INSTALLEDDS;
import static org.eclipse.hawkbit.repository.TargetFields.IPADDRESS;
import static org.eclipse.hawkbit.repository.TargetFields.LASTCONTROLLERREQUESTAT;
import static org.eclipse.hawkbit.repository.TargetFields.LASTMODIFIEDAT;
import static org.eclipse.hawkbit.repository.TargetFields.METADATA;
import static org.eclipse.hawkbit.repository.TargetFields.NAME;
import static org.eclipse.hawkbit.repository.TargetFields.TAG;
import static org.eclipse.hawkbit.repository.TargetFields.UPDATESTATUS;
import static org.eclipse.hawkbit.repository.model.TargetUpdateStatus.IN_SYNC;
import static org.eclipse.hawkbit.repository.model.TargetUpdateStatus.PENDING;
import static org.eclipse.hawkbit.repository.test.util.TestdataFactory.DEFAULT_VERSION;

import static org.junit.Assert.assertTrue;

public class TargetFieldExtractorTest extends AbstractJpaIntegrationTest {

    private String targetId;
    private String targetId2;

    @Autowired
    protected TargetFieldExtractor extractorService;

    @Before
    public void setUp() {

        Target target = targetManagement.create(entityFactory.target().create()
                .controllerId("targetId123")
                .name("targetName123")
                .description("targetDescription123"));

        targetId = target.getControllerId();

        final DistributionSet assignedDs = testdataFactory.createDistributionSet("AssignedDs");
        assignDistributionSet(assignedDs.getId(), target.getControllerId(), Action.ActionType.SOFT);

        final Map<String, String> attributes = new HashMap<>();
        attributes.put("revision", "1.11");
        attributes.put("device_type", "dev_test");
        controllerManagement.updateControllerAttributes(targetId, attributes, UpdateMode.REPLACE);


        createTargetMetadata(targetId, entityFactory.generateDsMetadata("metakey_1", "metavalue_1"));
        createTargetMetadata(targetId, entityFactory.generateDsMetadata("metakey_2", "metavalue_2"));

        controllerManagement.findOrRegisterTargetIfItDoesNotExist(targetId, LOCALHOST);

        final TargetTag tag_alpha = targetTagManagement.create(entityFactory.tag().create().name("alpha"));
        final TargetTag tag_beta = targetTagManagement.create(entityFactory.tag().create().name("beta"));
        targetManagement.assignTag(Collections.singletonList(targetId), tag_alpha.getId());
        targetManagement.assignTag(Collections.singletonList(targetId), tag_beta.getId());

        Target target2 = targetManagement.create(entityFactory.target().create()
                .controllerId("targetId123_2")
                .name("targetName123_2")
                .description("targetDescription123_2"));

        targetId2 = target2.getControllerId();

        final DistributionSet installedDs = testdataFactory.createDistributionSet("InstalledDs");
        DistributionSetAssignmentResult result = assignDistributionSet(installedDs.getId(), targetId2, Action.ActionType.SOFT);

        addUpdateActionStatus(getFirstAssignedActionId(result), Action.Status.FINISHED);
    }


    private void addUpdateActionStatus(final Long actionId, final Action.Status actionStatus) {
        controllerManagement.addUpdateActionStatus(entityFactory.actionStatus().create(actionId).status(actionStatus));
    }

    @Test
    public void extractFieldsTest() {

        Target testTarget = targetManagement.getByControllerID(targetId).orElseThrow(EntityNotFoundException::new);
        TargetFieldData fieldData = extractorService.extractData(testTarget, testTarget.getControllerAttributes());

        Target testTarget2 = targetManagement.getByControllerID(targetId2).orElseThrow(EntityNotFoundException::new);
        TargetFieldData fieldData2 = extractorService.extractData(testTarget2, testTarget2.getControllerAttributes());

        assertTrue(fieldData.hasEntry(ID.name(), "targetId123"));

        assertTrue(fieldData.hasEntry(CONTROLLERID.name(), "targetId123"));
        assertTrue(fieldData.hasEntry(NAME.name(), "targetName123"));
        assertTrue(fieldData.hasEntry(DESCRIPTION.name(), "targetDescription123"));
        assertTrue(fieldData.hasEntry(UPDATESTATUS.name(), PENDING.name()));
        assertTrue(fieldData.hasEntry(ASSIGNEDDS.name() + ".name", "AssignedDs"));
        assertTrue(fieldData.hasEntry(ASSIGNEDDS.name() + ".version", DEFAULT_VERSION));

        assertTrue(fieldData2.hasEntry(INSTALLEDDS.name() + ".name", "InstalledDs"));
        assertTrue(fieldData2.hasEntry(INSTALLEDDS.name() + ".version", DEFAULT_VERSION));
        assertTrue(fieldData2.hasEntry(UPDATESTATUS.name(), IN_SYNC.name()));

        assertTrue(fieldData.hasEntry(ATTRIBUTE.name() + ".revision", "1.11"));
        assertTrue(fieldData.hasEntry(ATTRIBUTE.name() + ".device_type", "dev_test"));
        assertTrue(fieldData.hasEntry(METADATA.name() + ".metakey_1", "metavalue_1"));
        assertTrue(fieldData.hasEntry(METADATA.name() + ".metakey_2", "metavalue_2"));
        assertTrue(fieldData.hasEntry(IPADDRESS.name() , LOCALHOST.toString()));
        assertTrue(fieldData.hasEntry(TAG.name(), "alpha"));
        assertTrue(fieldData.hasEntry(TAG.name(), "beta"));
        assertTrue(fieldData.hasEntry(CREATEDAT.name(), Long.toString(testTarget.getCreatedAt())));
        assertTrue(fieldData.hasEntry(LASTMODIFIEDAT.name(),  Long.toString(testTarget.getLastModifiedAt())));
        assertTrue(fieldData.hasEntry(LASTCONTROLLERREQUESTAT.name(),  Long.toString(testTarget.getLastTargetQuery())));
    }
}