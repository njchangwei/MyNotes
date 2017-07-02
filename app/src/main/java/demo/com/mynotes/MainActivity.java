package demo.com.mynotes;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.ListView;

import demo.com.mynotes.db.NotesDB;

public class MainActivity extends ListActivity {

    private SimpleCursorAdapter simpleCursorAdapter = null;
    private NotesDB db;
    private SQLiteDatabase dbRead;
    public static final int REQUEST_CODE_ADD_NOTE = 1;

    public static final int REQUEST_CODE_EDIT_NOTE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        db = new NotesDB(this);
        dbRead = db.getReadableDatabase();

        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.note_list_cell, null,
                new String[]{NotesDB.CLOMN_NAME_NOTE_NAME, NotesDB.CLOMN_NAME_NOTE_DATE},
                new int[]{R.id.tvName, R.id.tvDate},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        setListAdapter(simpleCursorAdapter);

        setContentView(R.layout.activity_main);

        refreshNotesListView();

        findViewById(R.id.btnToAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,EditActivity.class));
            }
        });





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ADD_NOTE:
                if (resultCode == Activity.RESULT_OK) {
                    refreshNotesListView();
                }
                break;
            case REQUEST_CODE_EDIT_NOTE:
                refreshNotesListView();
                break;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor cursor = simpleCursorAdapter.getCursor();
        cursor.moveToPosition(position);

        Intent intent = new Intent(MainActivity.this,EditActivity.class);
        intent.putExtra(EditActivity.EXTRA_NOTE_ID,cursor.getInt(cursor.getColumnIndex(NotesDB.CLOMN_NAME_ID)));
        intent.putExtra(EditActivity.EXTRA_NOTE_NAME,cursor.getString(cursor.getColumnIndex(NotesDB.CLOMN_NAME_NOTE_NAME)));
        intent.putExtra(EditActivity.EXTRA_NOTE_CONTENT,cursor.getString(cursor.getColumnIndex(NotesDB.CLOMN_NAME_NOTE_CONTENT)));
        startActivityForResult(intent,REQUEST_CODE_EDIT_NOTE);
    }

    public void refreshNotesListView() {
        simpleCursorAdapter.changeCursor(dbRead.query(NotesDB.TABLE_NAME_NOTES, null, null, null, null, null, null));

    }


}
