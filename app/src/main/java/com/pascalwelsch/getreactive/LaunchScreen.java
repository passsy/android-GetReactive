package com.pascalwelsch.getreactive;

import com.pascalwelsch.getreactive.databinding.ActivityLaunchBinding;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by pascalwelsch on 11/25/15.
 */
public class LaunchScreen extends Activity {

    public static final Uri YOUTUBE_URI = Uri.parse("https://www.youtube.com/watch?v=ssC4nX_pP3o");

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final ActivityLaunchBinding binding = DataBindingUtil
                .setContentView(this, R.layout.activity_launch);

        binding.androidWay.setOnClickListener(v -> startActivity(
                RepoListActivity.newInstance(this, RepoListActivity.METHOD_THE_ANDROID_WAY)));
        binding.rxBeginnery.setOnClickListener(v -> startActivity(
                RepoListActivity.newInstance(this, RepoListActivity.METHOD_RX_BEGINNER)));
        binding.rxExpert.setOnClickListener(v -> startActivity(
                RepoListActivity.newInstance(this, RepoListActivity.METHOD_RX_EXPERT)));

        binding.videoFrame.setOnClickListener(
                v -> startActivity(new Intent(Intent.ACTION_VIEW, YOUTUBE_URI)));
    }
}
