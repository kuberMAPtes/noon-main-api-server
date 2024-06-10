package com.kube.noon.member.service;

public interface AuthService {

    void sendAuthentificationNumber(String phoneNumber);

    boolean confirmAuthenticationNumber(String phoneNumber, String randomNumber);

}
