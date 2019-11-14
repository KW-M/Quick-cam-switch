package com.kylewm.switchcam;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity implements AppChooserBottomSheetFragment.Listener {
    private static final String TAG = "MainActivity";
    private FrameLayout switcherPreviewLayout;
    private MainViewModel viewModel;
    int fortyDP, sixDP, previewLogoSize, previewLogoMargin;
    int logosPerRow = 0;
    ImageView selectedAppLogo = null;
    Point touchStartPosition;
    int touchStartIndex, draggingIndex, lastIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        switcherPreviewLayout = (FrameLayout) findViewById(R.id.switcherPreviewLayout);
        fortyDP = previewLogoSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        sixDP = previewLogoMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        switcherPreviewLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "onTouch entered");
                int X = (int) event.getX();
                int Y = (int) event.getY();
                int eventAction = event.getAction();
                switch (eventAction) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d(TAG, "ACTION_DOWN AT COORDS " + "X: " + X + " Y: " + Y);
                        touchStartIndex = getShortcutLogoIndexFromTouch(new Point(X, Y));
                        selectedAppLogo = (ImageView) switcherPreviewLayout.getChildAt(touchStartIndex + 1);
                        System.out.println(selectedAppLogo);
                        touchStartPosition = new Point((int) (X - selectedAppLogo.getTranslationX()), (int) (Y - selectedAppLogo.getTranslationY()));

                        break;

                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "MOVE Index:" + getShortcutLogoIndexFromTouch(new Point(X, Y)) + "X: " + X + " Y: " + Y);
                        selectedAppLogo.setTranslationX(X - touchStartPosition.x);
                        selectedAppLogo.setTranslationY(Y - touchStartPosition.y);
                        lastIndex = draggingIndex;
                        draggingIndex = getShortcutLogoIndexFromTouch(new Point(X, Y));
                        if (lastIndex < draggingIndex) {
                            System.out.println((lastIndex) + ">>" + (draggingIndex) + " Target = " + touchStartIndex);
                            for (int i = lastIndex + 1; i <= draggingIndex; i++) {
                                rearangeShortcutIcon(i, 1);
                            }
                        } else if (lastIndex > draggingIndex) {
                            for (int i = lastIndex - 1; i >= draggingIndex; i--) {
                                rearangeShortcutIcon(i, -1);
                            }
                            System.out.println((draggingIndex) + "<<" + (lastIndex));
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        int endingIndex = getShortcutLogoIndexFromTouch(new Point(X, Y));
                        viewModel.removeShortcutAtIndex(touchStartIndex);
                        if (endingIndex != -1) {
                            viewModel.insertShortcutAtIndex(endingIndex);
                        } else {
                            // theyre delete it
                        }
//                        switcherPreviewLayout.removeView(selectedAppLogo);
//                        Point endingPosition = getShortcutLogoPosition(endingIndex - 1);
//                        selectedAppLogo.setTranslationY(endingPosition.y);
//                        selectedAppLogo.setTranslationX(endingPosition.x);
//                        switcherPreviewLayout.addView(selectedAppLogo, endingIndex);
////                        for (int i = 1; i <= viewModel.selectedShortcutsList.size() + 1; i++) {
////                            System.out.println(i+" : "+switcherPreviewLayout.getChildAt(i).getId());
//////                            switcherPreviewLayout.removeViewAt(i);
////                        }
//                        for (int i = endingIndex +1; i < touchStartIndex; i++) {
//                                ImageView shortcutAtIndex = (ImageView) switcherPreviewLayout.getChildAt(i -1);
//                                Point newShortcutPosition = getShortcutLogoPosition(i);
//                                shortcutAtIndex.setTranslationX(newShortcutPosition.x);
//                                shortcutAtIndex.setTranslationY(newShortcutPosition.y);
//                            }
                        layoutShortcutPreviewLogos();
                        break;
                }
                return true;
            }
        });
    }

    public void openAppChooser(View v) {
        viewModel.AppChooserBottomSheet.show(getSupportFragmentManager(), "dialog");
    }

    public void rearangeShortcutIcon(int i, int direction) {
        if (i < 0) return;
        try {
            if (touchStartIndex == i) {

                ImageView shortcutAtIndex = viewModel.selectedShortcutsList.get(i + 1).shortcutView;
                Point newShortcutPosition = getShortcutLogoPosition(i + 1);
                shortcutAtIndex.setTranslationX(newShortcutPosition.x);
                shortcutAtIndex.setTranslationY(newShortcutPosition.y);
                shortcutAtIndex = viewModel.selectedShortcutsList.get(i - 1).shortcutView;
                newShortcutPosition = getShortcutLogoPosition(i - 1);
                shortcutAtIndex.setTranslationX(newShortcutPosition.x);
                shortcutAtIndex.setTranslationY(newShortcutPosition.y);

            } else if (touchStartIndex < i) {
                System.out.print(
                        "rearanging" + i + ">" + (i - 1)
                );
                if (direction == 1) {
                    ImageView shortcutAtIndex = viewModel.selectedShortcutsList.get(i).shortcutView;
                    Point newShortcutPosition = getShortcutLogoPosition(i - 1);
                    shortcutAtIndex.setTranslationX(newShortcutPosition.x);
                    shortcutAtIndex.setTranslationY(newShortcutPosition.y);
                }
                if (direction == -1) {
                    ImageView shortcutAtIndex = viewModel.selectedShortcutsList.get(i + 1).shortcutView;
                    Point newShortcutPosition = getShortcutLogoPosition(i + 1);
                    shortcutAtIndex.setTranslationX(newShortcutPosition.x);
                    shortcutAtIndex.setTranslationY(newShortcutPosition.y);
                }
            } else if (touchStartIndex > i) {
                if (direction == 1) {
                    ImageView shortcutAtIndex = viewModel.selectedShortcutsList.get(i - 1).shortcutView;
                    Point newShortcutPosition = getShortcutLogoPosition(i - 1);
                    shortcutAtIndex.setTranslationX(newShortcutPosition.x);
                    shortcutAtIndex.setTranslationY(newShortcutPosition.y);
                }
                if (direction == -1) {
                    ImageView shortcutAtIndex = viewModel.selectedShortcutsList.get(i).shortcutView;
                    Point newShortcutPosition = getShortcutLogoPosition(i + 1);
                    shortcutAtIndex.setTranslationX(newShortcutPosition.x);
                    shortcutAtIndex.setTranslationY(newShortcutPosition.y);
                }
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onAppChoiceClicked(ActivityShortcutItem activityShortcut) {
        viewModel.onAppChoiceClicked(activityShortcut);
//        for (ActivityShortcutItem shortcut : viewModel.selectedShortcutsList) {
//            try {
//                ActivityInfo shortcutActivityInfo = getPackageManager().getActivityInfo(new ComponentName(shortcut.pkgName, shortcut.activityName), 0);
//                System.out.println(shortcutActivityInfo);
//                ImageView appLogo = new ImageView(this);
//                LinearLayout.LayoutParams btnParameters = new LinearLayout.LayoutParams(fortyDP, fortyDP);
//                btnParameters.setMargins(20, 20, 20, 20);
//                appLogo.setImageDrawable(shortcutActivityInfo.loadIcon(getPackageManager()));
//                appLogo.setLayoutParams(btnParameters);
//                switcherPreviewLayout.addView(appLogo);
//            } catch (PackageManager.NameNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//        calculatePreviewLayoutSize();
        layoutShortcutPreviewLogos();
    }

    public void layoutShortcutPreviewLogos() {
        View button = switcherPreviewLayout.getChildAt(0);
        switcherPreviewLayout.removeAllViews();
        switcherPreviewLayout.addView(button);
        int totalWidth = viewModel.selectedShortcutsList.size() * (previewLogoMargin + previewLogoSize) + (previewLogoMargin * 2 + previewLogoSize);
        int avalableWidth = (getWindow().getDecorView().getWidth() - (sixDP * 2));
        for (int i = 0; i < viewModel.selectedShortcutsList.size(); i++) {
            ActivityShortcutItem shortcut = null;
            ActivityInfo shortcutActivityInfo = null;
            try {
                shortcut = viewModel.selectedShortcutsList.get(i);
                shortcutActivityInfo = getPackageManager().getActivityInfo(new ComponentName(shortcut.pkgName, shortcut.activityName), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            ImageView appLogo = new ImageView(this);
            appLogo.setId(i);
            appLogo.setImageDrawable(shortcutActivityInfo.loadIcon(getPackageManager()));
            Point logoPosition = getShortcutLogoPosition(i);
            appLogo.setTranslationX(logoPosition.x);
            appLogo.setTranslationY(logoPosition.y);
            LinearLayout.LayoutParams btnParameters = new LinearLayout.LayoutParams(fortyDP, fortyDP);
            appLogo.setLayoutParams(btnParameters);
            shortcut.shortcutView = appLogo;
            switcherPreviewLayout.addView(appLogo);
        }

        ViewGroup.LayoutParams previewLayoutParams = switcherPreviewLayout.getLayoutParams();
        if (totalWidth > avalableWidth) {
            logosPerRow = (int) Math.floor((avalableWidth - previewLogoMargin) / (previewLogoMargin + previewLogoSize));
            previewLayoutParams.width = logosPerRow * (previewLogoMargin + previewLogoSize) + previewLogoMargin;
            System.out.println("herer" + (viewModel.selectedShortcutsList.size() / logosPerRow));
            previewLayoutParams.height = ((int) Math.floor(viewModel.selectedShortcutsList.size() / logosPerRow) + 1) * (previewLogoMargin + previewLogoSize) + previewLogoMargin;
        } else {
            logosPerRow = viewModel.selectedShortcutsList.size();
            previewLayoutParams.height = (previewLogoMargin * 2 + previewLogoSize);
            previewLayoutParams.width = totalWidth;
        }
        switcherPreviewLayout.setLayoutParams(previewLayoutParams);
    }

    public Point getShortcutLogoPosition(int logoIndex) {
        logoIndex++;
        int totalWidth = (logoIndex) * (previewLogoMargin + previewLogoSize) + (previewLogoMargin * 2 + previewLogoSize);
        int avalableWidth = (getWindow().getDecorView().getWidth() - (sixDP * 2));
        int top, left;
        if (totalWidth > avalableWidth) {
            int logosPerRow = (int) Math.floor((avalableWidth - previewLogoMargin) / (previewLogoMargin + previewLogoSize));
            top = (int) Math.floor((logoIndex) / logosPerRow) * (previewLogoMargin + previewLogoSize) + previewLogoMargin;
            left = ((logoIndex) % logosPerRow) * (previewLogoMargin + previewLogoSize) + previewLogoMargin;
            System.out.println("LIndex:" + logoIndex + "logosPerRow:" + logosPerRow + "totalWidth:" + totalWidth + "avalableWidth:" + avalableWidth + "top:" + top + "left:" + left);
        } else {
            top = previewLogoMargin;
            left = totalWidth - (previewLogoMargin + previewLogoSize);
            System.out.println("LIndex:" + logoIndex + "totalWidth:" + totalWidth + "avalableWidth:" + avalableWidth + "top:" + top + "left:" + left);
        }
        return new Point(left, top);
    }

    public int getShortcutLogoIndexFromTouch(Point touchPoint) {
        int column = (int) Math.floor((touchPoint.x) / (previewLogoMargin + previewLogoSize));
        int row = (int) Math.floor((touchPoint.y - previewLogoMargin / 2) / (previewLogoMargin + previewLogoSize));

        int index = Math.min(Math.max(0, Math.min(logosPerRow, column)) + (row * logosPerRow), viewModel.selectedShortcutsList.size());
        return index - 1;
    }
}
