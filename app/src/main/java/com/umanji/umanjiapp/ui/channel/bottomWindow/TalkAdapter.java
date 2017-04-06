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
public class TalkAdapter extends RecyclerView.Adapter<TalkAdapter.ViewHolder> {
    private static final String TAG = "TalkAdapter";
    private Context mContext;

    protected static Activity mActivity;
    private String[] mDataSet;
    private ArrayList<ChannelData> mChannels;

    public static ChannelData mChannel;

    protected int mCurrentPage = 0;

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public TalkAdapter(String[] dataSet) {
        mDataSet = dataSet;
    }

    public TalkAdapter(Activity activity, Context context, ArrayList<ChannelData> channelData) {
        this.mActivity = activity;
        this.mContext = context;
        mChannels = channelData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bottom_card_talk, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element

        viewHolder.getTalkCard().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChannels.get(position).getParent() != null) {
                    mChannel = mChannels.get(position).getParent();
                    String[] passKeywords = mChannel.getKeywords();
                    if (passKeywords != null && passKeywords[0] != null) {
                        Helper.startKeywordActivity(mActivity, mChannel, passKeywords[0], "BottomList");
                    } else {
                        Helper.startActivity(mActivity, mChannel, "BottomList");
                    }

                } else {        // 발자취 흔적
                    mChannel = mChannels.get(position);
                    Helper.startActivity(mActivity, mChannel, "BottomList");
                }

            }
        });
        if (mChannels.get(position).getOwner().getPhoto() != null) {
            String userPhoto = mChannels.get(position).getOwner().getPhoto();
//            Picasso.with(mContext).load(userPhoto).into(viewHolder.getUserPhoto());

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

        viewHolder.getUserName().setText(mChannels.get(position).getOwner().getUserName());
        viewHolder.getName().setText(mChannels.get(position).getName());
//        viewHolder.getParentType().setText(mChannels.get(position).getParent().getType());


        if (mChannels.get(position).getParent() != null) {
            String parentType = mChannels.get(position).getParent().getType();
            if (parentType.equals("POST")) {
                viewHolder.getParentName().setText("댓글");
            } else {
                String preName = mChannels.get(position).getParent().getName();
                String name ;
                if (preName.length() >= 10){
                    name = preName.substring(0, 10) + "..";
                } else if(preName.length() == 0) {
                    name = "일반장소";
                } else {
                    name = preName;
                }
                viewHolder.getParentName().setText(name);
            }

        } else {
            viewHolder.getParentName().setText("발자취 흔적");
        }

        String dateString = mChannels.get(position).getCreatedAt();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date parsedDate = dateFormat.parse(dateString);
            Timestamp timestamp = new Timestamp(parsedDate.getTime());
            viewHolder.getCreatedAt().setText(Helper.toPrettyDate(timestamp.getTime()));
        } catch (Exception e) {
            Log.e(TAG, "error " + e.toString());
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
        private final RoundedImageView mUserPhoto;
        private final TextView mUserName;
        private final TextView mName;
        //        private final TextView mParentType;
        private final TextView mParentName;
        private final TextView mCreatedAt;


        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("Paul", "Element " + getAdapterPosition() + " clicked.");

                }
            });


            mTalkCard = (RelativeLayout) v.findViewById(R.id.talkCard);
            mUserPhoto = (RoundedImageView) v.findViewById(R.id.userPhoto);
            mUserName = (TextView) v.findViewById(R.id.userName);
            mName = (TextView) v.findViewById(R.id.name);
//            mParentType = (TextView) v.findViewById(R.id.parentType);
            mParentName = (TextView) v.findViewById(R.id.parentName);
            mCreatedAt = (TextView) v.findViewById(R.id.createdAt);

        }

        public RelativeLayout getTalkCard() {
            return mTalkCard;
        }

        public RoundedImageView getUserPhoto() {
            return mUserPhoto;
        }

        public TextView getUserName() {
            return mUserName;
        }

        public TextView getName() {
            return mName;
        }

        /*
               public TextView getParentType() {
                   return mParentType;
               }
          */
        public TextView getParentName() {
            return mParentName;
        }

        public TextView getCreatedAt() {
            return mCreatedAt;
        }
    }
}