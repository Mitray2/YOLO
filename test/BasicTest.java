//import org.junit.*;
//import java.util.*;
//import play.test.*;
//import utils.SecurityHelper;
//import models.*;
//
//public class BasicTest extends UnitTest {
//
//    @Before
//    public void setup() {
//        Fixtures.deleteAllModels();
//    }
//	
//	@Test
//	public void createAndRetrieveUser() {
//		User user = createUser("Aleksey");
//	    user.save();
//	    User bob = User.find("byEmail", "test@test.te").first();
//	    assertNotNull(bob);
//	    //assertEquals("Aleksey", bob.);
//	}
//
//	@Test
//	public void createPost() {
//		User aleksey = createUser("Aleksey");
//		aleksey.save();
//	    
//	    Post post = createPost(aleksey);
//	    post.save();
//	    
//	    assertEquals(1, Post.count());
//	    
//	    List<Post> alekseyPosts = Post.find("byAuthor", aleksey).fetch();
//	    
//	    assertEquals(1, alekseyPosts.size());
//	    Post firstPost = alekseyPosts.get(0);
//	    assertNotNull(firstPost);
//	    assertEquals(aleksey, firstPost.author);
//	    assertEquals("Play coockbook.", firstPost.title);
//	    assertEquals("long text of post will be there/....", firstPost.text);
//	    assertNotNull(firstPost.postDate);
//	}
//	
//	@Test
//	public void postComments() {
//	 
//		User aleksey = createUser("Aleksey");
//		aleksey.save();
//		
//		User bender = createUser("Bender");
//		bender.save();
//	    
//	    Post post = createPost(aleksey);
//	    post.save();
//	 
//	    Comment comment1 = createComment(aleksey, post, "avtor zgot! peshi esche!").save();
//	    Comment comment2 = createComment(bender, post, "Rebyata, YAAAZZZZb! ZDOROVENNYI!!!!!!!").save();
//	 
//	    List<Comment> alekseyPostComments = Comment.find("byPost", post).fetch();
//	 
//	    assertEquals(2, alekseyPostComments.size());
//	 
//	    Comment firstComment = alekseyPostComments.get(0);
//	    assertNotNull(firstComment);
//	    assertEquals(aleksey, firstComment.author);
//	    assertEquals("avtor zgot! peshi esche!", firstComment.text);
//	    assertNotNull(firstComment.date);
//	 
//	    Comment secondComment = alekseyPostComments.get(1);
//	    assertNotNull(secondComment);
//	    assertEquals(bender, secondComment.author);
//	    assertEquals("Rebyata, YAAAZZZZb! ZDOROVENNYI!!!!!!!", secondComment.text);
//	    assertNotNull(secondComment.date);
//	}
//	
//	@Test
//	public void useTheCommentsRelation() {
//		
//		User aleksey = createUser("Aleksey");
//		aleksey.save();
//		
//		User bender = createUser("Bender");
//		bender.save();
//		
//	    Post post = createPost(aleksey);
//	    post.save();
//		
//	    post.addComment(createComment(aleksey, null, "test1"));
//	    post.addComment(createComment(aleksey, null, "test1"));
//	 
//	    assertEquals(2, User.count());
//	    assertEquals(1, Post.count());
//	    assertEquals(2, Comment.count());
//	 
//	    post = Post.find("byAuthor", aleksey).first();
//	    assertNotNull(post);
//	 
//	    assertEquals(2, post.comments.size());
//	    assertEquals(aleksey, post.comments.get(0).author);
//	    
//	    post.delete();
//	    
//	    assertEquals(2, User.count());
//	    assertEquals(0, Post.count());
//	    assertEquals(0, Comment.count());
//	}
//	
//	@Test
//	public void testBlogType() {
//		BlogType blogType = new BlogType();
//		blogType.description = "descr";
//		blogType.name = "common";
//		blogType.save();
//		assertEquals(1, BlogType.count());
//		
//		User bender = createUser("Bender");
//		bender.save();
//	    
//	    Post post = createPost(bender);
//	    post.type = blogType;
//	    post.save();
//
//	    blogType.addPost(post);
//	    blogType = BlogType.find("byName", "common").first();
//	    assertEquals(1, blogType.posts.size());
//	    
//	    post = Post.find("byTitle", "Play coockbook.").first();
//	    assertEquals("descr", post.type.description);
//	}
//
//	private Comment createComment(User author, Post post, String text) {
//		Comment result = new Comment();
//		result.author = author;
//		result.date = new Date();
//		result.post = post;
//		result.text = text;
//		return result;
//	}
//
//	private Post createPost(User user) {
//		Post post = new Post();
//	    post.author = user;
//	    post.postDate = new Date();
//	    post.preview = "post preview";
//	    post.text = "long text of post will be there/....";
//	    post.title = "Play coockbook.";
//	    post.updateDate = new Date();
//		return post;
//	}
//	
//	private User createUser(String name) {
//		User user = new User();
//		user.birthDate = new Date();
//		user.email = "test@test.te";
//		user.firstName = name;
//		user.lastName = "Tsuber";
//		user.middleName = ":)";
//		user.status = User.STATUS_LEVEL_3;
//		user.isAdmin = true;
//		user.passwordHash = SecurityHelper.createPasswordHash("password");
//		return user;
//	}
//	
//	@Test
//	public void testTags() {
//	    // Create a new user and save it
//	    User bob = createUser("Bob");
//	    bob.save();
//	    // Create a new post
//	    Post bobPost = new Post(bob, "My first post", "Hello world").save();
//	    Post anotherBobPost = new Post(bob, "Hop", "Hello world").save();
//	    
//	    // Well
//	    assertEquals(0, Post.findTaggedWith("Red").size());
//	    
//	    // Tag it now
//	    bobPost.tagItWith("Red").tagItWith("Blue").save();
//	    anotherBobPost.tagItWith("Red").tagItWith("Green").save();
//	    
//	    // Check
//	    assertEquals(2, Post.findTaggedWith("Red").size());        
//	    assertEquals(1, Post.findTaggedWith("Blue").size());
//	    assertEquals(1, Post.findTaggedWith("Green").size());
//	    
//	    
//	    assertEquals(1, Post.findTaggedWith("Red", "Blue").size());   
//	    assertEquals(1, Post.findTaggedWith("Red", "Green").size());   
//	    assertEquals(0, Post.findTaggedWith("Red", "Green", "Blue").size());  
//	    assertEquals(0, Post.findTaggedWith("Green", "Blue").size());
//	    
//	    List<Map> cloud = Tag.getCloud();
//	    assertEquals(
//	        "[{tag=Blue, pound=1}, {tag=Green, pound=1}, {tag=Red, pound=2}]", 
//	        cloud.toString()
//	    );
//	}
//
//}
