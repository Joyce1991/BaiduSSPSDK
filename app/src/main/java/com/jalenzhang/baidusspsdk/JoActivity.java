package com.jalenzhang.baidusspsdk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.mobads.AdView;
import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAdListener;
import com.jalenzhang.baidusspsdk.utils.LogUtils;
import com.umeng.onlineconfig.OnlineConfigAgent;

/**
 * <p>Title: 启动页面</p>
 * <p/>
 * <p>Description: </p>
 * 百度广告配置
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: kuaiyouxi.com</p>
 * <p>Created on 2016/1/6.
 *
 * @author jalen-pc
 * @version 1.1.1
 */

public class JoActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 更新友盟在线参数到本地缓存
        OnlineConfigAgent.getInstance().updateOnlineConfig(this);

        // 填充广告
        configBaiduSsp();
    }
    /**
     * 配置百度广告信息
     */
    private void configBaiduSsp() {
        FrameLayout lFrameLayout = new FrameLayout(this);
        FrameLayout.LayoutParams lLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lFrameLayout.setLayoutParams(lLayoutParams);

        AdView lAdView = new AdView(this);
        lAdView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        lFrameLayout.addView(lAdView);

        // 外部矩形弧度
        float[] lOuterR = new float[] { 8, 8, 8, 8, 8, 8, 8, 8 };
        // 内部矩形与外部矩形的距离
        RectF lInsetRectF = new RectF(100, 100, 50, 50);
        ShapeDrawable lShapeDrawable = new ShapeDrawable();
        lShapeDrawable.setShape(new RoundRectShape(lOuterR,lInsetRectF, null));
        //指定填充颜色
        lShapeDrawable.getPaint().setColor(Color.argb(179, 255, 255, 255));
        final TextView lTextView = new TextView(this);
        lTextView.setText("跳过");
        lTextView.setTextColor(Color.WHITE);
        lTextView.setBackground(lShapeDrawable);
        lTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        lTextView.setClickable(true);
        lTextView.setVisibility(View.INVISIBLE);
        FrameLayout.LayoutParams lTextViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lTextViewParams.gravity = Gravity.TOP|Gravity.RIGHT;
        lTextViewParams.rightMargin = 20;
        lTextViewParams.topMargin = 20;
        lFrameLayout.addView(lTextView,lTextViewParams);
        setContentView(lFrameLayout);

        AdView.setAppSid(this, OnlineConfigAgent.getInstance().getConfigParams(this, Constant.BAIDU_AD_APPID));
        LogUtils.d(OnlineConfigAgent.getInstance().getConfigParams(this, Constant.BAIDU_AD_APPID));
        SplashAdListener listener = new SplashAdListener() {
            @Override
            public void onAdPresent() {
                LogUtils.i("广告成功加载");
                lTextView.setClickable(false);
                lTextView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAdDismissed() {
                LogUtils.i("广告位消失");
                next();
            }
            @Override
            public void onAdFailed(String arg0) {
                LogUtils.w("广告加载失败", arg0);
                next();
            }
            @Override
            public void onAdClick() {
                //设置开屏可接受点击时，该回调可用
                LogUtils.i("广告位被点击了");
                next();
            }
        };
        String lReplacePackageName = this.getPackageName().replace(".", "_");
        String lConfigAdParcelID = OnlineConfigAgent.getInstance().getConfigParams(this, lReplacePackageName);
        new SplashAd(this,
                lAdView,
                listener,
                lConfigAdParcelID,
                true);
        LogUtils.d(lConfigAdParcelID);
    }

    /**
     * 跳转至游戏入口activity
     */
    private void next() {
        ComponentName name = getOriginalActivity();
        if (name == null) {
            throw new RuntimeException("original activity not found");
        }
        Intent intent = new Intent();
        intent.setComponent(name);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        this.finish();
    }

    /**
     * 获取游戏的入口activity
     *
     * @return {@link ComponentName}
     */
    private ComponentName getOriginalActivity() {
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(
                    this.getPackageName(), PackageManager.GET_META_DATA);
            String name = info.applicationInfo.metaData
                    .getString("main_original");
            if (name.startsWith(".")) {
                name = this.getPackageName() + name;
            }
            ComponentName cName = new ComponentName(this.getPackageName(),
                    name);
            return cName;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
