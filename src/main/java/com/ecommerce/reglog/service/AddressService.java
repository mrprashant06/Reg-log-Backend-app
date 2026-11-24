package com.ecommerce.reglog.service;

import com.ecommerce.reglog.dto.request.AddressRequest;
import com.ecommerce.reglog.dto.response.AddressResponse;
import java.util.List;

public interface AddressService {
    List<AddressResponse> getUserAddresses(Long userId);
    AddressResponse getAddressById(Long userId, Long addressId);
    AddressResponse addAddress(Long userId, AddressRequest request);
    AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request);
    void deleteAddress(Long userId, Long addressId);
    void setDefaultAddress(Long userId, Long addressId);
}
