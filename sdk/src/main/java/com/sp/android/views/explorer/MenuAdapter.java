package com.sp.android.views.explorer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sp.android.views.R;

import java.util.ArrayList;
import java.util.List;


class MenuAdapter extends BaseAdapter implements ListView.OnItemClickListener {

    static final int MENU_TYPE_PICTURE = 0;
    static final int MENU_TYPE_VIDEO = 1;
    static final int MENU_TYPE_AUDIO = 2;
    static final int MENU_TYPE_OTHER = 3;

    static final class MenuItem {
        int type;
        int text;
        int icon;
        boolean selected;

        public MenuItem(int type, int text, int icon, boolean selected) {
            this.type = type;
            this.text = text;
            this.icon = icon;
            this.selected = selected;
        }
    }

    static final class ViewHolder {
        LinearLayout linearLayoutMenuItem;
        ImageView imgItemIcon;
        TextView txtItemText;

        public ViewHolder(View view) {
            this.linearLayoutMenuItem = (LinearLayout)view;
            this.imgItemIcon = (ImageView) view.findViewById(R.id.img_menu_item_icon);
            this.txtItemText = (TextView) view.findViewById(R.id.txt_menu_item_text);
        }
    }

    interface Callback {
        void onMenuSelected(int type);
    }

    private Context mContext;
    private LayoutInflater mInflater;
    private Callback mCallback;
    private List<MenuItem> mMenus;

    MenuAdapter(Context context, Callback callback) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mCallback = callback;
        prepareMenu();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mCallback != null) {
            mCallback.onMenuSelected(mMenus.get(position).type);
        }
        setSelected(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mMenus.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_explorer_menu_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MenuItem item = mMenus.get(position);
        bind(holder, item);
        return convertView;
    }

    private void bind(ViewHolder holder, MenuItem item) {
        if (item.selected) {
            holder.linearLayoutMenuItem.setBackgroundColor(mContext.getResources().getColor(R.color.menu_item_selected));
        } else {
            holder.linearLayoutMenuItem.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        holder.imgItemIcon.setImageDrawable(mContext.getResources().getDrawable(item.icon));
        holder.txtItemText.setText(mContext.getResources().getText(item.text));
    }

    private void prepareMenu() {
        mMenus = new ArrayList<>();
        mMenus.add(new MenuItem(MENU_TYPE_PICTURE, R.string.picture, R.drawable.icon_type_pic, true));
        mMenus.add(new MenuItem(MENU_TYPE_VIDEO, R.string.video, R.drawable.icon_type_video, false));
        mMenus.add(new MenuItem(MENU_TYPE_AUDIO, R.string.audio, R.drawable.icon_type_audio, false));
        mMenus.add(new MenuItem(MENU_TYPE_OTHER, R.string.other, R.drawable.icon_type_other, false));
    }

    private void setSelected(int position) {
        for (int i = 0; i < mMenus.size(); i++) {
            MenuItem item = mMenus.get(i);
            item.selected = false;
            if (i == position) {
                item.selected = true;
            }
        }
    }
}
