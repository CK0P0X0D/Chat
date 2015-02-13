package ua.ckopoxod.chat;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CK0P0X0D on 12.02.2015.
 */
public class TextPattern extends TextView {
    // The String Containing the Text that we have to gather links from private SpannableString linkableText;
// Populating and gathering all the links that are present in the Text
    private SpannableString linkableText;

    public TextView _textView;

    private ArrayList<Hyperlink> listOfLinks;

    // A Listener Class for generally sending the Clicks to the one which requires it
    BoxAdapter mListener;

    // Pattern for gathering @usernames from the Text
    Pattern screenNamePattern = Pattern.compile("(@[a-zA-Z0-9_]+)");

    // Pattern for gathering #hasttags from the Text
    Pattern hashTagsPattern = Pattern.compile("(#[a-zA-Z0-9_-]+)");

    // Pattern for gathering http:// links from the Text
    Pattern hyperLinksPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+.[a-z]+");

    public TextPattern (Context context, AttributeSet attrs) {
        super(context, attrs);

        listOfLinks = new ArrayList<Hyperlink>();

    }

    public void gatherLinksForText(String text, boolean UnderlineText, View _view, int _rId, int TextColor, int LinkTextColor) {
        linkableText = new SpannableString(text);

        //gatherLinks basically collects the Links depending upon the Pattern that we supply
        //and add the links to the ArrayList of the links
        gatherLinks(listOfLinks, linkableText, screenNamePattern, Type.USER);
        gatherLinks(listOfLinks, linkableText, hashTagsPattern, Type.HASTAG);
        gatherLinks(listOfLinks, linkableText, hyperLinksPattern, Type.URL);

        for (int i = 0; i < listOfLinks.size(); i++) {
            Hyperlink linkSpec = listOfLinks.get(i);
            android.util.Log.v("listOfLinks :: " + linkSpec.textSpan, "listOfLinks :: " + linkSpec.textSpan);

            // this process here makes the Clickable Links from the text
            if (UnderlineText)  linkableText.setSpan(new URLSpanNoUnderline(linkSpec.span), linkSpec.start, linkSpec.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); //ckopoxod убираем подчеркивание
            else linkableText.setSpan(linkSpec.span, linkSpec.start, linkSpec.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);            //ckopoxod остается подчеркивание
        }


        // sets the text for the TextView with enabled links
        _textView.setText(linkableText);
        _textView.setTextColor(TextColor);
        _textView.setLinkTextColor(LinkTextColor);
        //_textView.setOnTextLinkClickListener(this);

        //((TextView) _view.findViewById(_rId)).setText(linkableText);
        //((TextView) _view.findViewById(_rId)).setTextColor(TextColor);
        //((TextView) _view.findViewById(_rId)).setLinkTextColor(LinkTextColor);
        //((TextView) _view.findViewById(_rId)).setOnTextLinkClickListener(this);
    }

    // sets the Listener for later click propagation purpose
    public void setOnTextLinkClickListener(TextLinkClickListener newListener) {   //ect
        //mListener = newListener;
        Log.d("myLog","TextPattern_setOnTextLinkClickListener");
    }

    //The Method mainly performs the Regex Comparison for the Pattern and adds them to
    //listOfLinks array list
    private final void gatherLinks(ArrayList<Hyperlink> links, Spannable s, Pattern pattern, Type type) {  //ect
        // Matcher matching the pattern
        Matcher m = pattern.matcher(s);

        while (m.find()) {
            int start = m.start();
            int end = m.end();


            // Hyperlink is basically used like a structure for storing the information about
            // where the link was found.
            Hyperlink spec = new Hyperlink();

            spec.textSpan = s.subSequence(start, end);
            spec.span = new InternalURLSpan(spec.textSpan.toString(), type);
            spec.start = start;
            spec.end = end;

            links.add(spec);
        }
    }


    // This is class which gives us the clicks on the links which we then can use.
    public class InternalURLSpan extends ClickableSpan { //ect
        private String clickedSpan;
        private Type type;

        public InternalURLSpan(String clickedString, Type type) {
            clickedSpan = clickedString;
            this.type = type;
        }

        @Override
        public void onClick(View textView) {
            Log.d("myLog","onClick-------------------------");
            Log.d("myLog","clickedSpan="+clickedSpan);
            Log.d("myLog","type="+type);
            //mListener.onTextLinkClick(textView, clickedSpan, type);
        }
    }

    // The type of tag that was clicked
    public enum Type {
        USER, HASTAG, URL
    }


// Class for storing the information about the Link Location
    public class Hyperlink {  //ect
        CharSequence textSpan;
        InternalURLSpan span;
        int start;
        int end;
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(InternalURLSpan url) {
            super(String.valueOf(url));
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}

