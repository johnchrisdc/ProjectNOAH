package xyz.jcdc.projectnoah.helper;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jcdc on 9/3/2016.
 */

public class DateHelper {

    public static void loadDateToTextView(TextView textView){
        new SNTP().execute(textView);
    }

    private static class SNTP extends AsyncTask<TextView, Void, Date> {
        TextView textView;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Date doInBackground(TextView... textViews) {
            textView = textViews[0];

            SntpClient client = new SntpClient();
            if (client.requestTime("0.sg.pool.ntp.org", 30000)) {
                long now = client.getNtpTime() + SystemClock.elapsedRealtime() -
                        client.getNtpTimeReference();
                Date current = new Date(now);
                Log.i("NTP tag", current.toString());
                return current;
            }else{
                Log.i("NTP tag", "Failed");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Date date) {
            super.onPostExecute(date);
            if (date != null){
                SimpleDateFormat ft = new SimpleDateFormat ("MMM dd, EEEE");
                textView.setText(ft.format(date));
            }
        }
    }

}
