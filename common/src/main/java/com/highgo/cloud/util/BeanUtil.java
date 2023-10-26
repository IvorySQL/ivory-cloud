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

import com.highgo.cloud.exception.PropertyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

/**
 * 原来是处理Loushang平台中的bean工具类
 * 现用于处理在卫生平台下的bean赋值
 * 已测试能够处理的属性类型包括：基本类型、enum
 * @author wanggang
 * @version 1.0
 * @date 2012-2-15 上午9:04:52
 */
/**
 * 功能: 调用apache的bean组件实现对bean的赋值
 *
 * @author yaoxm mailto:yaoxm@langchao.com.cn $log$
 *
 */
@Slf4j
public final class BeanUtil {

    /**
     * 列表数据序号标志后缀
     */
    private static String LIST_FIELD_INDEX_SUFFIX = "_INDEX";
    private static String LIST_FIELD_PROPERTY_SUFFIX = "_PROPERTY";

    private BeanUtil() {
    }
    static {
        ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
        ConvertUtils.register(new IntegerConverter(null), Integer.class);
        // fixed: org.apache.commons.beanutils.ConversionException: No value specified for 'Date'
        ConvertUtils.register(new DateConverter(null), Date.class);
    }

    /**
     * Copy the property values of the given source bean into the target bean.
     * <p>Note: The source and target classes do not have to match or even be derived
     * from each other, as long as the properties match. Any bean properties that the
     * source bean exposes but the target bean does not will silently be ignored.
     * <p>This is just a convenience method. For more complex transfer needs,
     * consider using a full BeanWrapper.
     * @param source the source bean
     * @param target the target bean
     * @throws BeansException if the copying failed
     * @see BeanWrapper
     */
    public static void copyProperties(Object source, Object target) {
        copyProperties(source, target, (String[]) null);
    }

    /**
     * Copy the property values of the given source bean into the given target bean,
     * ignoring the given "ignoreProperties".
     * <p>Note: The source and target classes do not have to match or even be derived
     * from each other, as long as the properties match. Any bean properties that the
     * source bean exposes but the target bean does not will silently be ignored.
     * <p>This is just a convenience method. For more complex transfer needs,
     * consider using a full BeanWrapper.
     * @param source the source bean
     * @param target the target bean
     * @param ignoreProperties array of property names to ignore
     * @see BeanWrapper
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /**
     * Copy the property values of the given source bean into the given target map,
     * ignoring the given "ignoreProperties".
     * @param bean
     * @param properties
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void copyProperties(Object bean, Map properties, String... ignoreProperties) {
        Map beanMap = toMap(bean);
        beanMap.remove("class");
        if (ignoreProperties != null && ignoreProperties.length > 0) {
            for (String p : ignoreProperties) {
                beanMap.remove(p);
            }
        }
        properties.putAll(beanMap);
    }

    /**
     * Copy the property values of the given source bean into the given target map,
     * ignoring the given "ignoreProperties".
     * @param bean
     * @param properties
     */
    @SuppressWarnings({"rawtypes"})
    public static void copyProperties(Object bean, Map properties) {
        copyProperties(bean, properties, (String[]) null);
    }

