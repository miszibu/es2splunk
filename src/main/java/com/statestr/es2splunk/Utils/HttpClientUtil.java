package com.statestr.es2splunk.Utils;

import sun.misc.BASE64Encoder;

import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Base64;


import org.apache.log4j.Logger;

import javax.net.ssl.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64.Encoder;

public class HttpClientUtil {
    private static final Logger logger = Logger.getLogger(HttpClientUtil.class);
    private URL url = null;
    private int responseCode;

    public HttpClientUtil(String urlStr) {
        try {
            this.url = new URL(urlStr);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param basicAuth BasicAuthorization String
     * @return String    Response String
     * @throws Exception
     */
    public String POST(String basicAuth, byte[] payload) throws Exception {

        HttpsURLConnection conn = null;
        BufferedReader responseReader = null;

        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            conn = (HttpsURLConnection) createURLConnection(url);
            conn.setRequestMethod("POST");
            if (basicAuth != null && basicAuth.length() > 0) {
                conn.setRequestProperty("Authorization", basicAuth);
            }
            if (true)
                conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Content-Type", "text/plain");//e.g. text/plain
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,*/*");//e.g. text/plain
            //conn.setUseCaches(false); //Not required
            conn.setDoInput(true); //This is to send input to service
            conn.setDoOutput(true); //If you want output to be read
            conn.setReadTimeout(30000);//set timeout for hung service
            conn.setConnectTimeout(10000); //set timeout for connection

            long reqStartTime = System.currentTimeMillis();
            conn.getOutputStream().write(payload);
            conn.setSSLSocketFactory(conn.getSSLSocketFactory());
            conn.connect();
            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };


            conn.setDefaultHostnameVerifier(allHostsValid);


            int responseCode = conn.getResponseCode();
            this.responseCode = responseCode;
            logger.debug("Response code " + responseCode);
            responseReader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = responseReader.readLine()) != null) {
                response.append(inputLine);
            }
            logger.info("Time taken by call - " + (System.currentTimeMillis() - reqStartTime));
            return response.toString();
        } catch (Exception exc) {
            logger.error("Error is calling service " + exc.getMessage());
            throw exc;
        } finally {
            if (conn != null && responseReader != null) {
                responseReader.close();
                conn.disconnect();
            }
        }
    }

    public String GET() throws Exception {
        HttpURLConnection conn = null;
        BufferedReader responseReader = null;
        try {
            conn = createURLConnection(url);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "text/plain");//e.g. text/plain
            conn.setRequestProperty("Accept", "text/plain");//e.g. text/plain

            BASE64Encoder enc = new BASE64Encoder();
            String userpassword = System.getProperty("elastic");
            //String userpassword = "elastic:logaggPa$$word";
            //String userpassword = "elastic:changeme";
            String encodedAuthorization = enc.encode(userpassword.getBytes());
            conn.setRequestProperty("Authorization", "Basic " + encodedAuthorization);
            //conn.setUseCaches(false); //Not required
            conn.setDoInput(true); //This is to send input to service
            conn.setDoOutput(true); //If you want output to be read
            conn.setReadTimeout(10000);//set timeout for hung service
            conn.setConnectTimeout(10000); //set timeout for connection
            conn.connect();
            long starttime = System.currentTimeMillis();
            //conn.getOutputStream().write(payload.getBytes("UTF8"));

            int responseCode = conn.getResponseCode();
            this.responseCode = responseCode;
            logger.debug("Response code " + responseCode);
            responseReader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = responseReader.readLine()) != null) {
                response.append(inputLine);
            }
            logger.info("Time taken by call - " + (System.currentTimeMillis() - starttime));
            return response.toString();
        } catch (Exception exc) {
            logger.error("Error is calling service " + exc.getMessage());
            throw exc;
        } finally {
            if (conn != null && responseReader != null) {
                responseReader.close();
                conn.disconnect();
            }
        }
    }

    public String GETXMLWithBasicAuth(String username, String password) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader responseReader = null;
        try {
            conn = createURLConnection(url);
            String rawStr = username + ":" + password;
            Encoder encoder = Base64.getEncoder();
            byte[] encodedStr = encoder.encode(rawStr.getBytes());
            String authEncoded = new String(encodedStr);

            conn.setRequestProperty("Authorization", "Basic " + authEncoded);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/xml");//e.g. text/plain
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");//e.g. text/plain
            //conn.setUseCaches(false); //Not required
            conn.setDoInput(true); //This is to send input to service
            conn.setDoOutput(true); //If you want output to be read
            conn.setReadTimeout(10000);//set timeout for hung service
            conn.setConnectTimeout(10000); //set timeout for connection
            conn.connect();
            long starttime = System.currentTimeMillis();
            //conn.getOutputStream().write(payload.getBytes("UTF8"));

            int responseCode = conn.getResponseCode();
            logger.debug("Response code " + responseCode);
            responseReader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = responseReader.readLine()) != null) {
                response.append(inputLine);
            }
            logger.info("Time taken by call - " + (System.currentTimeMillis() - starttime));
            return response.toString();
        } catch (Exception exc) {
//    		logger.error("Error is calling service "+exc.getMessage(),exc);
            logger.error("Error is calling service " + exc.getMessage());
            throw exc;
        } finally {
            if (conn != null && responseReader != null) {
                responseReader.close();
                conn.disconnect();
            }
        }

    }

    public String GETJSONWithParameters(String method, String basicAuth) throws Exception {
        HttpURLConnection conn = null;
        BufferedReader responseReader = null;
        try {

            conn = createURLConnection(url);
            conn.setRequestMethod(method);
            if (basicAuth != null && basicAuth.length() > 0) {
                conn.setRequestProperty("Authorization", basicAuth);
            }
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Content-Type", "text/plain");//e.g. text/plain
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,*/*");//e.g. text/plain
            //conn.setUseCaches(false); //Not required
            conn.setDoInput(true); //This is to send input to service
            conn.setDoOutput(true); //If you want output to be read
            conn.setReadTimeout(30000);//set timeout for hung service
            conn.setConnectTimeout(10000); //set timeout for connection
            conn.connect();
            long starttime = System.currentTimeMillis();
            //conn.getOutputStream().write(payload.getBytes("UTF8"));

            int responseCode = conn.getResponseCode();
            this.responseCode = responseCode;
            logger.debug("Response code " + responseCode);
            responseReader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = responseReader.readLine()) != null) {
                response.append(inputLine);
            }
            logger.info("Time taken by call - " + (System.currentTimeMillis() - starttime));
            return response.toString();
        } catch (Exception exc) {
            logger.error("Error is calling service " + exc.getMessage());
            throw exc;
        } finally {
            if (conn != null && responseReader != null) {
                responseReader.close();
                conn.disconnect();
            }
        }
    }

    public String GETJSON() throws Exception {
        return GETJSONWithParameters("GET", null);
    }

    public int GETResponseCode() {
        return this.responseCode;
    }

    private HttpURLConnection createURLConnection(URL url) throws IOException {
        HttpURLConnection conn = null;
        conn = (HttpURLConnection) url.openConnection();
        return conn;
    }

    public static void main(String args[]) {
        HttpClientUtil client = new HttpClientUtil("http://jabpl3261:4410/QuartzManager/rest/MonitorService/puased-quartz-jobs");
        try {
            //System.out.println("Response:"+client.GET());
            System.out.println("Response:" + client.GETJSON());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
