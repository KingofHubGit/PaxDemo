package com.pax.telephonymanager.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 *  @项目名：  TeleponyManager 
 *  @包名：    com.pax.teleponymanager.utils
 *  @文件名:   DateUtil
 *  @创建者:   Frezrik
 *  @创建时间:  2016/11/20 9:35
 *  @描述：    
 */
public class DateUtil {
    public static String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String getDateN() {
        return getDate() + "\n";
    }

}
