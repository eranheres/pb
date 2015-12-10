package com.pb.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 *  Validate hand through HTTP request
 */
@Component
public class ValidationQuery {
    @Value("${validator.url}")
    String validatorUrl;

    @NoArgsConstructor
    @Getter
    @Setter
    public static class validatorRes {
        String validation;
        String reason;

        public static validatorRes deserialize(String str) throws IOException {
            return new ObjectMapper().readValue(str, validatorRes.class);
        }
    }

    public validatorRes validateHand(String handId) throws IOException {
        String fullUrl = String.format("%s/validate/snapshot/%s",validatorUrl,handId);
        URL url = new URL(fullUrl);
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        String response = "";
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            response += line;
        }

        return validatorRes.deserialize(response);
    }
}
