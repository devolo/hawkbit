package org.eclipse.hawkbit.app;

import org.springframework.boot.actuate.trace.http.HttpExchangeTracer;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.web.trace.servlet.HttpTraceFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class TraceRequestFilter extends HttpTraceFilter {

    public TraceRequestFilter(HttpTraceRepository repository, HttpExchangeTracer tracer) {
        super(repository, tracer);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().contains("UI") || request.getServletPath().contains("httptrace");
    }
}
