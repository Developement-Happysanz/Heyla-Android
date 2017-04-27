package com.findafun.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

import com.findafun.R;

public class AViewFlipper extends ViewFlipper {

    Paint paint = new Paint();

    public AViewFlipper(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {
        super.dispatchDraw(canvas);
        int width = getWidth();

        float margin = 2;
        float radius = 7;
        float cx = (width / 2) - ((radius + margin) * 2 * getChildCount() / 2);
        float cy = getHeight() - 15;

        canvas.save();

        for (int i = 0; i < getChildCount(); i++)
        {
            if (i == getDisplayedChild())
            {
                int color = ContextCompat.getColor(getContext(), R.color.apptheme_primary);
                paint.setColor(color);
                canvas.drawCircle(cx, cy, radius, paint);

            } else
            {

                int color1 = ContextCompat.getColor(getContext(), R.color.white);
                paint.setColor(color1);
                canvas.drawCircle(cx, cy, radius, paint);
            }
            cx += 2 * (radius + margin);
        }
        canvas.restore();
    }

}