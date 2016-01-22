/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.syncope.fit.core.reference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;
import javax.ws.rs.core.Response;
import org.apache.syncope.common.lib.SyncopeClientException;
import org.apache.syncope.common.lib.SyncopeConstants;
import org.apache.syncope.common.lib.patch.AnyObjectPatch;
import org.apache.syncope.common.lib.to.ConnObjectTO;
import org.apache.syncope.common.lib.to.AnyObjectTO;
import org.apache.syncope.common.lib.to.AttrTO;
import org.apache.syncope.common.lib.to.MembershipTO;
import org.apache.syncope.common.lib.to.PagedResult;
import org.apache.syncope.common.lib.to.RelationshipTO;
import org.apache.syncope.common.lib.types.AnyTypeKind;
import org.apache.syncope.common.lib.types.ClientExceptionType;
import org.apache.syncope.common.lib.types.SchemaType;
import org.apache.syncope.common.rest.api.beans.AnyListQuery;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.JVM)
public class AnyObjectITCase extends AbstractITCase {

    public static AnyObjectTO getSampleTO(final String location) {
        AnyObjectTO anyObjectTO = new AnyObjectTO();
        anyObjectTO.setRealm(SyncopeConstants.ROOT_REALM);
        anyObjectTO.setType("PRINTER");
        anyObjectTO.getPlainAttrs().add(attrTO("location", location + getUUIDString()));

        anyObjectTO.getResources().add(RESOURCE_NAME_DBSCRIPTED);
        return anyObjectTO;
    }

    @Test
    public void create() {
        AnyObjectTO anyObjectTO = getSampleTO("create");

        anyObjectTO = createAnyObject(anyObjectTO).getAny();
        assertNotNull(anyObjectTO);

        ConnObjectTO connObjectTO =
                resourceService.readConnObject(RESOURCE_NAME_DBSCRIPTED, anyObjectTO.getType(), anyObjectTO.getKey());
        assertNotNull(connObjectTO);
        assertNotNull(connObjectTO.getPlainAttrMap().get("location"));
        assertEquals(anyObjectTO.getPlainAttrMap().get("location"), connObjectTO.getPlainAttrMap().get("location"));
    }

    @Test
    public void createInvalidMembership() {
        // 1. create anyObject in realm /odd and attempt to assign group 15, from realm /even => exception
        AnyObjectTO anyObjectTO = getSampleTO("createInvalidMembership");
        anyObjectTO.setRealm("/odd");
        anyObjectTO.getMemberships().add(new MembershipTO.Builder().group(15L).build());

        try {
            createAnyObject(anyObjectTO);
            fail();
        } catch (SyncopeClientException e) {
            assertEquals(ClientExceptionType.InvalidMembership, e.getType());
        }

        // 2. change anyObject's realm to /even/two, now it works
        anyObjectTO.setRealm("/even/two");

        anyObjectTO = createAnyObject(anyObjectTO).getAny();
        assertTrue(anyObjectTO.getMembershipMap().containsKey(15L));
    }

    @Test
    public void delete() {
        try {
            anyObjectService.delete(0L);
        } catch (SyncopeClientException e) {
            assertEquals(Response.Status.NOT_FOUND, e.getType().getResponseStatus());
        }

        AnyObjectTO anyObjectTO = getSampleTO("deletable");
        anyObjectTO.setRealm("/even");

        anyObjectTO = createAnyObject(anyObjectTO).getAny();
        assertNotNull(anyObjectTO);

        AnyObjectTO deletedAnyObject = deleteAnyObject(anyObjectTO.getKey()).getAny();
        assertNotNull(deletedAnyObject);

        try {
            anyObjectService.read(deletedAnyObject.getKey());
        } catch (SyncopeClientException e) {
            assertEquals(Response.Status.NOT_FOUND, e.getType().getResponseStatus());
        }
    }

    @Test
    public void list() {
        PagedResult<AnyObjectTO> anyObjectTOs = anyObjectService.list(
                "PRINTER", new AnyListQuery.Builder().realm(SyncopeConstants.ROOT_REALM).build());
        assertNotNull(anyObjectTOs);
        assertTrue(anyObjectTOs.getResult().size() >= 2);
        for (AnyObjectTO anyObjectTO : anyObjectTOs.getResult()) {
            assertNotNull(anyObjectTO);
        }
    }

    @Test
    public void read() {
        AnyObjectTO anyObjectTO = anyObjectService.read(1L);
        assertNotNull(anyObjectTO);
        assertNotNull(anyObjectTO.getPlainAttrs());
        assertFalse(anyObjectTO.getPlainAttrs().isEmpty());
    }

    @Test
    public void update() {
        AnyObjectTO anyObjectTO = getSampleTO("update");
        anyObjectTO = createAnyObject(anyObjectTO).getAny();

        assertEquals(1, anyObjectTO.getPlainAttrs().size());

        AnyObjectPatch anyObjectPatch = new AnyObjectPatch();
        anyObjectPatch.setKey(anyObjectTO.getKey());
        String newLocation = "new" + getUUIDString();
        anyObjectPatch.getPlainAttrs().add(attrAddReplacePatch("location", newLocation));

        anyObjectTO = updateAnyObject(anyObjectPatch).getAny();

        assertEquals(newLocation, anyObjectTO.getPlainAttrMap().get("location").getValues().get(0));
    }

    @Test
    public void readAttrs() {
        AnyObjectTO anyObjectTO = getSampleTO("readAttrs");
        anyObjectTO = createAnyObject(anyObjectTO).getAny();
        assertNotNull(anyObjectTO);

        Set<AttrTO> attrs = anyObjectService.read(anyObjectTO.getKey(), SchemaType.PLAIN);
        assertEquals(anyObjectTO.getPlainAttrs(), attrs);

        AttrTO location = anyObjectService.read(anyObjectTO.getKey(), SchemaType.PLAIN, "location");
        assertEquals(anyObjectTO.getPlainAttrMap().get("location"), location);
    }

    @Test
    public void updateAttr() {
        AnyObjectTO anyObjectTO = getSampleTO("updateAttr");
        anyObjectTO = createAnyObject(anyObjectTO).getAny();
        assertNotNull(anyObjectTO);

        AttrTO updated = attrTO("location", "newlocation");
        anyObjectService.update(anyObjectTO.getKey(), SchemaType.PLAIN, updated);

        AttrTO location = anyObjectService.read(anyObjectTO.getKey(), SchemaType.PLAIN, "location");
        assertEquals(updated, location);
    }

    @Test
    public void deleteAttr() {
        AnyObjectTO anyObjectTO = getSampleTO("deleteAttr");
        anyObjectTO = createAnyObject(anyObjectTO).getAny();
        assertNotNull(anyObjectTO);
        assertNotNull(anyObjectTO.getPlainAttrMap().get("location"));

        anyObjectService.delete(anyObjectTO.getKey(), SchemaType.PLAIN, "location");

        try {
            anyObjectService.read(anyObjectTO.getKey(), SchemaType.PLAIN, "location");
            fail();
        } catch (SyncopeClientException e) {
            assertEquals(ClientExceptionType.NotFound, e.getType());
        }
    }

    @Test
    public void issueSYNCOPE756() {
        AnyObjectTO anyObjectTO = getSampleTO("issueSYNCOPE756");
        anyObjectTO.getRelationships().add(new RelationshipTO.Builder().right(AnyTypeKind.USER.name(), 1).build());

        try {
            createAnyObject(anyObjectTO).getAny();
            fail();
        } catch (SyncopeClientException e) {
            assertEquals(ClientExceptionType.InvalidAnyType, e.getType());
        }
    }
}
