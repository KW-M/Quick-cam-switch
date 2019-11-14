package com.kylewm.switchcam;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "MainModelView";
    AppChooserBottomSheetFragment AppChooserBottomSheet;
    private static SharedPreferences sPrefs;
    private static PackageManager packageManager;
    public static List<ActivityShortcutItem> selectedShortcutsList = new ArrayList<ActivityShortcutItem>();
    private static ActivityShortcutItem shortcutBeingDragged = null;

    MainViewModel(Application app) {
        super(app);
        packageManager = app.getApplicationContext().getPackageManager();
        sPrefs = app.getSharedPreferences("Prefs", app.MODE_PRIVATE);
        AppChooserBottomSheet = AppChooserBottomSheetFragment.newInstance(packageManager);
        loadApps();
    }

    private void loadApps() {
        int numShortcuts = sPrefs.getInt("NumShortcuts", -1);
        if (numShortcuts == -1) {
            setInitialShortcutSet(sPrefs);
        } else {
            getShortcutSet(sPrefs, numShortcuts);
        }
        System.out.println(sPrefs.getAll());
        System.out.println(selectedShortcutsList.size());
    }

    private static void getShortcutSet(SharedPreferences sPrefs, int numShortcuts) {
        for (int i = 0; i < numShortcuts; i++) {
            System.out.print("here["+i);
            try{
                String[] activityNames = sPrefs.getString("ShortcutArray" + i, null).split("\\|", 2);
                System.out.println(activityNames[0]);
                ActivityInfo  appActivityInfo = packageManager.getActivityInfo(new ComponentName(activityNames[0], activityNames[1]), 0);
                String appName = packageManager.getApplicationLabel(appActivityInfo.applicationInfo).toString();
                selectedShortcutsList.add(new ActivityShortcutItem(appActivityInfo.packageName,appActivityInfo.name,appActivityInfo.loadIcon(packageManager),appName,true));
            } catch (Exception e) {
                Log.e(TAG,e.getLocalizedMessage());
            }
        }

    }

    private static void setInitialShortcutSet(SharedPreferences sPrefs) {
        Intent mainIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        List<ResolveInfo> camAppsList = packageManager.queryIntentActivities( mainIntent, 0);
        SharedPreferences.Editor edit = sPrefs.edit();
        for (int i = 0; i < camAppsList.size(); i++) {

            ResolveInfo element = camAppsList.get(i);
            System.out.println(element);
            String appName = packageManager.getApplicationLabel(element.activityInfo.applicationInfo).toString();
            selectedShortcutsList.add(new ActivityShortcutItem(element.activityInfo.packageName,element.activityInfo.name,element.activityInfo.loadIcon(packageManager),appName,true));
            edit.putString("ShortcutArray" + i,element.activityInfo.packageName + "|" + element.activityInfo.name);
        }
        edit.putInt("NumShortcuts",camAppsList.size());
        edit.commit();
    }

    public void onAppChoiceClicked(ActivityShortcutItem activityShortcut) {
        selectedShortcutsList.add(activityShortcut);
        int numShortcuts = sPrefs.getInt("NumShortcuts", -1);
        SharedPreferences.Editor edit = sPrefs.edit();
//        edit.putString("ShortcutArray" + numShortcuts, activityShortcut.pkgName + "|" + activityShortcut.activityName);
//        edit.putInt("NumShortcuts", selectedShortcutsList.size());
//        edit.commit();
    }

    public void removeShortcutAtIndex(int index) {
        shortcutBeingDragged = selectedShortcutsList.remove(index);
    }

    public void insertShortcutAtIndex(int index) {
        selectedShortcutsList.add(index,shortcutBeingDragged);
        shortcutBeingDragged = null;
    }

}