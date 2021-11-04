package com.example.check_all.constant;

import com.example.check_all.activity.CameraActivity;
import com.example.check_all.activity.NetumActivity;
import com.example.check_all.activity.ZebraActivity;

public class Data {
    public final static String[] NAME_TOOLS_BTN = {"Camera","Netum"};
    public final static String[] NAME_ACTION_BTN = {"Pre-Check Coupe file","Pre-Check Billet","Pre-Check Badge","Check Coupe file","Check Billet","Check Badge","Vente Billet","Check + vente"};
    public final static String[] USER_ROLES = {"ROLE_USER_SHOP","ROLE_USER_CHECKER"};
    public final static Class[] ACTIVITY_LIST = {CameraActivity.class, NetumActivity.class, ZebraActivity.class};


    public final static int margin = 50;
}
