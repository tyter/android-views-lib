package com.sp.android.views.explorer.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.sp.android.views.R;
import com.sp.android.views.explorer.ExplorerContract;
import com.sp.android.views.explorer.model.bean.MediaMeta;

import java.util.List;

public class PictureFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView mPictureView;
    private PictureAdapter mAdapter;

    private TextView mSelectedCount;

    public PictureFragment(Callback callback, ExplorerContract.Presenter presenter) {
        super(callback, presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_picture_fragment, null);

        mPictureView = (RecyclerView) view.findViewById(R.id.list_pictures);
        mAdapter = new PictureAdapter(getActivity(), this);
        mAdapter.setGroupByDate(true);
        mPictureView.setAdapter(mAdapter);
        mPictureView.setLayoutManager(mAdapter.getLayoutManger());

        mSelectedCount = (TextView) view.findViewById(R.id.txt_selected_count);
        mSelectedCount.setOnClickListener(this);

        return view;
    }

    public void clearMedia() {
        mAdapter.clearMediaMeta();
        Context context = getActivity();
        if (context != null) {
            String text = getActivity().getResources().getText(R.string.upload).toString();
            mSelectedCount.setText(text);
        }
    }

    public void setMedia(List<MediaMeta> data) {
        mAdapter.setMediaMeta(data);
    }

    public void addMedia(List<MediaMeta> data) {
        mAdapter.addMediaMeta(data);
    }

    public void updateSelectedCount(int count) {
        Context context = getActivity();
        if (context != null) {
            String text = getActivity().getResources().getText(R.string.upload_count).toString();
            String show = String.format(text, count);
            mSelectedCount.setText(show);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txt_selected_count) {
            if (mCallback != null) {
                List<MediaMeta> paths = mAdapter.getSelected();
                mCallback.onSelected(paths);
            }
        }
    }
}
