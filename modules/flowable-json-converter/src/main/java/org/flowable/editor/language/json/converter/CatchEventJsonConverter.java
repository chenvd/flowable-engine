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
package org.flowable.editor.language.json.converter;

import java.util.List;
import java.util.Map;

import org.flowable.bpmn.model.BaseElement;
import org.flowable.bpmn.model.ConditionalEventDefinition;
import org.flowable.bpmn.model.EventDefinition;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.IntermediateCatchEvent;
import org.flowable.bpmn.model.MessageEventDefinition;
import org.flowable.bpmn.model.SignalEventDefinition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Tijs Rademakers
 */
public class CatchEventJsonConverter extends BaseBpmnJsonConverter {

    public static void fillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap, Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {

        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }

    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_EVENT_CATCH_TIMER, CatchEventJsonConverter.class);
        convertersToBpmnMap.put(STENCIL_EVENT_CATCH_MESSAGE, CatchEventJsonConverter.class);
        convertersToBpmnMap.put(STENCIL_EVENT_CATCH_SIGNAL, CatchEventJsonConverter.class);
        convertersToBpmnMap.put(STENCIL_EVENT_CATCH_CONDITIONAL, CatchEventJsonConverter.class);
    }

    public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(IntermediateCatchEvent.class, CatchEventJsonConverter.class);
    }

    @Override
    protected String getStencilId(BaseElement baseElement) {
        IntermediateCatchEvent catchEvent = (IntermediateCatchEvent) baseElement;
        List<EventDefinition> eventDefinitions = catchEvent.getEventDefinitions();
        if (eventDefinitions.size() != 1) {
            // return timer event as default;
            return STENCIL_EVENT_CATCH_TIMER;
        }

        EventDefinition eventDefinition = eventDefinitions.get(0);
        if (eventDefinition instanceof MessageEventDefinition) {
            return STENCIL_EVENT_CATCH_MESSAGE;
        } else if (eventDefinition instanceof SignalEventDefinition) {
            return STENCIL_EVENT_CATCH_SIGNAL;
        } else if (eventDefinition instanceof ConditionalEventDefinition) {
            return STENCIL_EVENT_CATCH_CONDITIONAL;
        } else {
            return STENCIL_EVENT_CATCH_TIMER;
        }
    }

    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement) {
        IntermediateCatchEvent catchEvent = (IntermediateCatchEvent) baseElement;
        addEventProperties(catchEvent, propertiesNode);
    }

    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap) {
        IntermediateCatchEvent catchEvent = new IntermediateCatchEvent();
        String stencilId = BpmnJsonConverterUtil.getStencilId(elementNode);
        if (STENCIL_EVENT_CATCH_TIMER.equals(stencilId)) {
            convertJsonToTimerDefinition(elementNode, catchEvent);
        } else if (STENCIL_EVENT_CATCH_MESSAGE.equals(stencilId)) {
            convertJsonToMessageDefinition(elementNode, catchEvent);
        } else if (STENCIL_EVENT_CATCH_SIGNAL.equals(stencilId)) {
            convertJsonToSignalDefinition(elementNode, catchEvent);
        } else if (STENCIL_EVENT_CATCH_CONDITIONAL.equals(stencilId)) {
            convertJsonToConditionalDefinition(elementNode, catchEvent);
        } 
        return catchEvent;
    }
}
