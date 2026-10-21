package com.ecommerce.reglog.dto.response;

import com.ecommerce.reglog.enums.AddressType;

public class AddressResponse {

    private Long id;
    private AddressType addressType;
    private String streetAddress;
    private String apartmentSuite;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private boolean isDefault;

    public AddressResponse() {}

    public AddressResponse(Long id, AddressType addressType, String streetAddress, String apartmentSuite,
                           String city, String state, String postalCode, String country, boolean isDefault) {
        this.id = id;
        this.addressType = addressType;
        this.streetAddress = streetAddress;
        this.apartmentSuite = apartmentSuite;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
        this.isDefault = isDefault;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AddressType getAddressType() {
        return addressType;
    }

    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getApartmentSuite() {
        return apartmentSuite;
    }

    public void setApartmentSuite(String apartmentSuite) {
        this.apartmentSuite = apartmentSuite;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
