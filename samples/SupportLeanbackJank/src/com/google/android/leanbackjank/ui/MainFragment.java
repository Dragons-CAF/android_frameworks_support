/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.google.android.leanbackjank.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;

import com.google.android.leanbackjank.IntentDefaults;
import com.google.android.leanbackjank.IntentKeys;
import com.google.android.leanbackjank.R;
import com.google.android.leanbackjank.data.VideoProvider;
import com.google.android.leanbackjank.model.VideoInfo;
import com.google.android.leanbackjank.presenter.CardPresenter;
import com.google.android.leanbackjank.presenter.GridItemPresenter;
import com.google.android.leanbackjank.presenter.HeaderItemPresenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main class to show BrowseFragment with header and rows of videos
 */
public class MainFragment extends BrowseFragment {

    private BackgroundManager mBackgroundManager;
    private ArrayObjectAdapter mRowsAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Define defaults.
        int categoryCount = IntentDefaults.CATEGORY_COUNT;
        int entriesPerCat = IntentDefaults.ENTRIES_PER_CATEGORY;
        boolean disableShadows = IntentDefaults.DISABLE_SHADOWS;
        int cardWidth = IntentDefaults.CARD_WIDTH;
        int cardHeight = IntentDefaults.CARD_HEIGHT;
        boolean playVideo = IntentDefaults.PLAY_VIDEO;
        boolean useSingleBitmap = IntentDefaults.USE_SINGLE_BITMAP;

        Intent intent = getActivity().getIntent();
        if (intent.getExtras() != null) {
            categoryCount = intent.getIntExtra(IntentKeys.CATEGORY_COUNT, categoryCount);
            entriesPerCat = intent.getIntExtra(IntentKeys.ENTRIES_PER_CATEGORY, entriesPerCat);
            disableShadows = intent.getBooleanExtra(IntentKeys.DISABLE_SHADOWS, disableShadows);
            cardWidth = intent.getIntExtra(IntentKeys.CARD_WIDTH, cardWidth);
            cardHeight = intent.getIntExtra(IntentKeys.CARD_HEIGHT, cardHeight);
            playVideo = intent.getBooleanExtra(IntentKeys.PLAY_VIDEO, playVideo);
            useSingleBitmap = intent.getBooleanExtra(IntentKeys.USE_SINGLE_BITMAP, useSingleBitmap);
        }

        loadVideoData(categoryCount, entriesPerCat, disableShadows, useSingleBitmap, cardWidth,
                cardHeight);
        setBackground();
        setupUIElements();

        if (playVideo) {
            Uri uri = Uri.parse("android.resource://" + getContext().getPackageName() + "/"
                    + R.raw.testvideo_1080p_60fps);
            Intent videoIntent = new Intent(Intent.ACTION_VIEW, uri, getContext(),
                    VideoActivity.class);
            startActivity(videoIntent);
        }
    }

    private void setBackground() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mBackgroundManager.setDrawable(
                getResources().getDrawable(R.drawable.default_background, null));
    }

    private void setupUIElements() {
        setBadgeDrawable(getActivity().getResources().getDrawable(R.drawable.app_banner, null));
        // Badge, when set, takes precedent over title
        setTitle(getString(R.string.browse_title));
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        // set headers background color
        setBrandColor(getResources().getColor(R.color.jank_yellow));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));

        setHeaderPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object o) {
                return new HeaderItemPresenter();
            }
        });
    }

    private void loadVideoData(int categoryCount, int entriesPerCat, boolean disableShadows,
            boolean useSingleBitmap, int cardWidth, int cardHeight) {
        ListRowPresenter listRowPresenter = new ListRowPresenter();
        listRowPresenter.setShadowEnabled(!disableShadows);
        mRowsAdapter = new ArrayObjectAdapter(listRowPresenter);
        HashMap<String, List<VideoInfo>> data = VideoProvider.buildMedia(categoryCount,
                entriesPerCat, cardWidth, cardHeight, getContext(), useSingleBitmap);
        CardPresenter cardPresenter = new CardPresenter(cardWidth, cardHeight);

        int i = 0;
        for (Map.Entry<String, List<VideoInfo>> entry : data.entrySet()) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            for (VideoInfo videoInfo : entry.getValue()) {
                listRowAdapter.add(videoInfo);
            }
            HeaderItem header = new HeaderItem(i++, entry.getKey());
            mRowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        ArrayObjectAdapter settingsListAdapter = new ArrayObjectAdapter(new GridItemPresenter());
        for (int j = 0; j < entriesPerCat; j++) {
            settingsListAdapter.add("Settings " + j);
        }
        HeaderItem settingsHeader = new HeaderItem(i++, "Settings");
        mRowsAdapter.add(new ListRow(settingsHeader, settingsListAdapter));

        setAdapter(mRowsAdapter);
    }
}
