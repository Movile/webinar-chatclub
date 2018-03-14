package com.movile.chatclub.webinar.sender.components;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class FacebookDispatcher {

    @Value("${facebook.page.access-token}")
    private String accessToken;

    public String dispatch(String messaging) {
        try {
            System.out.print("Sending messaging " + messaging);

            //Creating request
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("https://graph.facebook.com/v2.6/me/messages?access_token=" + accessToken);

            //Arguments
            List<NameValuePair> arguments = Arrays.asList(new BasicNameValuePair(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString()));
            post.setEntity(new UrlEncodedFormEntity(arguments));

            //Response
            HttpResponse response = client.execute(post);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            System.out.print("Unable to send messaging " + messaging);
            return null;
        }
    }
}
