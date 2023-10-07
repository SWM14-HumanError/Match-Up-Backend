package com.example.matchup.matchupbackend.dto.request.team;

import com.example.matchup.matchupbackend.dto.Member;
import com.example.matchup.matchupbackend.dto.response.team.MeetingSpotResponse;
import com.example.matchup.matchupbackend.dto.response.team.TeamTypeResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class TeamCreateRequest {
    private String base64Thumbnail;
    private String fileName;
    @NotBlank(message = "제목은 필수 입력 값입니다.")
    @Size(max = 30, message = "제목은 30자를 넘을 수 없습니다.")
    private String name;
    @Valid
    @NotNull(message = "팀 타입은 필수 입력 값입니다.")
    private TeamTypeResponse type;
    @NotBlank(message = "내용은 필수 입력 값입니다.")
    @Size(max = 5000, message = "내용은 최대 5000자를 넘을 수 없습니다.")
    private String description;
    @Valid
    @NotNull(message = "모임 장소는 필수 입력 값입니다.")
    private MeetingSpotResponse meetingSpot;
    private String meetingDate;
    @Valid
    @NotNull(message = "모집 팀원은 필수 입력 값입니다.")
    private List<Member> memberList;

    //계층구조가 있는 데이터는 굳이 클래스로 다 안나누고 1. 띄어쓰기 2. List로 보내고 받을수 있다(프론트와 상의)
    public List<String> returnTagList() //사용자의 태그들을 모아서 중복을 제거함
    {
        List<String> userStacks = new ArrayList<>();
        for (Member member : memberList) {
            userStacks.addAll(member.getStacks());
        }
        return userStacks.stream().distinct().collect(Collectors.toList());
    }

    public List<String> returnTagListByRole(String role) //사용자의 태그들을 모아서 중복을 제거함
    {
        List<String> userStacks = new ArrayList<>();
        for (Member member : memberList) {
            if (role.equals(member.getRole())) {
                userStacks.addAll(member.getStacks());
            }
        }
        return userStacks.stream().distinct().collect(Collectors.toList());
    }

    public MultipartFile getThumbnailIMG() {
        ByteArrayMultipartFileEditor byteArrayMultipartFileEditor = new ByteArrayMultipartFileEditor();
        byteArrayMultipartFileEditor.setValue(decodeIMG);
        return (MultipartFile) byteArrayMultipartFileEditor.getValue();
//        String base64 = base64Thumbnail.replace('-', '+').replace('_', '/');
//
//        // Base64 문자열을 디코딩하여 바이트 배열로 변환
//        byte[] decodedBytes = Base64.getDecoder().decode(base64);
//
//        // 바이트 배열을 InputStream으로 변환
//        InputStream inputStream = new ByteArrayInputStream(decodedBytes);
//
//        // InputStream을 사용하여 MultipartFile 생성
//        MultipartFile multipartFile  = new MockMultipartFile("file",
//                "thumbnail.png", "image/png", inputStream);
//        return multipartFile;
//        return null;
    }

}
