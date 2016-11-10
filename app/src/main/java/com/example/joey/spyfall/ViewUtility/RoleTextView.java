package com.example.joey.spyfall.ViewUtility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.joey.spyfall.R;

/**
 * Created by Joey on 09-Nov-16.
 */

public class RoleTextView extends TextView {
    public RoleTextView(Context context) {
        super(context, null, R.attr.GenericTextViewStyleBorderless);
        setupAttributes();
    }

    public RoleTextView(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.GenericTextViewStyleBorderless);
        setupAttributes();
    }

    private void setupAttributes() {
        setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
    }
}
