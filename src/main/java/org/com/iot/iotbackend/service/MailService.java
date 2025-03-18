package org.com.iot.iotbackend.service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Value("${mail-api.key}")
    private String API_KEY;

    @Value("${mail-api.secret-key}")
    private String API_SECRET_KEY;

    Logger logger = LoggerFactory.getLogger(MailService.class);

    public MailjetResponse callSendVerifyEmail(String email, String verifyCode) {
        // ClientOptions 생성
        ClientOptions clientOptions = ClientOptions.builder()
                .apiKey(API_KEY)
                .apiSecretKey(API_SECRET_KEY)
                .build();

        // MailjetClient 생성
        MailjetClient client = new MailjetClient(clientOptions);

        // 요청 생성
        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(
                        Emailv31.MESSAGES,
                        new JSONArray()
                                .put(
                                        new JSONObject()
                                                .put(
                                                        Emailv31.Message.FROM, new JSONObject()
                                                                .put("Email", "kodh10@gmail.com")
                                                                .put("Name", "iot-toy-project")
                                                )
                                                .put(
                                                        Emailv31.Message.TO, new JSONArray()
                                                                .put(
                                                                        new JSONObject()
                                                                                .put("Email", email)
                                                                )
                                                )
                                                .put("TemplateID", 6818528)
                                                .put("TemplateLanguage", true)
                                                .put(Emailv31.Message.SUBJECT, "[iot-toy-project] 회원가입 본인인증 메일입니다.")
                                                .put(Emailv31.Message.VARS, new JSONObject()
                                                        .put("verifyCode", verifyCode)
                                                )
                                )
                );

        // 요청 전송 및 응답 받기
        MailjetResponse response = null;
        try {
            response = client.post(request);
        } catch (MailjetException e) {
            throw new RuntimeException(e.getMessage());
        }
        logger.info("Mail Send Address : {}, Send Status : {} ", email, response.getStatus());
        return response;
    }
}