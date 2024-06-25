package com.kube.noon.common.messagesender;

import reactor.core.publisher.Mono;

public interface ApickApiAgent {

    public Object checkPhoneNumber(String phoneNumber);

}
