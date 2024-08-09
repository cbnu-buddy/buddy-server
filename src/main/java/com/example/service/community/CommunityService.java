package com.example.service.community;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.community.*;
import com.example.domain.member.Member;
import com.example.domain.service.Service;
import com.example.dto.request.CreatePostRequest;
import com.example.dto.request.UpdatePostRequest;
import com.example.dto.response.MyPostResponse;
import com.example.dto.response.TagInfoResponse;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.community.*;
import com.example.repository.member.MemberRepository;
import com.example.repository.plan.PlanRepository;
import com.example.repository.service.ServiceRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import com.example.domain.community.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommunityService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PhotoRepository photoRepository;
    private final PostServiceRepository postServiceRepository;
    private final MemberRepository memberRepository;
    private final ServiceRepository serviceRepository;
    private final TokenProvider tokenProvider;
    private final CommentRepository commentRepository;
    private final ReplyRepository replyRepository;
    private final PostLikeRepository postLikeRepository;
    private final PlanRepository planRepository;


    // 토큰에서 사용자 ID 추출
    public String getUserIdFromToken(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(tokenProvider.resolveToken(request));
        Claims claims = tokenProvider.getTokenClaims(tokenProvider.resolveToken(request));
        return claims.getSubject();
    }

    /*
    게시글 생성
    */
    @Transactional
    public ApiResult<?> createPost(CreatePostRequest postRequest, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회

        // 새로운 게시글 생성, 저장
        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setMember(member);
        post.setCreatedTime(LocalDateTime.now());
        post.setViews(0);

        post = postRepository.save(post);

        // 태그 생성, 저장
        for (String tagName : postRequest.getTags()) {
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setTagName(tagName);
                        return tagRepository.save(newTag);
                    });

            PostTag postTag = new PostTag(post, tag);
            postTagRepository.save(postTag);
        }

        // 서비스 조회 후 저장
        for (Long serviceId : postRequest.getServiceIds()) {
            Service service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_SERVICE_ID)); // 서비스 조회
            PostService postService = new PostService(post, service);
            postServiceRepository.save(postService);
        }

        // 사진 생성, 저장
        for (String photoPath : postRequest.getPostImagePathUrls()) {
            Photo photo = new Photo();
            photo.setPost(post);
            photo.setPhotoPath(photoPath);
            photoRepository.save(photo);
        }

        return ApiResult.success("게시글 작성이 완료되었습니다");
    }

    /*
    게시글 수정
    */
    public ApiResult<?> updatePost(Long postId, UpdatePostRequest updatePostRequest, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 게시글 조회

        if (!post.getMember().equals(member)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS); // 게시글 작성자와 현재 사용자가 일치하는지 확인
        }

        // 제목, 내용, 생성 시기 수정 (post table)
        post.setTitle(updatePostRequest.getTitle());
        post.setContent(updatePostRequest.getContent());
        post.setCreatedTime(LocalDateTime.now());

        // 기존 태그, 서비스, 사진 삭제 (post_tags, post_services, photo table)
        deleteExistingPostTags(post);
        deleteExistingPostServices(post);
        deleteExistingPhotos(post);

        // 수정된 태그, 서비스, 사진 저장
        for (String tagName : updatePostRequest.getTags()) {
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setTagName(tagName);
                        return tagRepository.save(newTag);
                    });

            PostTag postTag = new PostTag(post, tag);
            postTagRepository.save(postTag);
        }

        for (Long serviceId : updatePostRequest.getServiceIds()) {
            Service service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_SERVICE_ID));
            PostService postService = new PostService(post, service);
            postServiceRepository.save(postService);
        }

        for (String photoPath : updatePostRequest.getPostImagePathUrls()) {
            Photo photo = new Photo();
            photo.setPost(post);
            photo.setPhotoPath(photoPath);
            photoRepository.save(photo);
        }

        postRepository.save(post);
        return ApiResult.success("게시글 수정이 완료되었습니다");
    }

    private void deleteExistingPostTags(Post post) {
        List<PostTag> postTags = postTagRepository.findByPost(post);
        postTagRepository.deleteAll(postTags);
    }

    private void deleteExistingPostServices(Post post) {
        List<PostService> postServices = postServiceRepository.findByPost(post);
        postServiceRepository.deleteAll(postServices);
    }

    private void deleteExistingPhotos(Post post) {
        List<Photo> photos = photoRepository.findByPost(post);
        photoRepository.deleteAll(photos);
    }

    /*
    게시글 삭제
    */
    @Transactional
    public ApiResult<?> deletePost(Long postId, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)); // 사용자 조회

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND)); // 게시글 조회

        if (!post.getMember().equals(member)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS); // 게시글 작성자와 현재 사용자가 일치하는지 확인
        }

        // 기존 태그, 서비스, 사진 삭제
        deleteExistingPostTags(post);
        deleteExistingPostServices(post);
        deleteExistingPhotos(post);

        // 게시글 삭제
        postRepository.delete(post);

        return ApiResult.success("게시글 삭제가 완료되었습니다");
    }

    /*
    내가 쓴 커뮤니티 게시글 목록 조회
    */
    public ApiResult<List<MyPostResponse>> getMyCommunityPosts(HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<Post> posts = postRepository.findByMember(member);

        List<MyPostResponse> response = posts.stream().map(post -> {
            List<String> postImagePathUrls = post.getPhotos().stream()
                    .map(Photo::getPhotoPath)
                    .collect(Collectors.toList());

            List<String> tags = post.getPostTags().stream()
                    .map(postTag -> postTag.getTag().getTagName())
                    .collect(Collectors.toList());

            int commentCount = commentRepository.countByPostId(post.getId());
            int replyCount = replyRepository.countByCommentPostId(post.getId());
            int totalCommentCount = commentCount + replyCount;
            int likeCount = postLikeRepository.countByPostId(post.getId());


            return MyPostResponse.builder()
                    .postId(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .createdAt(post.getCreatedTime())
                    .postImagePathUrls(postImagePathUrls)
                    .author(MyPostResponse.Author.builder()
                            .memberId(member.getMemberId())
                            .username(member.getUsername())
                            .profileImagePathUrl(member.getProfile_path())
                            .build())
                    .tags(tags)
                    .views(post.getViews())
                    .commentCount(totalCommentCount)
                    .likeCount(likeCount)
                    .build();
        }).collect(Collectors.toList());

        return ApiResult.success(response);
    }


    /*
    태그 기반 커뮤니티 게시글 목록 정보 조회
    */
//    public ApiResult<?> getPostsByTag(String tag, int limit) {
//            PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
//
//            List<PostsByTagInfoResponse> response = postRepository.findByTagName(tag, pageRequest)
//                .stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//
//            return ApiResult.success(response);
//    }
//
////    private PostsByTagInfoResponse convertToDto(Post post) {
//
//        Member author = memberRepository.findById(post.getMemberId())
//                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
//
//        List<PostTag> postTags = postTagRepository.findByPost(post);
//        List<PostService> postServices = postServiceRepository.findByPost(post);
//        List<Plan> plans = planRepository.findByService_Id(service.getId());
//
//        return PostsByTagInfoResponse.builder()
//                .postId(post.getId())
//                .title(post.getTitle())
//                .content(post.getContent())
//                .createdAt(post.getCreatedTime())
//                //.modifiedAt(post.getModifiedAt()) modifiedAt 속성 추가시 추가 예정
//                11.postImagePathUrls(post.getId()getPostImagePathUrls())
//                .author(PostsByTagInfoResponse.AuthorDto.builder()
//                        .memberId(author.getMemberId())
//                        .username(author.getUsername())
//                        .profileImagePathUrl(author.getProfile_path())
//                        .build())
//                .tags(postTags.stream()
//                        .map(tag -> PostsByTagInfoResponse.TagsDto.builder()
//                                .tagId(tag.getTag().getId())
//                                .tag(tag.getTag().getTagName())
//                                .build())
//                        .collect(Collectors.toList()))
//                .views(post.getViews())
//                .services(postServices.stream()
//                        .map(service -> PostsByTagInfoResponse.ServiceDto.builder()
//                                .serviceId(service.getService().getId())
//                                .planIds(planRepository.findByService_Id(service.getId()))
//                                .name(service.getService().getName())
//                                .url(service.getService().getUrl())
//                                .build())
//                        .collect(Collectors.toList()))
//                .comments(post.getComments().stream()
//                        .map(comment -> PostResponse.CommentDto.builder()
//                                .comment(comment.getPostContent())
//                                .likeCount(comment.getLikeCount())
//                                .createdAt(comment.getCreatedTime())
//                                .writer(PostResponse.AuthorDto.builder()
//                                        .memberId(comment.getMember().getMemberId())
//                                        .username(comment.getMember().getUsername())
//                                        .profileImagePathUrl(comment.getMember().getProfileImagePathUrl())
//                                        .build())
//                                .replies(comment.getReplies().stream()
//                                        .map(reply -> PostResponse.CommentDto.ReplyDto.builder()
//                                                .reply(reply.getContent())
//                                                .likeCount(reply.getLikeCount())
//                                                .createdAt(reply.getCreatedTime())
//                                                .writer(PostResponse.AuthorDto.builder()
//                                                        .memberId(reply.getMember().getMemberId())
//                                                        .username(reply.getMember().getUsername())
//                                                        .profileImagePathUrl(reply.getMember().getProfileImagePathUrl())
//                                                        .build())
//                                                .build())
//                                        .collect(Collectors.toList()))
//                                .build())
//                        .collect(Collectors.toList()))
//                .likeCount(post.getLikeCount())
//                .build();
//    }


    /*
    추천 태그 목록 정보 조회
    */
    public ApiResult<?> getTop10Tags() {
        List<Object[]> tagPostCounts = postTagRepository.findTopTagsWithPostCount();

        List<TagInfoResponse> topTagsResponse = tagPostCounts.stream()
                .limit(10)
                .map(result -> {
                    Tag tag = (Tag) result[0];
                    Long postCount = (Long) result[1];
                    return TagInfoResponse.builder()
                            .tagId(tag.getId())
                            .tagName(tag.getTagName())
                            .postCount(postCount)
                            .build();
                })
                .collect(Collectors.toList());

        return  ApiResult.success(topTagsResponse);
    }

    /*
    연관 검색 태그 목록 정보 조회
    */

    public ApiResult<?> getRelatedTags(String query) {
        List<Object[]> tagPostCounts = postTagRepository.findRelatedTagsWithPostCount(query);

        List<TagInfoResponse> relatedTagsResponse = tagPostCounts.stream()
                .map(result -> {
                    Tag tag = (Tag) result[0];
                    Long postCount = (Long) result[1];
                    return TagInfoResponse.builder()
                            .tagId(tag.getId())
                            .tagName(tag.getTagName())
                            .postCount(postCount)
                            .build();
                })
                .collect(Collectors.toList());

        return  ApiResult.success(relatedTagsResponse);
    }
}