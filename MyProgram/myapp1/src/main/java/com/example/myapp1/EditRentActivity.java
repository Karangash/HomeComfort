package com.example.myapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

import static com.example.myapp1.R.id.EditTextDuration;
import static com.example.myapp1.R.id.EditTextNoteRent;
import static com.example.myapp1.R.id.end;
import static com.example.myapp1.R.id.fabSaveRent;

public class EditRentActivity extends AppCompatActivity {


    private Button delButton;
    private Button saveButton;
    private Button buttonSelectGuest;
    FloatingActionButton fabDeleteRent;
    FloatingActionButton fabSaveRent;

    private AutoCompleteTextView acEditTextViewFIO;

    private DatabaseHelper sqlHelper;
    private SQLiteDatabase db;

    private EditText editTextFio;
    private EditText editTextPhone;
    private EditText editTextCostFlat;
    private EditText editTextCostSum;
    private EditText editTextDtIn;
    private EditText editTextDtOut;
    private EditText editTextDuration;
    private Spinner spinnerNameFlat;
    private Spinner spinnerRaiting;
    private CheckBox checkBoxReserv;
    private EditText editTextNoteRent;


    private long idGuest= 0;
    private long idRent = 0;
    private int idFlat = 0;
    private int duration = 0;
    private long differenceDay; // разница в днях
    private long differenceHour; // разница в часах
    private float costFlat;
    private float costSum;
    private int raitingRent ;
    private int changeCountFlatId = 0 ;
    private Dialog dialogInDateTime,dialogOutDateTime;
    private DatePicker dpIn,dpOut;
    private TimePicker tpIn,tpOut;
    private AlertDialog.Builder alertDialog;



    private Button btTpIn,btTpOut;


    private Calendar calendar       = Calendar.getInstance();
    private Calendar beginCalendar  = Calendar.getInstance();
    private Calendar endCalendar    = Calendar.getInstance();
    private Calendar beginDayTime  = Calendar.getInstance();
    private Calendar endDayTime    = Calendar.getInstance();


    private Cursor guestCursor;
    private Cursor flatCursor;
    private Cursor rentCursor;
    private SimpleCursorAdapter flatAdapter, guestAdapter;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rent);



