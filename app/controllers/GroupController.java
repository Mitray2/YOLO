package controllers;

import modelDTO.CommandDTO;
import modelDTO.SimpleTopicMessage;
import models.*;
import notifiers.Mails;
import play.db.jpa.JPA;
import play.modules.paginate.ModelPaginator;
import play.mvc.Before;
import play.mvc.Catch;
import utils.ApplicationConstants;
import utils.SessionData.SessionUserMessage;
import utils.SessionHelper;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupController extends BasicController implements ApplicationConstants {



  @Catch(value = Throwable.class, priority = 1)
  public static void onError(Throwable throwable) {
    User user = User.findById(SessionHelper.getCurrentUser(session).id);
    if (user.command != null) {
      index(user.command.id);
    } else {
      UserController.index(user.id);
    }
  }

  @Before(unless={"getNewTopicMessages"})
  public static void checkSecurity() {
    // TODO warnings on page
    User currentUser = SessionHelper.getCurrentUser(session);
    if (currentUser == null)
      CommonController.error(CommonController.ERROR_SECURITY);
    if (currentUser != null) {
      User user = User.findById(currentUser.id);
      user.lastSeen = new Date();
      user.save();
    }
    if (currentUser.role == User.ROLE_INPERFECT_USER) {
      redirect(request.getBase() + ApplicationConstants.BLANK_FORM_PATH);
    }
    if (currentUser.role.equals(User.ROLE_WITHOUT_BLANK)) {
      redirect(request.getBase() + ApplicationConstants.BLANK_FORM_PATH);
    }
  }

  private static boolean memberOfGroup(Long groupId) {
    User user = User.findById(SessionHelper.getCurrentUser(session).id);
    if (groupId.equals(user.command != null ? user.command.id : null)) {
      return true;
    } else {
      SessionHelper.setCurrentUser(session, user);
      return false;
    }
  }

  public static void index(Long id) {
    Command group = Command.findById(id);
    //User sessionUser = SessionHelper.getCurrentUser(session);
//		LastUserData lUserData = new LastUserData();
//		String statement = "select * from LastUserData as lUser where lUser.lastSeen=(select max(l.lastSeen) from LastUserData as l where l.commandId=" + group.id
//				+ " and l.userId<> " + sessionUser.id + ")";
//		ResultSet luData = DB.executeQuery(statement);
//		try {
//			if (luData != null) {
//				while (luData.next()) {
//
//					lUserData.userId = luData.getLong("userId");
//					lUserData.commandId = luData.getLong("commandId");
//					lUserData.name = luData.getString("name");
//					lUserData.lastName = luData.getString("lastName");
//					String strDate = luData.getString("lastSeen");
//					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					try {
//						lUserData.lastSeen = format.parse(strDate);
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    render(group);
  }

  public static void editGroup(Long id) {
    if (!memberOfGroup(id)) {
      index(id);
    }
    Command group = Command.findById(id);
    User sessionUser = SessionHelper.getCurrentUser(session);
//		LastUserData lUserData = new LastUserData();
//		String statement = "select * from LastUserData as lUser where lUser.lastSeen=(select max(l.lastSeen) from LastUserData as l where l.commandId=" + group.id
//				+ " and l.userId<> " + sessionUser.id + ")";
//		ResultSet lastUserDatas = DB.executeQuery(statement);
//		try {
//			if (lastUserDatas != null) {
//				while (lastUserDatas.next()) {
//
//					lUserData.userId = lastUserDatas.getLong("userId");
//					lUserData.commandId = lastUserDatas.getLong("commandId");
//					lUserData.name = lastUserDatas.getString("name");
//					lUserData.lastName = lastUserDatas.getString("lastName");
//					String strDate = lastUserDatas.getString("lastSeen");
//					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//					try {
//						lUserData.lastSeen = format.parse(strDate);
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    render(group);
  }

  public static void createGroup() {
    User currentUser = SessionHelper.getCurrentUser(session);
    User user = User.findById(currentUser.id);
    if (user.role == User.ROLE_GROUP_ADMIN) {
      SessionHelper.setCurrentUser(session, user);
      UserController.index(user.id);
    }
    user.commandToInvite = null;
    user.command = null;
    user.role = User.ROLE_USER;
    user.save();
    SessionHelper.setCurrentUser(session, user);
    render();
  }

  public static void saveGroup(CommandDTO group) {
//        if (!memberOfGroup(group.id)) {
//            index(group.id);
//        }
    User currentUser = SessionHelper.getCurrentUser(session);
    User user = User.findById(currentUser.id);
    if (group.id == null && user.role == User.ROLE_GROUP_ADMIN) {
      SessionHelper.setCurrentUser(session, user);
      UserController.index(user.id);
    }
    Command command = new Command();
    command.global = group.global;
    command.name = group.name;
    command.city = group.city;
    group.country = Country.findById(group.country.id);
    command.country = group.country;
    command.description = group.description;
    command.isVacancy = group.isVacancy;
    group.phase = ProjectPhase.findById(group.phase.id);
    command.phase = group.phase;
    group.type = BType.findById(group.type.id);
    command.type = group.type;
    group.sphere = BSphere.findById(group.sphere.id);
    command.sphere = group.sphere;

    command.setMarketing(group.marketing);
    command.setLegal(group.legal);
    command.setTrade(group.sale);
    command.setFinance(group.finance);
    command.setProgramming(group.programming);
    command.setManagement(group.management);
    command.setOtherSkill(group.other);
    command.setCommunication(group.communication);
    command.setIdealize(group.idealize);
    command.setPragmatica(group.pragmatica);

    if (command.users == null) {
      command.users = new ArrayList<User>();
    }
    command.users.add(user);
    command.regDate = new Date();
    command.founderUser = user;
    command.save();
    user.command = command;
    user.role = user.ROLE_GROUP_ADMIN;
    user.save();
    //	creating main topic
    Topic mainTopic = new Topic();
    mainTopic.mainTopic = true;
    mainTopic.publicTopic = false;
    mainTopic.groupId = command.id;
    mainTopic.save();
    // creating public main topic
    Topic mainPublicTopic = new Topic();
    mainPublicTopic.mainTopic = true;
    mainPublicTopic.publicTopic = true;
    mainPublicTopic.groupId = command.id;
    mainPublicTopic.save();
    if (command.topics == null) {
      command.topics = new ArrayList<Topic>();
    }
    command.topics.add(mainTopic);
    command.topics.add(mainPublicTopic);
    command.save();
    // user lastSeen
//		LastUserData lastSeenUserData = null;
//		String statement = "select l from LastUserData as l where l.userId=?";
//		List<LastUserData> lastSeenUserDatas = LastUserData.find(statement, currentUser.id).fetch();
//		if (lastSeenUserDatas.isEmpty()) {
//			lastSeenUserData = new LastUserData();
//		} else {
//			lastSeenUserData = LastUserData.findById(lastSeenUserDatas.get(0).id);
//		}
//		lastSeenUserData.userId = currentUser.id;
//		lastSeenUserData.name = currentUser.name;
//		lastSeenUserData.lastName = currentUser.lastName;
//		lastSeenUserData.commandId = group.id;
//		lastSeenUserData.save();
    SessionHelper.setCurrentUser(session, user);
    index(command.id);
  }

  public static void updateGroup(CommandDTO group) {
    if (!memberOfGroup(group.id)) {
      index(group.id);
    }
    Command currentGroup = Command.findById(group.id);
    currentGroup.city = group.city;
    group.country = Country.findById(group.country.id);
    currentGroup.setCountry(group.country);
    currentGroup.name = group.name;
    currentGroup.setDescription(group.description);
    group.phase = ProjectPhase.findById(group.phase.id);
    currentGroup.setPhase(group.phase);
    currentGroup.setVacancy(group.isVacancy);
    group.type = BType.findById(group.type.id);
    currentGroup.setType(group.type);
    group.sphere = BSphere.findById(group.sphere.id);
    currentGroup.setSphere(group.sphere);

    currentGroup.finance.setActive(group.finance.active);
    currentGroup.finance.setDescrition(group.finance.descrition);

    currentGroup.trade.setActive(group.sale.active);
    currentGroup.trade.setDescrition(group.sale.descrition);

    currentGroup.marketing.setActive(group.marketing.active);
    currentGroup.marketing.setDescrition(group.marketing.descrition);

    currentGroup.management.setActive(group.management.active);
    currentGroup.management.setDescrition(group.management.descrition);

    currentGroup.programming.setActive(group.programming.active);
    currentGroup.programming.setDescrition(group.programming.descrition);

    currentGroup.legal.setActive(group.legal.active);
    currentGroup.legal.setDescrition(group.legal.descrition);

    currentGroup.otherSkill.setActive(group.other.active);
    currentGroup.otherSkill.setDescrition(group.other.descrition);
    currentGroup.otherSkill.setSkillName(group.other.skillName);

    currentGroup.pragmatica.setActive(group.pragmatica.active);
    currentGroup.pragmatica.setDescrition(group.pragmatica.descrition);

    currentGroup.idealize.setActive(group.idealize.active);
    currentGroup.idealize.setDescrition(group.idealize.descrition);

    currentGroup.communication.setActive(group.communication.active);
    currentGroup.communication.setDescrition(group.communication.descrition);

    currentGroup.global = group.global;

    currentGroup.save();
    User userCurrent = User.findById(SessionHelper.getCurrentUser(session).id);
    SessionHelper.setCurrentUser(session, userCurrent);
    index(currentGroup.getId());
  }

  public static void approveJoin(Long userForAproveId, Long groupId) {
    Command group = Command.findById(groupId);
    int lengthUsers = group.usersForAprove.size();
    for (int i = 0; i < lengthUsers; i++) {
      if (group.usersForAprove.get(i).id.equals(userForAproveId)) {
        User user = User.findById(group.usersForAprove.get(i).id);
        user.commandToInvite = null;
        group.getUsersForAprove().remove(i);
        group.getUsers().add(user);
        for (Command command : user.commandsForAprove) {
          if (command.id.equals(group.id)) {
            user.commandsForAprove.remove(command);
            break;
          }
        }
        user.command = group;

        logGroupMemberActivity(user, group, TeamMemberActivity.ACTION_MEMBER_JOINED);

        user.save();
        group.save();

        Mails.teamMemberApproved(user);

        break;
      }
    }
    index(groupId);
  }

  public static void declineJoin(Long userForAproveId, Long groupId) {
    Command group = Command.findById(groupId);
    int lengthUsers = group.usersForAprove.size();
    for (int i = 0; i < lengthUsers; i++) {
      if (group.usersForAprove.get(i).id.equals(userForAproveId)) {
        User user = User.findById(group.usersForAprove.get(i).id);
        user.commandToInvite = null;
        group.getUsersForAprove().remove(i);
        group.save();
        user.save();

        Mails.teamMemberDeclined(user, group);

        break;
      }
    }
    index(groupId);
  }

  public static void joinMyGroup(Long userForJoinId, Long groupId, String text) {
    User user = User.findById(userForJoinId);
    Command group = Command.findById(groupId);
    if (user.commandsForAprove == null) {
      user.commandsForAprove = new ArrayList<Command>();
    }
    boolean haveGroup = false;
    if (user.command != null) {
      if (user.command.id.equals(group.id))
        haveGroup = true;
    }
    if (!haveGroup) {
      for (Command command : user.commandsForAprove) {
        if (command.id.equals(group.id)) {
          haveGroup = true;
        }
      }
    }
    if (!haveGroup) {
      user.commandsForAprove.add(group);
      user.save();
    }
    Mails.groupRequest(user, request.getBase(), group, text);
    SessionHelper.setUserMessage(session, new SessionUserMessage(MESSAGE_GROUP_CONTROLLER_INVITATION_SENT));
    UserController.index(user.id);
  }

  public static void createTopic(Long groupId) {
    Command group = Command.findById(groupId);
    render(group);
  }

  public static void editTopic(Long topicId) {
    Topic topic = Topic.findById(topicId);
    if (!topic.publicTopic) {
      if (!memberOfGroup(topic.groupId)) {
        index(topic.groupId);
      }
    }
    Command group = Command.findById(topic.groupId);
    render(topic, group);
  }

  public static void saveEditTopic(Topic topic) {
    Topic currentTopic = Topic.findById(topic.id);
    if (!currentTopic.publicTopic) {
      if (!memberOfGroup(currentTopic.groupId)) {
        index(currentTopic.groupId);
      }
    }
    currentTopic.description = topic.description;
    currentTopic.name = topic.name;
    currentTopic.lastUpdateDate = new Date();
    currentTopic.save();
    indexTopic(currentTopic.id, topic.groupId);
  }

  public static void saveTopic(Topic topic) {
    User user = SessionHelper.getCurrentUser(session);
    Long groupId = user.command.id;
    if (!memberOfGroup(groupId)) {
      index(groupId);
    }
    Command group = Command.findById(groupId);
    topic.createdUserId = user.id;
    topic.createdDateTime = new Date();
    topic.lastUpdateDate = new Date();
    topic.groupId = groupId;
    topic.createdUserName = user.name;
    topic.createdUserLastName = user.lastName;
    topic.save();
    if (group.topics == null) {
      group.topics = new ArrayList<Topic>();
    }
    group.topics.add(topic);
    group.save();
    indexTopic(topic.id, group.id);
  }

  public static void indexTopic(Long topicId, Long groupId) {
    Command group = Command.findById(groupId);
    Topic topic = Topic.findById(topicId);
    if (!topic.publicTopic) {
      if (!memberOfGroup(groupId)) {
        index(groupId);
      }
    }
    ModelPaginator<TopicMessage> topicMessages = new ModelPaginator<TopicMessage>(TopicMessage.class, "topic.id=?", topic.id).orderBy("createDate");
    topicMessages.setPageSize(GROUP_TOPICS_PAGE_SIZE);
    Integer currentPage = request.params._contains(PARAMS_PAGE) ? Integer.valueOf(request.params.get(PARAMS_PAGE)) : topicMessages.getPageCount();
    topicMessages.setPageNumber(currentPage == 0 ? 1 : currentPage);
    Integer topicMessagesCount = topicMessages.size();
    render(topic, group, topicMessages, topicMessagesCount);
  }

  public static void addMsgToMainTopic(TopicMessage msg, Long topicId, Long groupId) {
    if (!memberOfGroup(groupId)) {
      index(groupId);
    }
    addMainMsg(msg, topicId, groupId);
    groupTopics(groupId);
  }

  public static void addMsgToPublicMainTopic(TopicMessage msg, Long topicId, Long groupId) {
    addMainMsg(msg, topicId, groupId);
    publicTopics(groupId);
  }

    public static void addTopicMessage(Long groupId, Long topicId, String message){
        Topic topic = Topic.findById(topicId);
        if (!topic.publicTopic && !memberOfGroup(groupId)) {
            renderJSON("{\"status\": 403 }");
        }
        //int res = JPA.em().createNativeQuery("insert into TopicMessage values ()")
        TopicMessage msg = new TopicMessage();
        msg.text = message;                     // TODO validate required and max size !!!
        msg.from = User.findById(SessionHelper.getCurrentUser(session).id);
        msg.createDate = new Date();
        msg.topic = topic;
        msg.save();

        topic.lastUpdateDate = msg.createDate;
        topic.lastUpdateUserId = msg.from.id;
        topic.lastUpdateUserName = msg.from.name;
        topic.lastUpdateUserLastName = msg.from.lastName;
        topic.save();

        renderJSON("{\"status\": 200, \"id\": " + msg.id + "}");
    }

    public static void getNewTopicMessages(Long topicId, long time) {
        List<SimpleTopicMessage> newMessages = new ArrayList<SimpleTopicMessage>();

        User user = SessionHelper.getCurrentUser(session);
        if(user != null){
            List<TopicMessage> newTopicMessages = TopicMessage.find("topic.id = ? AND createDate > ? ORDER BY createDate ASC", topicId, new Date(time)).fetch();
            for(TopicMessage msg : newTopicMessages) {
                newMessages.add(SimpleTopicMessage.fromFullMessage(msg));
            }
        }

        renderJSON(newMessages);
    }

  protected static void addMainMsg(TopicMessage msg, Long topicId, Long groupId){
    Topic topic = Topic.findById(topicId);
    if (!topic.publicTopic && !memberOfGroup(groupId)) {
        index(groupId);
    }
    msg.from = User.findById(SessionHelper.getCurrentUser(session).id);
    msg.createDate = new Date();
    msg.topic = topic;
    msg.save();
    /*if (topic.messages == null) {
      topic.messages = new ArrayList<TopicMessage>();
    }
    topic.messages.add(msg);
    topic.save();*/
  }

  public static void addMsgToTopic(TopicMessage msg, Long topicId, Long groupId) {
    Command group = Command.findById(groupId);
    Topic topic = Topic.findById(topicId);
    if (!topic.publicTopic) {
      if (!memberOfGroup(groupId)) {
        index(groupId);
      }
    }
    User user = User.findById(SessionHelper.getCurrentUser(session).id);
    msg.from = user;
    msg.createDate = new Date();
    msg.topic = topic;
    msg.save();
/*    if (topic.messages == null) {
      topic.messages = new ArrayList<TopicMessage>();
    }
    topic.messages.add(msg);*/
    topic.lastUpdateDate = msg.createDate;
    topic.lastUpdateUserId = user.id;
    topic.lastUpdateUserName = user.name;
    topic.lastUpdateUserLastName = user.lastName;
    topic.save();
    indexTopic(topic.id, group.id);
  }

  public static void groupTopics(Long groupId) {
    if (!memberOfGroup(groupId)) {
      index(groupId);
    }
    topics(groupId, false);
  }

  public static void publicTopics(Long groupId) {
    topics(groupId, true);
  }

  private static void topics(Long groupId, Boolean isPublic) {
    Command group = Command.findById(groupId);
    if (!isPublic) {
      if (!memberOfGroup(groupId)) {
        index(groupId);
      }
    }
    //find main topic
    Query ptQueryMainTopic = JPA.em().createQuery("select t from Topic t where t.groupId=? and t.publicTopic=? and t.mainTopic=? order by t.lastUpdateDate desc");
    ptQueryMainTopic.setParameter(1, groupId);
    ptQueryMainTopic.setParameter(2, isPublic);
    ptQueryMainTopic.setParameter(3, true);
    List<Topic> mainTopics = ptQueryMainTopic.getResultList();
    Topic mainTopic = mainTopics.size() > 0 ? mainTopics.get(0) : null;

    //find top 3 all topics
    Query ptQueryAllTopics = JPA.em().createQuery("select t from Topic t where t.groupId=? and t.publicTopic=? and t.mainTopic=? order by t.lastUpdateDate desc");
    ptQueryAllTopics.setParameter(1, groupId);
    ptQueryAllTopics.setParameter(2, isPublic);
    ptQueryAllTopics.setParameter(3, false);
    ptQueryAllTopics.setMaxResults(3);
    List<Topic> topics = ptQueryAllTopics.getResultList();

    Query ptQueryAllTopicsCount = JPA.em().createQuery("select count(t.id) from Topic t where t.groupId=? and t.publicTopic=? and t.mainTopic=?");
    ptQueryAllTopicsCount.setParameter(1, groupId);
    ptQueryAllTopicsCount.setParameter(2, isPublic);
    ptQueryAllTopicsCount.setParameter(3, false);
    Integer topicsCount = ((Long) ptQueryAllTopicsCount.getResultList().get(0)).intValue();

    Integer mainTopicMsgCount = 0;
    if (mainTopic != null) {
      //find top 10 messages of main topic to show on wall
      Query ptmQueryMainTopicMessages = JPA.em().createQuery("select t from TopicMessage t where t.topic.id=? order by t.createDate desc");
      ptmQueryMainTopicMessages.setParameter(1, mainTopic.id);
      ptmQueryMainTopicMessages.setMaxResults(GROUP_TOPICS_PAGE_SIZE);
      List<TopicMessage> msgs = ptmQueryMainTopicMessages.getResultList();
      mainTopic.messages = msgs;

      //get total count of main topic messages
      Query mainTopicMsgCountQuery = JPA.em().createQuery("select count(t.id) from TopicMessage t where t.topic.id = ?");
      mainTopicMsgCountQuery.setParameter(1, mainTopic.id);
      mainTopicMsgCount = ((Long) mainTopicMsgCountQuery.getResultList().get(0)).intValue();
    }

    render(topics, group, mainTopic, mainTopicMsgCount, topicsCount);
  }

  public static void moreTopics(Long groupId, Boolean isPublic) {
    if (!isPublic) {
      if (!memberOfGroup(groupId)) {
        index(groupId);
      }
    }
    Command group = Command.findById(groupId);

    if (!SessionHelper.getCurrentUser(session).command.id.equals(group.id)) {
      render("access error");
    }

    //find remain all topics
    Query ptQueryAllTopics = JPA.em().createQuery("select t from Topic t where t.groupId=? and t.publicTopic=? and t.mainTopic=? order by t.lastUpdateDate desc");
    ptQueryAllTopics.setParameter(1, groupId);
    ptQueryAllTopics.setParameter(2, isPublic);
    ptQueryAllTopics.setParameter(3, false);
    ptQueryAllTopics.setFirstResult(3);
    List<Topic> topics = ptQueryAllTopics.getResultList();
    Long userId = SessionHelper.getCurrentUser(session).getId();
    Boolean isAdmin = SessionHelper.getCurrentUser(session).role.equals(User.ROLE_ADMIN);
    render(topics, group, userId, isAdmin);
  }

  public static void more(Integer page, Long mainTopicId, Long groupId, String formAction, String removeAction) {
    Query ptmQuery = JPA.em().createQuery("select t from TopicMessage t where t.topic.id=? order by t.createDate desc");
    ptmQuery.setParameter(1, mainTopicId);
    ptmQuery.setFirstResult(page * GROUP_TOPICS_PAGE_SIZE);
    ptmQuery.setMaxResults(GROUP_TOPICS_PAGE_SIZE);
    List<TopicMessage> topicMessages = ptmQuery.getResultList();
    Long userId = SessionHelper.getCurrentUser(session).getId();
    Boolean isAdmin = SessionHelper.getCurrentUser(session).role.equals(User.ROLE_ADMIN);
    render(topicMessages, mainTopicId, userId, isAdmin, groupId, formAction, removeAction);
  }

  public static void groupUsers(Long groupId) {
    Command group = Command.findById(groupId);
    List<User> users = group.users;
    render(users, group);
  }

  public static void makeAdmin(Long userId) {
    User currentUser = User.findById(SessionHelper.getCurrentUser(session).id);
    User user = User.findById(userId);
    if (user.command == null || !currentUser.command.id.equals(user.command.id)) {
      groupUsers(currentUser.command.id);
    }
    currentUser.role = User.ROLE_USER;
    currentUser.save();
    user.role = User.ROLE_GROUP_ADMIN;
    user.save();
    SessionHelper.updateUser(user);
    SessionHelper.setCurrentUser(session, currentUser);
    index(user.command.id);
  }

  public static void removeUser(Long userId) {
    User user = User.findById(userId);
    Long groupId = user.command.id;
    user.command = null;
    user.save();
    groupUsers(groupId);
  }

  public static void editMainMsg(TopicMessage msg, Long topicId, Long groupId) {
    if (!memberOfGroup(groupId)) {
      index(groupId);
    }
    modifyMainMsg(msg, topicId, groupId);
    groupTopics(groupId);
  }

  public static void editPublicMainMsg(TopicMessage msg, Long topicId, Long groupId) {
    modifyMainMsg(msg, topicId, groupId);
    publicTopics(groupId);
  }

  protected static void modifyMainMsg(TopicMessage msg, Long topicId, Long groupId){
    TopicMessage topicMsg = TopicMessage.findById(msg.id);
    topicMsg.text = msg.text;
    msg.createDate = new Date();
    topicMsg.save();
  }

  public static void editMsg(TopicMessage msg, Long topicId, Long groupId) {
    Command group = Command.findById(groupId);
    TopicMessage topicMsg = TopicMessage.findById(msg.id);
    topicMsg.text = msg.text;
    msg.createDate = new Date();
    topicMsg.save();
    indexTopic(topicId, group.id);
  }

  public static void removeGroup(Long groupId) {
    Command group = Command.findById(groupId);
    if (group.users.size() > 1) {
      index(groupId);
    }
    User user = group.users.get(0);
    User userState = User.findById(user.id);
    if (group.usersForAprove.size() != 0) {
      index(groupId);
    }
    userState.command = null;
    userState.role = User.ROLE_USER;
    userState.save();
    group.users = null;
    group.founderUser = null;
    group.usersForAprove = null;
    for (User us : group.usersForInvite) {
      User userForAprove = User.findById(us.id);
      userForAprove.commandsForAprove.remove(group);
      userForAprove.save();
    }
    group.usersForInvite = null;
    List<Topic> tempTopics = group.topics;
    for (Topic top : tempTopics) {
      Topic topic = Topic.findById(top.id);
      for (TopicMessage msg : topic.messages) {
        TopicMessage message = TopicMessage.findById(msg.id);
        message.from = null;
        message.topic = null;
        message.save();

      }
      int length = topic.messages.size() - 1;
      topic.save();
      for (int i = length; i >= 0; i--) {
        topic.messages.remove(i);
      }
    }

    group.topics = null;
    group.save();
    for (Topic top : tempTopics) {
      Topic topic = Topic.findById(top.id);
      topic.delete();
    }
    group.delete();
    Query query = JPA.em().createQuery("delete from TopicMessage where topic_id IS NULL");
    query.executeUpdate();
    user = SessionHelper.getCurrentUser(session);
    if (userState.id.equals(user.id)) {
      SessionHelper.setCurrentUser(session, userState);
    }
    UserController.index(user.id);
  }

  public static void removeMessage(Long msgId, Long groupId) {
    TopicMessage message = TopicMessage.findById(msgId);
    Long topicId = message.topic.id;
    Topic topic = Topic.findById(topicId);
    topic.messages.remove(message);
    topic.save();
    int length = topic.messages.size() - 1;
    if (length < 0) {
      topic.lastUpdateDate = null;
      topic.lastUpdateUserId = null;
      topic.lastUpdateUserName = null;
      topic.lastUpdateUserLastName = null;
      topic.save();
    } else {
      TopicMessage msg = topic.messages.get(length);
      topic.lastUpdateDate = msg.createDate;
      topic.lastUpdateUserId = msg.from.id;
      topic.lastUpdateUserName = msg.from.name;
      topic.lastUpdateUserLastName = msg.from.lastName;
      topic.save();
    }
    message.from = null;
    message.topic = null;
    message.save();
    message.delete();

    indexTopic(topicId, groupId);
  }

  public static void removeMainMessage(Long msgId, Long groupId) {
    deleteMainMsg(msgId, groupId);
    groupTopics(groupId);
  }

  public static void removePublicMainMessage(Long msgId, Long groupId) {
    deleteMainMsg(msgId, groupId);
    publicTopics(groupId);
  }

  protected static void deleteMainMsg(Long msgId, Long groupId) {
    TopicMessage message = TopicMessage.findById(msgId);
    Long topicId = message.topic.id;
    Topic topic = Topic.findById(topicId);
    topic.messages.remove(message);
    topic.save();
    message.from = null;
    message.topic = null;
    message.save();
    message.delete();
  }

  public static void removeTopic(Long topicId, Long groupId) {
    Topic topic = Topic.findById(topicId);
    Boolean isPublic = topic.publicTopic;
    if (!isPublic) {
      if (!memberOfGroup(groupId)) {
        index(groupId);
      }
    }
    Command command = Command.findById(groupId);
    command.topics.remove(topic);
    command.save();
    for (TopicMessage msg : topic.messages) {
      TopicMessage message = TopicMessage.findById(msg.id);
      message.from = null;
      message.topic = null;
      message.save();
    }
    topic.messages = null;
    topic.save();
    topic.delete();
    Query query = JPA.em().createQuery("delete from TopicMessage where topic_id IS NULL");
    query.executeUpdate();
    if (isPublic) {
      publicTopics(groupId);
    } else {
      groupTopics(groupId);
    }
  }

    private static void logGroupMemberActivity(User user, Command team, int action) {
        TeamMemberActivity activity = new TeamMemberActivity(team, user, action, new Date());
        activity.create();
    }
}