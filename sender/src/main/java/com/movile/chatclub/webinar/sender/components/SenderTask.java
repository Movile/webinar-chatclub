package com.movile.chatclub.webinar.sender.components;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SenderTask {

    @Autowired
    private FacebookDispatcher facebookDispatcher;

    @Scheduled(cron = "0 * * * * *")
    public void checkQueue() {
        System.out.println("Checking new messages");
        try {
            String messaging = StringUtils.EMPTY;

            if(StringUtils.isEmpty(messaging)) {
                System.out.print("No message in the queue");
                return;
            }

            String response = facebookDispatcher.dispatch(messaging);
            System.out.println("Facebook response: " + response);
        } finally {
            System.out.println("Finished checking");
        }
    }

}
