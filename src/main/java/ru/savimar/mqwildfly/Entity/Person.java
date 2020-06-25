package ru.savimar.mqwildfly.Entity;

import javax.persistence.*;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.util.List;

@Entity
@Table(name = "persons")
public class Person {

    @Id
    @GeneratedValue
    private long id;
    private String lastName;
    private String firstName;
    private String middleName;
    private LocalDate birthDate;
    private String snils;
    private String mobilePhone;
    private String ssoId;
    @OneToMany(fetch=FetchType.EAGER, cascade = {CascadeType.ALL})
    @JoinColumn(name = "person_id",  referencedColumnName = "id")
    @Column(nullable = true)
    private List<PersonDocument> documents;

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", lastName='" + lastName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleName='" + middleName + '\'' +
                ", birthDate=" + birthDate.toString(DateTimeFormat.shortDate()) +
                ", snils='" + snils + '\'' +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", ssoId='" + ssoId + '\'' +
                ", documents=" + printDocuments(documents)+
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getSnils() {
        return snils;
    }

    public void setSnils(String snils) {
        this.snils = snils;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getSsoId() {
        return ssoId;
    }

    public void setSsoId(String ssoId) {
        this.ssoId = ssoId;
    }

    public List<PersonDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<PersonDocument> documents) {
        this.documents = documents;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    private String printDocuments (List<PersonDocument> docs){
        StringBuilder sb = new StringBuilder(", ");
        for (PersonDocument doc:docs) {
           sb.append(doc.toString());
        }
        return sb.toString();
    }
}
