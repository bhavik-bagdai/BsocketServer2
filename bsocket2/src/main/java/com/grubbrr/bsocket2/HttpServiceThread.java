package com.grubbrr.bsocket2;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class HttpServiceThread extends Thread {

    ServerSocket serverSocket;
    Socket socket;
    HttpService httpService;
    BasicHttpContext basicHttpContext;
    static int HttpServerPORT = 8080;
    boolean RUNNING = false;

    public int getHttpServerPORT(){
        return HttpServerPORT;
    }
    public HttpServiceThread() {
        RUNNING = true;
        startHttpService();
    }

    public HttpServiceThread(int port) {
        HttpServerPORT = port;
        RUNNING = true;
        startHttpService();
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(HttpServerPORT);
            serverSocket.setReuseAddress(true);

            while (RUNNING) {
                socket = serverSocket.accept();
                DefaultHttpServerConnection httpServerConnection = new DefaultHttpServerConnection();
                httpServerConnection.bind(socket, new BasicHttpParams());
                httpService.handleRequest(httpServerConnection,basicHttpContext);
                httpServerConnection.shutdown();
            }
            serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (HttpException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private synchronized void startHttpService() {
        BasicHttpProcessor basicHttpProcessor = new BasicHttpProcessor();
        basicHttpContext = new BasicHttpContext();

        basicHttpProcessor.addInterceptor(new ResponseDate());
        basicHttpProcessor.addInterceptor(new ResponseServer());
        basicHttpProcessor.addInterceptor(new ResponseContent());
        basicHttpProcessor.addInterceptor(new ResponseConnControl());

        httpService = new HttpService(basicHttpProcessor,
                new DefaultConnectionReuseStrategy(),
                new DefaultHttpResponseFactory());

        HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();
        registry.register("/", new HttpServiceThread.HomeCommandHandler());
        httpService.setHandlerResolver(registry);
    }

    public synchronized void stopServer() {
        RUNNING = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    class HomeCommandHandler implements HttpRequestHandler {

        @Override
        public void handle(HttpRequest request, HttpResponse response,
                           HttpContext httpContext) throws HttpException, IOException {

            HttpEntity httpEntity = new EntityTemplate(
                    new ContentProducer() {

                        public void writeTo(final OutputStream outstream)
                                throws IOException {

                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                                    outstream, "UTF-8");
                            String response = "<html><head></head><body><h1>Hello HttpService, from Bhavik<h1></body></html>";

                            outputStreamWriter.write(response);
                            outputStreamWriter.flush();
                        }
                    });
            response.setHeader("Content-Type", "text/html");
            response.setEntity(httpEntity);
        }
    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "SiteLocalAddress: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

}


