package com.findafun.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.findafun.R;
import com.findafun.adapter.PreferenceListAdapter;
import com.findafun.bean.categories.Category;
import com.findafun.bean.categories.Preference;
import com.findafun.bean.categories.SetCategory;
import com.findafun.helper.AlertDialogHelper;
import com.findafun.helper.ProgressDialogHelper;
import com.findafun.interfaces.DialogClickListener;
import com.findafun.servicehelpers.CategoryServiceHelper;
import com.findafun.serviceinterfaces.ICategoryServiceListener;
import com.findafun.utils.FindAFunConstants;
import com.findafun.utils.PreferenceStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SelectPreferenceActivity extends AppCompatActivity implements PreferenceListAdapter.OnItemClickListener, ICategoryServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = SelectPreferenceActivity.class.getName();

    private RecyclerView mRecyclerView;
    private PreferenceListAdapter preferenceAdatper;
    private ArrayList<Category> categoryArrayList, selectedList;
    private CategoryServiceHelper categoryServiceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private MenuItem menuSet;
    private GridLayoutManager mLayoutManager;
    private TextView txtGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_preference);

        txtGetStarted = (TextView) findViewById(R.id.text_getStarted);
        txtGetStarted.setOnClickListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.listView_categories);

        mLayoutManager = new GridLayoutManager(this, 6);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (preferenceAdatper.getItemViewType(position) > 0) {
                    return preferenceAdatper.getItemViewType(position);
                } else {
                    return 4;
                }
                //return 2;
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);


        //mRecyclerView.setOnItemClickListener(this);
        selectedList = new ArrayList<>();

        categoryServiceHelper = new CategoryServiceHelper(this);
        categoryServiceHelper.setCategoryServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(FindAFunConstants.PARAMS_FUNC_NAME, "category_list");
            jsonObject.put(FindAFunConstants.PARAMS_USER_ID, PreferenceStorage.getUserId(this));
            jsonObject.put(FindAFunConstants.PARAMS_USER_TYPE, PreferenceStorage.getUserType(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        categoryServiceHelper.makeGetCategoryServiceCall(jsonObject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select, menu);
        menuSet = (MenuItem) menu.findItem(R.id.action_set);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_set) {
            setPreferences();
            return true;
        } else if (id == R.id.action_skip) {
            Intent intent = new Intent(this, LandingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setPreferences() {
        //save preferences selected
        Log.d(TAG, "size of selected preferences" + selectedList.size());
        PreferenceStorage.savePreferencesSelected(this, true);
        ArrayList<Preference> preferences = new ArrayList<>();
        for (Category category : selectedList) {
            Preference preference = new Preference();
            Log.d(TAG, "add category id" + category.getId());
            preference.setCategoryId(category.getId());

            preferences.add(preference);
        }
        SetCategory setCategory = new SetCategory();
        setCategory.setPreferences(preferences);
        setCategory.setFuncName("user_preference");
        setCategory.setUserId(PreferenceStorage.getUserId(this));
        setCategory.setUserType(PreferenceStorage.getUserType(this));
        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        Gson gson = new Gson();
        String json = gson.toJson(setCategory);
        categoryServiceHelper.makeSetCategoryServiceCall(json);
    }


    @Override
    public void onCategoryResponse(JSONArray response) {
        progressDialogHelper.hideProgressDialog();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Category>>() {
        }.getType();
        categoryArrayList = (ArrayList<Category>) gson.fromJson(response.toString(), listType);
        preferenceAdatper = new PreferenceListAdapter(this, categoryArrayList, this);
        mRecyclerView.setAdapter(preferenceAdatper);
    }

    @Override
    public void onCategoryError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);

    }

    @Override
    public void onSetCategoryResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onSetCategoryError(String error) {
        progressDialogHelper.hideProgressDialog();
        AlertDialogHelper.showSimpleAlertDialog(this, error);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    public void onCategorySelected(int position) {
        Log.d(TAG, "selected category position" + position);
        if (selectedList != null) {
            Category category = (Category) preferenceAdatper.getItem(position);
            Log.d(TAG, "id" + category.getId());
            selectedList.add(category);
        }
    }

    @Override
    public void onItemClick(View view, int position) {

        Category tag = preferenceAdatper.getItem(position);
        RelativeLayout textView = (RelativeLayout) view;

//        GradientDrawable bgShape = (GradientDrawable) textView.getBackground();
        if (tag.getCategoryPreference().equals("no")) {
//            bgShape.setColor(getResources().getColor(R.color.preference_orange));
            textView.setBackgroundColor(getResources().getColor(R.color.preference_orange));
//            TextView mtextView = (TextView) view;
//            mtextView.setTextColor(getResources().getColor(R.color.preference_orange));
            tag.setCategoryPreference("yes");
        } else {
//            bgShape.setColor(getResources().getColor(R.color.white));
            textView.setBackgroundColor(getResources().getColor(R.color.white));
//            TextView mtextView = (TextView) view;
//            mtextView.setTextColor(getResources().getColor(R.color.white));
            tag.setCategoryPreference("no");
        }
//        ImageView tickImage = (ImageView) view.findViewById(R.id.tickImage);
//        ImageView logoImage = (ImageView) view.findViewById(R.id.img_category);
//        if (tag.getCategoryPreference().equals("no")) {
//            tickImage.setVisibility(View.VISIBLE);
//            tag.setCategoryPreference("yes");
//            logoImage.setAlpha((float) 0.1);
//        } else {
//            tickImage.setVisibility(View.INVISIBLE);
//            tag.setCategoryPreference("no");
//            logoImage.setAlpha((float) 1);
//        }
        if (selectedList.contains(tag)) {
            selectedList.remove(tag);
        } else {
            selectedList.add(tag);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == txtGetStarted) {
            if (selectedList.size() > 0) {
                setPreferences();
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                //alertDialogBuilder.setTitle("Registration Successful");
                alertDialogBuilder.setTitle("No Preference selected");
                alertDialogBuilder.setMessage("Please select atleast one Preference");
                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {


                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        }
    }
}
