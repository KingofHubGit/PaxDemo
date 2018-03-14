package com.pax.telephonymanager.utils;

import android.content.Context;

/*
 *  @项目名：  TCPTest 
 *  @包名：    com.pax.teleponymanager.utils
 *  @文件名:   DensityUtil
 *  @创建者:   Frezrik
 *  @创建时间:  2016/11/19 17:24
 *  @描述：    dp to px
 *             px to dp
 */
public class DensityUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
