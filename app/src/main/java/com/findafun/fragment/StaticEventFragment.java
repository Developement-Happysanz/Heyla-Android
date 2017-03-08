package com.findafun.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.costum.android.widget.LoadMoreListView;
import com.findafun.R;
import com.findafun.activity.StaticEventDetailActivity;
import com.findafun.adapter.StaticEventListAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class StaticEventFragment extends Fragment implements LoadMoreListView.OnLoadMoreListener, IEventServiceListener, AdapterView.OnItemClickListener {
    private static final String TAG = StaticEventFragment.class.getName();

    protected LoadMoreListView loadMoreListView;
    protected View view;
    protected StaticEventListAdapter staticEventsListAdapter;
    protected EventServiceHelper eventServiceHelper;
    //protected StaticEventServiceHelper staticEventServiceHelper;
    protected ArrayList<Event> eventsArrayList;
    //protected ArrayList<Event> staticEventsArrayList;
    int pageNumber = 0, totalCount = 0;
    protected ProgressDialogHelper progressDialogHelper;
    protected boolean isLoadingForFirstTime = true;
    Handler mHandler = new Handler();
    private TextView mNoEventsFound = null;


    public static StaticEventFragment newInstance(int position) {
        StaticEventFragment frag = new StaticEventFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_landing_pager, container, false);

        initializeViews();
        initializeEventHelpers();

        return view;
    }

    protected void initializeViews() {
        Log.d(TAG, "initialize pull to refresh view");
        loadMoreListView = (LoadMoreListView) view.findViewById(R.id.listView_events);
        mNoEventsFound = (TextView) view.findViewById(R.id.no_home_events);
        if(mNoEventsFound != null)
            mNoEventsFound.setVisibility(View.GONE);
        loadMoreListView.setOnLoadMoreListener(this);
        loadMoreListView.setOnItemClickListener(this);
        eventsArrayList = new ArrayList<>();
    }

    protected void initializeEventHelpers() {
        eventServiceHelper = new EventServiceHelper(getActivity());
        eventServiceHelper.setEventServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(getActivity());
    }

    public void callGetEventService(int position) {
        Log.d(TAG, "fetch event list" + position);
        /*if(eventsListAdapter != null){
            eventsListAdapter.clearSearchFlag();
        }*/

        if (isLoadingForFirstTime) {
            Log.d(TAG, "Loading for the first time");
            if (eventsArrayList != null)
                eventsArrayList.clear();

            if (CommonUtils.isNetworkAvailable(getActivity())) {
                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                makeEventListServiceCall(1);
            } else {
                AlertDialogHelper.showSimpleAlertDialog(getActivity(), getString(R.string.no_connectivity));
            }
        } else {
            Log.d(TAG, "Do nothing");
        }
    }

    public void callGetFilterService() {
        /*if(eventsListAdapter != null){
            eventsListAdapter.clearSearchFlag();
        }*/

        if (eventsArrayList != null)
            eventsArrayList.clear();

        if (CommonUtils.isNetworkAvailable(getActivity())) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            //    eventServiceHelper.makeRawRequest(FindAFunConstants.GET_ADVANCE_SINGLE_SEARCH);
            new HttpAsyncTask().execute("");
        } else {
            AlertDialogHelper.showSimpleAlertDialog(getActivity(), getString(R.string.no_connectivity));
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

    protected void updateListAdapter(ArrayList<Event> eventsArrayList) {
        this.eventsArrayList.addAll(eventsArrayList);
        if(mNoEventsFound != null)
            mNoEventsFound.setVisibility(View.GONE);

        if (staticEventsListAdapter == null) {
            staticEventsListAdapter = new StaticEventListAdapter(getActivity(), this.eventsArrayList);
            loadMoreListView.setAdapter(staticEventsListAdapter);
        } else {
            staticEventsListAdapter.notifyDataSetChanged();
        }
    }

    public  void searchForEvent(String eventname) {
        Log.d(TAG, "searchevent called");
        if (staticEventsListAdapter != null) {
            staticEventsListAdapter.startSearch(eventname);
            staticEventsListAdapter.notifyDataSetChanged();
            //loadMoreListView.invalidateViews();
        }
    }

    public void exitSearch() {
        Log.d(TAG, "exit event called");
        if (staticEventsListAdapter != null) {
            staticEventsListAdapter.exitSearch();
            staticEventsListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoadMore() {

        int pageNumber = -1;
        int size = eventsArrayList.size();
        Log.d(TAG, "onLoad more called" + size + "totalcount" + totalCount);
        if (size < totalCount) {
            pageNumber = (size / 10) + 1;
        }
        Log.d(TAG, "Page number" + pageNumber);
        makeEventListServiceCall(pageNumber);
    }

    public void makeEventListServiceCall(int pageNumber) {
        if ((pageNumber != -1) && (pageNumber >= 0 && pageNumber <= 6)) {
            switch (getArguments().getInt("position")) {
//                case 0:
//                    Log.d(TAG, "fetch favourites");
//                    eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_EVENTS_FAVOURITES, Integer.parseInt(PreferenceStorage.getUserId(getActivity())), pageNumber, PreferenceStorage.getUserCity(getActivity())));
//                    break;
                case 2:
                    Log.d(TAG, "fetch Nearby events");
                    eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_STATIC_EVENTS, Integer.parseInt(PreferenceStorage.getUserId(getActivity())), pageNumber, PreferenceStorage.getUserCity(getActivity())));
                    break;
//                case 1:
//                    Log.d(TAG, "fetch ALL events");
//                    eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_EVENTS_ALL_URL, pageNumber, PreferenceStorage.getUserCity(getActivity())));
//                    break;
             /*   case 3:
                    Log.d(TAG, "fetch Filter events");
                    eventServiceHelper.makeGetEventServiceCall(String.format(FindAFunConstants.GET_ADVANCE_SINGLE_SEARCH, PreferenceStorage.getFilterSingleDate(getActivity())));
                    break;*/
            }
        } else {
            Log.d(TAG, "ignoring this page");
            loadMoreListView.onLoadMoreComplete();
            progressDialogHelper.hideProgressDialog();
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
                    if(mNoEventsFound != null)
                        mNoEventsFound.setVisibility(View.GONE);
                    totalCount = eventsList.getCount();
                    isLoadingForFirstTime = false;
                    updateListAdapter(eventsList.getEvents());
                }else{
                    if(mNoEventsFound != null)
                        mNoEventsFound.setVisibility(View.VISIBLE);
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
                AlertDialogHelper.showSimpleAlertDialog(getActivity(), error);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onEvent list item clicked" + i);
        Event event = null;
        if ((staticEventsListAdapter != null) && (staticEventsListAdapter.ismSearching())) {
            Log.d(TAG, "while searching");
            int actualindex = staticEventsListAdapter.getActualEventPos(i);
            Log.d(TAG, "actual index" + actualindex);
            event = eventsArrayList.get(actualindex);
        } else {
            event = eventsArrayList.get(i);
        }
        Intent intent = new Intent(getActivity(), StaticEventDetailActivity.class);
        intent.putExtra("eventObj", event);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        // getActivity().overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }

    public void onWindowFoucusChanged() {

    }
}
