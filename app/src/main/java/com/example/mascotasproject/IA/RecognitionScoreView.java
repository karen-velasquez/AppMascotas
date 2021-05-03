package com.example.mascotasproject.IA;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

// custom textview to show recognition results
@SuppressLint("AppCompatCustomView")
public class RecognitionScoreView extends TextView {

    public RecognitionScoreView(final Context context, final AttributeSet set) {
        super(context, set);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf"));
    }
}
