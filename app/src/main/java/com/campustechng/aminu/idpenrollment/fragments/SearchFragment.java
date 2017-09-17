package com.campustechng.aminu.idpenrollment.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.campustechng.aminu.idpenrollment.core.Logger;
import com.campustechng.aminu.idpenrollment.core.Operations;
import com.campustechng.aminu.idpenrollment.R;
import com.campustechng.aminu.idpenrollment.activity.HistoryActivity;
import com.campustechng.aminu.idpenrollment.activity.MainActivity;
import com.campustechng.aminu.idpenrollment.activity.ReliefCardActivity;
import com.campustechng.aminu.idpenrollment.models.EnrollmentModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SearchFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private IDPDetailsListAdapter idpDetailsListAdapter;
    private IDPDetailsLoader idpDetailsLoader;
    static ArrayList<EnrollmentModel> enrollmentList;
    EnrollmentModel enrollmentModel;
    SearchView searchView;
    ListView enrollmentListView;
    Cursor enrollments;
    Logger logger;
    public SearchFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view =  getView();
        enrollmentListView = (ListView) view.findViewById(R.id.enrollmentListView);

        searchView = (SearchView)view.findViewById(R.id.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                    idpDetailsListAdapter.filter(newText);
                return false;
            }
        });

        enrollmentList = new ArrayList<>();
        logger = Logger.getInstance(getContext());
        idpDetailsLoader = new IDPDetailsLoader();
        idpDetailsLoader.execute();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }



    class IDPDetailsLoader extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {

            //super.onPostExecute(aVoid);

            idpDetailsListAdapter = new IDPDetailsListAdapter(enrollmentList, getContext());
            enrollmentListView.setAdapter(idpDetailsListAdapter);
            enrollmentListView.setFastScrollEnabled(true);
            enrollmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   // EnrollmentModel userContact = historyModels.get(position);
                }
            });
        }

        @Override
        protected Void doInBackground(Void... params) {

            Log.i("search","cursor loader running");
            enrollments = logger.selectAllEnrollments();
            if(enrollments != null) {
                Log.i("search","cursor loaded");
                if(enrollments.getCount() > 0) {
                    while (enrollments.moveToNext()) {
                       /* String id = enrollments.getString(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_IDP_ID));
                        String first_name = enrollments.getString(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_FIRST_NAME));
                        String last_name = enrollments.getString(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_LAST_NAME));
                        String other_names = enrollments.getString(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_OTHER_NAMES));
                        String gender = enrollments.getString(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_GENDER));
                        String dob = enrollments.getString(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_DOB));
                        String yob = enrollments.getString(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_AGE));
                        String status = enrollments.getString(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_MARITAL_STATUS));
                        String state = enrollments.getString(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_STATE));
                        String lga = enrollments.getString(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_LOCAL_GOV));
                        String location = enrollments.getString(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_LOCATION));
                        long timestamp = enrollments.getLong(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_TIME_STAMP));
                        byte[] photo = enrollments.getBlob(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_PROFILE));
                        byte[] right_thumb = enrollments.getBlob(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_RIGHT_THUMB));
                        byte[] left_thumb = enrollments.getBlob(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_LEFT_THUMB));
                        byte[] barcode =enrollments.getBlob(enrollments.getColumnIndex(Logger.EnrollmentEntry.COLUMN_NAME_BARCODE));*/


                        enrollmentModel = Operations.generateEnrollmentModelFromCursor(getContext(),enrollments); //new EnrollmentModel();
                       /* enrollmentModel.IDP_ID = id;
                        enrollmentModel.FIRST_NAME = first_name;
                        enrollmentModel.LAST_NAME = last_name;
                        enrollmentModel.OTHER_NAMES = other_names;
                        enrollmentModel.GENDER = gender;
                        enrollmentModel.DOB = dob;
                        enrollmentModel.ESTIMATED_AGE = yob;
                        enrollmentModel.MARITAL_STATUS = status;
                        enrollmentModel.STATE = state;
                        enrollmentModel.LOCAL_GOVERNMENT = lga;
                        enrollmentModel.LOCATION = location;
                        enrollmentModel.TIME_STAMP = timestamp;
                        enrollmentModel.PROFILE = photo;
                        enrollmentModel.RIGHT_THUMB = right_thumb;
                        enrollmentModel.LEFT_THUMB = left_thumb;
                        enrollmentModel.BARCODE = barcode;*/
                        enrollmentList.add(enrollmentModel);
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

    public class IDPDetailsListAdapter extends BaseAdapter {

        public List<EnrollmentModel> enrollments;
        private ArrayList<EnrollmentModel> enrollmentModelArrayList;
        Context context;
        IDPDetailsHolder idpDetailsHolder;
        private Button viewReliefCard = null;
        private Button viewHistory = null;
        private Button giveRelief = null;
        private ImageView photo;
        LinearLayout mainLayout, containerLayout;
        PopupWindow popUpWindow;
        ImageView imageView;

        public IDPDetailsListAdapter(List<EnrollmentModel> enrollmentModels, Context context) {
            this.enrollments = enrollmentModels;
            this.context = context;
            enrollmentModelArrayList = new ArrayList<>();
            enrollmentModelArrayList.addAll(enrollmentModels);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if(view == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.idp_details_layout, parent, false);
                viewReliefCard = (Button) view.findViewById(R.id.view_relief_card);
                viewHistory = (Button) view.findViewById(R.id.view_history);
                giveRelief = (Button) view.findViewById(R.id.give_relief_button);
                photo = (ImageView) view.findViewById(R.id.photo);
                viewReliefCard = (Button) view.findViewById(R.id.view_relief_card);
                viewHistory = (Button) view.findViewById(R.id.view_history);
                giveRelief = (Button) view.findViewById(R.id.give_relief_button);
                viewReliefCard.setTag(position);
                viewHistory.setTag(position);
                giveRelief.setTag(position);

            }
           /* viewReliefCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"position: "+v.getId() + ", model count: "+getCount(),Toast.LENGTH_SHORT).show();
                    ReliefCardActivity.IDP_MODEL = (EnrollmentModel) getItem((int)v.getTag());
                    startActivity(new Intent(getContext(),ReliefCardActivity.class));

                }
            });*/

            idpDetailsHolder = new IDPDetailsHolder(view);
            final EnrollmentModel model = (EnrollmentModel) getItem(position);
            idpDetailsHolder.viewReliefCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReliefCardActivity.IDP_MODEL = model; //(EnrollmentModel) getItem(position);
                    startActivity(new Intent(getContext(),ReliefCardActivity.class));
                }
            });
            idpDetailsHolder.viewHistory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(),HistoryActivity.class);
                    intent.putExtra("id",model.IDP_ID);
                    intent.putExtra("name",model.FIRST_NAME +" "+model.LAST_NAME+" "+model.OTHER_NAMES);
                    startActivity(intent);

                }
            });
            idpDetailsHolder.giveRelief.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReliefItemsFragment.IDP_ID = model.IDP_ID;
                    ReliefItemsFragment.IDP_Name = String.format("%s %s %s",model.FIRST_NAME,model.LAST_NAME,model.OTHER_NAMES);
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("CURRENT_TAG","relief");
                    intent.putExtra("navIndex",3);
                    startActivity(intent);
                }
            });
            idpDetailsHolder.populateFrom(enrollments.get(position));
            view.setTag(idpDetailsHolder);
            return view;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return enrollments.get(position);
        }

        @Override
        public int getCount() {
            return enrollments.size();
        }

        public void filter(String text) {
            text = text.toLowerCase(Locale.getDefault());
            enrollments.clear();
            if(text.length() == 0) {
                enrollments.addAll(enrollmentModelArrayList);
            }
            else {
                for (EnrollmentModel enrollmentModel : enrollmentModelArrayList) {
                    if(enrollmentModel.FIRST_NAME.toLowerCase(Locale.getDefault()).contains(text)) {
                        enrollments.add(enrollmentModel);
                    } else if(enrollmentModel.LAST_NAME.toLowerCase(Locale.getDefault()).contains(text)) {
                        enrollments.add(enrollmentModel);
                    }else if(enrollmentModel.OTHER_NAMES.toLowerCase(Locale.getDefault()).contains(text)) {
                        enrollments.add(enrollmentModel);
                    }else if(enrollmentModel.GENDER.toLowerCase(Locale.getDefault()).contains(text)) {
                        enrollments.add(enrollmentModel);
                    }else if(enrollmentModel.MARITAL_STATUS.toLowerCase(Locale.getDefault()).contains(text)) {
                        enrollments.add(enrollmentModel);
                    }else if(enrollmentModel.STATE.toLowerCase(Locale.getDefault()).contains(text)) {
                        enrollments.add(enrollmentModel);
                    }else if(enrollmentModel.LOCAL_GOVERNMENT.toLowerCase(Locale.getDefault()).contains(text)) {
                        enrollments.add(enrollmentModel);
                    }else if(enrollmentModel.LOCATION.toLowerCase(Locale.getDefault()).contains(text)) {
                        enrollments.add(enrollmentModel);
                    }else if(enrollmentModel.DOB != null && enrollmentModel.DOB.toLowerCase(Locale.getDefault()).contains(text)) {
                        enrollments.add(enrollmentModel);
                    }else if(enrollmentModel.ESTIMATED_AGE != null && enrollmentModel.ESTIMATED_AGE.toLowerCase(Locale.getDefault()).contains(text)) {
                        enrollments.add(enrollmentModel);
                    }else if(enrollmentModel.TIME_STAMP > 0 && Operations.getDate(enrollmentModel.TIME_STAMP).toLowerCase(Locale.getDefault()).contains(text)) {
                        enrollments.add(enrollmentModel);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public void update(List<EnrollmentModel> userContact) {
            enrollments.clear();
            enrollments.addAll(userContact);
            notifyDataSetChanged();
        }
    }

    public void updateAdapter(ListView baseAdapter, List<EnrollmentModel> userContactModels) {
        if(baseAdapter.getAdapter() != null) {
            ((IDPDetailsListAdapter) baseAdapter.getAdapter()).update(userContactModels);
        }else {
            IDPDetailsListAdapter contactListAdapter = new IDPDetailsListAdapter(userContactModels, getContext());
            baseAdapter.setAdapter(contactListAdapter);
        }
    }

    static class IDPDetailsHolder {

        public TextView name = null;
        private TextView gender = null;
        private TextView status = null;
        private TextView dob = null;
        private TextView state = null;
        private TextView lga = null;
        private TextView enrollment_date = null;
        private TextView enrollment_location = null;
        private ImageView profile = null;
        private ImageView rightThumb = null;
        private ImageView leftThumb = null;
        public Button viewReliefCard, viewHistory, giveRelief;
        private View row = null;

        IDPDetailsHolder(View row) {
            this.row = row;
            profile = (ImageView) row.findViewById(R.id.photo) ;
            name = (TextView) row.findViewById(R.id.idp_name);
            gender = (TextView) row.findViewById(R.id.idp_gender);
            status = (TextView) row.findViewById(R.id.idp_marital_status);
            dob = (TextView) row.findViewById(R.id.idp_dob);
            state = (TextView) row.findViewById(R.id.idp_state);
            lga = (TextView) row.findViewById(R.id.idp_lga);
            enrollment_date = (TextView) row.findViewById(R.id.enrollment_date);
            enrollment_location = (TextView) row.findViewById(R.id.enrollment_location);
            rightThumb = (ImageView) row.findViewById(R.id.idp_right_thumb);
            leftThumb = (ImageView) row.findViewById(R.id.idp_left_thumb);
            viewReliefCard = (Button) row.findViewById(R.id.view_relief_card);
            viewHistory = (Button) row.findViewById(R.id.view_history);
            giveRelief = (Button) row.findViewById(R.id.give_relief_button);

        }

        void populateFrom(final EnrollmentModel helper) {
            name.setText(String.format("%s %s %s",helper.FIRST_NAME,helper.LAST_NAME,helper.OTHER_NAMES));
            gender.setText(helper.GENDER);
            status.setText(helper.MARITAL_STATUS);
            if(helper != null)
                dob.setText(helper.DOB);
            else dob.setText(helper.ESTIMATED_AGE);
            state.setText(helper.STATE);
            lga.setText(helper.LOCAL_GOVERNMENT);
            enrollment_date.setText(Operations.getDate(helper.TIME_STAMP));
            enrollment_location.setText(helper.LOCATION);
            if(helper.PROFILE != null )
            profile.setImageBitmap(BitmapFactory.decodeByteArray(helper.PROFILE,0,helper.PROFILE.length));
            if(helper.RIGHT_THUMB != null)
                rightThumb.setImageBitmap(BitmapFactory.decodeByteArray(helper.RIGHT_THUMB,0,helper.RIGHT_THUMB.length));
            if(helper.LEFT_THUMB != null)
                leftThumb.setImageBitmap(BitmapFactory.decodeByteArray(helper.LEFT_THUMB,0,helper.LEFT_THUMB.length));
        }
    }

}
