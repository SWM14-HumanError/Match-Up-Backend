package com.example.matchup.matchupbackend.dto.request.mentoring;

import com.example.matchup.matchupbackend.global.RoleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MentoringSearchParam {

    private String searchType;

    private String searchValue;

    private String stack;

    private String roleType;

    public SearchType getSearchType() {
        return this.searchType != null ? SearchType.valueOf(this.searchType) : null;
    }

    public RoleType getRoleType() {
        return this.roleType != null ? RoleType.valueOf(this.roleType) : null;
    }
}
