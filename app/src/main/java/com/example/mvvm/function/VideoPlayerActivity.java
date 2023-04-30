package com.example.mvvm.function;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mvvm.R;
import com.example.mvvm.databinding.ActivityVideoPlayerBinding;
import com.example.mvvm.model.Films;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VideoPlayerActivity extends AppCompatActivity {
    ExoPlayer player;
    Boolean flag = false;
    TextView textViewName;
    ImageButton controllerOrientation,controllerReplay,controllerPlay,controllerPause,controllerForward,controllerBack,controllerMore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityVideoPlayerBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_video_player);

        Intent intent = getIntent();
        String video = intent.getStringExtra("video");
        String name = intent.getStringExtra("name");

        binding.setVideoplayer( new VideoPlayerVM());
        binding.executePendingBindings();
        binding.setLifecycleOwner(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        init_UI();

        textViewName.setText(name);

        Uri uri = Uri.parse(""+video);
//        Uri uri = Uri.parse("https://5b44cf20b0388.streamlock.net:8443/vod/smil:bbb.smil/playlist.m3u8");
        player = new ExoPlayer.Builder(this).build();
        binding.videoview.setPlayer(player);
        MediaItem item = MediaItem.fromUri(uri);
        player.setMediaItem(item);
        player.prepare();
        player.seekTo(C.TIME_UNSET);
        player.play();

        initalizeTimeBar();
        setHandler();

        controllerOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.videoview.getLayoutParams();
                if (flag){
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if (getSupportActionBar() != null)
                    {getSupportActionBar().show();}
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    params.height = params.WRAP_CONTENT;
                    params.width = params.MATCH_PARENT;
                    flag=false;
                } else {
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if (getSupportActionBar() != null){getSupportActionBar().hide();}
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    params.height = params.MATCH_PARENT;
                    params.width = params.MATCH_PARENT;
                    flag=true;
                }
            }
        });
        controllerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        controllerReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.seekTo(player.getCurrentPosition() - 10000);
            }
        });
        controllerForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.seekTo(player.getCurrentPosition() + 10000);
            }
        });
        controllerPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.play();
                controllerPause.setVisibility(View.VISIBLE);
                controllerPlay.setVisibility(View.GONE);
            }
        });
        controllerPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.pause();
                controllerPause.setVisibility(View.GONE);
                controllerPlay.setVisibility(View.VISIBLE);
            }
        });
        player.addListener(new Player.Listener() {
            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Player.Listener.super.onTimelineChanged(timeline, reason);
            }

            @Override
            public void onTracksChanged(Tracks tracks) {
                Player.Listener.super.onTracksChanged(tracks);
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Player.Listener.super.onLoadingChanged(isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                if (playbackState == Player.STATE_BUFFERING){
                    binding.progressbar.setVisibility(View.VISIBLE);
                }else if (playbackState == Player.STATE_READY){
                    binding.progressbar.setVisibility(View.GONE);
                }else {player.setPlayWhenReady(true);}
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Player.Listener.super.onRepeatModeChanged(repeatMode);
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                Player.Listener.super.onShuffleModeEnabledChanged(shuffleModeEnabled);
            }
        });
    }

    private void setHandler() {
    }

    private void initalizeTimeBar() {
    }

    private void init_UI() {
        controllerOrientation = (ImageButton) findViewById(R.id.controller_orientation);
        controllerBack = (ImageButton) findViewById(R.id.controller_back);
        controllerForward = (ImageButton) findViewById(R.id.controller_forward);
        controllerMore = (ImageButton) findViewById(R.id.controller_more);
        controllerPlay = (ImageButton) findViewById(R.id.controller_play);
        controllerPause = (ImageButton) findViewById(R.id.controller_pause);
        controllerReplay = (ImageButton) findViewById(R.id.controller_replay);

        textViewName = (TextView) findViewById(R.id.controller_nameofvideo);
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(false);
        player.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        player.setPlayWhenReady(true);
    }
}