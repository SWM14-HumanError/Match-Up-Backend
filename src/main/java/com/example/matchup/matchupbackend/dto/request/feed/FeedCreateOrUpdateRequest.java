package com.example.matchup.matchupbackend.dto.request.feed;

import com.example.matchup.matchupbackend.entity.Feed;
import com.example.matchup.matchupbackend.entity.ProjectDomain;
import com.example.matchup.matchupbackend.entity.User;
import com.example.matchup.matchupbackend.global.annotation.validation.Enum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

@Data
public class FeedCreateOrUpdateRequest {

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 50, message = "피드의 제목은 50글자를 넘길 수 없습니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    @Size(max = 700, message = "피드의 내용은 700글자를 넘길 수 없습니다.")
    private String content;

    private String imageUrl;

    @NotNull(message = "피드의 타입은 필수 입력 값입니다.")
    @Range(max = 1, message = "0(Project) or 1(Study)로만 입력하세요")
    private Long type;

    @Enum(enumClass = ProjectDomain.class, message = "적절하지 않은 도메인입니다.")
    private String domain;

    // todo: refactoring
    public Feed toEntity(User user) {
        return Feed.builder()
                .title(title)
                .content(content)
                .thumbnailUrl(imageUrl)
                .type(type)
                .projectDomain(getDomain())
                .user(user)
                .build();
    }

    public ProjectDomain getDomain() {
        return (this.domain != null) ? ProjectDomain.valueOf(this.domain) : ProjectDomain.전체;
    }
}
