package com.ecommerce.reglog.dto.request;

import com.ecommerce.reglog.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddressRequest {

    private String fullName;

    private String mobileNumber;

    private AddressType addressType = AddressType.HOME;

    @NotBlank(message = "Street address is required")
    @Size(max = 255, message = "Street address cannot exceed 255 characters")
    private String streetAddress;

    @Size(max = 100, message = "House/Flat/Apartment number cannot exceed 100 characters")
    private String apartmentSuite;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @NotBlank(message = "State/Province is required")
    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @NotBlank(message = "Postal/PIN code is required")
    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    private String postalCode;

    private String country = "India";

    private boolean isDefault = false;

    public AddressRequest() {}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
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

    public String getHouseFlatNumber() {
        return apartmentSuite;
    }

    public void setHouseFlatNumber(String houseFlatNumber) {
        this.apartmentSuite = houseFlatNumber;
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

    public String getPinCode() {
        return postalCode;
    }

    public void setPinCode(String pinCode) {
        this.postalCode = pinCode;
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
