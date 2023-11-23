package org.groupscope.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

public class RequestInfo {
    public static void printRequest(HttpServletRequest request) {
        System.out.println("\n\nNEW REQUEST");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println(headerName + ": " + headerValue);
        }

        System.out.println("Request Method: " + request.getMethod());
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Query String: " + request.getQueryString());
        System.out.println("Request Parameters: " + request.getParameterMap());
    }
}
