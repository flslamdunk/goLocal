package com.golocal.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentManager;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.golocal.utils.Constants;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.Calendar;
import java.util.Date;

import static android.view.View.OnClickListener;


public class CreateEventActivity extends FragmentActivity implements CalendarDatePickerDialog.OnDateSetListener{

    EditText mTitle;
    EditText mUrl;
    Spinner mCategory;
    TextView mDateText;
    Button mDateButton;
    Button mCreateButton;

    private static final String TAG = "create_event_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.card_list_background));

        getActionBar().setTitle("New Event");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mCategory = (Spinner) findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(adapter);

        mTitle = (EditText) findViewById(R.id.edit_title);
        mUrl = (EditText) findViewById(R.id.edit_url);

        mTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        mUrl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        mDateText = (TextView) findViewById(R.id.date_text);
        mDateButton = (Button) findViewById(R.id.date_button);
        mDateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(CreateEventActivity.this, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH));
                calendarDatePickerDialog.show(fm, TAG);
            }
        });

        mCreateButton = (Button) findViewById(R.id.submit_button);
        mCreateButton.setBackgroundColor(getResources().getColor(R.color.blue));
        mCreateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                create();
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        mDateText.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_event_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_save:
                create();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void create(){
        JsonObject json = new JsonObject();
        json.addProperty("title", mTitle.getText().toString());
        json.addProperty("url", mUrl.getText().toString());
        json.addProperty("category", mCategory.getSelectedItem().toString().toUpperCase());
        json.addProperty("eventDate", mDateText.getText().toString());

        Ion.with(getApplicationContext()).load(Constants.CREATE_URL)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if(e != null){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "Event Created!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }
}
