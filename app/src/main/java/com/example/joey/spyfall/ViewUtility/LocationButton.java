package com.example.joey.spyfall.ViewUtility;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.joey.spyfall.R;

/**
 * Created by Joey on 09-Nov-16.
 */

public class LocationButton extends Button {

    private boolean enabled = true;

    private final int paintFlags;

    public LocationButton(Context context) {
        super(context, null, R.attr.LocationButtonStyle);
        paintFlags = getPaintFlags();
        setupAttributes();
        setupOnClickListener();
    }

    public LocationButton(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.LocationButtonStyle);
        paintFlags = getPaintFlags();
        setupAttributes();
        setupOnClickListener();
    }

    public boolean isEnabled() {
        return enabled;
    }

    private void disable() {
        enabled = false;
        setAlpha(0.3f);
        setPaintFlags(paintFlags | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void enable() {
        enabled = true;
        setAlpha(1f);
        setPaintFlags(paintFlags);
    }

    private void setupAttributes() {
        setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
        ));
    }

    private void setupOnClickListener() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEnabled())
                    disable();
                else
                    enable();
            }
        });
    }

}
