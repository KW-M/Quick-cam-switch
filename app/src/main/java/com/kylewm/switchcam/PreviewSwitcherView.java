//package com.kylewm.switchcam;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.graphics.drawable.Drawable;
//import android.util.TypedValue;
//import android.view.View;
//
//public class PreviewSwitcherView extends View {
//    public boolean drawShortcuts = false;
//    int widthSize;
//    int heightSize;
//    int fortyDP;
//    int sixDP;
//
//    public PreviewSwitcherView(Context context) {
//        super(context);
//        context.
//        fortyDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics());
//        sixDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics());
//    }
//
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
////        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
////        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension(widthSize, heightSize);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (drawShortcuts == true) {
//            ActivityShortcutItem[] appShortcutArray = AppShortcutsHolder.getShortcutList();
//            System.out.println(appShortcutArray.length);
//
//            int width = (2 * sixDP) + (appShortcutArray.length * (fortyDP + sixDP * 2));
//            System.out.println(width);
//
//
//            Drawable d = getResources().getDrawable(R.drawable.background_shape_right, null);
//            d.setBounds((widthSize - width) / 2, 20, (widthSize - width) / 2 + width, 20+ (fortyDP + sixDP * 2));
//            d.draw(canvas);
//
//            for (int i = 0;i < appShortcutArray.length; i++) {
//                Drawable logo = appShortcutArray[appShortcutArray.length - 1 - i].logo;
//                int leftOffset = (widthSize - width) / 2 + sixDP + (fortyDP + sixDP) * i;
//                logo.setBounds(leftOffset, 20 + sixDP, leftOffset + fortyDP,20+sixDP+fortyDP);
//                logo.draw(canvas);
//            }
//
//        }
//    }
//}
