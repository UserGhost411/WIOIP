package Module;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by UserGhost411 on 03/11/2019.
 */

public class Requester {
    String urlnya;
    String idnya = "";
    HashMap<String, String> datapost;
    public String requestpost(HashMap<String, String> datapost, String myurl) throws IOException { return postsend(datapost,myurl,10,15); }
    public String requestpost(HashMap<String, String> datapost, String myurl, int rto) throws IOException { return postsend(datapost,myurl,rto,15); }
    public String requestpost(HashMap<String, String> datapost, String myurl, int rto, int cto) throws IOException { return postsend(datapost,myurl,rto, cto); }
    public String requestget(String myurl) throws IOException { return getsend(myurl,10,15); }
    public String requestget(String myurl, int rto) throws IOException { return getsend(myurl,rto,15); }
    public String requestget(String myurl, int rto, int cto) throws IOException { return getsend(myurl,rto, cto); }
    private String lastid(int t){
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < t) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
    public Requester(){
        idnya = lastid(5);
    }

    private String getsend(String myurl, int rto, int cto) throws IOException {
        InputStream is = null;
        int length = 999999;
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000 * rto /* milliseconds */);
            conn.setConnectTimeout(1000 * cto /* milliseconds */);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("appid","5ff8331b"+idnya);
            conn.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            conn.setRequestProperty("weather-version","5.0.7");
            conn.setRequestProperty("accept-encoding","");
            conn.setRequestProperty("accept-language","id-ID,id;q=0.9,en-US;q=0.8,en;q=0.7");
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            if(response!=200)return "{\"status\":\""+response+"\",\"data\":\""+ convertInputStreamToString(conn.getErrorStream(), length)+"\"}";
            is = conn.getInputStream();
            return convertInputStreamToString(is, length);
        } finally {
            if (is != null) is.close();
        }
    }
    private String postsend(HashMap<String, String> datapost, String myurl, int rto, int cto) throws IOException {
        InputStream is = null;
        int length = 999999;
        String data = URLEncoder.encode("postdata", "UTF-8") + "=" + URLEncoder.encode("bysystem", "UTF-8");
        for (Map.Entry me : datapost.entrySet()) {
            data += "&" + URLEncoder.encode(me.getKey().toString(), "UTF-8") + "=" + URLEncoder.encode(me.getValue().toString(), "UTF-8");
        }
        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000 * rto /* milliseconds */);
            conn.setConnectTimeout(1000 * cto /* milliseconds */);
            conn.setRequestProperty("appid","5ff8331b"+idnya);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            conn.connect();
            int response = conn.getResponseCode();
            if(response!=200)return "{\"status\":\""+response+"\"}";
            is = conn.getInputStream();
            return convertInputStreamToString(is, length);
        } finally {
            if (is != null) is.close();
        }
    }
    public String convertInputStreamToString(InputStream stream, int length) throws IOException, UnsupportedEncodingException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}

