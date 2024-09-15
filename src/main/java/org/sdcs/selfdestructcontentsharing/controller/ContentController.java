package org.sdcs.selfdestructcontentsharing.controller;

import org.sdcs.selfdestructcontentsharing.dto.ContentResponse;
import org.sdcs.selfdestructcontentsharing.models.Content;
import org.sdcs.selfdestructcontentsharing.services.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/content")
@CrossOrigin("https://self-destruct-content-sharing.netlify.app")
public class ContentController {

    @Autowired
    private ContentService contentService;

    // Create new content
    @PostMapping
    public ResponseEntity<String> createContent(@RequestParam String content,
                                                 @RequestParam int viewLimit,
                                                 @RequestParam String expirationTime,
                                                 @RequestParam(required = false) MultipartFile file) throws IOException {
        LocalDateTime expiration = LocalDateTime.parse(expirationTime);
        Content createdContent = contentService.createContent(content, viewLimit, expiration, file);
        String shareableLink = "https://self-destruct-content-sharing-2b4ed394ac66.herokuapp.com/api/content/view/" + createdContent.getShareableLink();
        return ResponseEntity.ok(shareableLink);
    }

    // View content
    @GetMapping(value = "/view/{shareableLink}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContentResponse> viewContent(@PathVariable String shareableLink) {
        Optional<Content> contentOptional = contentService.viewContentByLink(shareableLink);

        if (contentOptional.isPresent()) {
            Content content = contentOptional.get();
            boolean isExpired = content.getStatus() == Content.Status.EXPIRED;
            int remainingViews = content.getViewLimit() - content.getViews();

            ContentResponse response = new ContentResponse(
                    content.getContent(), // Text content
                    content.getMediaUrl(), // Media URL if available
                    content.getExpirationTime(),
                    remainingViews,
                    isExpired
            );

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(410).body(null); // Content expired or not found
        }
    }
}
