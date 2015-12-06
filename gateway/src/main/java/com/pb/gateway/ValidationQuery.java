package com.pb.gateway;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *  Validate hand through HTTP request
 */
@Component
public class ValidationQuery {
    @Value("${validatorUrl}")
    String validatorUrl;

    @AllArgsConstructor
    @Getter
    public static class Ret {
        private Boolean isSuccess;
        private String Description;
    }
    public static final Ret OK = new Ret(true, "OK");

    public Ret validateHand(String handId)  {
        try {
            URL url = new URL(validatorUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String response = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                response += line;
            }
            return new Ret(true,response);

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        return OK;
    }
}
