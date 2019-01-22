package org.mozilla.raptor_webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    // private static final String URL = "https://www.ncalexander.net/blog/";
    private static final String URL = "https://www.mozilla.org/en-US/";
    // private static final String URL = "https://www.ncalexander.net/hello.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final WebView webView = new WebView(this);
        webView.setWebContentsDebuggingEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);

        setContentView(webView);

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                Log.e("TEST", "onPageFinished: " + url);

                if (URL.equals(url)) {
                    Log.e("TEST", "onPageFinished: evaluating JS");
                    // measure.js is lightly hacked up in the given string -- only very small
                    // changes needed.  I'll extract this out to the main repo, potentially just
                    // pull it out of a Gecko profile, etc.
                    view.evaluateJavascript("javascript:" + BigString.MEASURE_JS + "; void(0);", null);
                }
            }
        });

        webView.loadUrl(URL);
    }
}
