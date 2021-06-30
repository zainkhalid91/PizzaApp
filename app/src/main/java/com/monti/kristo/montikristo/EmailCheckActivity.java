package com.monti.kristo.montikristo;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EmailCheckActivity extends AppCompatActivity {

    Button vemail, btn_back;
    String email;
    TextView head1, head2;
    Typeface myFont, myFontReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // below code provide full screen mode
       /* this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/
        setContentView(R.layout.activity_email_check);

        myFont = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Bold.ttf");
        myFontReg = Typeface.createFromAsset(this.getAssets(), "fonts/SFUIDisplay-Regular.ttf");

        head1 = findViewById(R.id.header1);
        head2 = findViewById(R.id.header2);

        head1.setTypeface(myFont);
        head2.setTypeface(myFontReg);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            email = extras.getString("email");
        }

        // btn_back = (Button) findViewById(R.id.btn_back_chkemail);
        vemail = findViewById(R.id.btn_sent_vemail);
        vemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), VerificationCodeActivity.class);
                intent.putExtra("email", email);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

       /* btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), LoginScreenActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });*/
    }

    @Override
    public void onBackPressed() {

    }
}
