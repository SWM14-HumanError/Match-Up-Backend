package com.example.matchup.matchupbackend.dto.response.teamuser;

import com.example.matchup.matchupbackend.dto.ApprovedMemberCount;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class TeamApprovedInfoResponse {
    private boolean state;
    private List<ApprovedMemberCount> memberList;

    @Builder
    public TeamApprovedInfoResponse(boolean state, List<ApprovedMemberCount> memberList) {
        this.state = state;
        this.memberList = memberList;
    }
}
