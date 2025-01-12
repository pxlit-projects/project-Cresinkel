import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PostsComponent } from './posts.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PostService } from '../services/post.service';
import { CommentService } from '../services/comment.service';
import {of, throwError} from 'rxjs';
import { PostResponse } from '../models/post.response';
import { CommentResponse } from '../models/comment.response';
import {NavbarComponent} from "../navbar/navbar.component";

describe('PostsComponent', () => {
  let component: PostsComponent;
  let fixture: ComponentFixture<PostsComponent>;
  let postService: jasmine.SpyObj<PostService>;
  let commentService: jasmine.SpyObj<CommentService>;

  beforeEach(async () => {
    const postServiceSpy = jasmine.createSpyObj('PostService', [
      'getPosts',
      'getNotifications'
    ]);
    const commentServiceSpy = jasmine.createSpyObj('CommentService', [
      'getCommentsForPost',
      'addComment',
      'updateComment',
      'deleteComment'
    ]);

    postServiceSpy.getPosts.and.returnValue(of([
      {
        id: 1,
        title: 'Post 1',
        description: 'Description 1',
        author: 'Author 1',
        publicationDate: '2025-01-01',
        lastEditedDate: '2025-01-01',
        draft: false,
        accepted: true,
        rejectionReason: ''
      },
      {
        id: 2,
        title: 'Post 2',
        description: 'Description 2',
        author: 'Author 2',
        publicationDate: '2025-01-02',
        lastEditedDate: '2025-01-02',
        draft: false,
        accepted: true,
        rejectionReason: ''
      }
    ]));

    postServiceSpy.getNotifications.and.returnValue(of([
      {
        id: 1,
        description: "Description 1"
      },
      {
        id: 2,
        description: "Description 2"
      }
    ]))

    commentServiceSpy.getCommentsForPost.and.returnValue(of([
      {
        commentId: 1,
        description: "Description 1",
        author: "Author 1",
        postId: 1
      }
    ]))

    await TestBed.configureTestingModule({
      imports: [
        PostsComponent,
        FormsModule,
        CommonModule,
        NavbarComponent
      ],
      providers: [
        { provide: PostService, useValue: postServiceSpy },
        { provide: CommentService, useValue: commentServiceSpy }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(PostsComponent);
    component = fixture.componentInstance;
    postService = TestBed.inject(PostService) as jasmine.SpyObj<PostService>;
    commentService = TestBed.inject(CommentService) as jasmine.SpyObj<CommentService>;
    fixture.detectChanges();
  });

  afterEach(() => {
    fixture.destroy();
    TestBed.resetTestingModule();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize posts on ngOnInit', () => {
    component.ngOnInit();
    fixture.detectChanges();

    expect(component.posts.length).toBe(2);
    expect(component.filteredPosts.length).toBe(2);
  });

  it('should fetch comments for a post on getPosts', () => {
    const mockPost: PostResponse = {
      id: 1,
      title: 'Post 1',
      description: 'Description 1',
      author: 'Author 1',
      publicationDate: '2025-01-01',
      lastEditedDate: '2025-01-01',
      draft: false,
      accepted: true,
      rejectionReason: ''
    };
    const mockComments: CommentResponse[] = [
      { commentId: 1, description: 'First comment', author: 'Commenter 1', postId: 1 },
      { commentId: 2, description: 'Second comment', author: 'Commenter 2', postId: 1 }
    ];

    postService.getPosts.and.returnValue(of([mockPost]));
    commentService.getCommentsForPost.and.returnValue(of(mockComments));

    component.getPosts();
    fixture.detectChanges();

    expect(commentService.getCommentsForPost).toHaveBeenCalledWith(1);
    expect(component.comments[1]).toEqual(mockComments);
  });

  it('should add a comment', () => {
    const postId = 1;
    const newCommentText = 'New Comment';
    component.newComment[postId] = newCommentText;

    const newCommentResponse: CommentResponse = {
      commentId: 3,
      description: newCommentText,
      author: 'Commenter 3',
      postId: postId
    };

    commentService.addComment.and.returnValue(of(undefined));
    commentService.getCommentsForPost.and.returnValue(of([newCommentResponse]));

    component.addComment(postId);
    fixture.detectChanges();

    expect(commentService.addComment).toHaveBeenCalledWith(postId, newCommentText);
    expect(component.comments[postId].length).toBeGreaterThan(0);
    expect(component.newComment[postId]).toBe('');
  });

  it('should toggle edit mode for a comment', () => {
    const commentId = 1;
    component.toggleEditMode(commentId);

    expect(component.editMode[commentId]).toBeTrue();

    component.toggleEditMode(commentId);
    expect(component.editMode[commentId]).toBeFalse();
  });

  it('should save an edited comment', () => {
    const commentId = 1;
    const postId = 1;
    const updatedDescription = 'Updated Comment Description';
    component.editCommentText[commentId] = updatedDescription;

    commentService.updateComment.and.returnValue(of(undefined));

    component.saveComment(commentId, postId);
    fixture.detectChanges();

    expect(commentService.updateComment).toHaveBeenCalledWith(commentId, updatedDescription);
  });

  it('should delete a comment', () => {
    const commentId = 1;
    const postId = 1;

    commentService.deleteComment.and.returnValue(of(undefined));

    component.deleteComment(commentId, postId);
    fixture.detectChanges();

    expect(commentService.deleteComment).toHaveBeenCalledWith(commentId);
  });

  it('should filter posts by author, date, and description', () => {
    const mockPosts: PostResponse[] = [
      {
        id: 1,
        title: 'Post 1',
        description: 'Description 1',
        author: 'Author 1',
        publicationDate: '2025-01-01',
        lastEditedDate: '2025-01-01',
        draft: false,
        accepted: true,
        rejectionReason: ''
      },
      {
        id: 2,
        title: 'Post 2',
        description: 'Description 2',
        author: 'Author 2',
        publicationDate: '2025-01-02',
        lastEditedDate: '2025-01-02',
        draft: false,
        accepted: true,
        rejectionReason: ''
      },
      {
        id: 3,
        title: 'Post 3',
        description: 'Description 3',
        author: 'Author 1',
        publicationDate: '2025-01-01',
        lastEditedDate: '2025-01-01',
        draft: false,
        accepted: true,
        rejectionReason: ''
      }
    ];

    component.posts = mockPosts;
    component.dateFilter = '2025-01-01';
    component.authorFilter = 'Author 1';
    component.descriptionFilter = 'Description';

    component.filterPosts();
    fixture.detectChanges();

    expect(component.filteredPosts.length).toBe(2);
    expect(component.filteredPosts[0].author).toBe('Author 1');
    expect(component.filteredPosts[0].publicationDate).toBe('2025-01-01');
  });

  it('should handle no posts being returned from the service', () => {
    postService.getPosts.and.returnValue(of([]));
    component.getPosts();
    fixture.detectChanges();

    expect(component.posts.length).toBe(0);
    expect(component.filteredPosts.length).toBe(0);
  });

  it('should handle an error when fetching posts', () => {
    postService.getPosts.and.returnValue(throwError(() => new Error('Error fetching posts')));
    component.getPosts();
    fixture.detectChanges();

    expect(component.posts.length).toBe(0);
  });
});
