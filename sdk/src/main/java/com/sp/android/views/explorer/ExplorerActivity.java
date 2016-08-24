package com.sp.android.views.explorer;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sp.android.views.R;
import com.sp.android.views.explorer.fragment.BaseFragment;
import com.sp.android.views.explorer.fragment.FileListFragment;
import com.sp.android.views.explorer.fragment.PictureFragment;
import com.sp.android.views.explorer.injection.Injection;
import com.sp.android.views.explorer.model.bean.MediaMeta;
import com.sp.android.views.scheduler.ThreadPoolScheduler;

import java.util.List;

public class ExplorerActivity extends Activity implements MenuAdapter.Callback,
        ExplorerContract.View, BaseFragment.Callback {

    private ImageView mBtnBack;
    private TextView mTextTitle;
    private TextView mTextMenu;

    private DrawerLayout mDrawerLayoutMain;
    private ListView mListMenu;
    private MenuAdapter mMenuAdapter;
    private PictureFragment mPictureFragment;
    private FileListFragment mFileListFragment;

    private ExplorerContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_explorer_activity);

        mDrawerLayoutMain = (DrawerLayout)findViewById(R.id.drawer_layout_main);
        mListMenu = (ListView) findViewById(R.id.list_menu);

        initPresenter();

        initTitle();
        initMenu();
        initDrawer();
        initFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.start();
        mPresenter.loadInternal(MediaMeta.MEDIA_TYPE_PICTURE, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.stop();
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    private void initTitle() {
        mBtnBack = (ImageView) findViewById(R.id.img_title_bar_back);
        mTextTitle = (TextView) findViewById(R.id.txt_title_bar_title);
        mTextMenu = (TextView) findViewById(R.id.txt_title_bar_menu);

        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        mTextMenu.setVisibility(View.INVISIBLE);
        mTextTitle.setVisibility(View.VISIBLE);
    }

    private void initMenu() {
        mMenuAdapter = new MenuAdapter(this, this);
        mListMenu.setAdapter(mMenuAdapter);
        mListMenu.setOnItemClickListener(mMenuAdapter);
    }

    private void initDrawer() {
        mDrawerLayoutMain.openDrawer(GravityCompat.START);
    }

    private void initFragment() {
        mPictureFragment = new PictureFragment(this, mPresenter);
        mFileListFragment = new FileListFragment(this, mPresenter);

        FragmentManager manager = this.getFragmentManager();
        manager.beginTransaction()
                .add(R.id.frame_layout_main, mPictureFragment)
                .add(R.id.frame_layout_main, mFileListFragment)
                .hide(mFileListFragment)
                .commit();
    }

    private void initPresenter() {
        mPresenter = new ExplorerPresenter(
                ThreadPoolScheduler.getInstance(),
                this,
                Injection.provideMediaTask(this));
    }

    private void onBack() {
        if (mFileListFragment.onBack()) {
            finish();
        }
    }

    @Override
    public void onMenuSelected(int type) {
        if (type == MediaMeta.MEDIA_TYPE_OTHER) {
            mPresenter.loadExternal(null, true);
        } else {
            mPresenter.loadInternal(type, true);
        }
        mDrawerLayoutMain.closeDrawers();
    }

    @Override
    public void setPresenter(ExplorerContract.Presenter presenter) {

    }

    @Override
    public void onLoadStart(int type) {
        if (type == MediaMeta.MEDIA_TYPE_PICTURE) {
            mPictureFragment.clearMedia();
            FragmentManager manager = this.getFragmentManager();
            manager.beginTransaction()
                    .show(mPictureFragment)
                    .hide(mFileListFragment)
                    .commit();
        } else {
            mFileListFragment.clearMedia();
            FragmentManager manager = this.getFragmentManager();
            manager.beginTransaction()
                    .show(mFileListFragment)
                    .hide(mPictureFragment)
                    .commit();
        }
    }

    @Override
    public void onLoad(int type, List<MediaMeta> meta) {
        if (type == MediaMeta.MEDIA_TYPE_PICTURE) {
            mPictureFragment.addMedia(meta);
        } else {
            mFileListFragment.addMedia(meta);
        }
    }

    @Override
    public void onLoadEnd(int type) {
    }

    @Override
    public void onError(int code, String msg) {
        String showMsg = String.format("%s, code is %d", msg, code);
        Toast.makeText(this, showMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelected(List<MediaMeta> paths) {

    }
}
