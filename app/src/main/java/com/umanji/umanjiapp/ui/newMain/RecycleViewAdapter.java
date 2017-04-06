package com.umanji.umanjiapp.ui.newMain;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nam on 2017. 3. 1..
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>{
    private static final String TAG = "RecycleViewAdapter";

    private Context mContext;
    protected static Activity mActivity;
    private ArrayList<ChannelData> mChannels;

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

        holder.getBox().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChannels.get(position) != null) {
                    mChannel = mChannels.get(position);
                    Helper.startActivity(mActivity, mChannel);
                } else {
                    Toast.makeText(mActivity, "준비중입니다", Toast.LENGTH_SHORT).show();
                }

            }
        });

        holder.getText().setText(mChannels.get(position).getName());
        if(mChannels.get(position).getPhoto() != null) {
            String postPhoto = mChannels.get(position).getPhoto();

            Glide.with(mContext)
                    .load(postPhoto)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.getImage());

        }

/*

        if (mChannels.get(position).getKeywords() != null) {
            String[] keywords = mChannels.get(position).getKeywords();
            holder.getKeyword().setText(keywords[0]);
        } else {
            holder.getKeyword().setText("일반모임");
        }
*/

//        holder.getName().setText(mChannels.get(position).getName());

//        holder.getAddressShort().setText(mChannels.get(position).getAdminArea());

        if (mChannels.get(position).getParent() != null) {
            String parentType = mChannels.get(position).getParent().getType();
            if (parentType.equals("POST")) {
//                holder.getParentName().setText("댓글");
            }
//            holder.getParentName().setText(mChannels.get(position).getParent().getName());
        } else {
//            holder.getParentName().setText("일반장소");
        }


        JSONObject descObject = new JSONObject();
        if (mChannels.get(position).getDesc() != null){
            descObject = mChannels.get(position).getDesc();
            String title = "";
            if(descObject != null && descObject.optString("description") != null){
                title = descObject.optString("description");
//                holder.getExplain().setText(title);
            }
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
        private final ImageView mUserPhoto;
        private final TextView mName;
        private final TextView mKeyword;
        private final TextView mAddressShort;
        private final TextView mParentName;
        private final TextView mExplain;


        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Paul", "Element " + getAdapterPosition() + " clicked.");
                }
            });

            mUserPhoto = (ImageView) itemView.findViewById(R.id.userPhoto);
            mKeyword = (TextView) itemView.findViewById(R.id.keyword);
            mName = (TextView) itemView.findViewById(R.id.name);
            mParentName = (TextView) itemView.findViewById(R.id.parentName);
            mAddressShort = (TextView) itemView.findViewById(R.id.address_short);
            mExplain = (TextView) itemView.findViewById(R.id.explain);





            mBox = (RelativeLayout) itemView.findViewById(R.id.Box_newMain);
            mImage = (ImageView) itemView.findViewById(R.id.dataPicture_post);
            mText = (TextView) itemView.findViewById(R.id.data_post);

            mBox.setBackgroundColor(Color.parseColor("#ffffff"));

        }

        public RelativeLayout getBox() { return mBox; }
        public ImageView getImage() {
            return mImage;
        }
        public TextView getText() {
            return mText;
        }

        public TextView getName() {
            return mName;
        }

        public TextView getKeyword() {
            return mKeyword;
        }

        public TextView getParentName() {
            return mParentName;
        }

        public TextView getAddressShort() {
            return mAddressShort;
        }

        public TextView getExplain() {
            return mExplain;
        }
    }


}
