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

package com.highgo.cloud.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class CommonUtilTest {
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> timeZones() {
        return Arrays.asList(new Object[][]{{"UTC"}, {"Asia/Shanghai"}, {"America/Los_Angeles"}});
    }

    private final String timeZone;
    private TimeZone originalTimeZone;

    public CommonUtilTest(String timeZone) {
        this.timeZone = timeZone;
    }

    @Before
    public void setTimeZone() {
        originalTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }

    @After
    public void restoreTimeZone() {
        TimeZone.setDefault(originalTimeZone);
    }

    @Test
    public void parsesUtcTimestampIndependentlyOfDefaultTimeZone() throws ParseException {
        assertEquals(Date.from(Instant.parse("2026-09-23T14:00:00Z")),
                CommonUtil.stringToDate("2026-09-23T14:00:00Z"));
    }

    @Test
    public void preservesPodStartTimeOrderingAroundInstanceUpdate() throws ParseException {
        Date updatedAt = Date.from(Instant.parse("2026-09-23T14:00:00Z"));
        assertTrue(CommonUtil.stringToDate("2026-09-23T13:59:59Z").before(updatedAt));
        assertFalse(CommonUtil.stringToDate("2026-09-23T14:00:01Z").before(updatedAt));
    }
}
