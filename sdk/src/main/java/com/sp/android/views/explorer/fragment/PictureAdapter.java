package com.sp.android.views.explorer.fragment;


import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.sp.android.views.R;
import com.sp.android.views.explorer.model.bean.MediaMeta;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {

    private static final int ITEM_TYPE_PICTURE = 0;
    private static final int ITEM_TYPE_DATE = 1;

    private PictureFragment mPictureFragment;
    private Context mContext;
    private LayoutInflater mInflater;
    private GridLayoutManager mLayoutManger;

    private List<MediaMeta> mMediaMeta;
    private List<MediaMeta> mSelected;

    private boolean mGroupByDate;

    public PictureAdapter(Context context, PictureFragment pictureFragment) {
        mPictureFragment = pictureFragment;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        initLayoutManager();

        mMediaMeta = new ArrayList<>();
        mSelected = new ArrayList<>();
        mGroupByDate = false;
    }

    public void addMediaMeta(List<MediaMeta> mediaMeta) {
        if (mediaMeta != null) {
            processMediaMeta(mediaMeta);
            notifyDataSetChanged();
        }
    }

    public void setMediaMeta(List<MediaMeta> mediaMeta) {
        if (mediaMeta != null) {
            mMediaMeta.clear();
            mSelected.clear();
            processMediaMeta(mediaMeta);
        }
    }

    public void setGroupByDate(boolean useGroup) {
        this.mGroupByDate = useGroup;
    }

    public GridLayoutManager getLayoutManger() {
        return mLayoutManger;
    }

    public List<MediaMeta> getSelected() {
        return mSelected;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_DATE) {
            View view = mInflater.inflate(R.layout.layout_picture_fragment_seperator, null);
            return new ViewHolder(view);
        } else {
            View view = mInflater.inflate(R.layout.layout_picture_fragment_item, null);
            ViewHolder holder = new ViewHolder(view);
            holder.setOnClickListener();
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        MediaMeta data = mMediaMeta.get(position);
        if (viewType == ITEM_TYPE_DATE) {
            long time = data.getTime();
            String text = getTimeText(time);
            holder.txtDate.setText(text);
        } else if (viewType == ITEM_TYPE_PICTURE){
            String uri = "file://" + data.getFilePath();

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                    .setResizeOptions(new ResizeOptions(200, 200))
                    .build();
            DraweeController draweeController =
                    Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setAutoPlayAnimations(true)
                            .setOldController(holder.imgPicture.getController())
                            .build();
            holder.imgPicture.setController(draweeController);
            if (mSelected.contains(data.getId())) {
                holder.imgSelect.setImageDrawable(mContext.getResources().getDrawable(R.drawable.chk_checked));
            } else {
                holder.imgSelect.setImageDrawable(mContext.getResources().getDrawable(R.drawable.chk_unchecked));
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        MediaMeta meta = mMediaMeta.get(position);
        if (meta.getType() == MediaMeta.MEDIA_TYPE_NULL) {
            return ITEM_TYPE_DATE;
        } else {
            return ITEM_TYPE_PICTURE;
        }
    }

    @Override
    public int getItemCount() {
        return mMediaMeta.size();
    }

    private void initLayoutManager() {
        mLayoutManger = new GridLayoutManager(mContext, 4);
        mLayoutManger.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int viewType = getItemViewType(position);
                if (viewType == ITEM_TYPE_DATE) {
                    return 4;
                }
                return 1;
            }
        });
    }

    private void processMediaMeta(List<MediaMeta> mediaMeta) {
        Collections.sort(mediaMeta);
        if (mGroupByDate) {
            MediaMeta date = null;
            long timeBegin = 0;
            long timeEnd = 0;
            for (MediaMeta meta : mediaMeta) {
                if (meta.getType() == MediaMeta.MEDIA_TYPE_NULL) {
                    continue;
                }
                if (date == null) {
                    date = new MediaMeta();
                    date.setTime(meta.getTime());
                    timeBegin = getTimePointSecond(meta.getTime(), true);
                    timeEnd = getTimePointSecond(meta.getTime(), false);
                    mMediaMeta.add(date);
                }
                if (meta.getTime() < timeBegin || meta.getTime() > timeEnd) {
                    date = new MediaMeta();
                    date.setTime(meta.getTime());
                    timeBegin = getTimePointSecond(meta.getTime(), true);
                    timeEnd = getTimePointSecond(meta.getTime(), false);
                    mMediaMeta.add(date);
                }
                mMediaMeta.add(meta);
            }
        } else {
            mMediaMeta.addAll(mediaMeta);
        }
    }

    private long getTimePointSecond(long time, boolean start) {
        Date date = new Date(time * 1000);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        if (start) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
        }

        return calendar.getTime().getTime() / 1000;
    }

    private String getTimeText(long time) {
        Date date = new Date(time * 1000);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return String.format("%04d年%d月%d日", year, month, day);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SimpleDraweeView imgPicture;
        ImageView imgSelect;

        TextView txtDate;

        ViewHolder(View itemView) {
            super(itemView);
            imgPicture = (SimpleDraweeView) itemView.findViewById(R.id.img_picture);
            imgSelect = (ImageView) itemView.findViewById(R.id.img_btn_select);

            txtDate = (TextView) itemView.findViewById(R.id.txt_fragment_date);
        }

        void setOnClickListener() {
            imgPicture.setOnClickListener(this);
            imgSelect.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            MediaMeta meta = mMediaMeta.get(position);
            if (v.getId() == R.id.img_picture ||
                v.getId() == R.id.img_btn_select) {
                if (mSelected.contains(meta)) {
                    mSelected.remove(meta);
                } else {
                    mSelected.add(meta);
                }
                mPictureFragment.updateSelectedCount(mSelected.size());
                notifyItemChanged(position);
            }
        }
    }
}
