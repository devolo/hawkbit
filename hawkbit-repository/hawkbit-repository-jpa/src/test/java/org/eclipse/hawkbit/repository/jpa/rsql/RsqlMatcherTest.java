/**
 * Copyright (c) 2020 devolo AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.jpa.rsql;

import org.eclipse.hawkbit.repository.jpa.TargetFieldData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.GregorianCalendar;

import static java.util.Calendar.DECEMBER;
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
import static org.eclipse.hawkbit.repository.jpa.rsql.RsqlMatcher.matches;
import static org.eclipse.hawkbit.repository.model.TargetUpdateStatus.PENDING;
import static org.eclipse.hawkbit.repository.model.TargetUpdateStatus.UNKNOWN;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class RsqlMatcherTest {
    final static String LOCALHOST = URI.create("http://127.0.0.1").toString();

    final static String idEqualTrue = "id == 012345";
    final static String idNotEqualTrue = "id != 54321";
    final static String idNotEqualFalse = "id != 012345";
    final static String idInTrue = "id =in= (abcd, 9999, 012345, 7456)";
    final static String idInFalse = "id =in= (abcd, 9999, 7456)";
    final static String idOutTrue = "id =out= (abcd, 9999, 8546)";
    final static String idOutFalse = "id =out= (abcd, 9999, 8546, 012345)";

    final static String idWildCardEqualTrue1 = "id == *2345";
    final static String idWildCardEqualTrue2 = "id == 0*";
    final static String idWildCardEqualTrue3 = "id == *123*";
    final static String idWildCardEqualTrue4 = "id == *1*4*";
    final static String idWildCardEqualTrue5 = "id == 01*45";
    final static String idWildCardEqualTrue6 = "id == *012345*";
    final static String idWildCardEqualTrue7= "id == *0*1*2*3*4*5*";
    final static String idWildCardEqualFalse2 = "id == 0124*5";

    final static String controllerIdEqualTrue = "controllerid == 012345";
    final static String controllerIdInTrue = "controllerid =in= (abcd, 9999, 012345, 7456)";
    final static String controllerIdInFalse = "controllerid =in= (abcd, 9999, 7456)";
    final static String controllerIdOutTrue = "controllerid =out= (abcd, 9999, 8546)";
    final static String controllerIdOutFalse = "controllerid =out= (abcd, 9999, 8546, 012345)";

    final static String nameEqualTrue1 = "name == targetName1";
    final static String nameEqualTrue2 = "name == 'targetName1'";
    final static String nameInTrue = "name =in= (abcd, targetName1, 012345, 7456)";
    final static String nameInFalse = "name =in= (abcd, 9999, 7456, asdfgh)";
    final static String nameOutTrue = "name =out= (abcd, 9999, 8546)";
    final static String nameOutFalse = "name =out= (abcd, 9999, 8546, targetName1, 012345)";

    final static String descEqualTrue1 = "description == TargetDescription1";
    final static String descEqualTrue2 = "description == \"TargetDescription1\"";
    final static String descInTrue = "description =in= (abcd, TargetDescription1, 012345, 7456)";
    final static String descInFalse = "description =in= (abcd, 9999, 7456, asdfgh)";
    final static String descOutTrue = "description =out= (abcd, 9999, 8546, ghjktrz)";
    final static String descOutFalse = "description =out= (abcd, 9999, 8546, TargetDescription1, 012345)";

    final static String ipAddressEqualTrue1 = "ipaddress == " + LOCALHOST;
    final static String ipAddressEqualTrue2 = "ipaddress == '" + LOCALHOST + "'";
    final static String ipAddressEqualFalse = "ipaddress == \"http:\\\\abcde.123485\"";
    final static String ipAddressNotEqualTrue = "ipaddress != http:\\\\192.168.0.1";
    final static String ipAddressNotEqualFalse = "ipaddress != " + LOCALHOST;
    final static String ipAddressOutTrue = "ipaddress =OUT= (abcd, 9999, 8546, ghjktrz, http:\\\\devolo.com)";
    final static String ipAddressOutFalse = "ipaddress=oUt= (abcd, 9999, 8546,"+ LOCALHOST + ",InstalledDs, 012345)";
    final static String ipAddressInTrue = "ipaddress=iN=(abcd, " + LOCALHOST + ", 8546, ghjktrz)";
    final static String ipAddressInFalse = "ipaddress =In=(abcd, 9874, 8546, ghjktrz)";

    final static String updateStatusEqualTrue1 = "updatestatus == pending";
    final static String updateStatusEqualTrue2 = "updatestatus == pENdInG";
    final static String updateStatusEqualFalse = "updatestatus == in_sync";
    final static String updateStatusInTrue = "updatestatus =IN= (pending, error, abcdefgh123)";
    final static String updateStatusInFalse = "updatestatus =iN= (in_sync, error, abcdefgh123)";
    final static String updateStatusOutTrue = "updatestatus =OUT= (finished, error, abcdefgh123)";
    final static String updateStatusOutFalse = "updatestatus =oUt= (in_sync, pending, error, abcdefgh123)";

    final static String attributeRevisionEqualTrue1 = "attribute.revision == 1.123";
    final static String attributeRevisionEqualTrue2 = "attribute.revision == \"1.123\"";
    final static String attributeRevisionEqualFalse = "attribute.revision == \"abcde\"";
    final static String attributeRevisionNotEqualTrue = "attribute.revision != 6.6.6";
    final static String attributeRevisionNotEqualFalse = "attribute.revision != 1.123";
    final static String attributeRevisionOutTrue = "attribute.revision =out= (abcd, 9999, 8546, ghjktrz)";
    final static String attributeRevisionOutFalse = "attribute.revision =out= (abcd, 9999, 8546, 1.123, 012345)";
    final static String attributeRevisionInTrue = "attribute.revision =in= (abcd, 1.123, 8546, ghjktrz)";
    final static String attributeRevisionInFalse = "attribute.revision =in= (abcd, 9874, 8546, ghjktrz)";

    final static String attributeDevTypeEqualTrue1 = "attribute.device_type == dev_test";
    final static String attributeDevTypeEqualTrue2 = "attribute.device_type == \"dev_test\"";
    final static String attributeDevTypeEqualFalse1 = "attribute.device_type == \"abcde\"";
    final static String attributeDevTypeEqualFalse2 = "attribute.keynotexistant == \"abcde\"";
    final static String attributeDevTypeNotEqualTrue = "attribute.device_type != qwertz";
    final static String attributeDevTypeNotEqualFalse1 = "attribute.device_type != dev_test";
    final static String attributeDevTypeNotEqualFalse2 = "attribute.keynotexistant != dev_test";
    final static String attributeDevTypeOutTrue = "attribute.device_type =OUT= (abcd, 9999, 8546, ghjktrz)";
    final static String attributeDevTypeOutFalse = "attribute.device_type =oUt= (abcd, 9999, 8546, dev_test, 012345)";
    final static String attributeDevTypeInTrue = "attribute.device_type =iN= (abcd, dev_test, 8546, ghjktrz)";
    final static String attributeDevTypeInFalse = "attribute.device_type =In= (abcd, 9874, 8546, ghjktrz)";

    final static String assignedDsNameEqualTrue1 = "assignedds.name == AssignedDs";
    final static String assignedDsNameEqualTrue2 = "assignedds.name == 'AssignedDs'";
    final static String assignedDsNameEqualFalse = "assignedds.name == \"abcde\"";
    final static String assignedDsNameNotEqualTrue = "assignedds.name != asdfg";
    final static String assignedDsNameNotEqualFalse = "assignedds.name != AssignedDs";
    final static String assignedDsNameOutTrue = "assignedds.name =OUT= (abcd, 9999, 8546, ghjktrz, InstalledDs)";
    final static String assignedDsNameOutFalse = "assignedds.name =oUt= (abcd, 9999, 8546, AssignedDs, 012345)";
    final static String assignedDsNameInTrue = "assignedds.name =iN= (abcd, AssignedDs, 8546, ghjktrz)";
    final static String assignedDsNameInFalse = "assignedds.name =In= (abcd, 9874, 8546, ghjktrz)";

    final static String installedDsNameEqualTrue1 = "installedds.name == InstalledDs";
    final static String installedDsNameEqualTrue2 = "installedds.name == 'InstalledDs'";
    final static String installedDsNameEqualFalse = "installedds.name == \"abcde\"";
    final static String installedDsNameNotEqualTrue = "installedds.name != asdfg";
    final static String installedDsNameNotEqualFalse = "installedds.name != InstalledDs";
    final static String installedDsNameOutTrue = "installedds.name =OUT= (abcd, 9999, 8546, ghjktrz, AssignedDs)";
    final static String installedDsNameOutFalse = "installedds.name =oUt= (abcd, 9999, 8546, InstalledDs, 012345)";
    final static String installedDsNameInTrue = "installedds.name =iN= (abcd, InstalledDs, 8546, ghjktrz)";
    final static String installedDsNameInFalse = "installedds.name =In= (abcd, 9874, 8546, ghjktrz)";

    final static String tagEqualTrue1 = "tag == alpha";
    final static String tagEqualTrue2 = "tag == beta";
    final static String tagNotEqualTrue = "tag != nightly";
    final static String tagNotEqualFalse = "tag != alpha";
    final static String tagInTrue1 = "tag =in= (asdh, 9999, 012345, 7456, alpha)";
    final static String tagInTrue2 = "tag =in= (aasdfdg, beta, 012345, 7456)";
    final static String tagInFalse = "tag =in= (abcd, 9999, asdfg, 7456)";
    final static String tagOutTrue = "tag =out= (abcd, 9999, 8546, nightly, random123)";
    final static String tagOutFalse1 = "tag =out= (abcd, alpha, 8546, 012345)";
    final static String tagOutFalse2 = "tag =out= (abcd, rtzu, beta, 012345)";

    final static String metadata1EqualTrue = "metadata.metakey1 == metavalue1";
    final static String metadataEqualFalse = "metadata.wrongkey == metavalue1";
    final static String metadataNotEqualFalse = "metadata.wrongkey != metavalue1";
    final static String metadata1EqualFalse = "metadata.metakey1 == value";
    final static String metadata1NotEqualTrue = "metadata.metakey1 != mtvl";
    final static String metadata1NotEqualFalse = "metadata.metakey1 != metavalue1";
    final static String metadata1OutTrue = "metadata.metakey1 =out= (abcd, 9999, 8546, ghjktrz)";
    final static String metadata1OutFalse = "metadata.metakey1 =out= (abcd, 9999, metavalue1, 1.123, 012345)";
    final static String metadata1InTrue = "metadata.metakey1 =in= (abcd, metavalue1, 8546, ghjktrz)";
    final static String metadata1InFalse = "metadata.metakey1 =in= (abcd, 9874, 8546, ghjktrz)";

    final static String metadata2EqualTrue = "metadata.metakey2 == metavalue2";
    final static String metadata2EqualFalse = "metadata.metakey2 == value";
    final static String metadata2NotEqualTrue = "metadata.metakey2 != mtvl";
    final static String metadata2NotEqualFalse = "metadata.metakey2 != metavalue2";
    final static String metadata2OutTrue = "metadata.metakey2 =out= (abcd, 9999, 8546, ghjktrz)";
    final static String metadata2OutFalse = "metadata.metakey2 =out= (abcd, 9999, metavalue2, 1.123, 012345)";
    final static String metadata2InTrue = "metadata.metakey2 =in= (abcd, metavalue2, 8546, ghjktrz)";
    final static String metadata2InFalse = "metadata.metakey2 =in= (abcd, 9874, 8546, ghjktrz)";

    final static String createdAtLesserTrue =
            "createdat =lt= " + new GregorianCalendar(2020, DECEMBER, 1).getTimeInMillis();
    final static String createdAtLesserFalse =
            "createdat =lt= " + new GregorianCalendar(2010, DECEMBER, 1).getTimeInMillis();
    final static String createdAtGreaterTrue =
            "createdat =gt= " + new GregorianCalendar(2014, DECEMBER, 1).getTimeInMillis();
    final static String createdAtGreaterFalse =
            "createdat =gt= " + new GregorianCalendar(2020, DECEMBER, 1).getTimeInMillis();
    final static String createdAtLesserEqualTrue =
            "createdat =le= " + new GregorianCalendar(2020, DECEMBER, 1).getTimeInMillis();
    final static String createdAtLesserEqualFalse =
            "createdat =le= " + new GregorianCalendar(2014, DECEMBER, 1).getTimeInMillis();
    final static String createdAtGreaterEqualTrue =
            "createdat =ge= " + new GregorianCalendar(2010, DECEMBER, 1).getTimeInMillis();
    final static String createdAtGreaterEqualFalse =
            "createdat =ge= " + new GregorianCalendar(2016, DECEMBER, 1).getTimeInMillis();
    final static String createdAtGreaterEqualEqualTrue =
            "createdat =ge= " + new GregorianCalendar(2015, DECEMBER, 1).getTimeInMillis();
    final static String createdAtLesserEqualEqualTrue =
            "createdat =le= " + new GregorianCalendar(2015, DECEMBER, 1).getTimeInMillis();

    final static String lastModifiedAtLesserTrue =
            "lastmodifiedat =lt= " + new GregorianCalendar(2020, DECEMBER, 1).getTimeInMillis();
    final static String lastModifiedAtLesserFalse =
            "lastmodifiedat =lt= " + new GregorianCalendar(2010, DECEMBER, 1).getTimeInMillis();
    final static String lastModifiedAtGreaterTrue =
            "lastmodifiedat =gt= " + new GregorianCalendar(2014, DECEMBER, 1).getTimeInMillis();
    final static String lastModifiedAtGreaterFalse =
            "lastmodifiedat =gt= " + new GregorianCalendar(2020, DECEMBER, 1).getTimeInMillis();
    final static String lastModifiedAtLesserEqualTrue =
            "lastmodifiedat =le= " + new GregorianCalendar(2020, DECEMBER, 1).getTimeInMillis();
    final static String lastModifiedAtLesserEqualFalse =
            "lastmodifiedat =le= " + new GregorianCalendar(2014, DECEMBER, 1).getTimeInMillis();
    final static String lastModifiedAtGreaterEqualTrue =
            "lastmodifiedat =ge= " + new GregorianCalendar(2010, DECEMBER, 1).getTimeInMillis();
    final static String lastModifiedAtGreaterEqualFalse =
            "lastmodifiedat =ge= " + new GregorianCalendar(2017, DECEMBER, 1).getTimeInMillis();
    final static String lastModifiedAtGreaterEqualEqualTrue =
            "lastmodifiedat =ge= " + new GregorianCalendar(2015, DECEMBER, 1).getTimeInMillis();
    final static String lastModifiedAtLesserEqualEqualTrue =
            "lastmodifiedat =le= " + new GregorianCalendar(2016, DECEMBER, 1).getTimeInMillis();

    final static String lastRequestLesserTrue =
            "lastcontrollerrequestat =lt= " + new GregorianCalendar(2020, DECEMBER, 1).getTimeInMillis();
    final static String lastRequestLesserFalse =
            "lastcontrollerrequestat =lt= " + new GregorianCalendar(2010, DECEMBER, 1).getTimeInMillis();
    final static String lastRequestGreaterTrue =
            "lastcontrollerrequestat =gt= " + new GregorianCalendar(2014, DECEMBER, 1).getTimeInMillis();
    final static String lastRequestGreaterFalse =
            "lastcontrollerrequestat =gt= " + new GregorianCalendar(2020, DECEMBER, 1).getTimeInMillis();
    final static String lastRequestLesserEqualTrue =
            "lastcontrollerrequestat =le= " + new GregorianCalendar(2020, DECEMBER, 1).getTimeInMillis();
    final static String lastRequestLesserEqualFalse =
            "lastcontrollerrequestat =le= " + new GregorianCalendar(2014, DECEMBER, 1).getTimeInMillis();
    final static String lastRequestGreaterEqualTrue =
            "lastcontrollerrequestat =ge= " + new GregorianCalendar(2010, DECEMBER, 1).getTimeInMillis();
    final static String lastRequestGreaterEqualFalse =
            "lastcontrollerrequestat =ge= " + new GregorianCalendar(2018, DECEMBER, 1).getTimeInMillis();
    final static String lastRequestGreaterEqualEqualTrue =
            "lastcontrollerrequestat =ge= " + new GregorianCalendar(2017, DECEMBER, 1).getTimeInMillis();
    final static String lastRequestLesserEqualEqualTrue =
            "lastcontrollerrequestat =le= " + new GregorianCalendar(2017, DECEMBER, 1).getTimeInMillis();

    final static String idAndTagTrue = idEqualTrue + " and " + tagInTrue1;
    final static String idAndTagTrueAndAttributeTrue = idEqualTrue + " and " + tagInTrue1 + " and " + attributeDevTypeInTrue;
    final static String idAndTagFalse = idEqualTrue + " and " + tagInFalse;
    final static String attributeOrMetadataTrue = attributeRevisionInTrue + " or " + metadata1EqualFalse;
    final static String idOrCreatedOrUpdateFalse = idInFalse + " or " + createdAtGreaterFalse + " or " + updateStatusEqualFalse;
    final static String idAndTagOrLastRequestTrue = "(" + idInFalse + " and " + tagInTrue1 + ")" + " or " + lastRequestGreaterTrue;

    final static String attributeKeyNotExistValueEmptyFalse = "attribute.keynotexistant != \"\"";

    private TargetFieldData fieldData;
    private TargetFieldData fieldData2;

    @BeforeEach
    public void setUp() {

        fieldData = new TargetFieldData();

        String createdTime = Long.toString(new GregorianCalendar(2015, DECEMBER, 1).getTimeInMillis());
        String lastModifiedTime = Long.toString(new GregorianCalendar(2016, DECEMBER, 1).getTimeInMillis());
        String lastRequestTime = Long.toString(new GregorianCalendar(2017, DECEMBER, 1).getTimeInMillis());

        fieldData.add(ID, "012345");
        fieldData.add(NAME, "targetName1");
        fieldData.add(DESCRIPTION, "TargetDescription1");
        fieldData.add(CREATEDAT, createdTime);
        fieldData.add(LASTMODIFIEDAT, lastModifiedTime);
        fieldData.add(CONTROLLERID, "012345");
        fieldData.add(UPDATESTATUS, PENDING.name());
        fieldData.add(IPADDRESS, LOCALHOST);
        fieldData.add(ATTRIBUTE, "revision", "1.123");
        fieldData.add(ATTRIBUTE, "device_type", "dev_test");
        fieldData.add(ASSIGNEDDS, "name", "AssignedDs");
        fieldData.add(ASSIGNEDDS, "version", "3.321");
        fieldData.add(INSTALLEDDS, "name", "InstalledDs");
        fieldData.add(INSTALLEDDS, "version", "9.876");
        fieldData.add(TAG, "alpha");
        fieldData.add(TAG, "beta");
        fieldData.add(LASTCONTROLLERREQUESTAT, lastRequestTime);
        fieldData.add(METADATA, "metakey1", "metavalue1");
        fieldData.add(METADATA, "metakey2", "metavalue2");

        fieldData2 = new TargetFieldData();

        fieldData.add(ID, "123456");
        fieldData.add(NAME, "targetName2");
        fieldData.add(DESCRIPTION, "TargetDescription2");
        fieldData.add(CREATEDAT, createdTime);
        fieldData.add(LASTMODIFIEDAT, lastModifiedTime);
        fieldData.add(CONTROLLERID, "123456");
        fieldData.add(UPDATESTATUS, UNKNOWN.name());
        fieldData.add(IPADDRESS, LOCALHOST);
        fieldData.add(ATTRIBUTE, "device_type", "dev_test");
        fieldData.add(LASTCONTROLLERREQUESTAT, lastRequestTime);
    }

    @Test
    public void match_combined_properties(){
        assertTrue(matches(idAndTagTrue, fieldData));
        assertTrue(matches(idAndTagTrueAndAttributeTrue, fieldData));
        assertFalse(matches(idAndTagFalse, fieldData));
        assertTrue(matches(attributeOrMetadataTrue, fieldData));
        assertFalse(matches(idOrCreatedOrUpdateFalse, fieldData));
        assertTrue(matches(idAndTagOrLastRequestTrue, fieldData));
    }

    @Test
    public void match_single_properties() {

        assertTrue(matches(idWildCardEqualTrue1, fieldData));
        assertTrue(matches(idWildCardEqualTrue2, fieldData));
        assertTrue(matches(idWildCardEqualTrue3, fieldData));
        assertTrue(matches(idWildCardEqualTrue4, fieldData));
        assertTrue(matches(idWildCardEqualTrue5, fieldData));
        assertTrue(matches(idWildCardEqualTrue6, fieldData));
        assertTrue(matches(idWildCardEqualTrue7, fieldData));
        assertFalse(matches(idWildCardEqualFalse2, fieldData));


        assertTrue(matches(ipAddressEqualTrue1, fieldData));
        assertTrue(matches(ipAddressEqualTrue2, fieldData));
        assertFalse(matches(ipAddressEqualFalse, fieldData));
        assertTrue(matches(ipAddressNotEqualTrue, fieldData));
        assertFalse(matches(ipAddressNotEqualFalse, fieldData));
        assertTrue(matches(ipAddressOutTrue, fieldData));
        assertFalse(matches(ipAddressOutFalse, fieldData));
        assertTrue(matches(ipAddressInTrue, fieldData));
        assertFalse(matches(ipAddressInFalse, fieldData));

        assertTrue(matches(lastRequestLesserTrue, fieldData));
        assertFalse(matches(lastRequestLesserFalse, fieldData));
        assertTrue(matches(lastRequestGreaterTrue, fieldData));
        assertFalse(matches(lastRequestGreaterFalse, fieldData));
        assertTrue(matches(lastRequestLesserEqualTrue, fieldData));
        assertFalse(matches(lastRequestLesserEqualFalse, fieldData));
        assertTrue(matches(lastRequestGreaterEqualTrue, fieldData));
        assertFalse(matches(lastRequestGreaterEqualFalse, fieldData));
        assertTrue(matches(lastRequestGreaterEqualEqualTrue, fieldData));
        assertTrue(matches(lastRequestLesserEqualEqualTrue, fieldData));

        assertTrue(matches(metadata2EqualTrue, fieldData));
        assertFalse(matches(metadata2EqualFalse, fieldData));
        assertTrue(matches(metadata2NotEqualTrue, fieldData));
        assertFalse(matches(metadata2NotEqualFalse, fieldData));
        assertTrue(matches(metadata2OutTrue, fieldData));
        assertFalse(matches(metadata2OutFalse, fieldData));
        assertTrue(matches(metadata2InTrue, fieldData));
        assertFalse(matches(metadata2InFalse, fieldData));

        assertTrue(matches(metadata1EqualTrue, fieldData));
        assertFalse(matches(metadataEqualFalse, fieldData));
        assertFalse(matches(metadataNotEqualFalse, fieldData));
        assertFalse(matches(metadata1EqualFalse, fieldData));
        assertTrue(matches(metadata1NotEqualTrue, fieldData));
        assertFalse(matches(metadata1NotEqualFalse, fieldData));
        assertTrue(matches(metadata1OutTrue, fieldData));
        assertFalse(matches(metadata1OutFalse, fieldData));
        assertTrue(matches(metadata1InTrue, fieldData));
        assertFalse(matches(metadata1InFalse, fieldData));

        assertTrue(matches(tagEqualTrue1, fieldData));
        assertTrue(matches(tagEqualTrue2, fieldData));
        assertTrue(matches(tagNotEqualTrue, fieldData));
        assertFalse(matches(tagNotEqualFalse, fieldData));
        assertTrue(matches(tagInTrue1, fieldData));
        assertTrue(matches(tagInTrue2, fieldData));
        assertFalse(matches(tagInFalse, fieldData));
        assertTrue(matches(tagOutTrue, fieldData));
        assertFalse(matches(tagOutFalse1, fieldData));
        assertFalse(matches(tagOutFalse2, fieldData));

        assertTrue(matches(assignedDsNameEqualTrue1, fieldData));
        assertTrue(matches(assignedDsNameEqualTrue2, fieldData));
        assertFalse(matches(assignedDsNameEqualFalse, fieldData));
        assertTrue(matches(assignedDsNameNotEqualTrue, fieldData));
        assertFalse(matches(assignedDsNameNotEqualFalse, fieldData));
        assertTrue(matches(assignedDsNameOutTrue, fieldData));
        assertFalse(matches(assignedDsNameOutFalse, fieldData));
        assertTrue(matches(assignedDsNameInTrue, fieldData));
        assertFalse(matches(assignedDsNameInFalse, fieldData));

        assertTrue(matches(installedDsNameEqualTrue1, fieldData));
        assertTrue(matches(installedDsNameEqualTrue2, fieldData));
        assertFalse(matches(installedDsNameEqualFalse, fieldData));
        assertTrue(matches(installedDsNameNotEqualTrue, fieldData));
        assertFalse(matches(installedDsNameNotEqualFalse, fieldData));
        assertTrue(matches(installedDsNameOutTrue, fieldData));
        assertFalse(matches(installedDsNameOutFalse, fieldData));
        assertTrue(matches(installedDsNameInTrue, fieldData));
        assertFalse(matches(installedDsNameInFalse, fieldData));

        assertTrue(matches(attributeRevisionEqualTrue1, fieldData));
        assertTrue(matches(attributeRevisionEqualTrue2, fieldData));
        assertFalse(matches(attributeRevisionEqualFalse, fieldData));
        assertTrue(matches(attributeRevisionNotEqualTrue, fieldData));
        assertFalse(matches(attributeRevisionNotEqualFalse, fieldData));
        assertTrue(matches(attributeRevisionOutTrue, fieldData));
        assertTrue(matches(attributeRevisionInTrue, fieldData));
        assertFalse(matches(attributeRevisionOutFalse, fieldData));
        assertFalse(matches(attributeRevisionInFalse, fieldData));

        assertTrue(matches(attributeDevTypeEqualTrue1, fieldData));
        assertTrue(matches(attributeDevTypeEqualTrue2, fieldData));
        assertFalse(matches(attributeDevTypeEqualFalse1, fieldData));
        assertFalse(matches(attributeDevTypeEqualFalse2, fieldData));
        assertTrue(matches(attributeDevTypeNotEqualTrue, fieldData));
        assertFalse(matches(attributeDevTypeNotEqualFalse1, fieldData));
        assertFalse(matches(attributeDevTypeNotEqualFalse2, fieldData));
        assertTrue(matches(attributeDevTypeOutTrue, fieldData));
        assertTrue(matches(attributeDevTypeInTrue, fieldData));
        assertFalse(matches(attributeDevTypeOutFalse, fieldData));
        assertFalse(matches(attributeDevTypeInFalse, fieldData));
        assertFalse(matches(attributeKeyNotExistValueEmptyFalse, fieldData2));

        assertTrue(matches(idEqualTrue, fieldData));
        assertTrue(matches(idNotEqualTrue, fieldData));
        assertFalse(matches(idNotEqualFalse, fieldData));
        assertTrue(matches(idInTrue, fieldData));
        assertFalse(matches(idInFalse, fieldData));
        assertTrue(matches(idOutTrue, fieldData));
        assertFalse(matches(idOutFalse, fieldData));

        assertTrue(matches(controllerIdEqualTrue, fieldData));
        assertTrue(matches(controllerIdInTrue, fieldData));
        assertFalse(matches(controllerIdInFalse, fieldData));
        assertTrue(matches(controllerIdOutTrue, fieldData));
        assertFalse(matches(controllerIdOutFalse, fieldData));

        assertTrue(matches(nameEqualTrue1, fieldData));
        assertTrue(matches(nameEqualTrue2, fieldData));
        assertTrue(matches(nameInTrue, fieldData));
        assertFalse(matches(nameInFalse, fieldData));
        assertTrue(matches(nameOutTrue, fieldData));
        assertFalse(matches(nameOutFalse, fieldData));

        assertTrue(matches(descEqualTrue1, fieldData));
        assertTrue(matches(descEqualTrue2, fieldData));
        assertTrue(matches(descInTrue, fieldData));
        assertFalse(matches(descInFalse, fieldData));
        assertTrue(matches(descOutTrue, fieldData));
        assertFalse(matches(descOutFalse, fieldData));

        assertTrue(matches(createdAtLesserTrue, fieldData));
        assertFalse(matches(createdAtLesserFalse, fieldData));
        assertTrue(matches(createdAtGreaterTrue, fieldData));
        assertFalse(matches(createdAtGreaterFalse, fieldData));
        assertTrue(matches(createdAtLesserEqualTrue, fieldData));
        assertFalse(matches(createdAtLesserEqualFalse, fieldData));
        assertTrue(matches(createdAtGreaterEqualTrue, fieldData));
        assertFalse(matches(createdAtGreaterEqualFalse, fieldData));
        assertTrue(matches(createdAtGreaterEqualEqualTrue, fieldData));
        assertTrue(matches(createdAtLesserEqualEqualTrue, fieldData));

        assertTrue(matches(lastModifiedAtLesserTrue, fieldData));
        assertFalse(matches(lastModifiedAtLesserFalse, fieldData));
        assertTrue(matches(lastModifiedAtGreaterTrue, fieldData));
        assertFalse(matches(lastModifiedAtGreaterFalse, fieldData));
        assertTrue(matches(lastModifiedAtLesserEqualTrue, fieldData));
        assertFalse(matches(lastModifiedAtLesserEqualFalse, fieldData));
        assertTrue(matches(lastModifiedAtGreaterEqualTrue, fieldData));
        assertFalse(matches(lastModifiedAtGreaterEqualFalse, fieldData));
        assertTrue(matches(lastModifiedAtGreaterEqualEqualTrue, fieldData));
        assertTrue(matches(lastModifiedAtLesserEqualEqualTrue, fieldData));

        assertTrue(matches(updateStatusEqualTrue1, fieldData));
        assertTrue(matches(updateStatusEqualTrue2, fieldData));
        assertFalse(matches(updateStatusEqualFalse, fieldData));
        assertTrue(matches(updateStatusInTrue, fieldData));
        assertFalse(matches(updateStatusInFalse, fieldData));
        assertTrue(matches(updateStatusOutTrue, fieldData));
        assertFalse(matches(updateStatusOutFalse, fieldData));

    }
}
