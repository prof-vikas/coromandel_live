package com.sipl.rfidtagscanner.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;

public class ApkDownloader {
    private static final String TAG = "ApkDownloader";

    public static void downloadApk(Context context, String apkUrl, String title, String description, DownloadCompleteListener downloadCompleteListener) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(apkUrl);

        DownloadManager.Request request = new DownloadManager.Request(uri);
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setMimeType("application/vnd.android.package-archive");
        request.setTitle(title);
        request.setDescription(description);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);

        long downloadId = downloadManager.enqueue(request);
        Log.d(TAG, "downloadApk: downloadId : " + downloadId);

        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadId == receivedDownloadId) {
                    context.unregisterReceiver(this);

                    int status = getDownloadStatus(context, downloadId);

                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        openDownloadedFile(context, downloadId);
                    } else {
                        Log.e(TAG, "Download failed with status: " + status);
                    }

                    if (downloadCompleteListener != null) {
                        downloadCompleteListener.onDownloadComplete(apkUrl, status);
                    }
                }
            }
        };

        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private static int getDownloadStatus(Context context, long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Cursor cursor = downloadManager.query(query);

        int status = DownloadManager.STATUS_FAILED; // Default to failed

        try {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);

                if (columnIndex >= 0) {
                    status = cursor.getInt(columnIndex);
                } else {
                    Log.e(TAG, "Column index for COLUMN_STATUS is invalid.");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while getting download status", e);
        } finally {
            cursor.close();
        }

        return status;
    }


    private static void openDownloadedFile(Context context, long downloadId) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);

        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);

            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                Log.i(TAG, "openDownloadedFile: in status of success fully");
                columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
                String uriString = cursor.getString(columnIndex);

                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(Uri.parse(uriString).getPath()));
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                Log.e(TAG, "openDownloadedFile: status : " + status);
            }
        }
        cursor.close();
    }

    public interface DownloadCompleteListener {
        void onDownloadComplete(String filePath, int status);
    }
}