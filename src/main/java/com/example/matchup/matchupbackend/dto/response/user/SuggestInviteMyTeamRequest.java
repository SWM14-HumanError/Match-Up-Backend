package com.example.matchup.matchupbackend.dto.response.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SuggestInviteMyTeamRequest {

    @NotNull(message = "팀 아이디는 필수 입력 값입니다.")
    private Long teamId;

    @NotNull(message = "초대하고자 하는 유저 아이디는 필수 입력 값입니다.")
    private Long receiverId;

    @Size(max = 200, message = "초대글은 200글자를 넘길 수 없습니다.")
    @NotBlank(message = "초대글은 비어있을 수 없습니다.")
    private String content;
}
