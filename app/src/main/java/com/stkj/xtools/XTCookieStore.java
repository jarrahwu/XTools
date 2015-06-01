package com.stkj.xtools;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by jarrah on 2015/5/29.
 */
public class XTCookieStore implements CookieStore {

    public static final String PREFERENCE_NAME = "xt_cookie";
    public static final String KEY_COOKIE = "cookie";
    public static final String COOKIE_NAME_PREFIX = "xt_prefix";
    private SharedPreferences mSharedPreferences;

    private HashMap<URI, Set<HttpCookie>> mCookies;

    public XTCookieStore(Context context) {
        mCookies = new HashMap<URI, Set<HttpCookie>>();
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

        // Load any previously stored cookies into the store
        String storedCookieNames = mSharedPreferences.getString(KEY_COOKIE,
                null);
        if (storedCookieNames != null) {
            String[] cookieNames = TextUtils.split(storedCookieNames, ",");
            for (String name : cookieNames) {
                String encodedCookie = mSharedPreferences.getString(COOKIE_NAME_PREFIX
                        + name, null);
                if (encodedCookie != null) {
                    HttpCookie decodedCookie = decodeCookie(encodedCookie);
                    if (decodedCookie != null) {
                        Set<HttpCookie> cookies = new HashSet<HttpCookie>();
                        cookies.add(decodedCookie);
                        mCookies.put(URI.create(name), cookies);
                    }
                }
            }
        }
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        Log.from(this, "add cookie store :" + cookie);
        uri = cookiesUri(uri);
        if (mCookies.containsKey(uri)) {
            mCookies.get(uri).add(cookie);
        } else {
            HashSet<HttpCookie> domainCookies = new HashSet<HttpCookie>();
            domainCookies.add(cookie);
            mCookies.put(uri, domainCookies);
        }
        save2preference(uri, cookie);
    }


    @Override
    public List<HttpCookie> get(URI uri) {
        uri = cookiesUri(uri);
        if (mCookies.containsKey(uri)) {
            return new ArrayList<HttpCookie>(mCookies.get(uri));
        } else {
            return new ArrayList<HttpCookie>();
        }
    }

    @Override
    public List<HttpCookie> getCookies() {
        Log.from(this, "get all cookies");
        Set<HttpCookie> result = new HashSet<HttpCookie>();
        for (Set<HttpCookie> list : mCookies.values()) {
            for (Iterator<HttpCookie> i = list.iterator(); i.hasNext(); ) {
                HttpCookie cookie = i.next();
                if (cookie.hasExpired()) {
                    i.remove(); // remove expired cookies
                } else if (!result.contains(cookie)) {
                    result.add(cookie);
                }
            }
        }
        return Collections.unmodifiableList(new ArrayList<HttpCookie>(result));
    }

    @Override
    public List<URI> getURIs() {
        List<URI> result = new ArrayList<URI>(mCookies.keySet());
        result.remove(null); // sigh
        return Collections.unmodifiableList(result);
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        uri = cookiesUri(uri);
        if (cookie == null) {
            throw new NullPointerException("cookie == null");
        }

//        if (mCookies.containsKey(uri)) {
//            return mCookies.get(uri).remove(cookie);
//        } else {
//            return false;
//        }

//        Set<HttpCookie> cookieSet = mCookies.get(uri);

        if (mCookies.get(uri) != null) {
            SharedPreferences.Editor prefsWriter = mSharedPreferences.edit();
            prefsWriter.remove(COOKIE_NAME_PREFIX + uri);
            prefsWriter.commit();
            return mCookies.get(uri).remove(cookie);
        } else {
            return false;
        }

    }

    @Override
    public boolean removeAll() {
        SharedPreferences.Editor ed = mSharedPreferences.edit();
        ed.clear();
        ed.commit();
        if (mCookies.isEmpty()) {
            return false;
        } else {
            mCookies.clear();
            return true;
        }
    }


    public static class SerializableHttpCookie implements Serializable {
        private static final long serialVersionUID = -6051428667568260064L;

        private transient HttpCookie cookie;

        public SerializableHttpCookie(HttpCookie cookie) {
            this.cookie = cookie;
        }

        public HttpCookie getCookie() {
            return cookie;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeObject(cookie.getName());
            out.writeObject(cookie.getValue());
            out.writeObject(cookie.getComment());
            out.writeObject(cookie.getCommentURL());
            out.writeBoolean(cookie.getDiscard());
            out.writeObject(cookie.getDomain());
            out.writeLong(cookie.getMaxAge());
            out.writeObject(cookie.getPath());
            out.writeObject(cookie.getPortlist());
            out.writeBoolean(cookie.getSecure());
            out.writeInt(cookie.getVersion());
        }

        private void readObject(ObjectInputStream in) throws IOException,
                ClassNotFoundException {
            String name = (String) in.readObject();
            String value = (String) in.readObject();
            cookie = new HttpCookie(name, value);
            cookie.setComment((String) in.readObject());
            cookie.setCommentURL((String) in.readObject());
            cookie.setDiscard(in.readBoolean());
            cookie.setDomain((String) in.readObject());
            cookie.setMaxAge(in.readLong());
            cookie.setPath((String) in.readObject());
            cookie.setPortlist((String) in.readObject());
            cookie.setSecure(in.readBoolean());
            cookie.setVersion(in.readInt());
        }
    }


    private void save2preference(URI uri, HttpCookie cookie) {
        // Save cookie into persistent store
        SharedPreferences.Editor prefsWriter = mSharedPreferences.edit();
        prefsWriter.putString(KEY_COOKIE,
                TextUtils.join(",", mCookies.keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + uri,
                encodeCookie(new SerializableHttpCookie(cookie)));
        prefsWriter.commit();
    }

    /**
     * Serializes HttpCookie object into String
     *
     * @param cookie cookie to be encoded, can be null
     * @return cookie encoded as String
     */
    protected String encodeCookie(SerializableHttpCookie cookie) {
        if (cookie == null)
            return null;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (IOException e) {
            Log.from(this, "IOException in encodeCookie" + e);
            return null;
        }

        return byteArrayToHexString(os.toByteArray());
    }

    /**
     * Returns HttpCookie decoded from cookie string
     *
     * @param cookieString string of cookie as returned from http request
     * @return decoded cookie or null if exception occured
     */
    protected HttpCookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                bytes);

        HttpCookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    byteArrayInputStream);
            cookie = ((SerializableHttpCookie) objectInputStream.readObject())
                    .getCookie();
        } catch (IOException e) {
            Log.from(this, "IOException in decodeCookie" + e);
        } catch (ClassNotFoundException e) {
            Log.from(this, "ClassNotFoundException in decodeCookie" + e);
        }

        return cookie;
    }

    /**
     * Using some super basic byte array &lt;-&gt; hex conversions so we don't
     * have to rely on any large Base64 libraries. Can be overridden if you
     * like!
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    /**
     * Converts hex values from strings to byte arra
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character
                    .digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    private URI cookiesUri(URI uri) {
        if (uri == null) {
            return null;
        }

        try {
            return new URI("http", uri.getHost(), null, null);
        } catch (URISyntaxException e) {
            return uri; // probably a URI with no host
        }
    }
}
