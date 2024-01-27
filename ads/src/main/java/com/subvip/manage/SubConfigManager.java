package com.subvip.manage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


import com.subvip.SubActivity;

import java.util.HashMap;

public class SubConfigManager {
    public static final String ACTION_FINISH = "action_finish";
    private HashMap<String, Boolean> configSub;
    private HashMap<String, Long> maxLimited;

    private static SubConfigManager instance;
    public static final String SUB_SPLASH = "sub_splash";

    public static final String SUB_LIMIT_INVOICE = "sub_limit_invoice";
    public static final String NUM_SUB_FOR_INVOICE = "sub_num_for_invoice";

    public static final String SUB_LIMIT_CLIENT = "sub_limit_client";
    public static final String NUM_SUB_LIMIT_CLIENT = "sub_num_for_client";

    public static final String SUB_LIMIT_SUPPLIER = "sub_limit_supplier";
    public static final String NUM_SUB_LIMIT_SUPPLIER = "sub_num_for_supplier";

    public static final String SUB_LIMIT_CONVERT = "sub_limit_convert_to_voice";
    public static final String NUM_SUB_LIMIT_CONVERT = "sub_num_for_convert_to_voice";


    private ICallBack callBack;

    private final BroadcastReceiver broadcastRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_FINISH)) {
                if (callBack != null) {
                    callBack.onFinish();
                    try {
                        context.unregisterReceiver(this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    public HashMap<String, Boolean> getConfigSub() {
        return configSub;
    }

    public static SubConfigManager getInstance() {
        if (instance == null) {
            instance = new SubConfigManager();
        }
        return instance;
    }

    private SubConfigManager() {
        initKeyConfig();
    }

    private void initKeyConfig() {
        configSub = new HashMap<>();
        configSub.put(SUB_SPLASH, true);
        configSub.put(SUB_LIMIT_INVOICE, true);
        configSub.put(SUB_LIMIT_CLIENT, true);
        configSub.put(SUB_LIMIT_SUPPLIER, true);
        configSub.put(SUB_LIMIT_CONVERT, true);

        maxLimited = new HashMap<>();
        maxLimited.put(NUM_SUB_FOR_INVOICE, 4L);
        maxLimited.put(NUM_SUB_LIMIT_CLIENT, 3L);
        maxLimited.put(NUM_SUB_LIMIT_SUPPLIER, 3L);
        maxLimited.put(NUM_SUB_LIMIT_CONVERT, 1L);
    }

    public void putSubConfig(String key, boolean value) {
        configSub.put(key, value);
    }

    public boolean getValueSubConfig(String key) {
        return configSub.get(key);
    }

    public void putLimitFeature(String key, long value) {
        maxLimited.put(key, value);
    }


    public boolean isLimited(String key, int count) {
        return count > maxLimited.get(key);
    }

    public boolean open(Context context, String key, ICallBack callBack) {
        this.callBack = callBack;
        if (key != null && !configSub.get(key) || PurchaseManager.getInstance().isPremiumed(context)) {
            return false;
        }
        if (callBack != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_FINISH);
            try {
                context.unregisterReceiver(broadcastRecevier);
            } catch (Exception e) {
                e.printStackTrace();
            }
            context.registerReceiver(broadcastRecevier, filter);
        }
        open(context);
        return true;
    }


    public void open(Context context) {
        context.startActivity(new Intent(context, SubActivity.class));
    }

    public boolean open(Context context, String key) {
        return open(context, key, null);
    }

    public interface ICallBack {
        void onFinish();
    }

}
