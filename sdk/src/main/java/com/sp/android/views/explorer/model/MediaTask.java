package com.sp.android.views.explorer.model;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.sp.android.views.explorer.model.bean.MediaMeta;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;


public class MediaTask {

    public static final int QUERY_COUNT = 50;

    private static final String SQL_NOT_LIKE = "_data not like '%Rhino/%' and _data not like '%/.%'";
    private static final String SQL_LIMIT = "date_modified desc limit  ";

    private static final String[][] FILE_SELECT = {
            {MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.SIZE,
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DATE_MODIFIED},
            {MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.SIZE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DATE_MODIFIED},
            {MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.SIZE,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DATE_MODIFIED},
            {MediaStore.Files.FileColumns.DISPLAY_NAME,
                    MediaStore.Files.FileColumns.SIZE,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.DATE_MODIFIED}};

    private Context mContext;

    public MediaTask(Context context) {
        mContext = context;
    }

    public List<MediaMeta> query(int type, int offset, int limit) {
        Uri uri = null;
        switch (type) {
            case MediaMeta.MEDIA_TYPE_PICTURE:
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                break;
            case MediaMeta.MEDIA_TYPE_AUDIO:
                uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                break;
            case MediaMeta.MEDIA_TYPE_VIDEO:
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                break;
            default:
                break;
        }

        if (uri == null) {
            return null;
        }
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                FILE_SELECT[type],
                SQL_NOT_LIKE, null,
                SQL_LIMIT + offset + "," + limit);

        List<MediaMeta> data = new LinkedList<>();
        assert cursor != null;
        if (cursor.moveToFirst()) {
            do {

                String fileName = cursor.getString(0);
                long size = cursor.getLong(1);
                String filePath = cursor.getString(2);
                long updateTime = cursor.getLong(3);

                MediaMeta meta = new MediaMeta();
                meta.setFileName(fileName);
                meta.setFilePath(filePath);
                meta.setSize(size);
                meta.setDirectory(false);
                meta.setTime(updateTime);
                meta.setType(type);
                if (new File(filePath).isDirectory()) {
                    meta.setDirectory(true);
                }
                data.add(meta);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return data;
    }

    public List<MediaMeta> queryExternal(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        List<MediaMeta> data = new LinkedList<>();
        File file = new File(filePath);
        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.startsWith(".")) {
                    return false;
                }
                return true;
            }
        });
        Arrays.sort(files, getCompare());
        if (files != null && files.length > 0) {
            for (File item : files) {
                MediaMeta meta = new MediaMeta();
                meta.setFileName(item.getName());
                meta.setFilePath(item.getAbsolutePath());
                meta.setSize(item.length());
                meta.setDirectory(item.isDirectory());
                meta.setTime(item.lastModified());
                meta.setType(MediaMeta.MEDIA_TYPE_OTHER);
                data.add(meta);
            }
        }
        return data;
    }

    private Comparator getCompare() {
        return new Comparator<File>() {

            @Override
            public int compare(File f1, File f2) {
                if (f1 == null || f2 == null) {
                    if (f1 == null) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    if (f1.isDirectory() && f2.isDirectory()) {
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    } else if ((f1.isDirectory() && !f2.isDirectory())) {
                        return -1;
                    } else if ((f2.isDirectory() && !f1.isDirectory())) {
                        return 1;
                    } else {
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }
                }
            }
        };
    }
}
