package com.sp.android.views.explorer.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.sp.android.views.R;
import com.sp.android.views.explorer.ExplorerContract;
import com.sp.android.views.explorer.model.bean.MediaMeta;

import java.util.List;

public class FileListFragment extends BaseFragment implements View.OnClickListener {

    private ListView mFileList;
    private FileListAdapter mFileListAdapter;

    private TextView mSelectedCount;

    public FileListFragment(Callback callback, ExplorerContract.Presenter presenter) {
        super(callback, presenter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_file_list_fragment, null);

        mFileList = (ListView) view.findViewById(R.id.list_files);
        mFileListAdapter = new FileListAdapter(getActivity(), this);
        mFileList.setAdapter(mFileListAdapter);
        mFileList.setOnItemClickListener(mFileListAdapter);

        mSelectedCount = (TextView) view.findViewById(R.id.txt_selected_count);
        mSelectedCount.setOnClickListener(this);

        return view;
    }

    public void setMedia(List<MediaMeta> data) {
        if (mFileListAdapter != null) {
            mFileListAdapter.setMediaMeta(data);
        }
    }

    public void addMedia(List<MediaMeta> data) {
        if (mFileListAdapter != null) {
            mFileListAdapter.addMediaMeta(data);
        }
    }

    public void updateSelectedCount(int count) {
        String text = String.format("上传(%d)", count);
        mSelectedCount.setText(text);
    }

    public void loadPath(String path) {
        mPresenter.loadExternal(path, true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txt_selected_count) {
            if (mCallback != null) {
                List<MediaMeta> paths = mFileListAdapter.getSelected();
                if (paths.size() > 0) {
                    mCallback.onSelected(paths);
                }
            }
        }
    }
}
