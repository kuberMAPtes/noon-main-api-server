package com.kube.noon.member.service;

import java.util.Map;

public interface AuthService {

    boolean sendAuthentificationNumber(String phoneNumber);

    Map<String,Object> confirmAuthenticationNumber(String phoneNumber, String randomNumber);

}
