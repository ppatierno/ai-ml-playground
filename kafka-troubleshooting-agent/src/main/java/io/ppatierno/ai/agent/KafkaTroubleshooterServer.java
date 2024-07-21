/*
 * Copyright Paolo Patierno.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.ppatierno.ai.agent;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * HTTP server exposing REST API endpoints to access the Kafka troubleshooter agent
 */
public class KafkaTroubleshooterServer {
    private static final String AGENT_CHAT_PATH = "/v1/chat";
    private static final long GRACEFUL_SHUTDOWN_TIMEOUT_MS = 30 * 1000;
    private static final int HTTP_PORT = 8080;

    private Server server;

    public KafkaTroubleshooterAgent kafkaTroubleshooterAgent;

    /**
     * Construsctor
     *
     * @param kafkaTroubleshooterAgent agent to interact with
     */
    public KafkaTroubleshooterServer(KafkaTroubleshooterAgent kafkaTroubleshooterAgent) {
        this.kafkaTroubleshooterAgent = kafkaTroubleshooterAgent;
    }
    public void startHttpServer() throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(HTTP_PORT);

        ContextHandler chatContext = new ContextHandler(AGENT_CHAT_PATH);
        chatContext.setHandler(chatHandler());

        server.setConnectors(new Connector[] {connector});
        server.setHandler(new ContextHandlerCollection(chatContext));

        server.setStopTimeout(GRACEFUL_SHUTDOWN_TIMEOUT_MS);
        server.setStopAtShutdown(true);
        server.start();
    }

    public void stopHttpServer() throws Exception {
        server.stop();
    }

    private Handler chatHandler() {
        return new AbstractHandler() {
            @Override
            public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException {
                httpServletResponse.setCharacterEncoding("UTF-8");
                request.setHandled(true);

                String question = httpServletRequest.getReader().readLine();
                String answer = kafkaTroubleshooterAgent.chat(question);

                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                httpServletResponse.getWriter().print(answer);
            }
        };
    }
}
