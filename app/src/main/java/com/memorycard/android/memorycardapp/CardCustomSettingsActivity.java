package com.memorycard.android.memorycardapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import utilities.DataBaseManager;
import utilities.ImageUtilities;

import static android.content.ContentValues.TAG;

/**
 * Created by yina on 2016/12/31.
 */

public class CardCustomSettingsActivity extends AppCompatActivity{

    public String tab_name;
    private Bitmap bitmap;
    private Card card;
    private Context context;
    private EditText txtQuestion;
    private EditText txtAnswer;
    private CardCustomSettingsActivity activity;
    final static int SELECT_PICTURE = 1;
    private static final String TAG = "CardCustomSettings";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_custom_settings);
        activity = this;
        Intent intent = getIntent();
        tab_name = intent.getStringExtra(MainCustomSettingsActivity.EXTRA_MESSAGE_TABNAME);
        context = getApplicationContext();
        Button addImgButton = (Button) findViewById(R.id.add_new_card_img);
        Button updateButton =  (Button) findViewById(R.id.add_new_card_update);
        txtQuestion = (EditText) findViewById(R.id.new_card_question_txt);

        txtAnswer = (EditText) findViewById(R.id.new_card_answer_txt);


        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean check = false;

                //generate card
                card = new Card();
                card.setMtabName(tab_name);
                card.setmDifficultyScore(0);
                card.setmDay(0);
                if(txtQuestion != null && !"".equals(txtQuestion.getText().toString())){
                    card.setMtxtQuestion(txtQuestion.getText().toString());
                    check = true;
                }
                if(txtAnswer != null && !"".equals(txtAnswer.getText().toString())){
                    card.setMtxtAnswer(txtAnswer.getText().toString());
                }
                if(bitmap != null){
                    check = true;
                    card.setMblobQuestion(ImageUtilities.BitmapToBytes(bitmap));
                }

                if(check){
                    UpdateCardTask mytask = new UpdateCardTask();
                    mytask.execute();
                }else{
                    Toast.makeText(context,"update card failed!",Toast.LENGTH_SHORT);
                }



            }
        });

        addImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, SELECT_PICTURE);          }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            ImageView mImageView = (ImageView) findViewById(R.id.add_new_card_imageView);
            bitmap = BitmapFactory.decodeFile(picturePath);
            mImageView.setImageBitmap(bitmap);

        }

    }

/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView mImageView = (ImageView) findViewById(R.id.add_new_card_imageView);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_PICTURE:
                    String path = getRealPathFromURI(data.getData());
                    //Log.d("Choose Picture", path);
                    //Transformer la photo en Bitmap
                    bitmap = BitmapFactory.decodeFile(path);
                    //Afficher le Bitmap
                    mImageView.setImageBitmap(bitmap);
                    break;
            }
        }
    }*/

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        String[] projection = { MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentURI, projection, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private class UpdateCardTask extends AsyncTask<String,Integer,Uri> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG,"onPreExecute");
            super.onPreExecute();
        }

        @Override
        protected Uri doInBackground(String... params) {

            ContentResolver cr = getContentResolver();
            ContentValues contentValues = DataBaseManager.generateContentValues(card);
            Uri baseUri = Uri.withAppendedPath(MemoryCardContentProvider.BASE_URI,tab_name);
            MemoryCardContentProvider.checkURI(baseUri,tab_name);

            return cr.insert(baseUri,contentValues);
        }


        @Override //update UI list
        protected void onPostExecute(Uri result)
        {
            super.onPostExecute(result);
            Toast.makeText(context,"update card successful",Toast.LENGTH_SHORT).show();

        }
    }

}
