package org.example;

import org.example.annotations.*;

@ClassInfo
@WithProxy(proxyName = "PersonProxy")
public class Person {

    @SetDefaultString(string = "John Doe")
    @Regex(regex = "[A-Z][a-z]* [A-Z][a-z]*( [A-Z][a-z]*)?")
    private String name;

    @SetDefaultString(string = "john@mail.com")
    @Regex(regex = "[\\w]+@[\\w]{2,4}(\\.[\\w]{2,4})+")
    private String email;

    @SetDefaultString(string = "0979836578")
    @Regex(regex = "[0-9]{10}")
    private String phone;


    public Person(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    @ProxyMethod
    public void introduceMyself() {
        System.out.println("Hi, I am "+name);
    }

    @ProxyMethod
    public String giveMyInfoTo(Person person) {
        return person.getName() + ", here is my info: "+email+", "+phone;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
