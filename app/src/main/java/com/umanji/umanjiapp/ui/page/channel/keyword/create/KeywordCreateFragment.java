package com.umanji.umanjiapp.ui.page.channel.keyword.create;

import android.os.Bundle;
import android.text.TextUtils;

import com.umanji.umanjiapp.ui.base.BaseChannelCreateFragment;


public class KeywordCreateFragment extends BaseChannelCreateFragment {
    private static final String TAG = "CommunityCreateFragment";

    public static KeywordCreateFragment newInstance(Bundle bundle) {
        KeywordCreateFragment fragment = new KeywordCreateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCreateApiName = api_links_createKeyword;
        mType = TYPE_KEYWORD;
    }
}
