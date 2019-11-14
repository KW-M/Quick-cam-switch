package com.kylewm.switchcam;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppChooserBottomSheetFragment extends BottomSheetDialogFragment {
    private Listener mainListener;
    private static ArrayList<ActivityShortcutItem> appChoicesList;
    private static PackageManager packageManager;
    
    public static AppChooserBottomSheetFragment newInstance(PackageManager packageManager) {
        final AppChooserBottomSheetFragment fragment = new AppChooserBottomSheetFragment();

        appChoicesList =  new ArrayList<ActivityShortcutItem>();
        AppChooserBottomSheetFragment.packageManager = packageManager;

        Intent launcherIntent = new Intent(Intent.ACTION_MAIN, null);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = packageManager.queryIntentActivities( launcherIntent, 0);
        Collections.sort(pkgAppsList, new ResolveInfo.DisplayNameComparator(packageManager));

        for (ResolveInfo element : pkgAppsList) {
            AppChooserBottomSheetFragment.appChoicesList.add(new ActivityShortcutItem(element.activityInfo.packageName,element.activityInfo.name,element.activityInfo.loadIcon(packageManager),packageManager.getApplicationLabel(element.activityInfo.applicationInfo).toString(),false));
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_chooser_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.appListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AppChoiceAdapter());
    }

//    @Override
//    public int getTheme() {
//        return R.style.BottomSheetDialogTheme;
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final Fragment parent = getParentFragment();
        if (parent != null) {
            mainListener = (Listener) parent;
        } else {
            mainListener = (Listener) context;
        }
    }

    @Override
    public void onDetach() {
        mainListener = null;
        super.onDetach();
    }

    public interface Listener {
        void onAppChoiceClicked(ActivityShortcutItem activityShortcut);
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        final TextView appName;
        final ImageView appLogo;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            // TODO: Customize the item layout
            super(inflater.inflate(R.layout.app_chooser_app_list_item, parent, false));
            appName = (TextView) itemView.findViewById(R.id.AppDisplayName);
            appLogo = (ImageView) itemView.findViewById(R.id.Logo);
        }

    }

    private class AppChoiceAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.appName.setText(appChoicesList.get(position).displayName);
            holder.appLogo.setImageDrawable(appChoicesList.get(position).logo);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mainListener != null) {
                        mainListener.onAppChoiceClicked(appChoicesList.get(holder.getAdapterPosition()));
                        dismiss();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return appChoicesList.size();
        }

    }

}
