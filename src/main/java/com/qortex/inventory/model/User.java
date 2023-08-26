package com.qortex.inventory.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;


@Data
@Entity
@Table(name = "iam_user")
public class User {
    @Id
    @GenericGenerator(name = "generator", strategy = "guid", parameters = {})
    @GeneratedValue(generator = "generator")
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private String id;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "middle_name")
    private String middleName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "password")
    private String password;
    @Column(name = "mobile_no")
    private String mobileNo;
    @Column(name = "status_code")
    private String statusCode;
    @Column(name = "tenant_id")
    private String tenantId;
    @Column(name = "picture")
    private String picture;
    @Column(name = "email_id", nullable = false)
    private String emailId;
    @Column(name = "designation")
    private String designation;
    @Column(name = "national_id")
    private String nationalId;
    @Column(name = "national_id_expiry")
    private String nationalIdExpiry;

}
