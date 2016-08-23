package com.sp.android.views.explorer.model.bean;


import java.util.LinkedList;
import java.util.List;

public class MediaMeta implements Comparable<MediaMeta> {

    public static final int MEDIA_TYPE_NULL = -1;
    public static final int MEDIA_TYPE_PICTURE = 0;
    public static final int MEDIA_TYPE_AUDIO = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_OTHER = 3;

    private static int ID = 0;

    private int mId;
    private int mType;
    private String mFileName;
    private String mFilePath;
    private boolean mIsDirectory;
    private long mSize;
    private long mTime;

    private List<MediaMeta> mChildren;

    public MediaMeta() {
        this.mId = ID++;
        this.mType = MEDIA_TYPE_NULL;
        this.mFileName = "";
        this.mFilePath = "";
        this.mIsDirectory = false;
        this.mSize = 0L;
        this.mTime = 0L;
        this.mChildren = new LinkedList<>();
    }

    public int getId() {
        return mId;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getFileName() {
        return mFileName;
    }

    public void setFileName(String fileName) {
        mFileName = fileName;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public boolean isDirectory() {
        return mIsDirectory;
    }

    public void setDirectory(boolean directory) {
        mIsDirectory = directory;
    }

    public long getSize() {
        return mSize;
    }

    public void setSize(long size) {
        mSize = size;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public List<MediaMeta> getChildren() {
        return mChildren;
    }

    public void addChild(MediaMeta meta) {
        if (meta != null && !mChildren.contains(meta)) {
            mChildren.add(meta);
        }
    }

    public void removeChild(MediaMeta meta) {
        if (meta != null) {
            mChildren.remove(meta);
        }
    }

    @Override
    public int compareTo(MediaMeta another) {
        if (getTime() > another.getTime()) {
            return -1;
        } else if (getTime() < another.getTime()) {
            return 1;
        }
        return 0;
    }
}
