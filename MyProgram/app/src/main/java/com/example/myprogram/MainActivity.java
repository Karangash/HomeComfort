package com.example.myprogram;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    String[] strRooms = {"Галеева 23-284", "Галеева 27-100","Галеева 27-123","Ленина 157А-34"};


    Calendar dateTimeNow = Calendar.getInstance();
    DBHelper dbHelper;
    Button   btnAdd, btnEdit, btnDelete;
    Spinner  spinnerRoom;
    EditText editTextFio;
    EditText editTextPhone;
    TextView editTextInDate;
    TextView editTextOutDate ;
    EditText editTextDuration;
    EditText editTextCost;
    CheckBox checkBoxReserv;

    int      intIdFlat;
    String   strFlatName;
    int      idRent;
    String   strFio;
    String   strPhone;
    Calendar dateTimeIn     = Calendar.getInstance();
    Calendar dateTimeOut    = Calendar.getInstance();
    int      intDuration;
    float    flCost;
    Boolean  blReserv;

    final String LOG_TAG = "myLogs";// тэг для при выводе в лог файл

  //  Date date = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // назначаем кнопкам действия

        btnAdd = (Button) findViewById(R.id.buttonAdd);
        btnAdd.setOnClickListener(this);

        btnEdit = (Button) findViewById(R.id.buttonEdit);
        btnEdit.setOnClickListener(this);

        btnDelete = (Button) findViewById(R.id.buttonDelete);
        btnDelete.setOnClickListener(this);

        // создаем переменные для хранения значений полей

        /*id
        idFlat
        idQuest
        inDateTime
        outDateTime
        duration
        reserv
        cost
        noteRent
        ratingRent
        idCloudRent
        dtEditRent*/

                // создаем объект для создания и управления версиями БД
        dbHelper = new DBHelper(this);


        editTextFio = (EditText) findViewById(R.id.editTextFio);
        editTextPhone  = (EditText) findViewById(R.id.editTextPhone);
        // находим редактор даты
        editTextInDate = (TextView) findViewById(R.id.textViewInDate);
        editTextOutDate = (TextView) findViewById(R.id.textViewOutDate);
        editTextDuration = (EditText) findViewById(R.id.editTextDuration);
        editTextCost = (EditText) findViewById(R.id.editTextCost);;
        checkBoxReserv = (CheckBox) findViewById(R.id.checkBoxReserv);;

        // устанавливаем начальное значение даты
        setInitialDate();

      // создаем адаптер для списка
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, strRooms);
        // указываем какой layout использовать для прорисовки пунктов выпадающего списка
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Находим спиннер
        spinnerRoom = (Spinner) findViewById(R.id.spinnerRoom);
        // передаем спиннеру адаптер с списком мест проживания
        spinnerRoom.setAdapter(arrayAdapter);
        // выделяем элемент
        // spinnerRoom.setSelection(2);
        // устанавливаем обработчик выбора из списка нажатия
        spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Position = " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void setInitialDate() {
      //  SimpleDateFormat df = new SimpleDateFormat("dd/MM/YYYY");
      //  editTextDate.setText(df.format(date));
        editTextInDate.setText(DateUtils.formatDateTime(this,
                dateTimeIn.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR ));
        editTextOutDate.setText(DateUtils.formatDateTime(this,
                dateTimeOut.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR ));

    }

    // установка обработчика выбора даты заезда
    DatePickerDialog.OnDateSetListener datePickerDialogIn = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateTimeIn.set(Calendar.YEAR, year);
            dateTimeIn.set(Calendar.MONTH, monthOfYear);
            dateTimeIn.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDate();
        }
    };
    // установка обработчика выбора даты выезда
    DatePickerDialog.OnDateSetListener datePickerDialogOut = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
             dateTimeOut.set(Calendar.YEAR, year);
             dateTimeOut.set(Calendar.MONTH, monthOfYear);
             dateTimeOut.set(Calendar.DAY_OF_MONTH, dayOfMonth);
             setInitialDate();
        }
    };

// отображаем диалоговое окно для выбора даты
    public void setDate(View view) {

        switch (view.getId()) {
            case R.id.textViewInDate:  // установка даты заезда
                new DatePickerDialog(MainActivity.this, datePickerDialogIn,
                        dateTimeIn.get(Calendar.YEAR),
                        dateTimeIn.get(Calendar.MONTH),
                        dateTimeIn.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;
            case R.id.textViewOutDate: // установка даты выезда
                new DatePickerDialog(MainActivity.this, datePickerDialogOut,
                        dateTimeOut.get(Calendar.YEAR),
                        dateTimeOut.get(Calendar.MONTH),
                        dateTimeOut.get(Calendar.DAY_OF_MONTH))
                        .show();
                break;
            default:
                break;
        };
    }


    @Override // обработка нажатий на кнопки
    public void onClick(View v) {



        // получаем данные из полей
        //id
        intIdFlat = spinnerRoom.getSelectedItemPosition();
        strFlatName = spinnerRoom.getSelectedItem().toString();
        //idRent;
    //    strFio = editTextFio.getText().toString() ;
    //    strPhone = editTextPhone.getText().toString();
       // dateTimeIn =
       // dateTimeOut
    //    intDuration = Integer.parseInt( editTextDuration.getText().toString());
    //    flCost = Float.parseFloat(editTextCost.getText().toString());
    //    blReserv = checkBoxReserv.isChecked();

        // подключаемся к БД
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // создаем объект для данных

        switch (v.getId()) {
            case R.id.buttonAdd:
                Log.d(LOG_TAG, "--- Insert in flat: ---");
                // проверка существования записей
                String selection = "_id = ?";
                String[] selectionArgs = new String[] { String.valueOf(intIdFlat) };
                Cursor c = db.query("flat", null, selection,selectionArgs, null, null, null);
                if (c.getCount() == 0){ // если нет квартиры с данным id
                    ContentValues cv = new ContentValues();
                    cv.put("flatName", strFlatName );
                    cv.put("address", strFlatName );
                    cv.put("note", "Примечание1" );
                    cv.put("dtEditFlat",String.valueOf(dateTimeNow));
                    Long loIdFlat = db.insert("flat", null, cv);
                    Log.d(LOG_TAG, "id = " + loIdFlat);
                }


                break;
            case R.id.buttonEdit:



                break;
            case R.id.buttonDelete:



                break;



        }






    }
}
