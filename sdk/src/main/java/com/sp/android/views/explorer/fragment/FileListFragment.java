package com.sp.android.views.explorer.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.sp.android.views.R;
import com.sp.android.views.explorer.ExplorerContract;
import com.sp.android.views.explorer.model.bean.MediaMeta;

import java.io.File;
import java.util.List;
import java.util.Stack;

public class FileListFragment extends BaseFragment implements View.OnClickListener {

    private ListView mFileList;
    private FileListAdapter mAdapter;

    private TextView mSelectedCount;
    private Stack<String> mPaths;

    public FileListFragment(Callback callback, ExplorerContract.Presenter presenter) {
        super(callback, presenter);
        mPaths = new Stack<>();
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
        mAdapter = new FileListAdapter(getActivity(), this);
        mFileList.setAdapter(mAdapter);
        mFileList.setOnItemClickListener(mAdapter);

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

    public void loadPath(String path) {
        mPresenter.loadExternal(path, true);

        String parent = new File(path).getParent();
        mPaths.push(parent);
    }

    public boolean onBack() {
        if (mPaths.size() == 0) {
            return true;
        }

        String path = mPaths.pop();
        mPresenter.loadExternal(path, true);
        return false;
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
