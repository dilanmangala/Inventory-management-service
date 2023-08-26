package com.qortex.inventory.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table(name = "cor_enterprise_temp")
public class EnterpriseDetails {

    @Id
    @GenericGenerator(name = "generator", strategy = "uuid2", parameters = {})
    @GeneratedValue(generator = "generator")
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private String id;

    @Column(name = "name_en")
    private String enterpriseNameEn; // english
    @Column(name = "name_ar")
    private String enterpriseNameAr; // arabic
    @Column(name = "logo")
    private String enterpriseLogo;
    @Column(name = "type")
    private String type;
    @Column(name = "trade_license_no")
    private String tradeLicenseNo;
    @Column(name = "trade_license_expiry")
    private String tradeLicenseExpiry;
    @Column(name = "trade_license_doc_upload")
    private String tradeLicenseDocUpload;// new 1
    @Column(name = "tax_registration_no")
    private String taxRegistrationNumber;
    @Column(name = "door_no")
    private String doorNo;
    @Column(name = "building_name")
    private String buildingName;
    @Column(name = "street_name")
    private String streetName;
    @Column(name = "city_name")
    private String cityName;
    @Column(name = "state")
    private String state;
    @Column(name = "country")
    private String country;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "status_code")
    private String statusCode;
    @Column(name = "zip_code")
    private String zipCode;
}
