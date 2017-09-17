package com.campustechng.aminu.idpenrollment.activity;


import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.campustechng.aminu.idpenrollment.core.Logger;
import com.campustechng.aminu.idpenrollment.core.Operations;
import com.campustechng.aminu.idpenrollment.R;
import com.campustechng.aminu.idpenrollment.models.HistoryModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class HistoryActivity extends BaseActivity {


    Toolbar toolbar;
    private HistoryListAdapter historyListAdapter;
    private HistoryLoader historyLoader;
    static ArrayList<HistoryModel> historyModels;
    HistoryModel historyModel;
    ListView historyListView;
    SearchView searchView;
    Cursor histories;
    Logger logger;
    public String IDP_ID = "0";
    public String NAME = "";
    TextView name;
    public HistoryActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = (TextView) findViewById(R.id.idp_name);
        searchView = (SearchView) findViewById(R.id.search_history);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                historyListAdapter.filter(newText);
                return false;
            }
        });
        historyListView = (ListView) findViewById(R.id.idp_history);
        if(getIntent().getExtras() != null){
            IDP_ID = getIntent().getExtras().getString("id");
            NAME = getIntent().getExtras().getString("name");
            name.setText(NAME);
        }

        historyModels = new ArrayList<>();
        logger = Logger.getInstance(this);
        historyLoader = new HistoryLoader();
        historyLoader.execute();
    }

    public class HistoryListAdapter extends BaseAdapter {

        public List<HistoryModel> historyModels;
        private ArrayList<HistoryModel> historyModelArrayList;
        Context context;
        HistoryHolder historyHolder;

        public HistoryListAdapter(List<HistoryModel> contacts, Context context) {
            this.historyModels = contacts;
            this.context = context;
            historyModelArrayList = new ArrayList<>();
            historyModelArrayList.addAll(contacts);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if(view == null) {
                LayoutInflater inflater = getLayoutInflater();
                view = inflater.inflate(R.layout.history_layout, parent, false);
            }

            historyHolder = new HistoryHolder(view);
            historyHolder.populateFrom(historyModels.get(position));
            view.setTag(historyHolder);
            return view;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return historyModels.get(position);
        }

        @Override
        public int getCount() {
            return historyModels.size();
        }


        public void filter(String text) {
            text = text.toLowerCase(Locale.getDefault());
            historyModels.clear();
            if(text.length() == 0) {
                historyModels.addAll(historyModelArrayList);
            }
            else {
                for (HistoryModel historyModel : historyModelArrayList) {
                    if(historyModel.EVENT.toLowerCase(Locale.getDefault()).contains(text)) {
                        historyModels.add(historyModel);
                    } else if(Operations.getDate(historyModel.DATE).toLowerCase(Locale.getDefault()).contains(text)) {
                        historyModels.add(historyModel);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void update(List<HistoryModel> idpHistory) {
            historyModels.clear();
            historyModels.addAll(idpHistory);
            notifyDataSetChanged();
        }
    }


    class HistoryLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {

            //super.onPostExecute(aVoid);

            historyListAdapter = new HistoryListAdapter(historyModels, HistoryActivity.this);
            historyListView.setAdapter(historyListAdapter);
            historyListView.setFastScrollEnabled(true);
            historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // EnrollmentModel userContact = historyModels.get(position);
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.i("search","cursor loader running");
            Log.i("search",IDP_ID);
            histories = logger.selectIDPHistory(IDP_ID);
            if(histories != null) {
                Log.i("search","cursor loaded");
                if(histories.getCount() > 0) {
                    while (histories.moveToNext()) {
                        long date = histories.getLong(histories.getColumnIndex(Logger.HistoryEntry.COLUMN_NAME_DATE));
                        String event = histories.getString(histories.getColumnIndex(Logger.HistoryEntry.COLUMN_NAME_EVENT));

                        Log.i("search","History found");
                        Log.i("search",String.valueOf(date));
                        historyModel = new HistoryModel();
                        historyModel.DATE = date;
                        historyModel.EVENT = event;
                        historyModels.add(historyModel);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }


    static class HistoryHolder {

        private TextView date = null;
        private TextView event = null;
        private View row = null;

        HistoryHolder(View row) {
            this.row = row;
            date = (TextView) row.findViewById(R.id.txtDate);
            event = (TextView) row.findViewById(R.id.txtEvent);

        }

        void populateFrom(final HistoryModel helper) {
            date.setText(Operations.getDate(helper.DATE));
            event.setText(helper.EVENT);
        }
    }


}
