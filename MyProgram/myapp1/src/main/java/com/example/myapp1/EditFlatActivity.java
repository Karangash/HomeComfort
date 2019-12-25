package com.example.myapp1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

public class EditFlatActivity extends AppCompatActivity {

    EditText editTextNameFlate;
    EditText editTextAddress;
    EditText editTextCostFlat;
    EditText editTextNoteFlat;
    EditText editTextDtEditFlat;

    Button delButton;
    Button saveButton;

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor flatCursor;
    long flatId=0;
    Date date = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flat);

        editTextNameFlate = (EditText) findViewById(R.id.EditTextFio);
        editTextAddress = (EditText) findViewById(R.id.EditTextPhone);
        editTextCostFlat= (EditText) findViewById(R.id.EditTextCostFlat);
        editTextNoteFlat= (EditText) findViewById(R.id.EditTextNoteFlat);
        editTextDtEditFlat = (EditText) findViewById(R.id.EditTextNoteRent);

        delButton = (Button) findViewById(R.id.DeleteButton);
        saveButton = (Button) findViewById(R.id.SaveButton);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            flatId = extras.getLong("id");
        }
        // если 0, то добавление
        if (flatId > 0) {
            // получаем элемент по id из бд
            flatCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_Flats + " where " +
                    DatabaseHelper.COLUMN_FLATS_ID + "=?", new String[]{String.valueOf(flatId)});
            flatCursor.moveToFirst();
            // заполняем поля реактирования
            editTextNameFlate.setText(flatCursor.getString(1));
            editTextAddress.setText(flatCursor.getString(2));
            editTextCostFlat.setText(flatCursor.getString(3));
            editTextNoteFlat.setText(flatCursor.getString(4));
            editTextDtEditFlat.setText(flatCursor.getString(5));


        //    nameBox.setText(flatCursor.getString(1));
       //     yearBox.setText(String.valueOf(flatCursor.getInt(flatCursor.getColumnIndex(DatabaseHelper.COLUMN_FLATS_DtEditFlat))));
            flatCursor.close();
        } else {
            // скрываем кнопку удаления
            delButton.setVisibility(View.GONE);

           // date = new Date();
          //  DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm");
          //  String date = df.format(Calendar.getInstance().getTime());
            editTextDtEditFlat.setText(Calendar.getInstance().getTime().toString());
            //yearBox.setText(String.valueOf(date.getTime()));
        }
    }

    public void save(View view){
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_FLATS_NameFlat, editTextNameFlate.getText().toString());
        cv.put(DatabaseHelper.COLUMN_FLATS_Address, editTextAddress.getText().toString());
        cv.put(DatabaseHelper.COLUMN_FLATS_CostFlat, editTextCostFlat.getText().toString());
        cv.put(DatabaseHelper.COLUMN_FLATS_NoteFlat, editTextNoteFlat.getText().toString());
        cv.put(DatabaseHelper.COLUMN_FLATS_DtEditFlat, Calendar.getInstance().getTime().toString());

        if (flatId > 0) {
            db.update(DatabaseHelper.TABLE_Flats, cv, DatabaseHelper.COLUMN_FLATS_ID + "=" + String.valueOf(flatId), null);
        } else {
            db.insert(DatabaseHelper.TABLE_Flats, null, cv);
        }
        goHome();
    }
    public void delete(View view){
        db.delete(DatabaseHelper.TABLE_Flats, "_id = ?", new String[]{String.valueOf(flatId)});
        goHome();
    }
    private void goHome(){
        // закрываем подключение
        db.close();
        // переход к главной activity
        Intent intent = new Intent(this, ShowFlatsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}