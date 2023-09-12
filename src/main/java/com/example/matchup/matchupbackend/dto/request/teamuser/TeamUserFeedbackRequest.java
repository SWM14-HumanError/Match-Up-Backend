package com.example.matchup.matchupbackend.dto.request.teamuser;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TeamUserFeedbackRequest {
    @NotNull(message = "피드백을 받는 유저 ID는 필수입니다.")
    private Long receiverID; //피드백을 받는 유저 ID
    @NotBlank(message = "피드백 등급은 필수입니다.(GREAT, NORMAL, BAD)")
    private FeedbackGrade grade; //피드백 등급
    /**
     * 평가 항목
     */
    private Boolean isContactable; //연락이 잘되는지
    private Boolean isOnTime; //시간을 잘 지키는지
    private Boolean isResponsible; //책임감이 있는지
    private Boolean isKind; //친절한지
    private Boolean isCollaboration; //협업이 잘 되는지
    private Boolean isFast; //빠르게 개발 하시는지
    private Boolean isActively; //프로젝트에 적극적으로 임하는지
    @NotBlank(message = "피드백 내용은 필수입니다.")
    @Size(max = 20, message = "최대 20자까지 입력 가능합니다.")
    private String commentToUser;
    @Size(max = 500, message = "최대 500자까지 입력 가능합니다.")
    private String commentToAdmin;
}
