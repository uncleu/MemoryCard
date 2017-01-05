package com.memorycard.android.memorycardapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import utilities.DownLoadAndInstallUtilities;


public class DownLoadAndInstallActivity extends AppCompatActivity {

    private static final String TAG = "DOWNLOAD";
    private  static  final  String tag = "INSTALL";
    private static final int INFILE_CODE = 3;

    private Context context;
    private String downloadFileName;
    private ListView listView;
    private DownloadManager dm;
    private Long nodifyId;
    private String uriFile;
    private Notification baseNF;
    private NotificationManager nm;
    private String[] list = {"family","food"};
    private ArrayAdapter<String> listAdapter;
    private Button installBtn;
    private String mFilePath;
    private Activity activity;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load_and_install);

         context = getApplicationContext();
        activity = this;

        listView = (ListView)findViewById(R.id.download_listview);
        listAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0){


                    downloadFileName = "family";
                    String s = "http://t.jgz.la/attachment/201701/0210/nanasisi-135397/5869b6b2e2f23.zip";
                    //String s = "http://t.jgz.la/attachment/201612/3005/nanasisi-135397/58658007cfef9.zip";
                    //new downloadTask().execute(s);
                    Intent intent = new Intent(DownLoadAndInstallActivity.this, DownLoadService.class);
                    intent.putExtra("getDownloadFileUrl", s);
                    intent.putExtra("downloadFileName", downloadFileName);
                    intent.setAction("start");
                    startService(intent);

                }else if(position == 1){

                    downloadFileName = "food";
                    String s = "http://t.jgz.la/attachment/201701/0210/nanasisi-135397/5869b6b2ecd95.zip";
                    //new downloadTask().execute(s);

                    Intent intent = new Intent(DownLoadAndInstallActivity.this, DownLoadService.class);
                    intent.putExtra("getDownloadFileUrl", s);
                    intent.putExtra("downloadFileName", downloadFileName);
                    intent.setAction("start");
                    startService(intent);
                }

            }
        });
        installBtn = (Button)findViewById(R.id.install_button);

        installBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,INFILE_CODE);
            }
        });


        Button backButton = (Button)findViewById(R.id.back_to_main_menu_button);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


        //for installation
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != activity.RESULT_OK) {
            finish();
        } else if (requestCode == INFILE_CODE) {

            mFilePath = getPath(data.getData());

            DownLoadAndInstallUtilities.install(context,mFilePath);

            Toast.makeText(getApplicationContext(), "Installation Card Group successful", Toast.LENGTH_SHORT).show();


        }
    }



    //get correct file path
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String getPath(final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if(isKitKat) {
            // MediaStore (and general)
            return getForApi19(uri);
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getForApi19(Uri uri) {
        Log.e(tag, "+++ API 19 URI :: " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            Log.e(tag, "+++ Document URI");
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                Log.e(tag, "+++ External Document URI");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    Log.e(tag, "+++ Primary External Document URI");
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                Log.e(tag, "+++ Downloads External Document URI");
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                Log.e(tag, "+++ Media Document URI");
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    Log.e(tag, "+++ Image Media Document URI");
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    Log.e(tag, "+++ Video Media Document URI");
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    Log.e(tag, "+++ Audio Media Document URI");
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.e(tag, "+++ No DOCUMENT URI :: CONTENT ");

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.e(tag, "+++ No DOCUMENT URI :: FILE ");
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
