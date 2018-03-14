package com.movile.chatclub.webinar.webhook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movile.chatclub.libraries.integration.facebook.entities.Callback;
import com.movile.chatclub.libraries.integration.facebook.entities.Messaging;
import com.movile.chatclub.libraries.integration.facebook.entities.UpdateEntry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;

@RestController
@RequestMapping
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Value("${facebook.verify-token}")
    private String verifyToken;

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private JedisPool jedisPool;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> validateWebHook(@RequestParam("hub.verify_token") String token, @RequestParam("hub.challenge") String challenge) {
        if (verifyToken.equals(token)) {
            logger.info("Webhook validation: TOKEN is valid!");
            return ResponseEntity.ok(challenge);
        } else {
            logger.error("Webhook validation: invalid token!");
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> process(@RequestBody String body) {
        if (StringUtils.isBlank(body)) {
            logger.error("Empty callback received");
            return ResponseEntity.badRequest().build();
        }

        try {
            Callback callback = jsonMapper.readValue(body, Callback.class);
            for (UpdateEntry entry : callback.getEntry()) {
                for (Messaging messaging : entry.getMessaging()) {
                    sendMessage(messaging);
                }
            }

            return ResponseEntity.ok().build();
        } catch (IOException e) {
            logger.error("Unable to process callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private void sendMessage(Messaging messaging) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.rpush("facebook-messages", jsonMapper.writeValueAsString(messaging));
        } catch (JedisException | JsonProcessingException e) {
            logger.error("Failed to send message", e);
        }
    }

}
