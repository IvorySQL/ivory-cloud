/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.highgo.platform.operator.service.impl;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;

class CrServiceImplTest {

    @Resource(name = "crServiceImpl")
    CrServiceImpl crServiceImpl;

    @Test
    void testCreateCr() {
        assertEquals(4, 2 + 2);
    }

    // @Test
    // void testApplyCr() {
    // fail("Not yet implemented");
    // }

    @Test
    void testIsCrExist() {

        // String clusterId = "123456";
        // String namespace = "nstest";
        // String crName = "test";
        // boolean result = crServiceImpl.isCrExist(clusterId, namespace, crName);
        //
        // assertFalse(result);
    }

    // @Test
    // void testPatchCrResource() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testPatchCrStorage() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testDeleteCr() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testDeleteAllPod() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testRestartDatabase() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testNodeportSwitch() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testCreateBackup() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testApplyBackupPolicy() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testDeleteBackup() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testApplyConfigParam() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetInstanceVOFromCR() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetMasterPod() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testRestore() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testGetHgadminPort() {
    // fail("Not yet implemented");
    // }
    //
    // @Test
    // void testPatchCrUsers() {
    // fail("Not yet implemented");
    // }

}
