package com.jalenzhang.baidusspsdk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
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
        setContentView(R.layout.activity_jo);

        // 更新友盟在线参数到本地缓存
        OnlineConfigAgent.getInstance().updateOnlineConfig(this);

        // 填充广告
        configBaiduSsp();
    }
    /**
     * 配置百度广告信息
     */
    private void configBaiduSsp() {
        AdView.setAppSid(this, OnlineConfigAgent.getInstance().getConfigParams(this, Constant.BAIDU_AD_APPID));
        final AdView lAdView = (AdView) findViewById(R.id.cbma_content);
        final TextView lTextView = (TextView) findViewById(R.id.tv_skip);
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
