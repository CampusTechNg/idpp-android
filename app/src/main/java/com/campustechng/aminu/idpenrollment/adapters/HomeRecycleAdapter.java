package com.campustechng.aminu.idpenrollment.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.campustechng.aminu.idpenrollment.models.HomeCardView;
import com.campustechng.aminu.idpenrollment.R;

import java.util.List;

/**
 * Created by Muhammad Amin on 4/4/2017.
 */

public class HomeRecycleAdapter extends RecyclerView.Adapter<HomeRecycleAdapter.MyViewHolder>{
    private Context mContext;
    private List<HomeCardView> menuList;
    CardView cv;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView description;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            cv = (CardView) view.findViewById(R.id.cv);
            description = (TextView) view.findViewById(R.id.person_name);
            thumbnail = (ImageView) view.findViewById(R.id.person_photo);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     TextView name = (TextView) v.findViewById(R.id.person_name);

                }
            });
        }
    }


    public HomeRecycleAdapter(Context mContext, List<HomeCardView> albumList) {
        this.mContext = mContext;
        this.menuList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_viewer_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        HomeCardView album = menuList.get(position);
        holder.description.setText(album.getDescription());
        holder.thumbnail.setImageBitmap(album.getThumbnail());
    }


    @Override
    public int getItemCount() {
        return menuList.size();
    }
}
