package com.umanji.umanjiapp.ui.channel.bottomWindow;

import android.app.Activity;
import android.content.Context;
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
public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {
    private static final String TAG = "TalkAdapter";
    private Context mContext;

    protected static Activity mActivity;
    private String[] mDataSet;
    private ArrayList<ChannelData> mChannels;

    public static ChannelData mChannel;

    protected int mCurrentPage = 0;


    public CommunityAdapter(Activity activity, Context context, ArrayList<ChannelData> channelData) {
        this.mActivity = activity;
        this.mContext = context;
        mChannels = channelData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bottom_card_community, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

//        viewHolder.itemView.setTag(mChannels.get(position));

        // Get element from your dataset at this position and replace the contents of the view
        // with that element

        viewHolder.getTalkCard().setOnClickListener(new View.OnClickListener() {
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
        if (mChannels.get(position).getPhoto() != null) {
            String userPhoto = mChannels.get(position).getPhoto();
//            Picasso.with(mContext).load(userPhoto).into(viewHolder.getUserPhoto());

            Glide.with(mContext)
                    .load(userPhoto)
//                    .override(40, 100)
                    .thumbnail(1f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.getUserPhoto());

        } else {
            Glide.with(mContext)
                    .load(R.drawable.test_background)
                    .thumbnail(1f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.getUserPhoto());
        }


        if (mChannels.get(position).getKeywords() != null) {
            String[] keywords = mChannels.get(position).getKeywords();
            viewHolder.getKeyword().setText(keywords[0]);
        } else {
            viewHolder.getKeyword().setText("일반모임");
        }

        viewHolder.getName().setText(mChannels.get(position).getName());

        viewHolder.getName().setText(mChannels.get(position).getAdminArea());

        if (mChannels.get(position).getParent() != null) {
            String parentType = mChannels.get(position).getParent().getType();
            if (parentType.equals("POST")) {
                viewHolder.getParentName().setText("댓글");
            }
            viewHolder.getParentName().setText(mChannels.get(position).getParent().getName());
        } else {
            viewHolder.getParentName().setText("일반장소");
        }

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
        private final ImageView mUserPhoto;
        private final TextView mName;
        private final TextView mKeyword;
        private final TextView mAddressShort;
        private final TextView mParentName;


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
            mUserPhoto = (ImageView) v.findViewById(R.id.userPhoto);
            mKeyword = (TextView) v.findViewById(R.id.keyword);
            mName = (TextView) v.findViewById(R.id.name);
            mParentName = (TextView) v.findViewById(R.id.parentName);
            mAddressShort = (TextView) v.findViewById(R.id.address_short);

        }

        public RelativeLayout getTalkCard() {
            return mTalkCard;
        }

        public ImageView getUserPhoto() {
            return mUserPhoto;
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
    }
}