package com.example.myapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class EditGuestActivity extends AppCompatActivity  {

    EditText editTextFio;
    EditText editTextPhone;
    EditText editTextRaitingGuest;
    EditText editTextNoteGuest;
    EditText editTextDtEditGuest;

    Button delButton;
    Button saveButton;

    DatabaseHelper sqlHelper;
    SQLiteDatabase db;
    Cursor guestCursor;
    long guestId=0;
    Date date = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_guest);
        editTextFio          = (EditText) findViewById(R.id.EditTextFio);
        editTextPhone        = (EditText) findViewById(R.id.EditTextPhone);
        FormatWatcher formatWatcher = new MaskFormatWatcher(
                MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER) // маска для номера телефона
        );
        formatWatcher.installOn(editTextPhone);
        editTextPhone.setText("+7 (");
        editTextRaitingGuest = (EditText) findViewById(R.id.EditTextCostFlat);
        editTextNoteGuest    = (EditText) findViewById(R.id.EditTextNoteRent);
        editTextDtEditGuest  = (EditText) findViewById(R.id.EditTextDtEditGuest);

        delButton = (Button) findViewById(R.id.DeleteButton);
        saveButton = (Button) findViewById(R.id.SaveButton);

        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            guestId = extras.getLong("id");
        }
        // если 0, то добавление
        if (guestId > 0) {
            // получаем элемент по id из бд
            guestCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_Guests + " where " +
                    DatabaseHelper.COLUMN_GUEST_ID + "=?", new String[]{String.valueOf(guestId)});
            guestCursor.moveToFirst();
            // заполняем поля реактирования
            editTextFio.setText(guestCursor.getString(1));
            editTextPhone.setText(guestCursor.getString(2));
            editTextRaitingGuest.setText(guestCursor.getString(3));
            editTextNoteGuest.setText(guestCursor.getString(4));
          //  editTextDtEditGuest.setText(guestCursor.getString(5));
            Log.d("myLogs", "--- В таблице Guest ---" + guestCursor.getString(5) );
          //  editTextDtEditGuest.setText(String.valueOf(guestCursor.getInt(guestCursor.getColumnIndex(DatabaseHelper.COLUMN_GUEST_DtEditGuest))));


            //    nameBox.setText(guestCursor.getString(1));
            //     yearBox.setText(String.valueOf(guestCursor.getInt(guestCursor.getColumnIndex(DatabaseHelper.COLUMN_FLATS_DtEditFlat))));
            guestCursor.close();
        } else {
            // скрываем кнопку удаления
            delButton.setVisibility(View.GONE);

            // date = new Date();
            //  DateFormat df = new SimpleDateFormat("d MMM yyyy, HH:mm");
            //  String date = df.format(Calendar.getInstance().getTime());
            //editTextDtEditGuest.setText(Calendar.getInstance().getTime().toString());
            //yearBox.setText(String.valueOf(date.getTime()));
        }
    }

    public void save(View view){

        // проверяем корректность ввода мобильного номера
        if (editTextPhone.getText().toString().trim().length() < 18 )  {
            Toast.makeText(getBaseContext(), "Не введен полный номер телефона" , Toast.LENGTH_LONG).show();
            return;
        }

        // проверяем заполнеие ФИО
        if (!(editTextFio.getText().toString().trim().length() > 1)) {
            Toast.makeText(getBaseContext(), "Введите имя" , Toast.LENGTH_LONG).show();
            return;
        };

        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_GUEST_Fio, editTextFio.getText().toString());
        cv.put(DatabaseHelper.COLUMN_GUEST_Phone, editTextPhone.getText().toString());
        cv.put(DatabaseHelper.COLUMN_GUEST_RaitingGuest, editTextRaitingGuest.getText().toString());
        cv.put(DatabaseHelper.COLUMN_GUEST_NoteGuest, editTextNoteGuest.getText().toString());
        cv.put(DatabaseHelper.COLUMN_GUEST_DtEditGuest, Calendar.getInstance().getTime().toString());

        if (guestId > 0) {
            db.update(DatabaseHelper.TABLE_Guests, cv, DatabaseHelper.COLUMN_GUEST_ID + "=" + String.valueOf(guestId), null);
        } else {
            db.insert(DatabaseHelper.TABLE_Guests, null, cv);
        }
        goHome();
    }

    public void delete(View view){

        // проверяем есть ли записи с данным пользователем в таблице аренды квартир
        //получаем данные из бд в виде курсора
        String strinSQL = "SELECT * FROM "+ DatabaseHelper.TABLE_Rents
                +" where "+ DatabaseHelper.COLUMN_RENTS_IdGuest + " = "+ guestId +" ;";
        Cursor cur  = db.rawQuery(strinSQL, null);
        // если количество записей больше 0 то имеются записи с данным гостем - нельзя удалять
        if (cur.getCount() > 0 ){
            Toast.makeText(getBaseContext(), "Удалить нельзя! Имеются связанные записи. " , Toast.LENGTH_LONG).show();
            return;
        }

        db.delete(DatabaseHelper.TABLE_Guests, "_id = ?", new String[]{String.valueOf(guestId)});
        goHome();
    }

    private void goHome(){

        // закрываем подключение
        db.close();
        // переход к главной activity
        Intent intent = new Intent(this, ShowGuestActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }



}