/*
 * Copyright (c) 2010-2012 Sonatype, Inc. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package com.ning.http.client.providers.apache;

import com.ning.http.client.*;
import com.ning.http.client.resumable.ResumableAsyncHandler;
import com.ning.http.util.AsyncHttpProviderUtils;
import com.ning.http.util.ProxyUtils;
import com.ning.http.util.UTF8UrlEncoder;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.PartSource;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ning.http.util.AsyncHttpProviderUtils.DEFAULT_CHARSET;
import static com.ning.http.util.MiscUtil.isNonEmpty;

/**
  * Copyright (c) 2013 jasonnerothin.com
  * <p/>
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  * <p/>
  * http://www.apache.org/licenses/LICENSE-2.0
  * <p/>
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  * <p/>
  * An {@link com.ning.http.client.AsyncHttpProvider} for Apache Http Client 3.1
  * <p/>
  * THIS CLASS IS COPY-PASTE-MODIFY of {@link ApacheAsyncHttpProvider} in
  * commons-httpclient:commons-httpclient:3.1 .
  * <p/>
  * Justification: As ugly as it is, the author needed to test code that relies upon
  * {@link #execute(com.ning.http.client.Request, com.ning.http.client.AsyncHandler)}.
  * This method is, to his knowledge, unpenetrable with mocks, spies and other nice
  * techniques, and writing a decompiler seemed like overkill.
  * <p/>
  * Date: 8/6/13
  * Time: 10:21 AM
  */
public class TestableApacheAsyncHttpProvider extends ApacheAsyncHttpProvider {

    private final static Logger logger = LoggerFactory.getLogger(TestableApacheAsyncHttpProvider.class);

    private final AsyncHttpClientConfig config;
    private final AtomicBoolean isClose = new AtomicBoolean(false);
    private IdleConnectionTimeoutThread idleConnectionTimeoutThread;
    private final AtomicInteger maxConnections = new AtomicInteger();
    private final MultiThreadedHttpConnectionManager connectionManager;
    private final HttpClientParams params;

    private HttpMethodFactory testMethodFactory;
    private HttpClient testClient;

    static {

        verifyMyCorrespondingClassHasNotChanged(); // this class will never be loaded if it doesn't reflect (test) the appropriate functionality

        final SocketFactory factory = new TrustingSSLSocketFactory();
        Protocol.registerProtocol("https", new Protocol("https", new ProtocolSocketFactory() {
            public Socket createSocket(String string, int i, InetAddress inetAddress, int i1) throws IOException {
                return factory.createSocket(string, i, inetAddress, i1);
            }

            public Socket createSocket(String string, int i, InetAddress inetAddress, int i1, HttpConnectionParams httpConnectionParams)
                    throws IOException {
                return factory.createSocket(string, i, inetAddress, i1);
            }

            public Socket createSocket(String string, int i) throws IOException {
                return factory.createSocket(string, i);
            }
        }, 443));
    }

    /**
     * Because COPY-PASTE-MODIFY is such a terribly bad design decision, we at least need to ensure
     * that the copy-paste-modified class still corresponds to the same implementing class. Therefore,
     * we calculate a few checksums to make sure that this class' parent has the same bytecode as the
     * one it was written against.
     */
    private static void verifyMyCorrespondingClassHasNotChanged()  {
        ChecksumClassFile checksum = new ChecksumClassFile();
        Class<?> clazz =  ApacheAsyncHttpProvider.class;

        String md5, sha;
        try{
            md5 = checksum.md5(clazz);
            sha = checksum.sha(clazz);
        } catch(CannotCompileException | NoSuchAlgorithmException | NotFoundException | IOException e){
            throw new ExceptionInInitializerError(e);
        }

        if( !md5.equals("PrXXU6W0YOncEoAdb/sbqA==")) throw new ExceptionInInitializerError("Invalid checksum. The test using this class is no longer valid because " + ApacheAsyncHttpProvider.class.getName() + " has changed.");
        if( !sha.equals("fo9e70O9IDEJl0y2g+6DDqnipYY=")) throw new ExceptionInInitializerError("Invalid checksum. The test using this class is no longer valid because " + ApacheAsyncHttpProvider.class.getName() + " has changed.");
    }

