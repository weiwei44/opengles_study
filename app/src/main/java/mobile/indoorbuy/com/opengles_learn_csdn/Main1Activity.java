package mobile.indoorbuy.com.opengles_learn_csdn;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import mobile.indoorbuy.com.opengles_learn_csdn.egl.EGLView1;

/**
 * Created by BMW on 2018/7/11.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Main1Activity extends AppCompatActivity {

    private EGLView1 egl1;
    private TextView sample;
    private TextView sample1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        //EGLView1 view1 = new EGLView1(this,null);

        egl1 = findViewById(R.id.egls);
        Log.e("weiwei", egl1.toString());
        sample = findViewById(R.id.sample_take);
        sample1 = findViewById(R.id.sample_stop);
        sample.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                egl1.setRecord(true);
            }
        });

        sample1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                egl1.setRecord(false);
            }
        });
    }
}
