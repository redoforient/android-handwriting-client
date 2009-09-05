package net.woft.handwriting;

import net.woft.handwriting.R.id;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HandwritingActivity extends Activity {

    FingerDrawView drawView;
    TextView resultView;

    private static final String LOGTAG = "HandwritingActivity";
    private static final String APP_URL = "http://cwritepad.appspot.com/reco/gb2312";
    private static final String APP_KEY = "11773edfd643f813c18d82f56a8103cd";
    private static final String RESP_ENC = "GB2312";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        drawView = (FingerDrawView)findViewById(id.draw_view);
        resultView = (TextView)findViewById(id.result);

        findViewById(id.draw_prompt).setOnClickListener(new OnClickListener(){
            public void onClick(View v) {
                drawView.dump();
            }
        });

        Button button;
        button = (Button)findViewById(id.reset_button);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                handleReset();
            }
        });

        button = (Button)findViewById(id.reco_button);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                handleReco();
            }
        });
    }

    private void handleReset() {
        drawView.resetView();
    }

    private void handleReco() {
        List<Point> points = drawView.getPoints();
        points.add(FingerDrawView.CHAR_END);
        String pointsData = pointsToString(points);
        //        Log.v(LOGTAG, pointsData);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("q", pointsData));
        params.add(new BasicNameValuePair("key", APP_KEY));
        String resp = null;
        try {
            resp = doHttpPost(APP_URL, params);
        } catch (IOException ioe) {
            Log.e(LOGTAG, "error reading server response.", ioe);
        }
        if (resp != null) {
            resultView.setText(resp);
        }
//        handleReset();
    }

    private String pointsToString(List<Point> points) {
        StringBuffer buf = new StringBuffer();
        buf.append('[');
        for (Point point : points) {
            buf.append(point.x);
            buf.append(", ");
            buf.append(point.y);
            buf.append(", ");
        }
        buf.setCharAt(buf.length() - 1, ']');
        return buf.toString();
    }

    private String doHttpPost(String urlString, List<NameValuePair> params) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(urlString);
        post.setEntity(new UrlEncodedFormEntity(params));
        HttpResponse response = client.execute(post);
        HttpEntity entity = response.getEntity();
        byte[] buf = new byte[512];
        int len = entity.getContent().read(buf);
        return new String(buf, 0, len, RESP_ENC);
    }
}
