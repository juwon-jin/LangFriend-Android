package makejin.langfriend.juwon.Utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import makejin.langfriend.R;


public class PopupRequest extends Activity {

    Button BT_yes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_request);

        BT_yes = (Button) findViewById(R.id.BT_yes);

        BT_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }


}
