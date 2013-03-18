package modelDTO;

import models.User;

/**
 * TODO class javadoc
 */
public class SimpleUser {

    public Long id;
    public String name;
    public String lastName;
    public Boolean sex;
    public Integer age;
    public String email;
    public Boolean hasAvatar;


    public SimpleUser(Long id, String name, String lastName, Boolean sex, Integer age, String email, Boolean hasAvatar) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.sex = sex;
        this.age = age;
        this.email = email;
        this.hasAvatar = hasAvatar;
    }


    public static SimpleUser fromFullUser(User user) {
        return new SimpleUser(user.id, user.name, user.lastName, user.sex, user.age, user.email, user.haveAvatar);
    }
}
