package org.gpswakeup.resources;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * A small utility class
 * Last update : 16.01.2013
 * @author Gregoire Aubert
 */
public abstract class Utility {
	
	// CONSTANTS
	public static final String ACTION_NEW = "ORG.GPSWAKEUP.ACTION_NEW";
	public static final String ACTION_EDIT = "ORG.GPSWAKEUP.ACTION_EDIT";
	
	/**
	 * Utility method to make toast in the center of the screen
	 * @param context is the activity context
	 * @param resId is the resource id of the text to display
	 * @param duration is the duration of the toast
	 * @return the toast to display
	 */
	public static Toast makeCenterToast (Context context, int resId, int duration){
		Toast t = Toast.makeText(context, resId, duration);
		t.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
		return t;
	}
}
