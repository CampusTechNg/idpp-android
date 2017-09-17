package com.campustechng.aminu.idpenrollment.fragments;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.campustechng.aminu.idpenrollment.core.Logger;
import com.campustechng.aminu.idpenrollment.core.Operations;
import com.campustechng.aminu.idpenrollment.R;
import com.campustechng.aminu.idpenrollment.models.HistoryModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class ReliefItemsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "id";
    private static final String ARG_PARAM2 = "name";

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    private TextView name;
    private Button confirm_button;
    public static String IDP_Name = null;
    public static String IDP_ID = null;
    Logger logger;
    private CoordinatorLayout coordinatorLayout;
    public ReliefItemsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_relief_items, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View view = getView();
        // get the listview
        expListView = (ExpandableListView) view.findViewById(R.id.relief_item_list);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinator_layout);
        name = (TextView) view.findViewById(R.id.idp_name);
        confirm_button = (Button) view.findViewById(R.id.confirm_button);
        name.setText(IDP_Name);

        logger = Logger.getInstance(getContext());
        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);


        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(IDP_ID != null && IDP_Name != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    Set<Pair<Long,Long>> selected = listAdapter.mCheckedItems;
                    int counter = 0;
                    for(Pair<Long,Long> pair : selected) {
                        String value = listDataChild.get(listDataHeader.get(pair.first.intValue())).get(pair.second.intValue());
                        stringBuilder.append("- ");
                        stringBuilder.append(value);
                        if(++counter < selected.size() )
                        stringBuilder.append("\n");
                       // Toast.makeText(getContext(),value,Toast.LENGTH_SHORT).show();
                    }
                    HistoryModel historyModel = Operations.prepareHistoryModel(IDP_ID,stringBuilder.toString());
                    logger.insertHistory(historyModel);
                    IDP_ID = IDP_Name = null;
                    getActivity().finish();
                } else {
                    showSnackBar();
                }

            }
        });



        if(IDP_ID == null && IDP_Name == null) {
            if (getArguments() != null) {
                IDP_ID = getArguments().getString(ARG_PARAM1);
                IDP_Name = getArguments().getString(ARG_PARAM2);
                confirm_button.setEnabled(true);
            } else {
               showSnackBar();
            }
        }else {
            confirm_button.setEnabled(true);
        }
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        String[] list_items = getResources().getStringArray(R.array.list_group);
        String[] shelter_items = getResources().getStringArray(R.array.shelter_items);
        String[] domestic_items = getResources().getStringArray(R.array.domestic_items);
        String[] food_items = getResources().getStringArray(R.array.food_items);
        String[] other_items = getResources().getStringArray(R.array.other_items);

        // Adding child data
        listDataHeader.addAll(Arrays.asList(list_items));

        // Adding child data
        List<String> shelterItems = new ArrayList<>();
        shelterItems.addAll(Arrays.asList(shelter_items));

        List<String> domesticItems = new ArrayList<>();
        domesticItems.addAll(Arrays.asList(domestic_items));

        List<String> foodItems = new ArrayList<>();
        foodItems.addAll(Arrays.asList(food_items));

        List<String> otherItems = new ArrayList<>();
        otherItems.addAll(Arrays.asList(other_items));


        listDataChild.put(listDataHeader.get(0), shelterItems); // Header, Child data
        listDataChild.put(listDataHeader.get(1), domesticItems);
        listDataChild.put(listDataHeader.get(2), foodItems);
        listDataChild.put(listDataHeader.get(3), otherItems);
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader; // header titles
        // child data in format of header title, child title
        private HashMap<String, List<String>> _listDataChild;
        private final Set<Pair<Long, Long>> mCheckedItems = new HashSet<Pair<Long, Long>>();


        public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                     HashMap<String, List<String>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.relief_items_list_layout, null);
            }

            final CheckBox cbListChild = (CheckBox) convertView.findViewById(R.id.lblListItem);
            final Pair<Long, Long> tag = new Pair<>(
                    getGroupId(groupPosition),
                    getChildId(groupPosition, childPosition));
            cbListChild.setTag(tag);
            // set checked if groupId/childId in checked items
            cbListChild.setChecked(mCheckedItems.contains(tag));
            cbListChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CheckBox cb = (CheckBox) v;
                    final Pair<Long, Long> tag = (Pair<Long, Long>) v.getTag();
                    if (cb.isChecked()) {
                        mCheckedItems.add(tag);
                    } else {
                        mCheckedItems.remove(tag);
                    }
                }
            });
            cbListChild.setText(childText);
            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.relief_items_group_list_layout, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar
                .make(getView(), "No verified IDP to receive relief material.", Snackbar.LENGTH_LONG)
                .setDuration(8000)
                .setAction("Verify", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Fragment fragment = new VerificationFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("stack","relief");
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right);
                        fragmentTransaction.replace(R.id.frame, fragment, "verify");
                        fragmentTransaction.commit();
                    }
                });

        snackbar.show();
    }

}
