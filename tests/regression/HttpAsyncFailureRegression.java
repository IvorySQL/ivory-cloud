/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.highgo.cloud.util.HttpUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import okhttp3.OkHttpClient;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Calls the complete production HTTP builder and real OkHttp against loopback sockets.
 * No client/call/callback/semaphore or logger is replaced.
 */
public final class HttpAsyncFailureRegression {
    private static int passed;
    private static int failed;

    private HttpAsyncFailureRegression() {
    }

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        ExecutorService serverWorker = Executors.newSingleThreadExecutor(task -> {
            Thread thread = new Thread(task, "http-regression-server");
            thread.setDaemon(true);
            return thread;
        });
        server.setExecutor(serverWorker);
        server.createContext("/disconnect", HttpExchange::close);
        server.createContext("/success", exchange -> {
            byte[] bytes = "actual-http-response".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream output = exchange.getResponseBody()) {
                output.write(bytes);
            } finally {
                exchange.close();
            }
        });
        server.start();
        String base = "http://127.0.0.1:" + server.getAddress().getPort();
        try {
            check("refused connection returns the prepared failure text", () -> {
                int unusedPort;
                try (ServerSocket reservation = new ServerSocket(
                        0, 1, InetAddress.getByName("127.0.0.1"))) {
                    unusedPort = reservation.getLocalPort();
                }
                String response = bounded(() -> HttpUtils.builder()
                        .url("http://127.0.0.1:" + unusedPort + "/unavailable").get().async());
                requireFailureText(response);
            });
            check("disconnect before HTTP headers returns failure text", () -> {
                String response = bounded(() -> HttpUtils.builder()
                        .url(base + "/disconnect").get().async());
                requireFailureText(response);
            });
            check("successful asynchronous response still returns its body", () -> {
                String response = bounded(() -> HttpUtils.builder()
                        .url(base + "/success").get().async());
                require("actual-http-response".equals(response), "unexpected async body: " + response);
            });
            check("synchronous response behavior is unchanged", () -> {
                String response = bounded(() -> HttpUtils.builder()
                        .url(base + "/success").get().sync());
                require("actual-http-response".equals(response), "unexpected sync body: " + response);
            });
        } finally {
            server.stop(0);
            serverWorker.shutdownNow();
            closeActualClient();
        }
        System.out.println("RESULT passed=" + passed + " failed=" + failed);
        if (failed != 0) {
            throw new AssertionError("HTTP completion regressions failed: " + failed);
        }
    }

    private static void requireFailureText(String response) {
        require(response.startsWith("请求出错："), "missing existing failure prefix: " + response);
        require(response.length() > "请求出错：".length(), "missing actual IOException detail");
    }

    private static String bounded(Callable<String> operation) throws Exception {
        ExecutorService caller = Executors.newSingleThreadExecutor(task -> {
            Thread thread = new Thread(task, "http-regression-caller");
            thread.setDaemon(true);
            return thread;
        });
        AtomicReference<String> interruptedResult = new AtomicReference<>();
        Future<String> result = caller.submit(() -> {
            String value = operation.call();
            interruptedResult.set(value);
            return value;
        });
        try {
            return result.get(8, TimeUnit.SECONDS);
        } catch (TimeoutException timeout) {
            result.cancel(true);
            caller.shutdownNow();
            if (!caller.awaitTermination(2, TimeUnit.SECONDS)) {
                throw new AssertionError("HTTP caller did not terminate after timeout", timeout);
            }
            String prepared = interruptedResult.get();
            if (prepared == null || !prepared.startsWith("请求出错：")) {
                throw new AssertionError("timeout without observed failure text; transport inconclusive", timeout);
            }
            throw new AssertionError("operation remained blocked after its real failure text: " + prepared, timeout);
        } finally {
            caller.shutdownNow();
            if (!caller.awaitTermination(2, TimeUnit.SECONDS)) {
                throw new AssertionError("HTTP caller did not terminate after interruption");
            }
        }
    }

    private static void closeActualClient() throws Exception {
        // Cleanup only: retrieve the production client; never replace it or its transport.
        Field field = HttpUtils.class.getDeclaredField("okHttpClient");
        field.setAccessible(true);
        OkHttpClient client = (OkHttpClient) field.get(null);
        if (client != null) {
            client.dispatcher().executorService().shutdownNow();
            client.connectionPool().evictAll();
            if (client.cache() != null) {
                client.cache().close();
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void check(String name, CheckedRunnable operation) {
        try {
            operation.run();
            passed++;
            System.out.println("PASS " + name);
        } catch (Throwable error) {
            failed++;
            System.out.println("FAIL " + name + ": " + error);
            error.printStackTrace(System.out);
        }
    }

    private interface CheckedRunnable {
        void run() throws Exception;
    }
}
