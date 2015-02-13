package ua.ckopoxod.chat;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {

    public static ArrayList<MessageItem> messageItems = new ArrayList<MessageItem>();
    public static BoxAdapter messAdapter;
    public static EditText editTextOk;
    public static int Chat_Mode = 0; //0-старт, 1-работа
    public static boolean curse=true; //false-вверх, true-вниз
    public static boolean _onScrollStateChanged=true;

    // The String Containing the Text that we have to gather links from private SpannableString linkableText;
    // Populating and gathering all the links that are present in the Text
    public SpannableString linkableText;

    public ArrayList<TextPattern.Hyperlink> listOfLinks;

    // A Listener Class for generally sending the Clicks to the one which requires it
    //TextLinkClickListener mListener;

    // Pattern for gathering @usernames from the Text
    public Pattern screenNamePattern = Pattern.compile("(@[a-zA-Z0-9_]+)");

    // Pattern for gathering #hasttags from the Text
    public Pattern hashTagsPattern = Pattern.compile("(#[a-zA-Z0-9_-]+)");

    // Pattern for gathering http:// links from the Text
    public Pattern hyperLinksPattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+.[a-z]+");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messAdapter = new BoxAdapter(this, messageItems);
        editTextOk = (EditText) findViewById(R.id.editText);



        DatabaseHandler db = new DatabaseHandler(MainActivity.this);


        //Toolbar старт
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        // Set an OnMenuItemClickListener to handle menu item clicks
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {


            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Log.d("myLog", "Нажали Меню ТулБар " + item.getItemId());
                DatabaseHandler db = new DatabaseHandler(MainActivity.this);
                // Handle the menu item
                switch (item.getItemId()) {
                    case R.id.action_clear_chat:
                        Log.d("myLog", "Нажали action_clear_chat");
                        messageItems.clear();
                        //DatabaseHandler db = new DatabaseHandler(MainActivity.this);
                        db.deleteAll();
                        db.close();
                        // настраиваем список
                        ListView lvMain = (ListView) findViewById(R.id.listViewChat);
                        lvMain.setAdapter(messAdapter);
                        break;
                    case R.id.action_bot:
                        tRunnable();

                         // DatabaseHandler db = new DatabaseHandler(MainActivity.this);
                   /**     Log.d("myLog", "messageItems.size()"+messageItems.size());

                        for (int i=1;i<messageItems.size();i++)
                        {
                            Contact contact = db.getContact(i);

                            Log.d("myLog", "Mess("+i+")="+contact.getMessage()+" "+contact.getYoumy());
                            Log.d("myLog", "messAdapter("+i+")="+ messAdapter.getMessage(i).message+" "+messAdapter.getMessage(i).youmy);

                        }
                        db.close();**/

                        break;
                }
                return true;
            }
        });
        // Inflate a menu to be displayed in the toolbar
        toolbar.inflateMenu(R.menu.toolbar_menu); // End Toolbar



        //DatabaseHandler db = new DatabaseHandler(this);
        //Log.d("myLog","Inserting ..");
        //db.addContact(new Contact("Привет! you", "you"));
        //db.addContact(new Contact("Привет! my", "my"));


        Log.d("myLog","Считываем all contacts..."); //Начинаем считывать
        List<Contact> contacts = db.getAllContacts();
        messageItems.clear();


        // делаем запрос всех данных из таблицы mytable, получаем Cursor
        //int c = db.getContactsCount();
        //Log.d("myLog","getContactsCount" + c);
        final ListView lvMain = (ListView) findViewById(R.id.listViewChat);

        if (Chat_Mode==0) {        //Проверяем пустали база, если да запускаем БОТа
            tRunnable();
            Chat_Mode = 1;
        }//Бот отработал


        for (Contact cn : contacts) {
            messageItems.add(new MessageItem(cn.getMessage(), cn.getYoumy()));
            Log.d("myLog","Id: "+cn.getID()+" ,Message: " + cn.getMessage() + " ,Youmy: " + cn.getYoumy());
        }
        lvMain.setDivider(getResources().getDrawable(android.R.color.transparent));
        lvMain.setAdapter(messAdapter);

        db.close(); //Считали и закрыли базу






        lvMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if(scrollState == 0) {
                    Log.i("myLog", "scrolling stopped...");
                    _onScrollStateChanged=true;
                }
                else{
                    Log.i("myLog", "scrolling working...");
                    _onScrollStateChanged=false;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

               // if(curse==true) {lvMain.smoothScrollToPosition(totalItemCount); curse=false;}


                if(mLastFirstVisibleItem<firstVisibleItem)
                 {
                 Log.i("SCROLLING DOWN","TRUE");

                     if (totalItemCount == firstVisibleItem + visibleItemCount && _onScrollStateChanged==true) {
                         //lvMain.setTranscriptMode(2);
                         //lvMain.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
                         //lvMain.setStackFromBottom(true);
                         lvMain.smoothScrollToPosition(totalItemCount);
                     }
                     else {
                         lvMain.setSmoothScrollbarEnabled(false);
                     }

                 }

                if(mLastFirstVisibleItem>firstVisibleItem)
                 {
                 Log.i("SCROLLING UP","TRUE");
                     //lvMain.setSmoothScrollbarEnabled(false);
                     lvMain.setStackFromBottom(false);
                 }


                if (totalItemCount == firstVisibleItem + visibleItemCount && _onScrollStateChanged==true) {
                    //lvMain.setTranscriptMode(2);
                    //lvMain.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
                    //lvMain.setStackFromBottom(true);
                    lvMain.smoothScrollToPosition(totalItemCount);
                }
                else {
                    lvMain.setSmoothScrollbarEnabled(false);
                }


                mLastFirstVisibleItem = firstVisibleItem;

            }
        });
    }


    public void onSend(View view) {
        if (editTextOk.getText().toString().length() > 0) {  //проверка на пустой ввод
            //messageItems.add(new MessageItem(editTextOk.getText().toString(), "my"));
            DatabaseHandler db = new DatabaseHandler(this);
            db.addContact(new Contact(editTextOk.getText().toString(), "my"));
            db.close();
            //ListView lvMain = (ListView) findViewById(R.id.listViewChat);
            //lvMain.setDivider(getResources().getDrawable(android.R.color.transparent));
            //lvMain.setAdapter(messAdapter);
            messAdapter.addItem(new MessageItem(editTextOk.getText().toString(), "my"));


            editTextOk.setText("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void tRunnable(){
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                        TimeUnit.SECONDS.sleep(1);runOnUiThread(runn1);
                        TimeUnit.SECONDS.sleep(2);runOnUiThread(runn2);
                        TimeUnit.SECONDS.sleep(4);runOnUiThread(runn3);
                        TimeUnit.SECONDS.sleep(2);runOnUiThread(runn4);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    Runnable runn1 = new Runnable() {
        public void run() {
            //messageItems.add(new MessageItem("Привет!", "you"));
            //ListView lvMain = (ListView) findViewById(R.id.listViewChat);
            //lvMain.setDivider(getResources().getDrawable(android.R.color.transparent));
            //lvMain.setAdapter(messAdapter);
            messAdapter.addItem(new MessageItem("Привет! хэштэг: #123 , usernames: @Max , email: das@das.com.ua", "you"));
            DatabaseHandler db = new DatabaseHandler(MainActivity.this);
            db.addContact(new Contact("Привет! хэштэг: #123 , usernames: @Max , email: das@das.com.ua", "you"));
            db.close();
        }
    };

    Runnable runn2 = new Runnable() {
        public void run() {
            //messageItems.add(new MessageItem("Живы, здоровы? Как дела на работе? Машина бегает? на рыбалку собираешься?", "you"));
            //ListView lvMain = (ListView) findViewById(R.id.listViewChat);
            //lvMain.setDivider(getResources().getDrawable(android.R.color.transparent));
            //lvMain.setAdapter(messAdapter);
            messAdapter.addItem(new MessageItem("Живы, здоровы? Как дела на работе? Машина бегает? на рыбалку собираешься?", "you"));
            DatabaseHandler db = new DatabaseHandler(MainActivity.this);
            db.addContact(new Contact("Живы, здоровы? Как дела на работе? Машина бегает? на рыбалку собираешься?", "you"));
            db.close();
        }
    };

    Runnable runn3 = new Runnable() {
        public void run() {
            //messageItems.add(new MessageItem("Если на рыбалку соберешься - зови и меня )", "you"));
            //ListView lvMain = (ListView) findViewById(R.id.listViewChat);
            //lvMain.setDivider(getResources().getDrawable(android.R.color.transparent));
            //lvMain.setAdapter(messAdapter);
            messAdapter.addItem(new MessageItem("Если на рыбалку соберешься - зови и меня )", "you"));
            DatabaseHandler db = new DatabaseHandler(MainActivity.this);
            db.addContact(new Contact("Если на рыбалку соберешься - зови и меня )", "you"));
            db.close();
        }
    };

    Runnable runn4 = new Runnable() {
        public void run() {
           // messageItems.add(new MessageItem("Пока!", "you"));
            //ListView lvMain = (ListView) findViewById(R.id.listViewChat);
            //lvMain.setDivider(getResources().getDrawable(android.R.color.transparent));
            //lvMain.setAdapter(messAdapter);
            messAdapter.addItem(new MessageItem("Пока!", "you"));
            DatabaseHandler db = new DatabaseHandler(MainActivity.this);
            db.addContact(new Contact("Пока!", "you"));
            db.close();
        }
    };


    public void onTextLinkClick(View textView, String clickedString) {
        android.util.Log.v("Hyperlink clicked is :: " + clickedString, "Hyperlink clicked is :: " + clickedString);
    }
}
