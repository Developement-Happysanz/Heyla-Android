package com.findafun.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.costum.android.widget.LoadMoreListView;
import com.findafun.R;
import com.findafun.adapter.EventsListAdapter;
import com.findafun.bean.events.Event;
import com.findafun.bean.events.EventList;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.servicehelpers.EventServiceHelper;
import com.findafun.serviceinterfaces.IEventServiceListener;
import com.findafun.utils.CommonUtils;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class AdvaSearchResAct extends AppCompatActivity implements LoadMoreListView.OnLoadMoreListener, IEventServiceListener, AdapterView.OnItemClickListener {
    private static final String TAG = "AdvaSearchResAct";
    LoadMoreListView loadMoreListView;
    View view;
    EventsListAdapter eventsListAdapter;
    EventServiceHelper eventServiceHelper;
    ArrayList<Event> eventsArrayList;
    int pageNumber = 0, totalCount = 0;
    protected ProgressDialogHelper progressDialogHelper;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    private SearchView mSearchView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adva_search_res);
        getSupportActionBar().hide();
        loadMoreListView = (LoadMoreListView) findViewById(R.id.listView_events);
        loadMoreListView.setOnLoadMoreListener(this);
        loadMoreListView.setOnItemClickListener(this);
        eventsArrayList = new ArrayList<>();
        eventServiceHelper = new EventServiceHelper(this);
        eventServiceHelper.setEventServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        findViewById(R.id.back_res).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (PreferenceStorage.getFilterApply(this)) {
            PreferenceStorage.IsFilterApply(this, false);
            callGetFilterService();
        }
    }

    public void callGetFilterService() {
        /*if(eventsListAdapter != null){
            eventsListAdapter.clearSearchFlag();
        }*/
        if (eventsArrayList != null)
            eventsArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            //    eventServiceHelper.makeRawRequest(FindAFunConstants.GET_ADVANCE_SINGLE_SEARCH);
            new HttpAsyncTask().execute("");
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.no_connectivity));
        }

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... urls) {
            eventServiceHelper.makeRawRequest(FindAFunConstants.GET_ADVANCE_SINGLE_SEARCH);
            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void result) {
            progressDialogHelper.cancelProgressDialog();
        }
    }

    @Override
    public void onEventResponse(final JSONObject response) {
        Log.d("ajazFilterresponse : ", response.toString());

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialogHelper.hideProgressDialog();
                loadMoreListView.onLoadMoreComplete();

                Gson gson = new Gson();
                EventList eventsList = gson.fromJson(response.toString(), EventList.class);
                if (eventsList.getEvents() != null && eventsList.getEvents().size() > 0) {
                    totalCount = eventsList.getCount();
                    isLoadingForFirstTime = false;
                    updateListAdapter(eventsList.getEvents());
                }
            }
        });
    }

    @Override
    public void onEventError(final String error) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialogHelper.hideProgressDialog();
                loadMoreListView.onLoadMoreComplete();
                AlertDialogHelper.showSimpleAlertDialog(AdvaSearchResAct.this, error);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onEvent list item clicked" + position);
        Event event = null;
        if ((eventsListAdapter != null) && (eventsListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = eventsListAdapter.getActualEventPos(position);
            Log.d(TAG, "actual index" + actualindex);
            event = eventsArrayList.get(actualindex);
        } else {
            event = eventsArrayList.get(position);
        }
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra("eventObj", event);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onLoadMore() {

    }

    protected void updateListAdapter(ArrayList<Event> eventsArrayList) {
        this.eventsArrayList.addAll(eventsArrayList);
        if (eventsListAdapter == null) {
            eventsListAdapter = new EventsListAdapter(this, this.eventsArrayList);
            loadMoreListView.setAdapter(eventsListAdapter);
        } else {
            eventsListAdapter.notifyDataSetChanged();
        }
    }

    public void searchForEvent(String eventname) {
        Log.d(TAG, "searchevent called");
        if (eventsListAdapter != null) {
            eventsListAdapter.startSearch(eventname);
            eventsListAdapter.notifyDataSetChanged();
            //loadMoreListView.invalidateViews();
        }
    }

    public void exitSearch() {
        Log.d(TAG, "exit event called");
        if (eventsListAdapter != null) {
            eventsListAdapter.exitSearch();
            eventsListAdapter.notifyDataSetChanged();
        }

    }
}
