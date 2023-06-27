package diplom_pack.client.client_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;

public class ExpActivity extends AppCompatActivity {

    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp);

        textView = findViewById(R.id.textView2);

        Bundle arguments = getIntent().getExtras();
        String name = arguments.get("url").toString();

        textView.setText(name);


    }
}