//        calendar.set(Calendar.HOUR, 00);
//        calendar.set(Calendar.MINUTE, 00);
//        calendar.set(Calendar.SECOND, 00);
//        calendar.set(Calendar.MILLISECOND, 000);


        spinnerNameFlat = (Spinner) findViewById(R.id.SpinnerNameFlat);
        delButton = (Button) findViewById(R.id.DeleteButton);
        saveButton = (Button) findViewById(R.id.SaveButton);
        fabSaveRent = findViewById(R.id.fabSaveRent);
        fabDeleteRent = findViewById(R.id.fabDeleteRent);

        buttonSelectGuest = (Button) findViewById(R.id.buttonSelectGuest);
        editTextFio = (EditText) findViewById(R.id.EditTextFio);
        editTextFio.setText("");
        editTextPhone = (EditText) findViewById(R.id.EditTextPhone);
        editTextPhone.setText("+7 (");

        // устанавливаем маску ввода телефонного номера
        final MaskImpl mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
       // mask.setShowingEmptySlots(true);
       // mask.setHideHardcodedHead(true); // default value
       // mask.setForbidInputWhenFilled(true);

        FormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        formatWatcher.installOn(editTextPhone);

        editTextCostFlat = (EditText) findViewById(R.id.EditTextCostFlat);
        editTextCostSum = (EditText) findViewById(R.id.EditTextCostSum);
        editTextDtIn = (EditText) findViewById(R.id.EditTextDtIn);
        editTextDtOut = (EditText) findViewById(R.id.EditTextDtOut);
        editTextDuration = (EditText) findViewById(EditTextDuration);
        spinnerRaiting = (Spinner) findViewById(R.id.SpinnerRaiting);
        //-------- заполняем список рейтинга проживания-----------
        spinnerRaiting.setSelection(2);
        checkBoxReserv = (CheckBox) findViewById(R.id.checkBoxReserv);
        editTextNoteRent = (EditText) findViewById(EditTextNoteRent);



        sqlHelper = new DatabaseHelper(this);
        db = sqlHelper.getWritableDatabase();

        // получаем начальные значения
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idRent = extras.getLong("idRent",0);
            idFlat = extras.getInt("idFlat",0);;
            idGuest = extras.getInt("idGuest",0);
            calendar.setTimeInMillis(extras.getLong("calendar", calendar.getTimeInMillis() ));
            Log.d("myLogs", "---Получено время  " + calendar.getTime() );
        }

        // устанавливаем время заезда по умолчанию 13:00- заезд 12:00- выезд
       // время заезда 13 часов
        beginCalendar.setTimeInMillis( calendar.getTimeInMillis());
        beginCalendar.set(Calendar.HOUR_OF_DAY, 13);
        beginCalendar.set(Calendar.MINUTE, 00);
        beginCalendar.set(Calendar.SECOND, 00);
        beginCalendar.set(Calendar.MILLISECOND, 000);
        Log.d("myLogs", "---beginCalendar  " + beginCalendar.getTime() );
         // время выезда 12 часов
        endCalendar.setTimeInMillis( calendar.getTimeInMillis());
        endCalendar.add(Calendar.DATE, 1); // прибавляем день т.е. выезд на следующий день
        endCalendar.set(Calendar.HOUR_OF_DAY, 12);
        endCalendar.set(Calendar.MINUTE, 00);
        endCalendar.set(Calendar.SECOND, 00);
        endCalendar.set(Calendar.MILLISECOND, 00);
        Log.d("myLogs", "---endCalendar  " + endCalendar.getTime() );
        // время начала дня устанавливаем 00 часов
        beginDayTime.setTimeInMillis(beginCalendar.getTimeInMillis());
        beginDayTime.set(Calendar.HOUR_OF_DAY, 00);
        // время конца дня устанавливаем 23:59 часов
        endDayTime.setTimeInMillis(endCalendar.getTimeInMillis());
        endDayTime.set(Calendar.HOUR_OF_DAY, 23);
        endDayTime.set(Calendar.MINUTE, 59);
        endDayTime.set(Calendar.SECOND, 59);
        endDayTime.set(Calendar.MILLISECOND, 99);


        //--------------- завполняем список квартир----------
        //получаем данные из бд в виде курсора
        flatCursor =  db.rawQuery("select * from "+ DatabaseHelper.TABLE_Flats, null);
        // определяем, какие столбцы из курсора будут выводиться в ListView
        String[] flatHeaders = new String[] {DatabaseHelper.COLUMN_FLATS_NameFlat, DatabaseHelper.COLUMN_FLATS_CostFlat};
        // создаем адаптер, передаем в него курсор
        flatAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                flatCursor, flatHeaders, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        spinnerNameFlat.setAdapter(flatAdapter);
        // учтанавливаем значение места проживания
        //spinnerNameFlat.setSelection(idFlat);





    //---------- если есть код гостя то выводим фамилию и номер телефона

        if (idRent > 0) { // то редактирование
            // получаем элемент по id из бд
            String strinSQL = "SELECT *, r._id as _id  FROM "+ DatabaseHelper.TABLE_Rents + " as r "
                    +" INNER JOIN " + DatabaseHelper.TABLE_Flats + " as f"
                    +" ON r.idFlat = f._id "
                    +" INNER JOIN " + DatabaseHelper.TABLE_Guests + " as g "
                    +" ON r.idGuest = g._id "
                    +" where r._id = " + idRent;

            //rentCursor.close();
            rentCursor = db.rawQuery(strinSQL, null);
            rentCursor.moveToFirst();
            Log.d("myLogs", "--- В таблице rent найдено записей---" + rentCursor.getCount() );
            if (rentCursor.getCount()>0) {

                // получаем значения из таблицы
                Log.d("myLogs", "--- IdFlat ---= " + rentCursor.getInt(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_RENTS_IdFlat)));
                idFlat = rentCursor.getInt(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_RENTS_IdFlat));
                // переменная для определения первого заполнения выбранной квартиры для правильного заполнения стоимости квартиры при редактировании
                changeCountFlatId = 0;
               // spinnerNameFlat.setId(idFlat+1); //в спиннере устанавливаем записанную квартиру
                spinnerNameFlat.setSelection(idFlat-1);
                spinnerRaiting.setSelection(rentCursor.getInt(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_RENTS_RatingRent)));
                idGuest = rentCursor.getInt(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_RENTS_IdGuest));
                differenceDay = rentCursor.getInt(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_RENTS_Duration));
                costSum = rentCursor.getFloat(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_RENTS_Cost));
                //costFlat = costSum/differenceDay;
               // заполняем поля для редактирования
                editTextFio.setText(rentCursor.getString(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_GUEST_Fio)));
                editTextPhone.setText(rentCursor.getString(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_GUEST_Phone)));
                beginCalendar.setTimeInMillis(rentCursor.getLong(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_RENTS_DtIn)));
                endCalendar.setTimeInMillis(rentCursor.getLong(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_RENTS_DtOut)));
                // editTextDuration.setText(Long.toString(differenceDay));
                //editTextCostFlat.setText(String.valueOf(costFlat));
                //editTextCostSum.setText(String.valueOf(costSum));
                editTextNoteRent.setText(rentCursor.getString(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_RENTS_NoteRent)));

                if (rentCursor.getInt(rentCursor.getColumnIndex(DatabaseHelper.COLUMN_RENTS_Reserv)) == 0) {
                    checkBoxReserv.setChecked(false);
                } else {
                    checkBoxReserv.setChecked(true);
                }
            }
            else {

            }
           // guestCursor.close();
        } else { // добавление новой записи
            // при добавлении новой записи изначально общая сумма проживания ровна стоимости одного дня проживания
            costSum = flatCursor.getFloat(flatCursor.getColumnIndex(DatabaseHelper.COLUMN_FLATS_CostFlat));
            // уствнвка начального значения резерва
            setCheckBoxReserv(beginCalendar);;
            // скрываем кнопку удаления
            delButton.setVisibility(View.GONE);
            fabDeleteRent.hide();
            //----- выводим начальное дата и время заезда и выезда
            changeCountFlatId = 1;
            setDateTimeToEditText();
        }


        // ------при потере фокуса с ввода телефонного номера ищем существующий
        editTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) { // если потерял фокус
                    // поиск номера телефона в базе гостей
                    guestCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_Guests + " where " +
                            DatabaseHelper.COLUMN_GUEST_Phone + "=?", new String[]{editTextPhone.getText().toString()});

                    if (guestCursor.getCount() > 0) {
                        guestCursor.moveToFirst();// заполняем поле имени из базы
                        editTextFio.setText(guestCursor.getString(guestCursor.getColumnIndex(DatabaseHelper.COLUMN_GUEST_Fio)));
                        idGuest = guestCursor.getLong(guestCursor.getColumnIndex(DatabaseHelper.COLUMN_GUEST_ID));
                        editTextFio.setEnabled(false);
                    } else {
                        idGuest = 0;
                        editTextFio.setEnabled(true);
                        editTextFio.setText(null);
                    }
                }
            }
        });


        //////////////////////// Рабора с батой и временем заезда и выезда
        // Формируем диалоговое окно для выбора даты и времени
 /*       dialogInDateTime = new Dialog(this);
        dialogInDateTime.setContentView(R.layout.date_time_select);

        dialogOutDateTime = new Dialog(this);
        dialogOutDateTime.setContentView(R.layout.date_time_select);

        // обработка выбора времени
 /*       dpIn = (DatePicker)dialogInDateTime.findViewById(R.id.datePicker1);
        tpIn = (TimePicker)dialogInDateTime.findViewById(R.id.timePicker1);

        tpOut = (TimePicker)dialogOutDateTime.findViewById(R.id.timePicker1);
        dpOut = (DatePicker)dialogOutDateTime.findViewById(R.id.datePicker1);
*/
// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        // ------------обработчик нажатий на дату и время для Выбор даты и времени

        // установка обработчика выбора времени заезда
        final TimePickerDialog.OnTimeSetListener tIn = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                beginCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                beginCalendar.set(Calendar.MINUTE, minute);
                // проверяем если дата заезда раньше даты выезда
                // если дата выезда раньше даты заезда то меняем дату выезда
                if (beginCalendar.getTimeInMillis()>endCalendar.getTimeInMillis()) {
                    // прибавляем количество дней проживания  к дате заезда
                    endCalendar.set(Calendar.YEAR,beginCalendar.get(Calendar.YEAR) );
                    endCalendar.set(Calendar.MONTH,beginCalendar.get(Calendar.MONTH));
                    endCalendar.set(Calendar.DAY_OF_MONTH,beginCalendar.get(Calendar.DAY_OF_MONTH) );
                    endCalendar.add(Calendar.DATE, (int)differenceDay);
                    Toast.makeText(getBaseContext(),"Дата выезда исправлена, т.к. дата заезда позже даты выезда ", Toast.LENGTH_LONG).show();
                };
                setDateTimeToEditText();
                setCheckBoxReserv(beginCalendar);
                //();
            }
        };
        // установка обработчика выбора даты заезда
        final DatePickerDialog.OnDateSetListener dIn = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                beginCalendar.set(Calendar.YEAR, year);
                beginCalendar.set(Calendar.MONTH, monthOfYear);
                beginCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // setInitialDateTime();
               // вызываем окно выбора времени заезда
                new TimePickerDialog(EditRentActivity.this, tIn,
                        beginCalendar.get(Calendar.HOUR_OF_DAY),
                        beginCalendar.get(Calendar.MINUTE), true)
                        .show();

            }
        };

        // вызываем диалог выбора дата и время заезда
        editTextDtIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new DatePickerDialog(EditRentActivity.this, dIn,
                        beginCalendar.get(Calendar.YEAR),
                        beginCalendar.get(Calendar.MONTH),
                        beginCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();

            }
        });
         // ------------------------------------------------------------------------

        // установка обработчика выбора времени выезда
        final TimePickerDialog.OnTimeSetListener tOut = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                endCalendar.set(Calendar.MINUTE, minute);
                // проверяем если дата выезда раньше даты заезда
                if (beginCalendar.getTimeInMillis() < endCalendar.getTimeInMillis()) {
                    setDateTimeToEditText();
                }
                else{
                    Toast.makeText(getBaseContext(),"Дата и время выезда раньше даты заезда ", Toast.LENGTH_LONG).show();
                    return;
                };
                setDateTimeToEditText();
                setCheckBoxReserv(beginCalendar);
                //();
            }
        };

        // установка обработчика выбора даты выезда
        final DatePickerDialog.OnDateSetListener dOut = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                endCalendar.set(Calendar.YEAR, year);
                endCalendar.set(Calendar.MONTH, monthOfYear);
                endCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                // setInitialDateTime();
                // вызываем окно выбора времени заезда
                new TimePickerDialog(EditRentActivity.this, tOut,
                        endCalendar.get(Calendar.HOUR_OF_DAY),
                        endCalendar.get(Calendar.MINUTE), true)
                        .show();

            }
        };

        // дата и время выезда
        editTextDtOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditRentActivity.this, dOut,
                        endCalendar.get(Calendar.YEAR),
                        endCalendar.get(Calendar.MONTH),
                        endCalendar.get(Calendar.DAY_OF_MONTH))
                        .show();
            }
        });
// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/*
        // ----------кнопка выбора даты и времени заезда, действия при нажатии выбрать
        btTpIn = (Button) dialogInDateTime.findViewById(R.id.buttonSelect);
        btTpIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // устанавливаем выбранный день и время
                beginCalendar.set(Calendar.YEAR,dpIn.getYear() );
                beginCalendar.set(Calendar.MONTH,dpIn.getMonth());
                beginCalendar.set(Calendar.DAY_OF_MONTH,dpIn.getDayOfMonth() );
                beginCalendar.set(Calendar.HOUR_OF_DAY, tpIn.getHour() );
                beginCalendar.set(Calendar.MINUTE,tpIn.getMinute());
                beginCalendar.set(Calendar.SECOND, 00);
                beginCalendar.set(Calendar.MILLISECOND, 000);
                //Log.d("myLogs", "---Выброна дата и время заезда = "+beginCalendar.getTime());

                // проверяем если дата заезда раньше даты выезда
                // если дата выезда раньше даты заезда то меняем дату выезда
                if (beginCalendar.getTimeInMillis()>endCalendar.getTimeInMillis()) {
                    // прибавляем количество дней проживания  к дате заезда
                    endCalendar.set(Calendar.YEAR,beginCalendar.get(Calendar.YEAR) );
                    endCalendar.set(Calendar.MONTH,beginCalendar.get(Calendar.MONTH));
                    endCalendar.set(Calendar.DAY_OF_MONTH,beginCalendar.get(Calendar.DAY_OF_MONTH) );
                    endCalendar.add(Calendar.DATE, (int)differenceDay);
                    Toast.makeText(getBaseContext(),"Дата выезда исправлена, т.к. дата заезда позже даты выезда ", Toast.LENGTH_LONG).show();
                };
                setDateTimeToEditText();
                setCheckBoxReserv(beginCalendar);
                dialogInDateTime.cancel();

            }
        });

        // кнопка выбора баты и времени выезда
        btTpOut = (Button) dialogOutDateTime.findViewById(R.id.buttonSelect);
        btTpOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endCalendar.set(Calendar.YEAR,dpOut.getYear() );
                endCalendar.set(Calendar.MONTH,dpOut.getMonth());
                endCalendar.set(Calendar.DAY_OF_MONTH,dpOut.getDayOfMonth() );
                endCalendar.set(Calendar.HOUR_OF_DAY,tpOut.getHour() );
                endCalendar.set(Calendar.MINUTE,tpOut.getMinute());
                endCalendar.set(Calendar.SECOND, 00);
                endCalendar.set(Calendar.MILLISECOND, 000);
               // Log.d("myLogs", "---Выброна дата и время выезда = "+endCalendar.getTime());

                // проверяем если дата заезда раньше даты выезда
                if (beginCalendar.getTimeInMillis()<endCalendar.getTimeInMillis()) {

                    setDateTimeToEditText();
                    dialogOutDateTime.cancel();
                }
                else{
                    Toast.makeText(getBaseContext(),"Дата и время выезда раньше даты заезда ", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

  */
        //////////////////////////////////////////////////
        //--------------------- обрабатываем выбор квартиры


        spinnerNameFlat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                idFlat = (int)id; //код выбранной квартиры
                costFlat = flatCursor.getFloat(flatCursor.getColumnIndex(DatabaseHelper.COLUMN_FLATS_CostFlat));
               // costSum =
                setDateTimeToEditText();

              //  Toast.makeText(getBaseContext(), "Position = " + position +"  id = "+id, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // обработка потери фокуса, редактирования количество дней проживания
        editTextDuration.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) { // если потерял фокус

                    try{
                        //String str = editTextCostSum.getText().toString();
                        differenceDay = Integer.valueOf(editTextDuration.getText().toString());
                        // расчитываем дату прибавиви к начальной дате количество дней
                        endCalendar.set(Calendar.YEAR,beginCalendar.get(Calendar.YEAR) );
                        endCalendar.set(Calendar.MONTH,beginCalendar.get(Calendar.MONTH));
                        endCalendar.set(Calendar.DAY_OF_MONTH,beginCalendar.get(Calendar.DAY_OF_MONTH) );
                        endCalendar.add(Calendar.DATE, (int)differenceDay); // прибавляем дни
                        setDateTimeToEditText();

                    }

                    catch(Exception e) {
                        System.out.println("Exception: "+ e);
                    }
                }
            }
        });


