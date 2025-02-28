package com.suktha.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.suktha.dtos.CommentDTO;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Date;

@Entity
@Data
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="content")
    private String content;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "voice_name")
    private String voiceName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private User user;

    //canvarting entity to dto
    public CommentDTO getCommentDto() {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setContent(content);
        commentDTO.setCreatedAt(createdAt);
        commentDTO.setId(id);
        commentDTO.setTaskId(task.getId());
        commentDTO.setPostedUserId(user.getId());
        commentDTO.setPostedUserName(user.getName());
        commentDTO.setImageName(imageName);
        commentDTO.setVoiceName(voiceName);
        return commentDTO;
    }
}
