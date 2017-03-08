package com.findafun.activity;

import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.findafun.R;
import com.findafun.adapter.EventsListAdapter;
import com.findafun.bean.events.Event;
import com.findafun.bean.events.EventList;
import com.findafun.bean.gamification.GamificationDataHolder;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.EventServiceHelper;
import com.findafun.serviceinterfaces.IEventServiceListener;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * Created by BXDC46 on 2/23/2016.
 */
public class BookmarksActivity extends AppCompatActivity implements IEventServiceListener, DialogClickListener {
    private static final String TAG = BookmarksActivity.class.getName();
    private ListView mBookMarksList = null;
    protected EventsListAdapter eventsListAdapter;
    protected EventServiceHelper eventServiceHelper;
    protected ArrayList<Event> eventsArrayList;
    protected ProgressDialogHelper progressDialogHelper;
    protected boolean isLoadingForFirstTime = true;
    private TextView mNoBookmarkEvents = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_right, 0);
        setContentView(R.layout.bookmark_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Bookmarks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mBookMarksList = (ListView) findViewById(R.id.bookmark_list);
        eventsArrayList = new ArrayList<>();
        eventServiceHelper = new EventServiceHelper(this);
        eventServiceHelper.setEventServiceListener(this);
        mNoBookmarkEvents = (TextView) findViewById(R.id.no_bookmark_events);
        mNoBookmarkEvents.setVisibility(View.GONE);
        mBookMarksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event event = eventsArrayList.get(position);
                Intent intent = new Intent(BookmarksActivity.this, EventDetailActivity.class);
                intent.putExtra("eventObj", event);
                // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        registerForContextMenu(mBookMarksList);
        progressDialogHelper = new ProgressDialogHelper(this);
        callGetEventService();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_directions) {
            return true;
        } else if (id == android.R.id.home) {
            Log.d(TAG, "home up button selected");
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void callGetEventService() {
        Log.d(TAG, "fetch event list");
        /*if(eventsListAdapter != null){
            eventsListAdapter.clearSearchFlag();
        }*/

        if (isLoadingForFirstTime) {
            Log.d(TAG, "Loading for the first time");
            if (eventsArrayList != null)
                eventsArrayList.clear();
            if (CommonUtils.isNetworkAvailable(this)) {
                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_EVENTS_BOOKMARK, Integer.parseInt(PreferenceStorage.getUserId(this))));
            } else {
                AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.no_connectivity));
            }
        } else {
            Log.d(TAG, "Do nothing");
        }
    }

    protected void updateListAdapter(ArrayList<Event> eventsArrayList) {
        this.eventsArrayList.addAll(eventsArrayList);
        if (eventsListAdapter == null) {
            eventsListAdapter = new EventsListAdapter(this, this.eventsArrayList);
            mBookMarksList.setAdapter(eventsListAdapter);
        } else {
            eventsListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onEventResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        Gson gson = new Gson();
        EventList eventsList = gson.fromJson(response.toString(), EventList.class);
        if (eventsList.getEvents() != null && eventsList.getEvents().size() > 0) {
            // totalCount = eventsList.getCount();
            isLoadingForFirstTime = false;
            GamificationDataHolder.getInstance().clearBookmarks();
            for (Event event : eventsList.getEvents()) {
                GamificationDataHolder.getInstance().addBookmarkedEvent(event.getId());
            }
            updateListAdapter(eventsList.getEvents());
            mNoBookmarkEvents.setVisibility(View.GONE);
        } else {
            mNoBookmarkEvents.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEventError(String error) {
        progressDialogHelper.hideProgressDialog();
        // AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.bookmark_list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            // menu.setHeaderTitle(Countries[info.position]);
            //menu.add("Delete");
            //String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i < 1; i++) {
                menu.add(Menu.NONE, i, i, "Delete");
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        Log.d(TAG, "delete the item at index" + info.position);
        Event event = eventsArrayList.get(info.position);
        eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.DELETE_BOOKMARK_URL,
                Integer.parseInt(PreferenceStorage.getUserId(BookmarksActivity.this)), Integer.parseInt((event.getId()))));
        eventsArrayList.remove(info.position);
        GamificationDataHolder.getInstance().removeBookmark(event.getId());
        eventsListAdapter.notifyDataSetChanged();
        return true;
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}
