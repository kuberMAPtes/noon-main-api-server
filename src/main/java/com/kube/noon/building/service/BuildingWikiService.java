package com.kube.noon.building.service;

/**
 * 건물 위키 로직을 처리하는 Service 인터페이스
 *
 * @author PGD
 */
public interface BuildingWikiService {

    /**
     * 빈 건물 위키 페이지를 생성한다.
     * @param title 생성할 건물 위키 페이지의 제목
     * @return 건물 위키가 생성되면 true, 이미 title에 해당하는 건물위키가 존재하여 새로운 건물
     *         위키가 생성되지 않았으면 false를 반환한다.
     */
    public void addPage(String title);
}