// обработка потери фокуса, редактирования стоимости проживания
        editTextCostFlat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) { // если потерял фокус

                    try{
                        //String str = editTextCostSum.getText().toString();

                        costFlat = Float.parseFloat(editTextCostFlat.getText().toString());
                        setDateTimeToEditText();

                    }

                    catch(Exception e) {
                        System.out.println("Exception: "+ e);
                    }
                }
            }
        });


        // обработка потери фокуса, редактирование суммы проживания
        editTextCostSum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) { // если потерял фокус

                    try{
                        //String str = editTextCostSum.getText().toString();

                        costFlat = Float.parseFloat(editTextCostSum.getText().toString())/differenceDay;
                        setDateTimeToEditText();

                    }

                    catch(Exception e) {
                        System.out.println("Exception: "+ e);
                    }
                }
            }
        });
/////////----------  Сохранение или добавление записи аренды


        fabSaveRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
*/
                // проверяем корректность ввода мобильного номера
                if (editTextPhone.getText().toString().trim().length() < 18 )  {
                    Toast.makeText(getBaseContext(), "Не введен полный номер телефона" , Toast.LENGTH_LONG).show();
                    return;
                }

                // проверяем заполнеие ФИО
                if (!(editTextFio.getText().toString().trim().length() > 1)) {
                    Toast.makeText(getBaseContext(), "Введите или выберите имя" , Toast.LENGTH_LONG).show();
                    return;
                };

                // проверяем занятость квартиры в данном промежутке времени
                if (chekRentInOccupation(idRent, idFlat, beginCalendar, endCalendar )) {
                     return;
                };

                // добавляем нового жильца если он не выбран из базы
                if (idGuest == 0){
                    ContentValues cvGuest = new ContentValues();
                    cvGuest.put(DatabaseHelper.COLUMN_GUEST_Fio, editTextFio.getText().toString());
                    cvGuest.put(DatabaseHelper.COLUMN_GUEST_Phone, editTextPhone.getText().toString());
                    cvGuest.put(DatabaseHelper.COLUMN_GUEST_RaitingGuest, 2);
                    cvGuest.put(DatabaseHelper.COLUMN_GUEST_DtEditGuest, Calendar.getInstance().getTime().toString());

                    idGuest =  db.insert(DatabaseHelper.TABLE_Guests, null, cvGuest);
                  //  Log.d("myLogs", "---Добавлена запись _Id = " + idGuest );
                }

                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.COLUMN_RENTS_IdFlat,      idFlat);
                cv.put(DatabaseHelper.COLUMN_RENTS_IdGuest,     idGuest);
                cv.put(DatabaseHelper.COLUMN_RENTS_DtIn,        beginCalendar.getTimeInMillis());
                cv.put(DatabaseHelper.COLUMN_RENTS_DtOut,       endCalendar.getTimeInMillis());
                cv.put(DatabaseHelper.COLUMN_RENTS_Duration,    differenceDay);
                cv.put(DatabaseHelper.COLUMN_RENTS_Cost,        costSum);
                cv.put(DatabaseHelper.COLUMN_RENTS_NoteRent,    editTextNoteRent.getText().toString());
                cv.put(DatabaseHelper.COLUMN_RENTS_RatingRent,  spinnerRaiting.getSelectedItemId());
                //Log.d("myLogs", "---рейтинг Id = " + spinnerRaiting.getSelectedItemId() );
                if (checkBoxReserv.isChecked() == false){
                    cv.put(DatabaseHelper.COLUMN_RENTS_Reserv,  0);
                }
                else {
                    cv.put(DatabaseHelper.COLUMN_RENTS_Reserv,  1);
                };
                cv.put(DatabaseHelper.COLUMN_RENTS_dtEditRent,  Calendar.getInstance().toString());
                if (idRent > 0) {
                    db.update(DatabaseHelper.TABLE_Rents, cv, DatabaseHelper.COLUMN_RENTS_ID + "=" + String.valueOf(idRent), null);
                } else {
                    idRent = db.insert(DatabaseHelper.TABLE_Rents, null, cv);
                    //Log.d("myLogs", "---Добавлена запись _Id = " + idRent );
                };
                goHome();
            }
        });


        // Удаляем запись проживания
        fabDeleteRent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // формируем запрос на подтверждение удаления
                alertDialog = new AlertDialog.Builder(EditRentActivity.this);
                alertDialog.setTitle("Удаление проживания"); // заголовок
                alertDialog.setMessage("Удалить запись!"); // сообщение
                alertDialog.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        // удаляем запись
                        db.delete(DatabaseHelper.TABLE_Rents, "_id = ?", new String[]{String.valueOf(idRent)});
                        idRent = 0;
                        goHome();

                    }
                });
                alertDialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {

                    }
                });
                alertDialog.show();
            }
        });
       //  ----------- удаление записи
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.delete(DatabaseHelper.TABLE_Rents, "_id = ?", new String[]{String.valueOf(idRent)});
                idRent = 0;
                goHome();
            }
        });
    }

    // по нажатию на кнопку запускаем activity выбора гостя
    public void addGuestButtonClick(View view){
        Intent intent = new Intent(this, ShowGuestActivity.class);
        intent.putExtra("idGuest", idGuest);
        intent.putExtra("intActionView", 1);
        startActivityForResult(intent, 1);
    }


    // получение данных гостя из списка гостей возвращаемая ShowGuestActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        idGuest = data.getIntExtra("idGuest",0);

        if (idGuest > 0) {// ищев выбранного пользователя в таблице и заполняем поле для редактирования
            // получаем элемент по id из бд
            guestCursor = db.rawQuery("select * from " + DatabaseHelper.TABLE_Guests + " where " +
                    DatabaseHelper.COLUMN_GUEST_ID + "=?", new String[]{String.valueOf(idGuest)});
            guestCursor.moveToFirst();
            // заполняем поля реактирования
            editTextFio.setText(guestCursor.getString(guestCursor.getColumnIndex(DatabaseHelper.COLUMN_GUEST_Fio)));
            editTextPhone.setText(guestCursor.getString(guestCursor.getColumnIndex(DatabaseHelper.COLUMN_GUEST_Phone)));
        }
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключение и курсор
        /*guestCursor.close();
        flatCursor.close();
        rentCursor.close();
        db.close();
         */
    }


