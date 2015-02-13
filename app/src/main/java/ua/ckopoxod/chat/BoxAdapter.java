package ua.ckopoxod.chat;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by CK0P0X0D on 30.01.2015.
 */
public class BoxAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    public ArrayList<MessageItem> objects;
    TextPattern check;


    BoxAdapter(Context context, ArrayList<MessageItem> messageitems) {
        ctx = context;
        objects = messageitems;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    BoxAdapter(Runnable context, ArrayList<MessageItem> messageitems) {
        ctx = (Context) context;
        objects = messageitems;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public void  addItem(MessageItem item) { //добавляем к адаптеру
        objects.add(item);
        notifyDataSetChanged();
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        // используем созданные, но не используемые view
        View view = convertView;
        //TextView _Text;// = (TextView) view.findViewById(R.id.tvDescr);
        int ColorText;



        MessageItem p = getMessage(position);

            if (p.youmy.equals("my")) { view = lInflater.inflate(R.layout.item_my, parent, false); ColorText=Color.GREEN;}
            else { view = lInflater.inflate(R.layout.item_you, parent, false); ColorText=Color.RED;}

        check = new TextPattern (ctx, null);

        check.gatherLinksForText(p.message, true, view, R.id.tvDescr, Color.GRAY, ColorText);
        view.setOnTextLinkClickListener(this);
        //check.setOnTextLinkClickListener(this);

          return view;
    }



    // товар по позиции
    public MessageItem getMessage(int position) {
        return ((MessageItem) getItem(position));
    }

}
