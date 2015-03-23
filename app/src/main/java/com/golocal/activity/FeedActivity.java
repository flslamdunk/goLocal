package com.golocal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.golocal.fragment.EventsFragment;
import com.golocal.utils.Helpers;

import java.util.Date;

public class FeedActivity extends Activity {

    EventsFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.card_list_background));
        mFragment = EventsFragment.newInstance(Helpers.dateToWeeksIndex(new Date()));
        getFragmentManager().beginTransaction().add(R.id.pane, mFragment, EventsFragment.TAG).commit();
        setActionBar(mFragment.mTime);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add:
                add();
                return true;
            case R.id.action_backward:
                backward();
                return true;
            case R.id.action_forward:
                forward();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void backward(){
        if(mFragment.mTime <= 1) {
            Toast.makeText(getBaseContext(),"No previous data available",Toast.LENGTH_SHORT).show();
        } else {
            EventsFragment newFragment = EventsFragment.newInstance(mFragment.mTime - 1);
            mFragment = newFragment;
            setActionBar(mFragment.mTime);
            getFragmentManager().beginTransaction().replace(R.id.pane, newFragment).commit();
        }
    }

    private void forward(){
        EventsFragment newFragment = EventsFragment.newInstance(mFragment.mTime + 1);
        mFragment = newFragment;
        setActionBar(mFragment.mTime);
        getFragmentManager().beginTransaction().replace(R.id.pane, newFragment).commit();
    }

    private void refresh(){
        mFragment.getFragmentManager().beginTransaction().detach(mFragment).commit();
        mFragment.getFragmentManager().beginTransaction().attach(mFragment).commit();
    }

    private void add(){
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }

    private void setActionBar(int weekIndex) {
        getActionBar().setTitle(Html.fromHtml("<font color='#ff6600'>Week of " + Helpers.getDateString(weekIndex) + "</font>"));
    }
}
