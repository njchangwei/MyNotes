package demo.com.mynotes;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import demo.com.mynotes.db.NotesDB;

import static android.provider.MediaStore.EXTRA_OUTPUT;


/**
 * Created by Administrator on 2017/6/30.
 */

public class EditActivity extends ListActivity {

    private int noteId = -1;
    public static final String EXTRA_NOTE_ID = "noteId";
    public static final String EXTRA_NOTE_NAME = "noteName";
    public static final String EXTRA_NOTE_CONTENT = "noteContent";
    public static final int REQUEST_CODE_GET_PHOTO =1;
    public static final int REQUEST_CODE_GET_VIDEO=2;
    private EditText etName;
    private EditText etContent;
    private MediaAdapter mediaAdapter;
    private NotesDB db;
    private SQLiteDatabase database;
    private Button btnSave;
    private Button btnCancel;
    private Button btnGetPhoto;
    private String currentPath = null;
    private Button btnGetVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editnote);

        mediaAdapter = new MediaAdapter(this);
        setListAdapter(mediaAdapter);

        db = new NotesDB(this);
        database = db.getReadableDatabase();

        etName = (EditText) findViewById(R.id.etName);
        etContent = (EditText) findViewById(R.id.etContent);

        noteId = getIntent().getIntExtra(EXTRA_NOTE_ID, -1);
        if (noteId > -1) {
            etName.setText(getIntent().getStringExtra(EditActivity.EXTRA_NOTE_NAME));
            etContent.setText(getIntent().getStringExtra(EditActivity.EXTRA_NOTE_CONTENT));

            Cursor cursor = database.query(NotesDB.TABLE_NAME_MEDIA, null, NotesDB.CLOMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{noteId + ""}, null, null, null);
            while (cursor.moveToNext()) {
                mediaAdapter.add(new MediaListCellData(cursor.getInt(cursor.getColumnIndex(NotesDB.CLOMN_NAME_ID)), cursor.getString(cursor.getColumnIndex(NotesDB.CLOMN_NAME_MEDIA_PATH))));
            }
            mediaAdapter.notifyDataSetChanged();
        }

        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (noteId > -1) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(NotesDB.CLOMN_NAME_NOTE_NAME, etName.getText().toString());
                    contentValues.put(NotesDB.CLOMN_NAME_NOTE_CONTENT, etContent.getText().toString());

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date date = new Date();
                    contentValues.put(NotesDB.CLOMN_NAME_NOTE_DATE, simpleDateFormat.format(date));
                    database.update(NotesDB.TABLE_NAME_NOTES, contentValues, NotesDB.CLOMN_NAME_ID + "=?", new String[]{noteId + ""});

                    contentValues.clear();
                    MediaListCellData mediaListCellData;
                    for (int i = 0; i < mediaAdapter.getCount(); i++) {
                        mediaListCellData = mediaAdapter.getItem(i);
                        if (mediaListCellData.mediaId == -1) {
                            contentValues.put(NotesDB.CLOMN_NAME_MEDIA_OWNER_NOTE_ID,noteId);
                            contentValues.put(NotesDB.CLOMN_NAME_MEDIA_PATH,mediaListCellData.mediaPath);
                            database.insert(NotesDB.TABLE_NAME_MEDIA,null,contentValues);
                            contentValues.clear();
                        }
                    }
                    contentValues.put(NotesDB.CLOMN_NAME_NOTE_NAME, etName.getText().toString());
                    contentValues.put(NotesDB.CLOMN_NAME_NOTE_CONTENT, etContent.getText().toString());

                    startActivityForResult(new Intent(EditActivity.this, MainActivity.class), Activity.RESULT_OK);
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(NotesDB.CLOMN_NAME_NOTE_NAME, etName.getText().toString());
                    contentValues.put(NotesDB.CLOMN_NAME_NOTE_CONTENT, etContent.getText().toString());

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date date = new Date();
                    contentValues.put(NotesDB.CLOMN_NAME_NOTE_DATE, simpleDateFormat.format(date));
                    database.insert(NotesDB.TABLE_NAME_NOTES, null, contentValues);
                    startActivityForResult(new Intent(EditActivity.this, MainActivity.class), Activity.RESULT_OK);
                }

            }


        });

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        btnGetPhoto = (Button)findViewById(R.id.btnPhoto);
        btnGetPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(getMediaDir(),System.currentTimeMillis()+".jpg");
                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                currentPath = file.getAbsolutePath();
                intent.putExtra(EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent,REQUEST_CODE_GET_PHOTO);

            }
        });


        btnGetVideo = (Button)findViewById(R.id.btnGetVideo);
        btnGetVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                File file = new File(getMediaDir(),System.currentTimeMillis()+".mp4");
                if(!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                currentPath = file.getAbsolutePath();
                intent.putExtra(EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intent,REQUEST_CODE_GET_VIDEO);

            }
        });

        if(currentPath==null && savedInstanceState!=null && savedInstanceState.getString("out_put")!=null){
            currentPath = savedInstanceState.getString("out_put");

        }

        Log.d("EditActivity onCreate","do it");
    }

    public File getMediaDir(){
        File dir = new File(Environment.getExternalStorageDirectory(),"notesMedia");
        if(!dir.exists()){
            dir.mkdir();
        }
        return dir;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    static class MediaListCellData {
        private int mediaId = -1;
        public String mediaPath;
        private int iconId = R.mipmap.ic_launcher;
        public int type;

        public MediaListCellData(String path) {
            this.mediaPath = path;
            if (mediaPath.endsWith(".jpg")) {
                iconId = R.mipmap.icon_photo;
                type = 1;
            } else if (mediaPath.endsWith(".mp4")) {
                iconId = R.mipmap.icon_video;
                type = 2;
            }
        }

        public MediaListCellData(int mediaId, String path) {
            this(path);
            this.mediaId = mediaId;
        }
    }

    static class MediaAdapter extends BaseAdapter {
        private Context context;
        private List<MediaListCellData> mediaListCellDataList = new ArrayList<MediaListCellData>();

        public MediaAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return mediaListCellDataList.size();
        }

        @Override
        public MediaListCellData getItem(int i) {
            return mediaListCellDataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.media_list_cell, null);
            }

            MediaListCellData data = getItem(i);
            ImageView imageView = view.findViewById(R.id.ivIcon);
            TextView textView = view.findViewById(R.id.tvPath);

            imageView.setImageResource(data.iconId);
            textView.setText(data.mediaPath);

            return view;
        }

        public void add(MediaListCellData data) {
            mediaListCellDataList.add(data);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case REQUEST_CODE_GET_PHOTO:
                    mediaAdapter.add(new MediaListCellData(currentPath));
                    mediaAdapter.notifyDataSetChanged();
                    break;
                case REQUEST_CODE_GET_VIDEO:
                    mediaAdapter.add(new MediaListCellData(currentPath));
                    mediaAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("out_put",currentPath);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        MediaListCellData mediaListCellData = (MediaListCellData)getListAdapter().getItem(position);
        if(mediaListCellData.type == 1){
            Intent intent = new Intent(EditActivity.this,PhotoViewerActivity.class);
            intent.putExtra("path",mediaListCellData.mediaPath);
            startActivity(intent);
        }else if(mediaListCellData.type == 2){
            Intent intent = new Intent(EditActivity.this,VideoViewerActivity.class);
            intent.putExtra("path",mediaListCellData.mediaPath);
            startActivity(intent);
        }

    }
}
