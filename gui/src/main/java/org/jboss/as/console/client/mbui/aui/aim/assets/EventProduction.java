package org.jboss.as.console.client.mbui.aui.aim.assets;

import org.jboss.as.console.client.mbui.aui.aim.Event;
import org.jboss.as.console.client.mbui.aui.aim.EventProducer;
import org.jboss.as.console.client.mbui.aui.aim.EventType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 10/31/12
 */
public class EventProduction<T extends Enum<T>> implements EventProducer<T> {

    private List<Event<T>> eventsProduced = Collections.EMPTY_LIST;
    private EventType type;

    public EventProduction(EventType type) {
        this.type = type;
    }

    @Override
    public boolean doesProduceEvents() {
        return !eventsProduced.isEmpty();
    }

    @Override
    public void setProducedEvents(Event<T>... events) {
        this.eventsProduced = new ArrayList<Event<T>>();
        for(Event event : events)
            this.eventsProduced.add(event);
    }
}