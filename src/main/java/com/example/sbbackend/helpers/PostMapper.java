package com.example.sbbackend.helpers;

import com.example.sbbackend.dto.*;
import com.example.sbbackend.models.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PostMapper {

    public static PostCommentDto toPostDto(Posts post) {
        PostCommentDto postDto = new PostCommentDto();
        postDto.setId(post.getId());
        postDto.setTitle(post.getTitle());
        postDto.setSlug(post.getSlug());
        postDto.setDomain(post.getDomain());
        postDto.setTags(post.getTags());
        postDto.setImage(post.getImage());
        postDto.setDescription(post.getDescription());
        postDto.setContent(post.getContent());
        postDto.setCreatedBy(toUserDto(post.getCreatedBy()));
        postDto.setComments(post.getComments().stream().map(PostMapper::toCommentDto).toList());
        postDto.setReactions(post.getReactions().stream().map(PostMapper::toReactionsDto).toList());
        postDto.setCreatedAt(post.getCreatedAt());

        postDto.setUpdatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt()  : null);
        return postDto;
    }
    public static PostDto toALLPostDto(Posts post) {
        PostDto postDtoo = new PostDto();
        postDtoo.setId(post.getId());
        postDtoo.setTitle(post.getTitle());
        postDtoo.setSlug(post.getSlug());
        postDtoo.setDomain(post.getDomain());
        postDtoo.setTags(post.getTags());
        postDtoo.setState(post.getState());
        postDtoo.setImage(post.getImage());
        postDtoo.setDescription(post.getDescription());
        postDtoo.setContent(post.getContent());
        postDtoo.setCreatedBy(toUserDto(post.getCreatedBy()));
        postDtoo.setComments(post.getComments().size());
        postDtoo.setReactions(post.getReactions().stream().map(PostMapper::toReactionsDto).toList());
        postDtoo.setCreatedAt(post.getCreatedAt());

        //postDto.setUpdatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt()  : null);
        return postDtoo;
    }
    public static UsersListDto toUsersListDto(User user) {
        UsersListDto userDto = new UsersListDto();
         userDto.setId(user.getId());
        userDto.setFirstname(user.getFirstname());
        userDto.setLastname(user.getLastname());
        userDto.setEmail(user.getEmail());
        userDto.setAvatar(user.getAvatar());
        userDto.setBirthday(user.getBirthday());

        userDto.setRole(user.getRole());
        userDto.setBio(user.getBio());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setUpdatedAt(user.getUpdatedAt());
        userDto.setAddress(user.getAddress());
        userDto.setConfirmed(user.getConfirmed());



        return userDto;
    }
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
       // userDto.setId(user.getId());
        userDto.setFirstname(user.getFirstname());
        userDto.setLastname(user.getLastname());
        userDto.setAvatar(user.getAvatar());
        return userDto;
    }
    public static MyCvsDto toMyCvsDto(MyCvs mcv) {
        MyCvsDto cv = new MyCvsDto();
         cv.setId(mcv.getId());
        cv.setTitle(mcv.getTitle());
        cv.setLanguage(mcv.getLanguage());
        cv.setFileUrl(mcv.getFileUrl());
        cv.setPhotoUrl(mcv.getPhotoUrl());
        cv.setContent(mcv.getContent());
        cv.setCreatedAt(mcv.getCreatedAt());
        cv.setUpdatedAt(mcv.getUpdatedAt());
        cv.setCreatedBy(toUserDto(mcv.getCreatedBy()));
        return cv;
    }
    public static User toAccountDto(User user) {
        User account = new User();
         account.setId(user.getId());
        account.setFirstname(user.getFirstname());
        account.setLastname(user.getLastname());
        account.setAvatar(user.getAvatar());
        account.setEmail(user.getEmail());
        account.setConfirmed(user.getConfirmed());
        account.setRole(user.getRole());
        account.setSavedItems(user.getSavedItems());
        account.setAddress(user.getAddress());
        account.setBirthday(user.getBirthday());
        account.setCreatedAt(user.getCreatedAt());



        return account;
    }

    public static CommentDto toCommentDtoList(List<Comments> comments) {
        return (CommentDto) comments.stream()
                .map(PostMapper::toCommentDto)
                .collect(Collectors.toList());
    }
    public static SavedItemDto toSavedItemDtoList(SavedItems savedItems) {
        SavedItemDto savedItemDto = new SavedItemDto();
               savedItemDto.setType(savedItems.getType());
        savedItemDto.setTitle(savedItems.getTitle());
        savedItemDto.setValue(savedItems.getValue());

        return savedItemDto;
    }
    public static SearchDto toSearchItemDto(Posts post) {
        SearchDto searchedItem = new SearchDto();
        searchedItem.setTitle(post.getTitle());
        searchedItem.setSlug(post.getSlug());
        searchedItem.setImage(post.getImage());

        return searchedItem;
    }
    public static CommentDto toCommentDto(Comments comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setContent(comment.getContent());
        commentDto.setCreatedAt(comment.getCreatedAt());
        commentDto.setCreatedBy(toUserDto(comment.getCreatedBy()));
        commentDto.setUpdatedAt(comment.getUpdatedAt() != null ? comment.getUpdatedAt() : null);
        return commentDto;
    }
    public static ReactionsDto toReactionsDto(Reactions rect) {
        ReactionsDto reaction = new ReactionsDto();

        reaction.setType(rect.getType());
        reaction.setCreatedAt(rect.getCreatedAt());
        reaction.setUser(toUserDto(rect.getUser()));
          return reaction;
    }
}
