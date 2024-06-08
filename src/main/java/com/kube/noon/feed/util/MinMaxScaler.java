package com.kube.noon.feed.util;

import com.kube.noon.feed.dto.MemberLikeTagDto;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MinMaxScaler {

    public static List<MemberLikeTagDto> tagCountScaler(List<MemberLikeTagDto> memberLikeTagDtoList) {
        Comparator comparator = new Comparator<MemberLikeTagDto>() {
            @Override
            public int compare(MemberLikeTagDto o1, MemberLikeTagDto o2) {
                if(o1.getTagCount() > o2.getTagCount()) {
                    return 1;
                } else if(o1.getTagCount() < o2.getTagCount()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        try {
            MemberLikeTagDto minDto = (MemberLikeTagDto) memberLikeTagDtoList.stream().min(comparator).orElse(new MemberLikeTagDto());
            MemberLikeTagDto maxDto = (MemberLikeTagDto) memberLikeTagDtoList.stream().max(comparator).orElse(new MemberLikeTagDto());
            min = minDto.getTagCount();
            max = maxDto.getTagCount();
        } catch (Exception e) {
            System.out.println("error");
            e.printStackTrace();
        }

        double finalMin = min;
        double finalMax = max;
        memberLikeTagDtoList.stream().forEach(s -> {
            double x = s.getTagCount();
            double scaleNum = (x - finalMin) * (5.0 - 1.0) / (finalMax - finalMin) + 1.0;

            s.setTagCount(scaleNum);
        });

        return memberLikeTagDtoList;
    }
}
