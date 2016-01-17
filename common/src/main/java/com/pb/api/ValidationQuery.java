package com.pb.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.dao.HandId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
    @AllArgsConstructor
    @Getter
    @Setter
    public static class validatorRes {
        String validation;
        String reason;

        public static validatorRes deserialize(String str) throws IOException {
            return new ObjectMapper().readValue(str, validatorRes.class);
        }
    }

    private validatorRes sendRequest(String fullUrl) throws IOException {
        URL url = new URL(fullUrl);
        URLConnection urlConnection = url.openConnection();
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        } catch (FileNotFoundException e) {
            throw new HandValidationException("Hand not found");
        }
        String response = "";
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            response += line;
        }

        return validatorRes.deserialize(response);
    }

    public validatorRes validateOngoingHand(HandId handId) throws IOException {
        return sendRequest(String.format("%s/validate/snapshot/%s",validatorUrl,handId.toString()));
    }

    public validatorRes validateHand(HandId handId) throws IOException {
        return sendRequest(String.format("%s/validate/fullhand/%s",validatorUrl,handId.toString()));
    }
}
