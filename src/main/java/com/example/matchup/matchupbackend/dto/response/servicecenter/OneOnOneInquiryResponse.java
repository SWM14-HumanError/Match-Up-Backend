package com.example.matchup.matchupbackend.dto.response.servicecenter;

import com.example.matchup.matchupbackend.entity.ServiceCenter;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class OneOnOneInquiryResponse {
    private List<InquiryInfo> inquiryList;
    private int size;
    private Boolean hasNextSlice;

    @Builder
    public OneOnOneInquiryResponse(List<InquiryInfo> inquiryList, int size, Boolean hasNextSlice) {
        this.inquiryList = inquiryList;
        this.size = size;
        this.hasNextSlice = hasNextSlice;
    }

    public static OneOnOneInquiryResponse from(Slice<ServiceCenter> serviceCenterSlice) {
        List<InquiryInfo> inquiryInfos = InquiryInfoListFrom(serviceCenterSlice.getContent());
        return OneOnOneInquiryResponse.builder()
                .inquiryList(inquiryInfos)
                .size(inquiryInfos.size())
                .hasNextSlice(serviceCenterSlice.hasNext())
                .build();
    }

    private static List<InquiryInfo> InquiryInfoListFrom(List<ServiceCenter> serviceCenterSlice) {
        return serviceCenterSlice.stream().map(serviceCenter -> InquiryInfo.builder()
                .title(serviceCenter.getTitle())
                .content(serviceCenter.getContent())
                .createdAt(serviceCenter.getCreateTime().toString())
                .userNickname(serviceCenter.getUser().getNickname())
                .userEmail(serviceCenter.getUser().getEmail())
                .build()).collect(Collectors.toList());
    }
}
