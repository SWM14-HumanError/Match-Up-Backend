package com.example.matchup.matchupbackend.dto.request.user;

import com.example.matchup.matchupbackend.global.RoleType;
import com.example.matchup.matchupbackend.error.annotation.Enum;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Getter
public class ProfileTagPositionRequest {

    @Enum(enumClass = RoleType.class, message = "적절하지 않은 RoleType 입니다.")
    private String type;

    @Size(max = 50, message = "기술 스택은 50가지를 넘길 수 없습니다.")
    private List<@Size(max = 50, message = "기술 스택은 50글자를 넘길 수 없습니다.")String> tags;

    @Range(max = 4, message = "스택의 범위는 0~4입니다.")
    private Integer typeLevel;

    public RoleType getType() {
        return RoleType.valueOf(this.type);
    }

}
