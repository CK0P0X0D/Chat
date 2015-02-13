package ua.ckopoxod.chat;

import android.view.View;

/**
 * Created by CK0P0X0D on 10.02.2015.
 */
public interface TextLinkClickListener {


    //  This method is called when the TextLink is clicked from LinkEnabledTextView
    public void onTextLinkClick(View textView, String clickedString, TextPattern.Type type);
}
