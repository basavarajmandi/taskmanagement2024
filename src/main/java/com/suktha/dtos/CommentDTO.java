package com.suktha.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Data
public class CommentDTO {
    private Long id;
    private String content;
    private Date createdAt;
    private Long postedUserId;
    private String postedUserName;
    private Long taskId;
    private String imageName;
    private String voiceName;
}
