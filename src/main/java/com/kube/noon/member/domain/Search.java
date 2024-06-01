package com.kube.noon.member.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//==>리스트화면을 모델링(추상화/캡슐화)한 Bean
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Search {

    ///Field
    private int currentPage;
    private String searchCondition;//상품번호,상품명,상품가격
    private String searchKeyword;//검색어
    private String searchType;//가격높은순
    private int searchBoundFirst;
    private int searchBoundEnd;
    private int pageSize;
    private int endRowNum;
    private int startRowNum;

    //==> Select Query 시 ROWNUM 마지막 값 3*5 = 15번
    public int getEndRowNum() {
        return getCurrentPage() * getPageSize();
    }

    //==> Select Query 시 ROWNUM 시작 값 2 *5 + 1 = 11번 커렌페 1 pageSize 최대값하면
    public int getStartRowNum() {
        return (getCurrentPage() - 1) * getPageSize() + 1;
    }

}