package com.pax.telephonymanager.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 *  @��Ŀ����  TeleponyManager 
 *  @������    com.pax.teleponymanager.utils
 *  @�ļ���:   DateUtil
 *  @������:   Frezrik
 *  @����ʱ��:  2016/11/20 9:35
 *  @������    
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
