package com.movile.chatclub.webinar.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movile.chatclub.libraries.integration.facebook.entities.FacebookUser;
import com.movile.chatclub.libraries.integration.facebook.entities.Message;
import com.movile.chatclub.libraries.integration.facebook.entities.Messaging;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;
import java.util.List;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Processor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Processor.class);

    private boolean keepRunning = true;

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private ObjectMapper jsonMapper;

    @Override
    public void run() {
        while (keepRunning) {
            Messaging messaging = receiveMessage();
            if (messaging == null) {
                logger.info("Nothing so far...");
            } else {
                respond(messaging);
            }
        }
    }

    private void respond(Messaging messaging) {
        if (messaging.getMessage() == null) {
            logger.info("Can't respond without a message");
            return;
        }

        FacebookUser recipient = new FacebookUser(messaging.getSender().getId());

        Message message = new Message();
        if (StringUtils.isNotBlank(messaging.getMessage().getText())) {
            message.setText("Você disse: " + messaging.getMessage().getText());
        } else {
            message.setText("Vocë me mandou algo diferente dessa vez...");
        }

        Messaging response = new Messaging();
        response.setRecipient(recipient);
        response.setMessage(message);

        sendMessage(response);
    }

    private Messaging receiveMessage() {
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> result = jedis.blpop(5, "facebook-messages");
            if (CollectionUtils.isEmpty(result)) {
                return null;
            }

            return jsonMapper.readValue(result.get(1), Messaging.class);
        } catch (JedisException | IOException e) {
            logger.error("Failed to receive message", e);
            return null;
        }
    }

    private void sendMessage(Messaging messaging) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.rpush("processor-messages", jsonMapper.writeValueAsString(messaging));
        } catch (JedisException | JsonProcessingException e) {
            logger.error("Failed to send message", e);
        }
    }

    public void shutdown() {
        this.keepRunning = false;
    }

}
