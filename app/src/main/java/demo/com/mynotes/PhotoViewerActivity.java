package demo.com.mynotes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/6/30.
 */

public class PhotoViewerActivity extends Activity {

    private ImageView iv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iv = new ImageView(this);
        setContentView(iv);

        String path = getIntent().getStringExtra(EXTRA_PATH);
        if (path != null) {
            try {
                Intent intent = getIntent();
                Uri imageUri = Uri.parse(path);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(), options);
                iv.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();;
            }

        } else {
            finish();
        }


    }

    public static final String EXTRA_PATH = "path";
}
