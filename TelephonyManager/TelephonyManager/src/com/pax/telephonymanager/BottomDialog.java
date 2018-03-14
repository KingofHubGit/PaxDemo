package com.pax.telephonymanager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;

import com.pax.telephonymanager.utils.DensityUtil;

public class BottomDialog
        extends Dialog {
    private Context mContext;
    private ScrollView mScroll;

    public BottomDialog(Context context) {
        super(context, R.style.quick_option_dialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_bottom);

        initView();
    }

    private void initView() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = this.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = wm.getDefaultDisplay().getHeight() - DensityUtil.dip2px(mContext, 100);
        getWindow().setAttributes(params);

        mScroll = (ScrollView) findViewById(R.id.container);
    }

    public void setInfo(String content) {
        TextView tv_content = (TextView) findViewById(R.id.tv_content);

        tv_content.setText(getColorContent(content));

        mScroll.smoothScrollTo(0, 0);
    }

    private Spanned getColorContent(String content) {
        String networkType = content.substring(content.indexOf("GetNetworkType()"),
                content.indexOf("GetPhoneType()"));
        String replaceContent = content.replace(networkType,
                "<font color='red'>" + networkType + "</font>").replaceAll(
                "\n", "<br />");

        return Html.fromHtml(replaceContent);
    }

}
