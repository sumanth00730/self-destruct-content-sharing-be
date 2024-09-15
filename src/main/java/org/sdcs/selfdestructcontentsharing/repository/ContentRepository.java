package org.sdcs.selfdestructcontentsharing.repository;

import org.sdcs.selfdestructcontentsharing.models.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    Optional<Content> findByShareableLinkAndStatus(String shareableLink, Content.Status status);
}
