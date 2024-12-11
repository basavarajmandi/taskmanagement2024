package com.suktha.dtos;

import lombok.Data;

import java.util.Date;

@Data

public class CommentDTO {

    private Long id;

    private String content;

    private Date createdAt;

    private Long postedUserId;

    private String postedUserName;

    private Long taskId;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostedUserId() {
        return postedUserId;
    }

    public void setPostedUserId(Long postedUserId) {
        this.postedUserId = postedUserId;
    }

    public String getPostedUserName() {
        return postedUserName;
    }

    public void setPostedUserName(String postedUserName) {
        this.postedUserName = postedUserName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}
