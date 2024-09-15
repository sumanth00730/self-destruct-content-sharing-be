package org.sdcs.selfdestructcontentsharing.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "content")
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1024)
    private String content; // Text or URL to the media

    private int viewLimit;  // Maximum number of views allowed
    private int views;      // Number of views so far
    private LocalDateTime expirationTime; // Expiry time of the content

    @Column(length = 1024)
    private String shareableLink;

    @Column(length = 1024)
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    private Status status; // ACTIVE or EXPIRED

    public enum Status {
        ACTIVE, EXPIRED
    }

    public Content() {
        this.shareableLink = UUID.randomUUID().toString(); // Generate unique link
    }
}

