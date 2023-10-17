package com.highgo.cloud.constant;

/**
 * 与table provider 内容一致
 * @author chushaolin
 *
 */
public enum ProviderTypeEnum {

    PHYSICAL(OrderContant.PROVIDER_CODE_PHYSICAL, OrderContant.PROVIDER_PURE_BMS_SERVER),
    HUAWEI(OrderContant.PROVIDER_CODE_HUAWEI, OrderContant.PROVIDER_HUAWEI),
    ALIYUN(OrderContant.PROVIDER_CODE_ALIYUN, OrderContant.PROVIDER_ALIYUN),
    TIANYI(OrderContant.PROVIDER_CODE_TIANYI, OrderContant.PROVIDER_TIANYI),
    LANGCHAO(OrderContant.PROVIDER_CODE_LANGCHAO, OrderContant.PROVIDER_LANGCHAO),
    XINFU(OrderContant.PROVIDER_CODE_XINFU, OrderContant.PROVIDER_XINFU);

    // 成员变量
    private String name;
    private int index;
    // 构造方法
    private ProviderTypeEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }

 // 普通方法
    public static String getName(int index) {
        for (ProviderTypeEnum c : ProviderTypeEnum.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }
    // get set 方法
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

}
