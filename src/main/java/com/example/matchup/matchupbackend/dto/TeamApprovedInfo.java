package com.example.matchup.matchupbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class TeamApprovedInfo {
    private boolean state;
    private List<ApprovedMember> memberList;

    @Builder
    public TeamApprovedInfo(boolean state, List<ApprovedMember> memberList) {
        this.state = state;
        this.memberList = memberList;
    }
}
