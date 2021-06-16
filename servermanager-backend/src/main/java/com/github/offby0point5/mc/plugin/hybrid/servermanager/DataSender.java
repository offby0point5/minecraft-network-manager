package com.github.offby0point5.mc.plugin.hybrid.servermanager;

import org.apache.http.conn.HttpHostConnectException;
import unirest.UnirestException;

import java.util.concurrent.TimeUnit;

public class DataSender {
    private Thread serverDataSender;
    private boolean isPinged = false;
    private boolean running = true;

    private final ILogger log;
    private final IDataProvider data;

    public DataSender(ILogger logger, IDataProvider dataProvider) {
        log = logger;
        data = dataProvider;
    }

    public void start() {
        // Start the data sender thread
        serverDataSender = new Thread(() -> {
            final long sleepTimeFail = 5;  // Seconds
            final long sleepTimeSuccess = 10;  // Seconds
            long sleepTime = sleepTimeFail;

            while (running) {
                try {
                    isPinged = false;  // reset pinged status
                    TimeUnit.SECONDS.sleep(sleepTime);
                    sleepTime = sleepTimeFail;
                    if (isPinged) continue;

                    log.warning("Proxy did not send ping! Try resending data!");
                    ProxyApi.putServerPorts(data.getName(),
                            new ServerAddresses(data.getGameAddress(),
                                    null, null));
                    ProxyApi.putServerGroups(data.getName(),
                            new ServerGroups(data.getMainGroup(), data.getAllGroups()));
                    log.info("Successfully sent server data.");
                    sleepTime = sleepTimeSuccess;
                } catch (UnirestException e) {
                    if (e.getCause() instanceof HttpHostConnectException) {
                        log.warning("Proxy is not reachable!");
                    }
                } catch (InterruptedException ignore) { }
            }
        });
        serverDataSender.start();
    }

    public void stop() throws InterruptedException {
        running = false;
        serverDataSender.join();
    }

    public void gotPinged() {
        isPinged = true;
    }
}
