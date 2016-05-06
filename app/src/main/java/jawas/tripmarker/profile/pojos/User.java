package jawas.tripmarker.profile.pojos;

public class User {

    private String id;
    private String name;
    private int age;
    private String homeplace;
    private String gender;

    public User(){}

    public User( String id, String name, int age, String homeplace, String gender ){
        this.id = id;
        this.name = name;
        this.age = age;
        this.homeplace = homeplace;
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getHomeplace() {
        return homeplace;
    }

    public String getName() {
        return name;
    }
}
