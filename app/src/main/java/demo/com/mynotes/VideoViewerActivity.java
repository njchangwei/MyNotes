package demo.com.mynotes;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.VideoView;

import java.io.File;

/**
 * Created by Administrator on 2017/6/30.
 */

public class VideoViewerActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vv = new VideoView(this);
        setContentView(vv);

        String path = getIntent().getStringExtra(EXTRA_PATH);
        File video = new File(path);
        if (video.exists()) {
            vv.setVideoPath(video.getAbsolutePath());
            vv.requestFocus();
            vv.start();
        }else{
            finish();
        }

    }

    private VideoView vv;
    public static final String EXTRA_PATH = "path";
}
