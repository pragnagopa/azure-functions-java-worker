package com.microsoft.azure.functions.worker.broker;

import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.google.common.collect.*;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.worker.binding.*;
import org.apache.commons.lang3.reflect.*;

/**
 * Resolve a Java method overload using reflection.
 * Thread-Safety: Multiple thread.
 */
public class OverloadResolver {
    OverloadResolver() {
        this.candidates = new ArrayList<>();
    }

    synchronized void addCandidate(Method method) {
        this.candidates.add(new MethodBindInfo(method));
    }

    public synchronized boolean hasCandidates() {
        return !this.candidates.isEmpty();
    }

    public synchronized boolean hasMultipleCandidates() {
        return this.candidates.size() > 1;
    }

    synchronized Optional<JavaMethodInvokeInfo> resolve(BindingDataStore dataStore) {
        InvokeInfoBuilder invoker = this.resolve(this.candidates.get(0), dataStore);
        if (invoker != null) {
            dataStore.promoteDataTargets(invoker.outputsId);
            return Optional.of(invoker.build());
        }
        return Optional.empty();
    }

    private InvokeInfoBuilder resolve(MethodBindInfo method, BindingDataStore dataStore) {
        try {
            final InvokeInfoBuilder invokeInfo = new InvokeInfoBuilder(method);
            for (ParamBindInfo param : method.params) {
                Optional<BindingData> argument;
                if (OutputBinding.class.isAssignableFrom(TypeUtils.getRawType(param.type, null))) {
                    argument = dataStore.getOrAddDataTarget(invokeInfo.outputsId, param.name, param.type);
                }else if(Collection.class.isAssignableFrom(TypeUtils.getRawType(param.type, null)))
                {
                	ParameterizedType pType = (ParameterizedType) param.type;
    				Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[0];
    				System.out.println(clazz); //print
                	argument = dataStore.getDataByNameList(param.name, clazz);
                }
                else if (param.name != null && !param.name.isEmpty()) {
                    argument = dataStore.getDataByName(param.name, param.type);
                } else {
                    argument = dataStore.getDataByType(param.type);
                }
                BindingData actualArg = argument.orElseThrow(WrongMethodTypeException::new);
                invokeInfo.appendArgument(actualArg.getValue());
            }
            if (!method.entry.getReturnType().equals(void.class) && !method.entry.getReturnType().equals(Void.class)) {
                dataStore.getOrAddDataTarget(invokeInfo.outputsId, BindingDataStore.RETURN_NAME, method.entry.getReturnType());
            }
            return invokeInfo;
        } catch (Exception ex) {
            //TODO log
            return null;
        }
    }

    private final class InvokeInfoBuilder extends JavaMethodInvokeInfo.Builder {
        InvokeInfoBuilder(MethodBindInfo method) { super.setMethod(method.entry); }
        private final UUID outputsId = UUID.randomUUID();
    }

    private final class MethodBindInfo {
        MethodBindInfo(Method m) {
            this.entry = m;
            this.params = Arrays.stream(this.entry.getParameters()).map(ParamBindInfo::new).toArray(ParamBindInfo[]::new);
        }
        private final Method entry;
        private final ParamBindInfo[] params;
    }

    private final class ParamBindInfo {
        ParamBindInfo(Parameter param) {
            this.name = CoreTypeResolver.getBindingName(param);
            this.type = param.getParameterizedType();
        }
        private final String name;
        private final Type type;
    }

    private final List<MethodBindInfo> candidates;
}
