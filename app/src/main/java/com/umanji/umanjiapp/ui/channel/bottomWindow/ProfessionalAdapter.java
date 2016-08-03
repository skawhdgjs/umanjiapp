package com.umanji.umanjiapp.ui.channel.bottomWindow;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.umanji.umanjiapp.R;
import com.umanji.umanjiapp.helper.Helper;
import com.umanji.umanjiapp.model.ChannelData;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by paul on 7/6/16.
 */
public class ProfessionalAdapter extends RecyclerView.Adapter<ProfessionalAdapter.ViewHolder> {
    private static final String TAG = "ProfessionalAdapter";
    private Context mContext;

    protected static Activity mActivity;
    private String[] mDataSet;
    private ArrayList<ChannelData> mChannels;

    public static ChannelData mChannel;

    protected int mCurrentPage = 0;
    private int count = 1;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public ProfessionalAdapter(String[] dataSet) {
        mDataSet = dataSet;
    }

    public ProfessionalAdapter(Activity activity, Context context, ArrayList<ChannelData> channelData) {
        this.mActivity = activity;
        this.mContext = context;
        mChannels = channelData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bottom_card_professional, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
//************************************************************************************************** KeywordCommunityMode
        viewHolder.getTalkCard().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChannels.get(position).getParent() != null) {
                    mChannel = mChannels.get(position).getParent();
                    Helper.startActivity(mActivity, mChannel);
                } else {
                    Toast.makeText(mActivity, "준비중입니다", Toast.LENGTH_SHORT).show();
                }

            }
        });
//************************************************************************************************** userPhoto
        if (mChannels.get(position).getOwner().getPhoto() != null) {
            String userPhoto = mChannels.get(position).getOwner().getPhoto();

            Glide.with(mContext)
                    .load(userPhoto)
                    .thumbnail(1f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.getUserPhoto());

        } else {
            Glide.with(mContext)
                    .load(R.drawable.user_default)
                    .thumbnail(1f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.getUserPhoto());
        }
//************************************************************************************************** userName
        viewHolder.getUserName().setText(mChannels.get(position).getOwner().getUserName());

//************************************************************************************************** organization
        if (mChannels.get(position).getParent() != null) {
            viewHolder.getParentName().setText(mChannels.get(position).getParent().getName());
        } else {

        }
//************************************************************************************************** rank

        String rank = Integer.toString(count);
        viewHolder.getRank().setText(rank);
        count++;

//**************************************************************************************************

    }

    public void SortedResult() {

    }

    public ArrayList<ChannelData> getDocs() {
        return mChannels;
    }

    public void resetDocs() {
        mChannels = new ArrayList<ChannelData>();
    }

    public void setCurrentPage(int mCurrentPage) {
        this.mCurrentPage = mCurrentPage;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void addBottom(ChannelData doc) {
        mChannels.add(doc);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (mChannels == null) {
            return 0;
        } else {
            return mChannels.size();
        }
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final RelativeLayout mTalkCard;
        private final TextView mRank;
        private final RoundedImageView mUserPhoto;
        private final TextView mUserName;
        private final TextView mOrganization;


        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");

                }
            });


            mTalkCard = (RelativeLayout) v.findViewById(R.id.talkCard);
            mRank = (TextView) v.findViewById(R.id.rank);
            mUserPhoto = (RoundedImageView) v.findViewById(R.id.userPhoto);
            mUserName = (TextView) v.findViewById(R.id.userName);
            mOrganization = (TextView) v.findViewById(R.id.organization);  // parentName

        }

        public RelativeLayout getTalkCard() {
            return mTalkCard;
        }

        public TextView getRank() {
            return mRank;
        }

        public RoundedImageView getUserPhoto() {
            return mUserPhoto;
        }

        public TextView getUserName() {
            return mUserName;
        }

        public TextView getParentName() {
            return mOrganization;
        }
    }
}