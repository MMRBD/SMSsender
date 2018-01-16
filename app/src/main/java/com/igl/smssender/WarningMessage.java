
//WarningMessage.java
package com.igl.smssender;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by RAD on 05-Apr-17.
 */

public class WarningMessage extends AlertDialog.Builder {

    public WarningMessage(Context context, String warningTitle, String warningMessage) {
        super(context);
        setTitle(warningTitle);
        setMessage(warningMessage);
    }

    public WarningMessage(Context context, String warningTitle, String warningMessage, int icon) {
        super(context);
        setTitle(warningTitle);
        setMessage(warningMessage);
        setIcon(icon);
    }

    public void display() {
        create();
        show();
    }

}
