package com.pulseai.googleformservice;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.forms.v1.Forms;
import com.google.api.services.forms.v1.FormsScopes;
import com.google.api.services.forms.v1.model.Form;
import com.google.api.services.forms.v1.model.Info;

import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.Collections;

public class TestCreateForm {
    @Test
    public void testCreateForm() {
        try {
            JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
            InputStream in = new FileInputStream("src/main/resources/credentials.json");
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, Collections.singleton(FormsScopes.FORMS_BODY))
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                    .setAccessType("offline")
                    .build();

            // We do not run LocalServerReceiver in the test because token should be cached.
            // Wait, authorize("user") will fail if token is invalid or missing without a receiver?
            // If the token is valid, it won't open a browser!
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8899).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            Forms formsService = new Forms.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                    .setApplicationName("Pulse AI Survey Automation")
                    .build();

            System.out.println("API initialized successfully. Trying to create form...");
            
            Form form = new Form();
            Info info = new Info();
            info.setTitle("Test Form");
            form.setInfo(info);
            
            form = formsService.forms().create(form).execute();
            System.out.println("Successfully created Google Form! Form ID: " + form.getFormId());
            System.out.println("Responder URI: " + form.getResponderUri());
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
