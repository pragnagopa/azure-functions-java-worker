package com.microsoft.azure.functions.endtoendtests;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import java.util.*;

/**
 * Azure Functions with Azure Event Hub.
 */
public class EventHubTriggerTests {
    /**
     * This function will be invoked when a new message is received at the specified EventHub. The message contents are provided as input to this function.
     */
    @FunctionName("EventHubTriggerAndOutputJSON")
    public void EventHubTriggerAndOutputJSON(
        @EventHubTrigger(name = "messages", eventHubName = "test-inputjson-java", connection = "AzureWebJobsEventHubSender") List<String> messages,
        @EventHubOutput(name = "output", eventHubName = "test-outputjson-java", connection = "AzureWebJobsEventHubSender") OutputBinding<String> output,
        final ExecutionContext context
    ) {
        context.getLogger().info("Java Event Hub trigger received " + messages.size() +" messages");
        output.setValue(messages.get(0));
    }

    @FunctionName("EventHubTriggerAndOutputString")
    public void EventHubTriggerAndOutputString(
        @EventHubTrigger(name = "messages", eventHubName = "test-input-java", connection = "AzureWebJobsEventHubSender", dataType = "string") String[] messages,
        @EventHubOutput(name = "output", eventHubName = "test-output-java", connection = "AzureWebJobsEventHubSender") OutputBinding<String> output,
        final ExecutionContext context
    ) {
        context.getLogger().info("Java Event Hub trigger received " + messages.length +" messages");
        output.setValue(messages[0]);
    }

    @FunctionName("EventHubTriggerCardinalityOne")
    public void EventHubTriggerCardinalityOne(
        @EventHubTrigger(name = "message", eventHubName = "test-inputOne-java", connection = "AzureWebJobsEventHubSender", dataType = "string") String message,
        @EventHubOutput(name = "output", eventHubName = "test-outputone-java", connection = "AzureWebJobsEventHubSender") OutputBinding<String> output,
        final ExecutionContext context
    ) {
        context.getLogger().info("Java Event Hub trigger received message" + message);
        output.setValue(message);
    }

    /**
     * This function verifies the above functions
     */
    @FunctionName("TestEventHubOutputJson")
    public void TestEventHubOutputJson(
        @EventHubTrigger(name = "message", eventHubName = "test-outputjson-java", connection = "AzureWebJobsEventHubSender") String message,
        @QueueOutput(name = "output", queueName = "test-eventhuboutputjson-java", connection = "AzureWebJobsStorage") OutputBinding<String> output,
        final ExecutionContext context
    ) {
        context.getLogger().info("Java Event Hub Output function processed a message: " + message);
        output.setValue(message);
    }

    @FunctionName("TestEventHubOutput")
    public void TestEventHubOutput(
        @EventHubTrigger(name = "message", eventHubName = "test-output-java", connection = "AzureWebJobsEventHubSender") String message,
        @QueueOutput(name = "output", queueName = "test-eventhuboutput-java", connection = "AzureWebJobsStorage") OutputBinding<String> output,
        final ExecutionContext context
    ) {
        context.getLogger().info("Java Event Hub Output function processed a message: " + message);
        output.setValue(message);
    }

    @FunctionName("TestEventHubOutputInputOne")
    public void TestEventHubOutputInputOne(
        @EventHubTrigger(name = "message", eventHubName = "test-outputone-java", connection = "AzureWebJobsEventHubSender") String message,
        @QueueOutput(name = "output", queueName = "test-eventhuboutputone-java", connection = "AzureWebJobsStorage") OutputBinding<String> output,
        final ExecutionContext context
    ) {
        context.getLogger().info("Java Event Hub Output function processed a message: " + message);
        output.setValue(message);
    }

   
}
