package org.sdcs.selfdestructcontentsharing.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ContentResponse {
    private String content;         // Text content
    private String mediaUrl;        // Media URL, if available
    private LocalDateTime expirationTime;
    private int remainingViews;
    private boolean isExpired;

    // Constructor
    public ContentResponse(String content, String mediaUrl, LocalDateTime expirationTime, int remainingViews, boolean isExpired) {
        this.content = content;
        this.mediaUrl = mediaUrl;
        this.expirationTime = expirationTime;
        this.remainingViews = remainingViews;
        this.isExpired = isExpired;
    }
}
