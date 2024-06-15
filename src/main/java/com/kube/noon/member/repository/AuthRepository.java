package com.kube.noon.member.repository;

public interface AuthRepository {

    public void createAuthentificationNumber(String phone, String certificationNumber);

    //휴대전화번호에 해당하는 인증번호 불러오기
    public String getAuthentificationNumber(String phone);

    //인증 완료 시, 인증번호 Redis에서 삭제
    public void deleteAuthentificationNumber(String phone);

    //Redis에 해당 휴대전화번호로 저장된 인증번호가 존재하는지 확인
    public boolean hasKey(String phone);

    public void incrementFailedAttempts(String phone);

    public int getFailedAttempts(String phone);

    public int getMaxAttempts();

}
