package makejin.langfriend.juwon.TabActivity.Tab5MyPage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import makejin.langfriend.R;
import makejin.langfriend.juwon.Utils.Constants.Constants;


public class PrivacyRule extends AppCompatActivity {
    WebView webView;
    Button BT_X;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_webview);

        webView = (WebView)findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());//클릭시 새창 안뜸
        webView.loadUrl(Constants.API_BASE_URL+"/privacy_rule");

        title = (TextView)findViewById(R.id.title);
        title.setText("LangFriend 개인정보 취급 방침");
        Toolbar cs_toolbar = (Toolbar)findViewById(R.id.cs_toolbar);
        setSupportActionBar(cs_toolbar);
        getSupportActionBar().setTitle("개인정보 취급 방침");

        BT_X = (Button) findViewById(R.id.BT_X);

        BT_X.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