    public static class HttpMethodFactory {
        public HttpMethodBase createMethod(String methodName, Request request) {
            if (methodName.equalsIgnoreCase("DELETE")) {
                return new DeleteMethod(request.getUrl());
            } else if (methodName.equalsIgnoreCase("HEAD")) {
                return new HeadMethod(request.getUrl());
            } else if (methodName.equalsIgnoreCase("GET")) {
                return new GetMethod(request.getUrl());
            } else if (methodName.equalsIgnoreCase("OPTIONS")) {
                return new OptionsMethod(request.getUrl());
            } else {
                throw new IllegalStateException(String.format("Invalid Method: %s", methodName));
            }
        }
    }

    protected void setTestMethodFactory(HttpMethodFactory methodFactory){
        this.testMethodFactory = methodFactory;
    }

    protected void setTestClient(HttpClient testClient) {
        this.testClient = testClient;
    }

    private HttpMethodBase createMethod(HttpClient client, Request request) throws IOException {
        return (testClient == null) ? getHttpMethodBase(client, request) : getHttpMethodBase(testClient, request);
    }

    public TestableApacheAsyncHttpProvider(AsyncHttpClientConfig config) {
        super(config);
        this.config = config;
        connectionManager = new MultiThreadedHttpConnectionManager();

        params = new HttpClientParams();
        params.setParameter(HttpMethodParams.SINGLE_COOKIE_HEADER, Boolean.TRUE);
        params.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        params.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());

    }

    public <T> ListenableFuture<T> execute(Request request, AsyncHandler<T> handler) throws IOException {
        if (isClose.get()) {
            throw new IOException("Closed");
        }

        if (ResumableAsyncHandler.class.isAssignableFrom(handler.getClass())) {
            request = ResumableAsyncHandler.class.cast(handler).adjustRequestRange(request);
        }

        if (config.getMaxTotalConnections() > -1 && (maxConnections.get() + 1) > config.getMaxTotalConnections()) {
            throw new IOException(String.format("Too many connections %s", config.getMaxTotalConnections()));
        }

        if (idleConnectionTimeoutThread != null) {
            idleConnectionTimeoutThread.shutdown();
            idleConnectionTimeoutThread = null;
        }

        int requestTimeout = requestTimeout(config, request.getPerRequestConfig());
        if (config.getIdleConnectionTimeoutInMs() > 0 && requestTimeout != -1 && requestTimeout < config.getIdleConnectionTimeoutInMs()) {
            idleConnectionTimeoutThread = new IdleConnectionTimeoutThread();
            idleConnectionTimeoutThread.setConnectionTimeout(config.getIdleConnectionTimeoutInMs());
            idleConnectionTimeoutThread.addConnectionManager(connectionManager);
            idleConnectionTimeoutThread.start();
        }

        HttpClient httpClient = testClient != null ? testClient : new HttpClient(params, connectionManager);

        Realm realm = request.getRealm() != null ? request.getRealm() : config.getRealm();
        if (realm != null) {
            httpClient.getParams().setAuthenticationPreemptive(realm.getUsePreemptiveAuth());
            Credentials defaultCredentials = new UsernamePasswordCredentials(realm.getPrincipal(), realm.getPassword());
            httpClient.getState().setCredentials(new AuthScope(null, -1, AuthScope.ANY_REALM), defaultCredentials);
        }

        HttpMethodBase method = createMethod(httpClient, request);
        ApacheResponseFuture<T> f = new ApacheResponseFuture<>(handler, requestTimeout, request, method);
        f.touch();

        f.setInnerFuture(config.executorService().submit(new ApacheClientRunnable<>(request, handler, method, f, httpClient)));
        maxConnections.incrementAndGet();
        return f;
    }

    public void close() {
        if (idleConnectionTimeoutThread != null) {
            idleConnectionTimeoutThread.shutdown();
            idleConnectionTimeoutThread = null;
        }
        if (connectionManager != null) {
            try {
                connectionManager.shutdown();
            } catch (Exception e) {
                logger.error("Error shutting down connection manager", e);
            }
        }
    }

    public Response prepareResponse(HttpResponseStatus status, HttpResponseHeaders headers, List<HttpResponseBodyPart> bodyParts) {
        return new ApacheResponse(status, headers, bodyParts);
    }

    /**
     * This one we can get into
     */
    protected HttpMethodBase getHttpMethodBase(HttpClient client, Request request) throws IOException {
        String methodName = request.getMethod();
        HttpMethodBase method;
        if (methodName.equalsIgnoreCase("POST") || methodName.equalsIgnoreCase("PUT")) {
            EntityEnclosingMethod post = methodName.equalsIgnoreCase("POST") ? new PostMethod(request.getUrl()) : new PutMethod(request.getUrl());

            String bodyCharset = request.getBodyEncoding() == null ? DEFAULT_CHARSET : request.getBodyEncoding();

            post.getParams().setContentCharset("ISO-8859-1");
            if (request.getByteData() != null) {
                post.setRequestEntity(new ByteArrayRequestEntity(request.getByteData()));
                post.setRequestHeader("Content-Length", String.valueOf(request.getByteData().length));
            } else if (request.getStringData() != null) {
                post.setRequestEntity(new StringRequestEntity(request.getStringData(), "text/xml", bodyCharset));
                post.setRequestHeader("Content-Length", String.valueOf(request.getStringData().getBytes(bodyCharset).length));
            } else if (request.getStreamData() != null) {
                InputStreamRequestEntity r = new InputStreamRequestEntity(request.getStreamData());
                post.setRequestEntity(r);
                post.setRequestHeader("Content-Length", String.valueOf(r.getContentLength()));

            } else if (request.getParams() != null) {
                StringBuilder sb = new StringBuilder();
                for (final Map.Entry<String, List<String>> paramEntry : request.getParams()) {
                    final String key = paramEntry.getKey();
                    for (final String value : paramEntry.getValue()) {
                        if (sb.length() > 0) {
                            sb.append("&");
                        }
                        UTF8UrlEncoder.appendEncoded(sb, key);
                        sb.append("=");
                        UTF8UrlEncoder.appendEncoded(sb, value);
                    }
                }

                post.setRequestHeader("Content-Length", String.valueOf(sb.length()));
                post.setRequestEntity(new StringRequestEntity(sb.toString(), "text/xml", "ISO-8859-1"));

                if (!request.getHeaders().containsKey("Content-Type")) {
                    post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                }
            } else if (request.getParts() != null) {
                MultipartRequestEntity mre = createMultipartRequestEntity(bodyCharset, request.getParts(), post.getParams());
                post.setRequestEntity(mre);
                post.setRequestHeader("Content-Type", mre.getContentType());
                post.setRequestHeader("Content-Length", String.valueOf(mre.getContentLength()));
            } else if (request.getEntityWriter() != null) {
                post.setRequestEntity(new EntityWriterRequestEntity(request.getEntityWriter(), computeAndSetContentLength(request, post)));
            } else if (request.getFile() != null) {
                File file = request.getFile();
                if (!file.isFile()) {
                    throw new IOException(String.format(Thread.currentThread()
                            + "File %s is not a file or doesn't exist", file.getAbsolutePath()));
                }
                post.setRequestHeader("Content-Length", String.valueOf(file.length()));

                try (FileInputStream fis = new FileInputStream(file)) {
                    InputStreamRequestEntity r = new InputStreamRequestEntity(fis);
                    post.setRequestEntity(r);
                    post.setRequestHeader("Content-Length", String.valueOf(r.getContentLength()));
                }
            } else if (request.getBodyGenerator() != null) {
                Body body = request.getBodyGenerator().createBody();
                try {
                    int length = (int) body.getContentLength();
                    if (length < 0) {
                        length = (int) request.getContentLength();
                    }

                    // TODO: This is suboptimal
                    if (length >= 0) {
                        post.setRequestHeader("Content-Length", String.valueOf(length));

                        // This is totally sub optimal
                        byte[] bytes = new byte[length];
                        ByteBuffer buffer = ByteBuffer.wrap(bytes);
                        for (; ; ) {
                            buffer.clear();
                            if (body.read(buffer) < 0) {
                                break;
                            }
                        }
                        post.setRequestEntity(new ByteArrayRequestEntity(bytes));
                    }
                } finally {
                    try {
                        body.close();
                    } catch (IOException e) {
                        logger.warn("Failed to close request body: {}", e.getMessage(), e);
                    }
                }
            }

            if (request.getHeaders().getFirstValue("Expect") != null
                    && request.getHeaders().getFirstValue("Expect").equalsIgnoreCase("100-Continue")) {
                post.setUseExpectHeader(true);
            }
            method = post;
        } else {
            method = testMethodFactory.createMethod(methodName, request);
        }

        ProxyServer proxyServer = ProxyUtils.getProxyServer(config, request);
        if (proxyServer != null) {

            if (proxyServer.getPrincipal() != null) {
                Credentials defaultCredentials = new UsernamePasswordCredentials(proxyServer.getPrincipal(), proxyServer.getPassword());
                client.getState().setProxyCredentials(new AuthScope(null, -1, AuthScope.ANY_REALM), defaultCredentials);
            }

            ProxyHost proxyHost = proxyServer == null ? null : new ProxyHost(proxyServer.getHost(), proxyServer.getPort());
            client.getHostConfiguration().setProxyHost(proxyHost);
        }
        if (request.getLocalAddress() != null) {
            client.getHostConfiguration().setLocalAddress(request.getLocalAddress());
        }

        method.setFollowRedirects(false);
        if (isNonEmpty(request.getCookies())) {
            method.setRequestHeader("Cookie", AsyncHttpProviderUtils.encodeCookies(request.getCookies()));
        }

        if (request.getHeaders() != null) {
            for (String name : request.getHeaders().keySet()) {
                if (!"host".equalsIgnoreCase(name)) {
                    for (String value : request.getHeaders().get(name)) {
                        method.setRequestHeader(name, value);
                    }
                }
            }
        }

        if (request.getHeaders().getFirstValue("User-Agent") != null) {
            method.setRequestHeader("User-Agent", request.getHeaders().getFirstValue("User-Agent"));
        } else if (config.getUserAgent() != null) {
            method.setRequestHeader("User-Agent", config.getUserAgent());
        } else {
            method.setRequestHeader("User-Agent", AsyncHttpProviderUtils.constructUserAgent(TestableApacheAsyncHttpProvider.class));
        }

        if (config.isCompressionEnabled()) {
            Header acceptableEncodingHeader = method.getRequestHeader("Accept-Encoding");
            if (acceptableEncodingHeader != null) {
                String acceptableEncodings = acceptableEncodingHeader.getValue();
                if (!acceptableEncodings.contains("gzip")) {
                    StringBuilder buf = new StringBuilder(acceptableEncodings);
                    if (buf.length() > 1) {
                        buf.append(",");
                    }
                    buf.append("gzip");
                    method.setRequestHeader("Accept-Encoding", buf.toString());
                }
            } else {
                method.setRequestHeader("Accept-Encoding", "gzip");
            }
        }

        if (request.getVirtualHost() != null) {

            String vs = request.getVirtualHost();
            int index = vs.indexOf(":");
            if (index > 0) {
                vs = vs.substring(0, index);
            }
            method.getParams().setVirtualHost(vs);
        }

        return method;
    }

    private static int computeAndSetContentLength(Request request, HttpMethodBase m) {
        int length = (int) request.getContentLength();
        if (length == -1 && m.getRequestHeader("Content-Length") != null) {
            length = Integer.valueOf(m.getRequestHeader("Content-Length").getValue());
        }

        if (length != -1) {
            m.setRequestHeader("Content-Length", String.valueOf(length));
        }
        return length;
    }

    private MultipartRequestEntity createMultipartRequestEntity(String charset, List<Part> params, HttpMethodParams methodParams) throws FileNotFoundException {
        org.apache.commons.httpclient.methods.multipart.Part[] parts = new org.apache.commons.httpclient.methods.multipart.Part[params.size()];
        int i = 0;

        for (Part part : params) {
            if (part instanceof StringPart) {
                parts[i] = new org.apache.commons.httpclient.methods.multipart.StringPart(part.getName(),
                        ((StringPart) part).getValue(),
                        charset);
            } else if (part instanceof FilePart) {
                parts[i] = new org.apache.commons.httpclient.methods.multipart.FilePart(part.getName(),
                        ((FilePart) part).getFile(),
                        ((FilePart) part).getMimeType(),
                        ((FilePart) part).getCharSet());

            } else if (part instanceof ByteArrayPart) {
                PartSource source = new ByteArrayPartSource(((ByteArrayPart) part).getFileName(), ((ByteArrayPart) part).getData());
                parts[i] = new org.apache.commons.httpclient.methods.multipart.FilePart(part.getName(),
                        source,
                        ((ByteArrayPart) part).getMimeType(),
                        ((ByteArrayPart) part).getCharSet());

            } else if (part == null) {
                throw new NullPointerException("Part cannot be null");
            } else {
                throw new IllegalArgumentException(String.format("Unsupported part type for multipart parameter %s",
                        part.getName()));
            }
            ++i;
        }
        return new MultipartRequestEntity(parts, methodParams);
    }

    private static class TrustingSSLSocketFactory extends SSLSocketFactory {
        private SSLSocketFactory delegate;

        private TrustingSSLSocketFactory() {
            try {
                SSLContext sslcontext = SSLContext.getInstance("SSL");

                sslcontext.init(null, new TrustManager[]{new TrustEveryoneTrustManager()}, new SecureRandom());
                delegate = sslcontext.getSocketFactory();
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                throw new IllegalStateException();
            }
        }

        @Override
        public Socket createSocket(String s, int i) throws IOException {
            return delegate.createSocket(s, i);
        }

        @Override
        public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException {
            return delegate.createSocket(s, i, inetAddress, i1);
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
            return delegate.createSocket(inetAddress, i);
        }

        @Override
        public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
            return delegate.createSocket(inetAddress, i, inetAddress1, i1);
        }

        @Override
        public String[] getDefaultCipherSuites() {
            return delegate.getDefaultCipherSuites();
        }

        @Override
        public String[] getSupportedCipherSuites() {
            return delegate.getSupportedCipherSuites();
        }

        @Override
        public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
            return delegate.createSocket(socket, s, i, b);
        }
    }

    private static class TrustEveryoneTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            // do nothing
        }

        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            // do nothing
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    protected static int requestTimeout(AsyncHttpClientConfig config, PerRequestConfig perRequestConfig) {
        int result;
        if (perRequestConfig != null) {
            int prRequestTimeout = perRequestConfig.getRequestTimeoutInMs();
            result = (prRequestTimeout != 0 ? prRequestTimeout : config.getRequestTimeoutInMs());
        } else {
            result = config.getRequestTimeoutInMs();
        }
        return result;
    }
}