// заполняем дату, время, сумму проживания значениями
    private void setDateTimeToEditText() {
        // задаем время конца дня выезда для расчета количесуа дней проживания
        endDayTime.setTimeInMillis(endCalendar.getTimeInMillis());
        endDayTime.set(Calendar.HOUR, 23);
        endDayTime.set(Calendar.MINUTE, 59);
        endDayTime.set(Calendar.SECOND, 59);
        endDayTime.set(Calendar.MILLISECOND, 99);

        // ---------------- вычисляем длительность проживания
        long difference = endDayTime.getTimeInMillis() - beginCalendar.getTimeInMillis();
        // при начальном заполнении полей при редактировании не вызываем расчет дней и суммы
        if (changeCountFlatId > 0){
            differenceDay = difference /(24*60 * 60 * 1000);
            //differenceHour = difference /(60 * 60 * 1000);
            //Log.d("myLogs", "--- разница дат  ---дней" + differenceDay + "  часов "+ differenceHour);
            // если количество дней проживания больше одного
            if (differenceDay>0 ){
                costSum = costFlat * differenceDay;
            }
        }
        else{

            costFlat = costSum/differenceDay;

        }


        changeCountFlatId=changeCountFlatId+1;


        editTextDuration.setText(Long.toString(differenceDay));
        editTextCostFlat.setText(String.valueOf(costFlat));
        editTextCostSum.setText(String.valueOf(costSum));

        editTextDtIn.setText(dateFormat.format(beginCalendar.getTime()));
        editTextDtOut.setText(dateFormat.format(endCalendar.getTime()));

    }

    // устанавливаем бронь если дата заезда позже следующего дня
    private void setCheckBoxReserv(Calendar bCalendar){
        Calendar bcal = Calendar.getInstance();
        bcal.add(Calendar.DATE, 1); // прибавляем день к текущему
        bcal = setHourInDayToCalendar (bcal, 12);
        // если дата заезда позже 12 следующего дня то бронь
        if  (bcal.getTimeInMillis() <  bCalendar.getTimeInMillis()){
            checkBoxReserv.setChecked(true);
        }
        else{
            checkBoxReserv.setChecked(false);
        }
    }


    // устанавливаем время в календаре на начал дня
    private Calendar setHourInDayToCalendar (Calendar cal, int hour){
         Calendar bcal = Calendar.getInstance();
         bcal.setTimeInMillis(cal.getTimeInMillis());
         bcal.set(Calendar.HOUR_OF_DAY, hour);
         bcal.set(Calendar.MINUTE, 00);
         bcal.set(Calendar.SECOND, 00);
         bcal.set(Calendar.MILLISECOND, 000);
         return bcal;
    }




    private void goHome(){
        // закрываем подключение
     //   db.close();
        // переход к главной activity
       // Intent intent = new Intent(this, ShowRentsActivity.class);
      //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
      //  startActivity(intent);

        Intent intent = new Intent();
        intent.putExtra("idR",idRent);
        intent.putExtra("calendar",beginCalendar.getTimeInMillis());
        setResult(RESULT_OK, intent);

        finish();
    }

    // проверяем свободна ли на данное время квартира для заселения
    protected Boolean  chekRentInOccupation (long idRe, int idFl, Calendar begCal, Calendar endCal ){
        //получаем данные из бд в виде курсора
       final String strinSQL = "SELECT * FROM "+ DatabaseHelper.TABLE_Rents
               +" where ( idFlat = " + idFl +") and ( _id <> " + (int)idRe +")"
                     +" and (( dtIn <= "+ begCal.getTimeInMillis()+"  and dtOut >= "+ begCal.getTimeInMillis()+" ) "
                     +" or ( dtIn <= "+ endCal.getTimeInMillis()+"  and dtOut >= "+ endCal.getTimeInMillis()+" )) ";


       final Cursor cur  = db.rawQuery(strinSQL, null);
       // если количество записей больше 0 то в данном промежутке квартира занята
        Log.d("myLogs", "---найдено записей при проверке = " + cur.getCount() );
        if (cur.getCount() > 0 ){
           Toast.makeText(getBaseContext(), "В данном промежутке времени квартира занята" , Toast.LENGTH_LONG).show();

           return true; // квартира занята
       }
       else{

           return false; // квартира свободна
       }

    }


}
