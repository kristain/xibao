package com.drjing.xibao.common.view.calendarview;

import android.content.Context;
import android.os.Build;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.RadioButton;

/**
 * An {@linkplain ImageView} to pragmatically set the color of arrows
 * using a {@linkplain android.graphics.ColorFilter}
 */
class DirectionButton extends RadioButton {

    public DirectionButton(Context context) {
        super(context);
        setBackgroundResource(getThemeSelectableBackgroundId(context));
    }

    public void setColor(int color) {
    //   setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }


    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1f : 0.1f);
    }

    private static int getThemeSelectableBackgroundId(Context context) {
        //Get selectableItemBackgroundBorderless defined for AppCompat
        int colorAttr = context.getResources().getIdentifier(
                "selectableItemBackgroundBorderless", "attr", context.getPackageName());

        if (colorAttr == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                colorAttr = android.R.attr.selectableItemBackgroundBorderless;
            } else {
                colorAttr = android.R.attr.selectableItemBackground;
            }
        }

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.resourceId;
    }
}
