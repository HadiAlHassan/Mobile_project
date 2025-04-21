package com.example.mobile_project_hza2m;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> mHeaders;

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.mHeaders = new HashMap<>();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data;boundary=" + boundary;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            // Text parameters
            for (Map.Entry<String, String> entry : getParams().entrySet()) {
                bos.write(("--" + boundary + LINE_FEED).getBytes());
                bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_FEED).getBytes());
                bos.write((LINE_FEED).getBytes());
                bos.write((entry.getValue() + LINE_FEED).getBytes());
            }

            // File parameters
            Map<String, DataPart> data = getByteData();
            for (Map.Entry<String, DataPart> entry : data.entrySet()) {
                DataPart file = entry.getValue();
                bos.write(("--" + boundary + LINE_FEED).getBytes());
                bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() +
                        "\"; filename=\"" + file.getFileName() + "\"" + LINE_FEED).getBytes());
                bos.write(("Content-Type: " + file.getType() + LINE_FEED).getBytes());
                bos.write((LINE_FEED).getBytes());

                bos.write(file.getContent());

                bos.write((LINE_FEED).getBytes());
            }

            bos.write(("--" + boundary + "--" + LINE_FEED).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bos.toByteArray();
    }

    @Override
    protected Response<NetworkResponse> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(NetworkResponse response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(com.android.volley.VolleyError error) {
        mErrorListener.onErrorResponse(error);
    }

    public Map<String, String> getParams() throws AuthFailureError {
        return new HashMap<>();
    }

    public Map<String, DataPart> getByteData() throws AuthFailureError {
        return new HashMap<>();
    }

    public static class DataPart {
        private final String fileName;
        private final byte[] content;
        private final String type;

        public DataPart(String name, byte[] data) {
            this(name, data, "application/octet-stream");
        }

        public DataPart(String name, byte[] data, String type) {
            fileName = name;
            content = data;
            this.type = type;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getType() {
            return type;
        }
    }

    private static final String LINE_FEED = "\r\n";
    private final String boundary = "apiclient-" + System.currentTimeMillis();
}
