package com.umanji.umanjiapp.ui.newMain;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.model.ChannelData;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by nam on 2017. 3. 1..
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>{
    private static final String TAG = "RecycleViewAdapter";

    private Context mContext;
    protected static Activity mActivity;
    private ArrayList<ChannelData> mChannels = new ArrayList<>();

    public static ChannelData mChannel;

    private String[] mDataSet;

    protected int mCurrentPage = 0;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public RecycleViewAdapter(String[] dataSet) {
        mDataSet = dataSet;
    }


    public RecycleViewAdapter(Activity activity, Context context, ArrayList<ChannelData> channelData){
        this.mActivity = activity;
        this.mContext = context;
        mChannels = channelData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listview_post, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG,"Element"+position + " set.");

        holder.getText().setText(mChannels.get(position).getName());
        if(mChannels.get(position).getPhoto() != null) {
            String postPhoto = mChannels.get(position).getPhoto();

            Glide.with(mContext)
                    .load(postPhoto)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.getImage());

        }


    }

    @Override
    public int getItemCount() {
        if(mChannels.size() == 0)
            return 0;
        else
            return mChannels.size();
    }

    public ArrayList<ChannelData> getDocs() {
        return mChannels;
    }

    public void resetDocs() {mChannels = new ArrayList<ChannelData>(); }

    public void setCurrentPage(int mCurrentPage) { this.mCurrentPage = mCurrentPage ; }

    public int getCurrentPage() { return mCurrentPage; }

    public void addBottom(ChannelData doc) {mChannels.add(doc); }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final RelativeLayout mBox;
        private final ImageView mImage;
        private final TextView mText;


        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");

                }
            });

            mBox = (RelativeLayout) itemView.findViewById(R.id.Box_newMain);
            mImage = (ImageView) itemView.findViewById(R.id.dataPicture_post);
            mText = (TextView) itemView.findViewById(R.id.data_post);

        }

        public RelativeLayout getBox() { return mBox; }
        public ImageView getImage() {
            return mImage;
        }
        public TextView getText() {
            return mText;
        }
    }


}
