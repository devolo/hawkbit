/**
 * Copyright (c) 2020 devolo GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.rsql;

import io.qameta.allure.Description;
import org.eclipse.hawkbit.repository.TargetFields;
import org.junit.jupiter.api.Test;

public class RSQLTargetFieldValidationTest {
    @Test
    @Description("Testing allowed RSQL keys based on TargetFields.class")
    public void rsqlValidTargetFields(){
        final String rsql1 = "ID == '0123' and NAME == abcd and DESCRIPTION == absd" +
                " and CREATEDAT =lt= 0123 and LASTMODIFIEDAT =gt= 0123" +
                " and CONTROLLERID == 0123 and UPDATESTATUS == PENDING" +
                " and IPADDRESS == 0123 and LASTCONTROLLERREQUESTAT == 0123" +
                " and tag == beta";

        RSQLUtility.validateRsqlFor(rsql1, TargetFields.class);

        final String rsql2 = "ASSIGNEDDS.name == abcd and ASSIGNEDDS.version == 0123" +
                " and INSTALLEDDS.name == abcd and INSTALLEDDS.version == 0123";
        RSQLUtility.validateRsqlFor(rsql2, TargetFields.class);

        final String rsql3 = "ATTRIBUTE.subkey1 == test and ATTRIBUTE.subkey2 == test" +
                " and METADATA.metakey1 == abcd and METADATA.metavalue2 == asdfg";
        RSQLUtility.validateRsqlFor(rsql3, TargetFields.class);

        final String rsql4 = "CREATEDAT =lt= ${NOW_TS} and LASTMODIFIEDAT =ge= ${OVERDUE_TS}";
        RSQLUtility.validateRsqlFor(rsql4, TargetFields.class);
    }
}
