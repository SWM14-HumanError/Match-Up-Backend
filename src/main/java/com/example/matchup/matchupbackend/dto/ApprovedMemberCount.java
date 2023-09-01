package com.example.matchup.matchupbackend.dto;

import com.example.matchup.matchupbackend.entity.TeamPosition;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class ApprovedMemberCount extends Member {
    private Long count;


    public ApprovedMemberCount(String role, List<String> stacks, Long maxCount, Long count) {
        super(role, stacks, maxCount);
        this.count = count;
    }

    public static ApprovedMemberCount fromEntity(TeamPosition teamPosition) {
        return new ApprovedMemberCount(teamPosition.getRole(),
                teamPosition.stringTagList(),
                teamPosition.getMaxCount(),
                teamPosition.getTeam().numberOfUserByPosition(teamPosition.getRole()));
    }

}
