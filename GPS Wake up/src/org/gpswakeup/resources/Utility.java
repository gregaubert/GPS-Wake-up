package org.gpswakeup.resources;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public abstract class Utility {
	
	public static Toast makeCenterToast (Context context, int resId, int duration){
		Toast t = Toast.makeText(context, resId, duration);
		t.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
		return t;
	}
}
