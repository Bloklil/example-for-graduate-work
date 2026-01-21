package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.entities.AdEntity;
import ru.skypro.homework.entities.CommentEntity;
import ru.skypro.homework.entities.UserEntity;
import ru.skypro.homework.exceptions.AdNotFoundException;
import ru.skypro.homework.exceptions.CommentNotFoundException;
import ru.skypro.homework.exceptions.UserNotFoundException;
import ru.skypro.homework.mappers.CollectionMapper;
import ru.skypro.homework.mappers.CommentMapper;
import ru.skypro.homework.repositories.AdRepository;
import ru.skypro.homework.repositories.CommentRepository;
import ru.skypro.homework.repositories.UserRepository;
import ru.skypro.homework.service.CommentService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final CollectionMapper collectionMapper;

    private UserEntity getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public CommentsDto getComments(Integer adId) {
        AdEntity adEntity = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));

        List<CommentEntity> comments = commentRepository.findAllByAd(adEntity);
        log.debug("Found {} comments for ad: {}", comments.size(), adId);
        return collectionMapper.commentsToDto(comments);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ru.skypro.homework.dto.CommentDto createComment(Integer adId, CreateOrUpdateCommentDto commentDto) {
        AdEntity adEntity = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));

        UserEntity author = getCurrentUserEntity();
        CommentEntity commentEntity = commentMapper.toEntity(commentDto, author);
        commentEntity.setAd(adEntity);

        CommentEntity savedComment = commentRepository.save(commentEntity);
        log.info("Comment created: {} for ad: {} by user: {}",
                savedComment.getId(), adId, author.getEmail());
        return commentMapper.toDto(savedComment);
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ru.skypro.homework.dto.CommentDto updateComment(Integer adId, Integer commentId, CreateOrUpdateCommentDto commentDto) {
        AdEntity adEntity = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));

        CommentEntity commentEntity = commentRepository.findByIdAndAd(commentId, adEntity)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

        UserEntity currentUser = getCurrentUserEntity();

        boolean isCommentAuthor = commentEntity.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        if (!isCommentAuthor && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to update this comment");
        }

        commentMapper.updateEntityFromDto(commentEntity, commentDto);
        CommentEntity updatedComment = commentRepository.save(commentEntity);
        log.info("Comment updated: {} for ad: {} by user: {}",
                commentId, adId, currentUser.getEmail());
        return commentMapper.toDto(updatedComment);
    }

    @Override
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public void deleteComment(Integer adId, Integer commentId) {
        AdEntity adEntity = adRepository.findById(adId)
                .orElseThrow(() -> new AdNotFoundException("Ad not found with id: " + adId));

        CommentEntity commentEntity = commentRepository.findByIdAndAd(commentId, adEntity)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with id: " + commentId));

        UserEntity currentUser = getCurrentUserEntity();

        boolean isCommentAuthor = commentEntity.getAuthor().getId().equals(currentUser.getId());
        boolean isAdAuthor = adEntity.getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        if (!isCommentAuthor && !isAdAuthor && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this comment");
        }

        commentRepository.delete(commentEntity);
        log.info("Comment deleted: {} from ad: {} by user: {}",
                commentId, adId, currentUser.getEmail());
    }
}
