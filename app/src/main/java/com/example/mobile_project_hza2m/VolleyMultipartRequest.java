package com.example.mobile_project_hza2m;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.*;
import java.util.*;

public abstract class VolleyMultipartRequest extends Request<NetworkResponse> {

    private final Response.Listener<NetworkResponse> mListener;
    private final Response.ErrorListener mErrorListener;
    private final Map<String, String> headers;

    public VolleyMultipartRequest(int method, String url,
                                  Response.Listener<NetworkResponse> listener,
                                  Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.mListener = listener;
        this.mErrorListener = errorListener;
        this.headers = new HashMap<>();
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    protected abstract Map<String, DataPart> getByteData();

    @Override
    public String getBodyContentType() {
        return "multipart/form-data; boundary=" + BOUNDARY;
    }

    private static final String LINE_FEED = "\r\n";
    private static final String BOUNDARY = "VolleyBoundary_" + System.currentTimeMillis();

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DataOutputStream writer = new DataOutputStream(output);

        try {
            // Text
            for (Map.Entry<String, String> entry : getParams().entrySet()) {
                writer.writeBytes("--" + BOUNDARY + LINE_FEED);
                writer.writeBytes("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"" + LINE_FEED);
                writer.writeBytes(LINE_FEED);
                writer.writeBytes(entry.getValue() + LINE_FEED);
            }

            // File
            for (Map.Entry<String, DataPart> entry : getByteData().entrySet()) {
                DataPart part = entry.getValue();
                writer.writeBytes("--" + BOUNDARY + LINE_FEED);
                writer.writeBytes("Content-Disposition: form-data; name=\"" +
                        entry.getKey() + "\"; filename=\"" + part.getFileName() + "\"" + LINE_FEED);
                writer.writeBytes("Content-Type: " + part.getType() + LINE_FEED);
                writer.writeBytes(LINE_FEED);
                writer.write(part.getContent());
                writer.writeBytes(LINE_FEED);
            }

            writer.writeBytes("--" + BOUNDARY + "--" + LINE_FEED);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output.toByteArray();
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
    public void deliverError(VolleyError error) {
        mErrorListener.onErrorResponse(error);
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
}
