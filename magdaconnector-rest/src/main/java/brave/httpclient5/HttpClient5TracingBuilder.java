package brave.httpclient5;

import brave.Tracing;
import brave.http.HttpTracing;
import org.apache.hc.client5.http.impl.ChainElement;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

/**
 * This is extends {@link HttpClient5Tracing} and provides a copy of the {@link #build(HttpClientBuilder builder)}
 * but it returns the HttpClientBuilder instead of already building it, this is done by means of the {@link #addTracer(HttpClientBuilder builder)} method.
 *
 * Yes, for some unimaginable reason, the instrumentation library decided to `builder.build()` before returning...
 */
public class HttpClient5TracingBuilder extends HttpClient5Tracing {

    HttpClient5TracingBuilder(HttpTracing httpTracing) {
        super(httpTracing);
    }

    public static HttpClient5TracingBuilder newBuilder(Tracing tracing) {
        return new HttpClient5TracingBuilder(HttpTracing.create(tracing));
    }

    public HttpClientBuilder addTracer(HttpClientBuilder builder) {
        if (builder == null) throw new NullPointerException("HttpClientBuilder == null");
        builder.addExecInterceptorBefore(ChainElement.MAIN_TRANSPORT.name(),
                HandleSendHandler.class.getName(),
                new HandleSendHandler(httpTracing));
        builder.addExecInterceptorBefore(ChainElement.PROTOCOL.name(),
                HandleReceiveHandler.class.getName(),
                new HandleReceiveHandler(httpTracing));
        return builder;
    }
}
