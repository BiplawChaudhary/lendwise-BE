package com.lendwise.iam.utils.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WebClientUtil {
    public Map<String, Object> initiateGetRequest(String url, Map<String, String> headers, Integer timeoutInSeconds, Integer retries, String urn) {
        log.info("[URN_{}] GET Request to {} initiated", urn, url);
        log.info("[URN_{}] Headers {}", urn, new Gson().toJson(headers));
        if(retries==-1){
            generateCurl(null,headers,urn, HttpMethod.GET,url);
        }
        Map<String, Object> response = new HashMap<>();
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(30 * 1000);
            connection.setReadTimeout(30 * 1000);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            connection.setRequestProperty("Content-Type", "application/json");
            int responseCode = connection.getResponseCode();
            InputStream stream = responseCode >= 200 && responseCode < 300
                ? connection.getInputStream()
                : connection.getErrorStream();
            log.info("[URN_{}] Stream received", urn);
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String responseBody = in.lines().collect(Collectors.joining());
            in.close();
            System.out.println("================================================");
            log.info("[URN_{}] Result from GET request {}",urn,responseBody);
            connection.disconnect();
            try{
                log.info("[URN_{}] Result from GET request {}",urn,new Gson().toJson(new ObjectMapper().readValue(responseBody, Map.class)));
            }catch (Exception e){

            }
            System.out.println("================================================");
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = new ObjectMapper().readValue(responseBody, Map.class);
                log.info("[URN_{}] Result from GET request on {} returned successfully the response : {}", urn, url, new Gson().toJson(response));
            } else {
                log.error("[URN_{}] GET request failed with response code {}", urn, responseCode);
                log.error("[URN_{}] GET request failed with response  {}", urn, responseBody);
            }

        } catch (Exception e) {
            log.error("[URN_{}] Exception occurred during GET request: {}", urn, e.getMessage());
            return new HashMap<>();
        }

        return response;
    }


    public Map<String, Object> initiatePostRequest(String url, Map<String, String> headers, Object request, Integer timeoutInSeconds, Integer retries,String urn) {
        log.info("[URN_{}] POST Request to {} initiated", urn, url);
        log.info("[URN_{}] Headers {}", urn, new Gson().toJson(headers));
        log.info("[URN_{}] Body {}", urn, new Gson().toJson(request));
        if(retries==-1){
            generateCurl(request,headers,urn,HttpMethod.POST,url);
        }

        Map<String, Object> response = new HashMap<>();
        try {
            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(30 * 1000);
            connection.setReadTimeout(30 * 1000);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            String jsonRequest = new Gson().toJson(request);
            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                byte[] input = jsonRequest.getBytes(StandardCharsets.UTF_8);
                wr.write(input, 0, input.length);
            }
            int responseCode = connection.getResponseCode();
            InputStream stream = responseCode >= 200 && responseCode < 300
                ? connection.getInputStream()
                : connection.getErrorStream();
            log.info("[URN_{}] Stream received", urn);
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            String responseBody = in.lines().collect(Collectors.joining());
            in.close();
            System.out.println("================================================");
            log.info("[URN_{}] Result from POST request {}",urn,responseBody);
            connection.disconnect();
            try{
                log.info("[URN_{}] Result from POST request {}",urn,new Gson().toJson(new ObjectMapper().readValue(responseBody, Map.class)));
            }catch (Exception e){

            }
            System.out.println("================================================");
            if (responseCode == HttpURLConnection.HTTP_OK) {
                response = new ObjectMapper().readValue(responseBody, Map.class);
                log.info("[URN_{}] Result from POST request on {} for request : {} returned successfully the response : {}", urn, url, new Gson().toJson(request), new Gson().toJson(response));
            } else {
                log.error("[URN_{}] POST request failed with response code {}", urn, responseCode);
                log.error("[URN_{}] POST request failed with response  {}", urn, responseBody);

                try{
                    response = new ObjectMapper().readValue(responseBody, Map.class);
                }catch (Exception e){
                    log.error("[URN_{}] Exception occurred during POST request: {}", urn, e.getMessage());
                }
            }

        } catch (Exception e) {
            log.error("[URN_{}] Exception occurred during POST request: {}", urn, e.getMessage());
            return new HashMap<>();
        }
        return response;
    }

    private void generateCurl(Object request, Map<String,String> headers, String urn, HttpMethod method,String url){
        try {
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = "";
            if (method != HttpMethod.GET && request != null) {
                requestBody = mapper.writeValueAsString(request);
            }
            StringBuilder curlCommand = new StringBuilder("curl -X ").append(method.name()).append(" ");
            curlCommand.append("\"").append(url).append("\"");

            for (Map.Entry<String, String> header : headers.entrySet()) {
                curlCommand.append(" -H \"")
                    .append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\"");
            }
            if(method!=HttpMethod.GET){
                curlCommand.append(" -d '").append(requestBody).append("'");
            }
            log.info("[URN_{}] Curl Command  {}", urn, curlCommand);
        }catch (Exception e){
            log.error("Error generating curl string");
        }
    }



}
