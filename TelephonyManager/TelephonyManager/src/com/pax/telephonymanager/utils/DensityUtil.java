package com.pax.telephonymanager.utils;

import android.content.Context;

/*
 *  @��Ŀ����  TCPTest 
 *  @������    com.pax.teleponymanager.utils
 *  @�ļ���:   DensityUtil
 *  @������:   Frezrik
 *  @����ʱ��:  2016/11/19 17:24
 *  @������    dp to px
 *             px to dp
 */
public class DensityUtil {

    /**
     * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
