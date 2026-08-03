package com.pulseai.googleformservice.service;

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
import com.google.api.services.forms.v1.model.BatchUpdateFormRequest;
import com.google.api.services.forms.v1.model.CreateItemRequest;
import com.google.api.services.forms.v1.model.Item;
import com.google.api.services.forms.v1.model.Location;
import com.google.api.services.forms.v1.model.Question;
import com.google.api.services.forms.v1.model.QuestionItem;
import com.google.api.services.forms.v1.model.Request;
import com.google.api.services.forms.v1.model.TextQuestion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.gson.GsonFactory;
import java.util.Arrays;

import com.google.api.client.http.javanet.NetHttpTransport;
import java.security.GeneralSecurityException;

@Service
public class GoogleFormsService {

    @org.springframework.beans.factory.annotation.Autowired
    private com.pulseai.googleformservice.repository.GoogleFormRepository googleFormRepository;

    private static final Logger log = LoggerFactory.getLogger(GoogleFormsService.class);
    private static final String APPLICATION_NAME = "Pulse AI Survey Automation";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private Forms formsService;

    public GoogleFormsService() {
        try {
            InputStream in = GoogleFormsService.class.getResourceAsStream("/credentials.json");
            if (in == null) {
                log.warn("credentials.json not found in resources!");
                return;
            }

            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            
            System.setProperty("java.net.preferIPv4Stack", "true");
            NetHttpTransport httpTransport = new NetHttpTransport.Builder().doNotValidateCertificate().build();

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, Arrays.asList(FormsScopes.FORMS_BODY, "https://www.googleapis.com/auth/forms.responses.readonly", "https://www.googleapis.com/auth/drive"))
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8899).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            formsService = new Forms.Builder(
                    httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();
            log.info("Google Forms API initialized successfully with OAuth2!");

        } catch (Exception e) {
            log.error("Failed to initialize Google Forms API", e);
        }
    }

    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "google-form-service", fallbackMethod = "createFormFallback")
    @io.github.resilience4j.retry.annotation.Retry(name = "google-form-service")
    public com.pulseai.googleformservice.dto.GoogleFormResult createForm(String title, java.util.List<com.pulseai.googleformservice.dto.QuestionResponseDTO> questions) {
        if (formsService == null) {
            throw new IllegalStateException("Google Forms API is not initialized.");
        }
        try {
            Form form = new Form();
            Info info = new Info();
            info.setTitle(title);
            form.setInfo(info);
            form = formsService.forms().create(form).execute();
            String formId = form.getFormId();
            log.info("Successfully created Google Form! Form ID: {}", formId);

            // Add questions based on the list
            java.util.List<Request> requests = new java.util.ArrayList<>();
            int index = 0;
            
            if (questions != null) {
                for (com.pulseai.googleformservice.dto.QuestionResponseDTO q : questions) {
                    Item item = new Item().setTitle(q.getQuestionText());
                    Question question = new Question().setRequired(true);
    
                    if (q.getQuestionType() == com.pulseai.googleformservice.enums.QuestionType.LIKERT_SCALE) {
                        int scaleMax = (q.getPositiveTo() != null) ? q.getPositiveTo() : 5;
                        question.setScaleQuestion(new com.google.api.services.forms.v1.model.ScaleQuestion().setLow(1).setHigh(scaleMax));
                    } else if (q.getQuestionType() == com.pulseai.googleformservice.enums.QuestionType.MCQ) {
                        question.setChoiceQuestion(new com.google.api.services.forms.v1.model.ChoiceQuestion()
                            .setType("RADIO")
                            .setOptions(Arrays.asList(
                                new com.google.api.services.forms.v1.model.Option().setValue("Yes"),
                                new com.google.api.services.forms.v1.model.Option().setValue("No")
                            )));
                    } else {
                        question.setTextQuestion(new TextQuestion().setParagraph(true));
                    }
                    
                    item.setQuestionItem(new QuestionItem().setQuestion(question));
                    
                    requests.add(new Request()
                        .setCreateItem(new CreateItemRequest()
                            .setItem(item)
                            .setLocation(new Location().setIndex(index++))));
                }
            }

            if (requests.isEmpty()) {
                Item item = new Item()
                    .setTitle("Please provide your feedback:")
                    .setQuestionItem(new QuestionItem()
                        .setQuestion(new Question()
                            .setRequired(true)
                            .setTextQuestion(new TextQuestion().setParagraph(true))));
                requests.add(new Request()
                    .setCreateItem(new CreateItemRequest()
                        .setItem(item)
                        .setLocation(new Location().setIndex(0))));
            }

            BatchUpdateFormRequest batchRequest = new BatchUpdateFormRequest()
                .setRequests(requests);

            formsService.forms().batchUpdate(formId, batchRequest).execute();
            log.info("Successfully added {} questions to Form ID: {}", requests.size(), formId);

            return new com.pulseai.googleformservice.dto.GoogleFormResult(formId, form.getResponderUri());
        } catch (Exception e) {
            log.error("Error creating Google Form", e);
            throw new RuntimeException("Failed to create Google Form", e);
        }
    }

    public com.pulseai.googleformservice.dto.GoogleFormResult createFormFallback(String title, java.util.List<com.pulseai.googleformservice.dto.QuestionResponseDTO> questions, Throwable t) {
        log.error("Circuit breaker activated for Google Forms API createForm: {}. Returning mock fallback response.", t.getMessage());
        return new com.pulseai.googleformservice.dto.GoogleFormResult("mock-form-id-fallback", "https://docs.google.com/forms/d/mock-form-id/viewform");
    }

    @io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker(name = "google-form-service", fallbackMethod = "getFormResponsesFallback")
    @io.github.resilience4j.retry.annotation.Retry(name = "google-form-service")
    public java.util.List<com.google.api.services.forms.v1.model.FormResponse> getFormResponses(String formId) {
        if (formsService == null) {
            throw new IllegalStateException("Google Forms API is not initialized.");
        }
        try {
            com.google.api.services.forms.v1.model.ListFormResponsesResponse response = formsService.forms().responses().list(formId).execute();
            return response.getResponses();
        } catch (Exception e) {
            log.warn("Failed to fetch responses for Form ID: {}. Reason: {}", formId, e.getMessage());
            return null;
        }
    }

    public java.util.List<com.google.api.services.forms.v1.model.FormResponse> getFormResponsesFallback(String formId, Throwable t) {
        log.error("Circuit breaker activated for Google Forms API getFormResponses: {}. Returning empty list.", t.getMessage());
        return Collections.emptyList();
    }

    public void closeForm(Long surveyId) {
        googleFormRepository.findBySurveyId(surveyId).ifPresent(googleForm -> {
            try {
                HttpRequestFactory requestFactory = formsService.getRequestFactory();
                GenericUrl url = new GenericUrl("https://forms.googleapis.com/v1/forms/" + googleForm.getGoogleFormId() + ":batchUpdate");

                // Google Forms API doesn't support changing acceptingResponses via API yet.
                // It is a known limitation. We'll simply update our local DB status.
                // See https://issuetracker.google.com/issues/219894334
                
                log.info("Google Forms API does not support closing forms programmatically. Only updating local status to CLOSED for Survey {}", surveyId);
                
                googleForm.setStatus("CLOSED");
                googleFormRepository.save(googleForm);
            } catch (Exception e) {
                log.error("Failed to update status for survey {}", surveyId, e);
            }
        });
    }

    public void reactivateForm(Long surveyId) {
        googleFormRepository.findBySurveyId(surveyId).ifPresent(googleForm -> {
            try {
                log.info("Google Forms API does not support opening forms programmatically. Only updating local status to ACTIVE for Survey {}", surveyId);
                
                googleForm.setStatus("ACTIVE");
                googleFormRepository.save(googleForm);
            } catch (Exception e) {
                log.error("Failed to update status for survey {}", surveyId, e);
            }
        });
    }
}
