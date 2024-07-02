package com.example.matchup.matchupbackend.dto.response.enterprise;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SliceEnterpriseApply {
    private List<EnterpriseApply> enterpriseApplyList;
    private int size;
    private Boolean hasNextSlice;
}
