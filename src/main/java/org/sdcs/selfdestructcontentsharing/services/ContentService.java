package org.sdcs.selfdestructcontentsharing.services;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.sdcs.selfdestructcontentsharing.models.Content;
import org.sdcs.selfdestructcontentsharing.repository.ContentRepository;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ContentService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private S3Service s3Service;

    // Create new content with view limit and expiration time
    public Content createContent(String content, int viewLimit, LocalDateTime expirationTime, MultipartFile file) throws IOException {
        Content newContent = new Content();
        newContent.setContent(content);
        newContent.setViewLimit(viewLimit);
        newContent.setViews(0);
        newContent.setExpirationTime(expirationTime);
        newContent.setStatus(Content.Status.ACTIVE);

        // Upload the media file to S3 and set the media URL
        if (file != null && !file.isEmpty()) {
            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File size exceeds the 5MB limit.");
            }
            String mediaUrl = s3Service.uploadFile(file);
            newContent.setMediaUrl(mediaUrl);
        }


        return contentRepository.save(newContent);
    }

    // View content, increment view count, and check for expiration or view limit
    public Optional<Content> viewContentByLink(String shareableLink) {
        Optional<Content> optionalContent = contentRepository.findByShareableLinkAndStatus(shareableLink, Content.Status.ACTIVE);

        if (optionalContent.isPresent()) {
            Content content = optionalContent.get();

            // Check if content is expired
            if (content.getExpirationTime().isBefore(LocalDateTime.now())) {
                expireContent(content);
                return Optional.empty();
            }

            // Increment the view count in Redis
            int currentViews = redisService.incrementViewCount(content.getId());  // Assuming RedisService increments the count

            content.setViews(currentViews);  // Set updated views count in content
            contentRepository.save(content);

            // Check if view limit is exceeded
            if (currentViews > content.getViewLimit()) {
                expireContent(content);
                return Optional.empty();
            }

            return Optional.of(content);
        }

        return Optional.empty();
    }

    // Expire content and mark it as EXPIRED
    private void expireContent(Content content) {
        content.setStatus(Content.Status.EXPIRED);
        contentRepository.save(content);
        redisService.deleteViewCount(content.getId()); // Remove the Redis entry for view tracking
    }
}
