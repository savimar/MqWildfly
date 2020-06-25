package ru.savimar.mqwildfly.Entity;

import org.joda.time.LocalDateTime;
import javax.persistence.*;


@Entity
@Table(name = "documents")
public class PersonDocument {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "person_id",  referencedColumnName = "id")
    private Person person;
    private String docKindName;
    private int docSubType;
    private String docSerie;
    private String docNumber;
    private LocalDateTime docDate;
    private String whoSign;
    private String divisionCode;
    private String docFileId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getDocKindName() {
        return docKindName;
    }

    public void setDocKindName(String docKindName) {
        this.docKindName = docKindName;
    }

    public int getDocSubType() {
        return docSubType;
    }

    public void setDocSubType(int docSubType) {
        this.docSubType = docSubType;
    }

    public String getDocSerie() {
        return docSerie;
    }

    public void setDocSerie(String docSerie) {
        this.docSerie = docSerie;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public LocalDateTime getDocDate() {
        return docDate;
    }

     public String getWhoSign() {
        return whoSign;
    }

    public void setWhoSign(String whoSign) {
        this.whoSign = whoSign;
    }

    public String getDivisionCode() {
        return divisionCode;
    }

    public void setDivisionCode(String divisionCode) {
        this.divisionCode = divisionCode;
    }

    public String getDocFileId() {
        return docFileId;
    }

    public void setDocFileId(String docFileId) {
        this.docFileId = docFileId;
    }

    public void setDocDate(LocalDateTime docDate) {
        this.docDate = docDate;
    }

    @Override
    public String toString() {
        return "PersonDocument{" +
                "id=" + id +
                ", docKindName='" + docKindName + '\'' +
                ", docSubType=" + docSubType +
                ", docSerie='" + docSerie + '\'' +
                ", docNumber='" + docNumber + '\'' +
                ", docDate=" +  docDate +
                ", whoSign='" + whoSign + '\'' +
                ", divisionCode='" + divisionCode + '\'' +
                ", docFileId='" + docFileId + '\'' +
                '}';
    }
}
