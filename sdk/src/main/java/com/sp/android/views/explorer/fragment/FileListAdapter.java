package com.sp.android.views.explorer.fragment;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.sp.android.views.R;
import com.sp.android.views.explorer.model.bean.MediaMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileListAdapter extends BaseAdapter implements ListView.OnItemClickListener {

    private FileListFragment mFileListFragment;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private List<MediaMeta> mMediaMeta;
    private List<MediaMeta> mSelected;

    public FileListAdapter(Context context, FileListFragment fileListFragment) {
        this.mContext = context;
        this.mFileListFragment = fileListFragment;
        this.mLayoutInflater = LayoutInflater.from(context);

        mMediaMeta = new LinkedList<>();
        mSelected = new ArrayList<>();
    }

    public void addMediaMeta(List<MediaMeta> mediaMeta) {
        if (mediaMeta != null) {
            mMediaMeta.addAll(mediaMeta);
            notifyDataSetChanged();
        }
    }

    public void setMediaMeta(List<MediaMeta> mediaMeta) {
        if (mediaMeta != null) {
            mMediaMeta = mediaMeta;
            mSelected.clear();
            notifyDataSetChanged();
        }
    }

    public List<MediaMeta> getSelected() {
        return mSelected;
    }

    @Override
    public int getCount() {
        return mMediaMeta.size();
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
            convertView = mLayoutInflater.inflate(R.layout.layout_file_list_fragment_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MediaMeta meta = mMediaMeta.get(position);
        bind(holder, meta);
        return convertView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MediaMeta meta = mMediaMeta.get(position);
        if (meta.isDirectory()) {
            String path = meta.getFilePath();
            mFileListFragment.loadPath(path);
        } else {
            if (mSelected.contains(meta)) {
                mSelected.remove(meta);
            } else {
                mSelected.add(meta);
            }
            mFileListFragment.updateSelectedCount(mSelected.size());
            notifyDataSetChanged();
        }
    }

    private void bind(ViewHolder holder, MediaMeta meta) {
        if (meta.isDirectory()) {
            holder.icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_file_folder));
            holder.title.setText(meta.getFileName());
            holder.size.setVisibility(View.GONE);
            holder.select.setVisibility(View.GONE);
        } else {
            switch (meta.getType()) {
                case MediaMeta.MEDIA_TYPE_AUDIO:
                    holder.icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_file_audio));
                    break;
                case MediaMeta.MEDIA_TYPE_VIDEO:
                    holder.icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_file_video));
                    break;
                case MediaMeta.MEDIA_TYPE_PICTURE:
                    holder.icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_file_picture));
                    break;
                default:
                    holder.icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_file_default));
                    break;
            }

            holder.title.setText(meta.getFileName());
            holder.size.setText(String.valueOf(meta.getSize()));
            holder.size.setVisibility(View.VISIBLE);
            holder.select.setVisibility(View.VISIBLE);
            if (mSelected.contains(meta.getId())) {
                holder.select.setImageDrawable(mContext.getResources().getDrawable(R.drawable.chk_checked));
            } else {
                holder.select.setImageDrawable(mContext.getResources().getDrawable(R.drawable.chk_unchecked));
            }
        }
    }

    static class ViewHolder {
        SimpleDraweeView icon;
        TextView title;
        TextView size;
        ImageView select;

        ViewHolder(View view) {
            icon = (SimpleDraweeView)view.findViewById(R.id.img_fragment_item_icon);
            title = (TextView) view.findViewById(R.id.txt_fragment_item_name);
            size = (TextView) view.findViewById(R.id.txt_fragment_item_size);
            select = (ImageView) view.findViewById(R.id.img_btn_file_select);
        }
    }
}
