package jahirfiquitiva.apps.iconshowcase.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by 7681 on 2016-02-03.
 */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = new Intent(HomeActivity.this, jahirfiquitiva.iconshowcase.activities.ShowcaseActivity.class);
        startActivity(intent);

        finish();

    }

}