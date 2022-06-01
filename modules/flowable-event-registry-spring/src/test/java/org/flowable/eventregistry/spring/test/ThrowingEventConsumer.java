/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.eventregistry.spring.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.flowable.eventregistry.api.EventConsumerInfo;
import org.flowable.eventregistry.api.EventRegistryEvent;
import org.flowable.eventregistry.api.EventRegistryEventConsumer;
import org.flowable.eventregistry.api.EventRegistryProcessingInfo;

/**
 * @author Filip Hrisafov
 */
public class ThrowingEventConsumer implements EventRegistryEventConsumer {

    protected final AtomicInteger numberOfExceptionsToThrow = new AtomicInteger(0);
    protected final List<EventRegistryEvent> events = new ArrayList<>();

    @Override
    public EventRegistryProcessingInfo eventReceived(EventRegistryEvent event) {
        events.add(event);
        if (numberOfExceptionsToThrow.decrementAndGet() >= 0) {
            throw new RuntimeException("Failed to receive event " + event.getType());
        }
        EventRegistryProcessingInfo eventRegistryProcessingInfo = new EventRegistryProcessingInfo();
        eventRegistryProcessingInfo.addEventConsumerInfo(new EventConsumerInfo());
        return eventRegistryProcessingInfo;
    }

    @Override
    public String getConsumerKey() {
        return "throwingTestEventConsumer";
    }

    public void reset() {
        numberOfExceptionsToThrow.set(0);
        events.clear();
    }

    public void setNumberOfExceptionsToThrow(int numberOfExceptionsToThrow) {
        this.numberOfExceptionsToThrow.set(Math.abs(numberOfExceptionsToThrow));
    }

    public int getNumberOfExceptionsToThrow() {
        return this.numberOfExceptionsToThrow.get();
    }

    public List<EventRegistryEvent> getEvents() {
        return events;
    }
}
