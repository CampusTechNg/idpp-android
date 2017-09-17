package com.campustechng.aminu.idpenrollment.fragments;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.campustechng.aminu.idpenrollment.models.EnrollmentModel;
import com.campustechng.aminu.idpenrollment.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewAllEnrollmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewAllEnrollmentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ViewAllEnrollmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewAllEnrollmentFragment.
     */
    // TODO: Rename and change types and gender of parameters
    public static ViewAllEnrollmentFragment newInstance(String param1, String param2) {
        ViewAllEnrollmentFragment fragment = new ViewAllEnrollmentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_all_enrollment, container, false);
    }



    public class ContactListAdapter extends BaseAdapter {

        public List<EnrollmentModel> enrollments;
        private ArrayList<EnrollmentModel> enrollmentModelArrayList;
        Context context;
        ContactHolder contactHolder;

        public ContactListAdapter(List<EnrollmentModel> contacts, Context context) {
            this.enrollments = contacts;
            this.context = context;
            enrollmentModelArrayList = new ArrayList<>();
            enrollmentModelArrayList.addAll(contacts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if(view == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                view = inflater.inflate(R.layout.content_relief_card, parent, false);
            }

            contactHolder = new ContactHolder(view);
            contactHolder.populateFrom(enrollments.get(position));
            view.setTag(contactHolder);
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
                for (EnrollmentModel userContact : enrollmentModelArrayList) {
                    if(userContact.FIRST_NAME.toLowerCase(Locale.getDefault()).contains(text)) {
                        enrollments.add(userContact);
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
            ((ContactListAdapter) baseAdapter.getAdapter()).update(userContactModels);
        }else {
            ContactListAdapter contactListAdapter = new  ContactListAdapter(userContactModels, getContext());
            baseAdapter.setAdapter(contactListAdapter);
        }
    }

    static class ContactHolder {

        private TextView name = null;
        private TextView gender = null;
        private TextView age = null;
        private TextView status = null;
        private ImageView profile = null;
        private ImageView barcode = null;
        private View row = null;

        ContactHolder(View row) {
            this.row = row;
            name = (TextView) row.findViewById(R.id.name);
            gender = (TextView) row.findViewById(R.id.id_gender);
            age = (TextView) row.findViewById(R.id.age);
            status = (TextView) row.findViewById(R.id.marital_status);
            profile = (ImageView) row.findViewById(R.id.profileImageView);
            barcode = (ImageView) row.findViewById(R.id.qrcodeImageView);

        }

        void populateFrom(EnrollmentModel helper) {
            name.setText(helper.FIRST_NAME +" "+helper.LAST_NAME);
            gender.setText(helper.GENDER);
            age.setText(helper.ESTIMATED_AGE);
            status.setText(helper.MARITAL_STATUS);
            if(helper.PROFILE != null)
            profile.setImageBitmap(BitmapFactory.decodeByteArray(helper.PROFILE,0,helper.PROFILE.length));
            if(helper.BARCODE != null)
            barcode.setImageBitmap(BitmapFactory.decodeByteArray(helper.BARCODE,0,helper.BARCODE.length));
        }
    }

}
