package com.microsoft.azure.functions.worker.handler;

import java.util.logging.*;

import com.microsoft.azure.functions.worker.*;
import com.microsoft.azure.functions.rpc.messages.*;

public class StartStreamHandler extends OutboundMessageHandler<StartStream.Builder> {
    public StartStreamHandler(String workerId) {
        super(() -> generateStartStream(workerId), StreamingMessage.Builder::setStartStream);
    }

    @Override
    Logger getLogger() { return WorkerLogManager.getSystemLogger(); }

    private static StartStream.Builder generateStartStream(String workerId) {
        assert workerId != null;
        StartStream.Builder startStream = StartStream.newBuilder();
        startStream.setWorkerId(workerId);
        return startStream;
    }
}
