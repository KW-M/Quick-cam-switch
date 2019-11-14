package com.kylewm.switchcam;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ActivityShortcutItem {
    public final String pkgName;
    public final String activityName;
    public final Drawable logo;
    public final String displayName;
    public ImageView shortcutView = null;
    boolean isActive = false;


    ActivityShortcutItem(String pkgName, String activityName, Drawable Logo ) {
        this.pkgName = pkgName;
        this.logo = Logo;
        this.activityName = activityName;
        this.displayName = null;
        this.shortcutView = null;
    }

    ActivityShortcutItem(String pkgName, String activityName, Drawable Logo, String DisplayName, boolean isActive) {
        this.logo = Logo;
        this.pkgName = pkgName;
        this.activityName = activityName;
        this.displayName = DisplayName;
        this.isActive = isActive;
        this.shortcutView = null;
    }

//    ActivityShortcutItem(String pkgName, String activityName, Drawable Logo, String DisplayName, ImageView ShortcutView) {
//        this.logo = Logo;
//        this.pkgName = pkgName;
//        this.activityName = activityName;
//        this.displayName = DisplayName;
//        this.isActive = false;
//        this.shortcutView = ShortcutView;
//    }
}
