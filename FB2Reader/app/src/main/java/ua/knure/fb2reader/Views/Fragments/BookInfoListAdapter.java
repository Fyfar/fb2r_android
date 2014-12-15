package ua.knure.fb2reader.Views.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ua.knure.fb2reader.R;

public class BookInfoListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Context context;
    String[] dataHeaders;
    String[] dataText;
    Bitmap cover = null;

    public BookInfoListAdapter(Context context, List<String> headers, List<String> texts, Bitmap cover) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dataHeaders = new String[headers.size()];
        dataText = new String[texts.size()];
        dataHeaders = headers.toArray(dataHeaders);
        dataText = texts.toArray(dataText);
        if (cover != null) {
            this.cover = cover;
        }
    }

    @Override
    public int getCount() {
        return dataHeaders.length;
    }

    @Override
    public Object getItem(int position) {
        return dataHeaders[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (vi == null) {
            vi = inflater.inflate(R.layout.book_info_listview_row_item, null);
        }
        if (position == 0 && cover != null) {
            ImageView imgView = (ImageView) vi.findViewById(R.id.info_list_row_imageView);
            imgView.setImageBitmap(cover);
        } else if (dataText[position].length() > 3) {
            TextView text1 = (TextView) vi.findViewById(R.id.info_list_row_textView1);
            TextView text2 = (TextView) vi.findViewById(R.id.info_list_row_textView2);
            text1.setText(dataHeaders[position]);
            text2.setText(dataText[position]);
        } else {
            TextView text1 = (TextView) vi.findViewById(R.id.info_list_row_textView1);
            TextView text2 = (TextView) vi.findViewById(R.id.info_list_row_textView2);
            text1.setText(" ");
            text2.setText(" ");
        }
        return vi;
    }
}
