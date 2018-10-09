package org.matsim.webvis.files.notifications;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AbstractNotification implements Notification {

    private String type;
    private String message;
}