    /**
     * 将Map中的数据copy值bean实例中
     * @param bean
     * @param properties
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void copyProperties(Map properties, Object bean) {

        // Do nothing unless both arguments have been specified
        if ((bean == null) || (properties == null)) {
            return;
        }
        log.debug("BeanUtil.populate({}, {}) --- {}", bean, properties,
                bean.getClass().getClassLoader().getClass().getName());
        // Loop through the property name/value pairs to be set
        Iterator names = properties.keySet().iterator();
        Map<String, Object> listTypes = new HashMap<String, Object>(0);

        while (names.hasNext()) {

            String name = (String) names.next();
            if (name == null) {
                continue;
            }
            Object value = properties.get(name);
            try {
                name = BeanUtil.correctListName(name);

                // 如果是含有点（.）的属性，检查listTypes是否已存在，如果已存在，且此属性没有下标索引，则
                // 忽略此 名称
                if (name.indexOf('.') > 0 && name.indexOf('[') < 0) {
                    String listProp = name.substring(0, name.lastIndexOf('.'));
                    if (listTypes.containsKey(listProp)) {
                        continue;
                    }
                }
                BeanUtil.checkProperty(bean, name, listTypes);

                // 如果数据类型为 字符串数组
                if (value != null && (value instanceof String[] || value instanceof String)) {
                    String[] vs = null;
                    if (value instanceof String[]) {
                        vs = (String[]) value;
                    }
                    if (value instanceof String) {
                        vs = new String[1];
                        vs[0] = (String) value;
                    }

                    Class cls = PropertyUtil.getPropertyType(bean, name);
                    if (cls != null) {
                        if (CommonUtil.isEnum(cls)) {// 属性为枚举类型时（TODO 是否可以重构为转换器？）
                            value = CommonUtil.getEnum(cls, vs[0]);
                        } else if (cls.isArray()) {// 属性为数组时
                            Class clazz = cls.getComponentType();
                            if (CommonUtil.isEnum(clazz)) {// 数组元素为枚举时
                                List es = new ArrayList();
                                for (String v : vs) {
                                    Object enumv = CommonUtil.getEnum(clazz, v);
                                    if (enumv != null) {
                                        es.add(enumv);
                                    }
                                }
                                value = CommonUtil.buildArray(clazz, es);
                            }
                        } else if (cls.getSuperclass().equals(Number.class)) {// 属性为数字类型时
                            String str = vs[0];
                            if (!NumberUtils.isNumber(str)) {
                                value = null;
                            }
                        } else if (cls.equals(Date.class) || cls.equals(java.sql.Date.class)) {// 日期类型
                            String str = vs[0];
                            if (StringUtils.isNotEmpty(str)) {
                                Date date = DateHelper.parseDate(str);
                                value = date;
                                if (cls.equals(java.sql.Date.class)) {
                                    value = new java.sql.Date(date.getTime());
                                }
                            } else {// 如果输入的值为空字符串, 则把值设置为null
                                value = null;
                            }
                        }
                    }
                }

            } catch (NoSuchMethodException e) {
                // cww 忽略此异常
                log.debug("bean没有此属性\"{}\"", name);
            } catch (PropertyException e) {
                // cww 忽略此异常
                log.debug("property do not processing: \"{}\"", name);
            } catch (Exception e) {
                // cww 忽略此异常
                log.debug("processing property.");
            }

            // Perform the assignment for this property
            try {
                // log.debug(">>>>>>>>>>>>value : " + name + " = " + value);
                org.apache.commons.beanutils.BeanUtils.setProperty(bean, name, value);
            } catch (Exception ex) {
                log.error("common-utils:copyProperties 异常", ex);
                throw new PropertyException("赋值错误!" + bean + "***" + "name:" + name + "****" + "value:" + value, ex);
            }
        }

    }
    /**
     * copy not null properties from one bean to another bean
     */
    public static void copyNotNullProperties(Object frombean, Object tobean) {
        Map<Object, Object> fromMap = new HashMap();
        BeanUtil.copyProperties(frombean, fromMap);

        Map<Object, Object> toMap = new HashMap();
        BeanUtil.copyProperties(tobean, toMap);
        for (Map.Entry<Object, Object> entry : fromMap.entrySet()) {
            if (entry.getValue() != null) {
                toMap.put(entry.getKey(), entry.getValue());
            }
        }
        // Filter invalid value in variable toMap.
        Iterator<Entry<Object, Object>> iterator = toMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Object, Object> entry = iterator.next();
            if (entry.getValue() == null) {
                iterator.remove();
            }
        }
        BeanUtil.copyProperties(toMap, tobean);
    }

    /**
     * 检查bean的属性
     * @param bean
     * @param propertyName
     * @param listTypes
     * @throws Exception
     */
    private static void checkProperty(Object bean, String propertyName, Map listTypes)
            throws Exception {
        try {
            PropertyUtil.getProperty(bean, propertyName);
        } catch (IndexOutOfBoundsException e) {
            // BeanUtil.initList(bean, propertyName, listTypes);
        } catch (IllegalArgumentException e) {
            // 2009-12-23 cww
            // 当bean有嵌套的对象，且需要赋值时，将出现此异常
            // 异常信息示例 : Null property value for 'entrust'
            String msg = e.getMessage();
            if (msg != null && msg.startsWith("Null property value for")) {
                int qIndex = msg.indexOf("'");
                String subPropertyName = msg.substring(qIndex + 1, msg.lastIndexOf("'"));
                BeanUtil.initProperty(bean, subPropertyName);
                BeanUtil.checkProperty(bean, propertyName, listTypes);
            } else {
                throw e;
            }
        }
    }

    // /**
    // * 利用java5的泛型和反射处理子记录，对于有list属性bean不再需要实现特定的接口：IAddEditGrid
    // * 并且不需要再关心grid在页面中的顺序
    // * @see com.inspur.asm.AsmClassGenerator#generateSubObject(Class, ClassLoader)
    // * @param bean
    // * @param name
    // * @param listTypes
    // * @throws Exception
    // */
    // private static void initList(Object bean, String name, Map listTypes) throws Exception {
    // int leftIndex = name.indexOf('[');
    // int rightIndex = name.indexOf(']');
    // String fieldName = name.substring(0, leftIndex);
    // String propName = name.substring(rightIndex + 2);
    //
    // String strIndex = name.substring(leftIndex + 1, rightIndex);
    // int objIndex = Integer.parseInt(strIndex);
    // if(listTypes.containsKey(fieldName)) {
    // Integer size = (Integer) listTypes.get(fieldName + LIST_FIELD_INDEX_SUFFIX);
    // if(size.intValue() < objIndex) {
    // Class genericCls = (Class) listTypes.get(fieldName);
    // Collection c = (Collection)listTypes.get(fieldName + LIST_FIELD_PROPERTY_SUFFIX);
    // listTypes.put(fieldName + LIST_FIELD_INDEX_SUFFIX, objIndex);
    // for(int i=0; i<objIndex - size; i++) {
    // Object obj = AsmClassGenerator.generateSubObject(genericCls, BeanUtil.class.getClassLoader());
    // c.add(obj);
    // }
    // }
    //
    // } else {
    // Class genericCls = GenericUtils.getListFieldGenericType(bean.getClass(), fieldName);
    // Collection c = (Collection) PropertyUtils.getProperty(bean, fieldName);
    // listTypes.put(fieldName + LIST_FIELD_PROPERTY_SUFFIX, c);
    // listTypes.put(fieldName + LIST_FIELD_INDEX_SUFFIX, objIndex);
    // listTypes.put(fieldName, genericCls);
    //
    // for(int i=0; i<=objIndex; i++) {
    // Object obj = AsmClassGenerator.generateSubObject(genericCls, BeanUtil.class.getClassLoader());
    // c.add(obj);
    // }
    // }
    // }

    /**
     * 当bean有嵌套的对象，且需要赋值时，初始化嵌套对象
     * @param bean
     * @param propertyName
     */
    private static void initProperty(Object bean, String propertyName) {
        try {
            // String[] properties = propertyName.split("\\.");

            Class cls = PropertyUtil.getPropertyType(bean, propertyName);
            PropertyUtil.setProperty(bean, propertyName, cls.newInstance());
        } catch (InstantiationException e1) {
            log.debug("当bean有嵌套的对象，且需要赋值时，初始化嵌套对象后， 设置bean属性：", e1);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("当bean有嵌套的对象，且需要赋值时，初始化嵌套对象后， 设置bean属性：", e);
        }
    }

    private static String correctListName(String name) {
        int pointCount = StringUtils.countMatches(name, ".");
        if (name.indexOf('[') < 0 || pointCount == 1) {
            return name;
        }
        StringBuffer cname = new StringBuffer();
        int leftIndex = name.indexOf('[');
        int rightIndex = name.indexOf(']');
        String propIndex = name.substring(leftIndex, rightIndex + 1);
        name = name.replaceAll("\\[\\d*\\]", "");
        cname.append(name);
        cname.insert(cname.lastIndexOf("."), propIndex);
        return cname.toString();
    }

    // @SuppressWarnings("unchecked")
    // public static Map<String, Object> toMap(Object object) {
    // try {
    // return org.apache.commons.beanutils.BeanUtils.describe(object);
    // } catch (IllegalAccessException e) {
    // throw new PropertyException(object.getClass().getName(), e);
    // } catch (InvocationTargetException e) {
    // throw new PropertyException(object.getClass().getName(), e);
    // } catch (NoSuchMethodException e) {
    // throw new PropertyException(object.getClass().getName(), e);
    // }
    // }

    /**
     * Convert a bean to a map
     * @param bean
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Map toMap(Object bean) {
        try {
            Class type = bean.getClass();
            Map returnMap = new HashMap();
            BeanInfo beanInfo = Introspector.getBeanInfo(type);

            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (!propertyName.equals("class")) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = readMethod.invoke(bean, new Object[0]);
                    returnMap.put(propertyName, result);
                }
            }
            return returnMap;
        } catch (IntrospectionException e) {
            throw new PropertyException(bean.getClass().getName(), e);
        } catch (IllegalAccessException e) {
            throw new PropertyException(bean.getClass().getName(), e);
        } catch (IllegalArgumentException e) {
            throw new PropertyException(bean.getClass().getName(), e);
        } catch (InvocationTargetException e) {
            throw new PropertyException(bean.getClass().getName(), e);
        }

    }

}
