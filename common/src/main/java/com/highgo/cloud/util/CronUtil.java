package com.highgo.cloud.util;

import com.cronutils.converter.CalendarToCronTransformer;
import com.cronutils.converter.CronConverter;
import com.cronutils.converter.CronToCalendarTransformer;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import java.time.ZoneId;
import java.util.*;

/**
 * @ClassName: CronUtil
 * @Description: Cron表达式工具类
 * # ┌───────────── 分钟 (0 - 59)
 * # │ ┌───────────── 小时 (0 - 23)
 * # │ │ ┌───────────── 月的某天 (1 - 31)
 * # │ │ │ ┌───────────── 月份 (1 - 12)
 * # │ │ │ │ ┌───────────── 周的某天 (0 - 6)（周日到周一；在某些系统上，7 也是星期日）
 * # │ │ │ │ │                          或者是 sun，mon，tue，web，thu，fri，sat
 * # │ │ │ │ │
 * # │ │ │ │ │
 * # * * * * *
 */
public class CronUtil {

    private final static CronConverter cronConverter = new CronConverter(new CronToCalendarTransformer(), new CalendarToCronTransformer());

    /**
     * 生成cron表达式
     *
     * @param startTime
     * @param period
     * @return String
     */
    public static String generateCron(String startTime, String period) {
        if (StringUtils.isBlank(startTime) || StringUtils.isBlank(period)) {
            return null;
        }
        startTime = startTime.split("-")[0].trim();
        Map<String, String> weekRelatioMap = new HashMap<String, String>() {{
            put("sunday", "0");
            put("monday", "1");
            put("tuesday", "2");
            put("wednesday", "3");
            put("thursday", "4");
            put("friday", "5");
            put("saturday", "6");
        }};

        String[] periodList = period.split(",");
        List<String> weekIntList = new ArrayList<>();
        for (String item : periodList) {
            item = item.trim().toLowerCase();
            if (weekRelatioMap.containsKey(item)) {
                String value = weekRelatioMap.get(item);
                if (!weekIntList.contains(value)) {
                    weekIntList.add(value);
                }
            }
        }
        Collections.sort(weekIntList);
        String weekStr = "0,1,2,3,4,5,6".equals(String.join(",", weekIntList)) ? "*" : String.join(",", weekIntList);

        int hour = Integer.parseInt(startTime.split(":")[0]);
        int min = Integer.parseInt(startTime.split(":")[1]);
        Assert.isTrue(hour < 24);
        Assert.isTrue(hour >= 0);
        Assert.isTrue(min < 60);
        Assert.isTrue(min >= 0);

        String timeStr = String.format("%s %s * *", min, hour);
        String cronStr = String.format("%s %s", timeStr, weekStr);

        return cronConverter.using(cronStr).from(ZoneId.of("Asia/Shanghai")).to(ZoneId.of("UTC")).convert();
    }
}
