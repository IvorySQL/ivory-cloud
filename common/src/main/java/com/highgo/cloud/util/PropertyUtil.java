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
/**
*
*/

import com.highgo.cloud.exception.PropertyException;

import java.lang.reflect.InvocationTargetException;

/**
 * 包装 commons-beanutils 的PropertyUtils类
 * @author cww<br>
 * @version 1.0
 * 2010-3-26 下午04:45:38<br>
 */
public final class PropertyUtil {

    private PropertyUtil() {

    }
    public static void setProperty(Object object, String attr, Object value) {
        try {
            org.apache.commons.beanutils.PropertyUtils.setProperty(object, attr, value);
        } catch (IllegalAccessException e) {
            throw new PropertyException(object.getClass().getName(), e);
        } catch (InvocationTargetException e) {
            throw new PropertyException(object.getClass().getName(), e);
        } catch (NoSuchMethodException e) {
            throw new PropertyException(object.getClass().getName(), e);
        }
    }

    public static Object getProperty(Object object, String attr) {
        Object value = null;
        try {
            value = org.apache.commons.beanutils.PropertyUtils.getProperty(object, attr);
        } catch (IllegalAccessException e) {
            throw new PropertyException(object.getClass().getName(), e);
        } catch (InvocationTargetException e) {
            throw new PropertyException(object.getClass().getName(), e);
        } catch (NoSuchMethodException e) {
            throw new PropertyException(object.getClass().getName(), e);
        }
        return value;
    }

    @SuppressWarnings("rawtypes")
    public static Class getPropertyType(Object object, String attr) {
        try {
            return org.apache.commons.beanutils.PropertyUtils.getPropertyType(object, attr);
        } catch (IllegalAccessException e) {
            throw new PropertyException(object.getClass().getName(), e);
        } catch (InvocationTargetException e) {
            throw new PropertyException(object.getClass().getName(), e);
        } catch (NoSuchMethodException e) {
            throw new PropertyException(object.getClass().getName(), e);
        }
    }

}
