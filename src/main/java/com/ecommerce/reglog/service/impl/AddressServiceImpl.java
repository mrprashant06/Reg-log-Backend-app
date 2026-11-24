package com.ecommerce.reglog.service.impl;

import com.ecommerce.reglog.dto.request.AddressRequest;
import com.ecommerce.reglog.dto.response.AddressResponse;
import com.ecommerce.reglog.entity.Address;
import com.ecommerce.reglog.entity.User;
import com.ecommerce.reglog.enums.AddressType;
import com.ecommerce.reglog.exception.ResourceNotFoundException;
import com.ecommerce.reglog.repository.AddressRepository;
import com.ecommerce.reglog.repository.UserRepository;
import com.ecommerce.reglog.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(AddressResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        return AddressResponse.fromEntity(address);
    }

    @Override
    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<Address> existing = addressRepository.findByUserId(userId);

        Address address = new Address();
        address.setUser(user);
        address.setFullName(request.getFullName() != null && !request.getFullName().isBlank() ? request.getFullName().trim() : user.getFullName());
        address.setMobileNumber(request.getMobileNumber() != null && !request.getMobileNumber().isBlank() ? request.getMobileNumber().trim() : user.getPhone());
        address.setAddressType(request.getAddressType() != null ? request.getAddressType() : AddressType.HOME);
        address.setStreetAddress(request.getStreetAddress().trim());
        address.setApartmentSuite(request.getApartmentSuite());
        address.setCity(request.getCity().trim());
        address.setState(request.getState().trim());
        address.setPostalCode(request.getPostalCode().trim());
        address.setCountry(request.getCountry() != null && !request.getCountry().isBlank() ? request.getCountry().trim() : "India");

        if (request.isDefault() || existing.isEmpty()) {
            for (Address a : existing) {
                a.setDefault(false);
            }
            addressRepository.saveAll(existing);
            address.setDefault(true);
        } else {
            address.setDefault(false);
        }

        Address saved = addressRepository.save(address);
        return AddressResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        if (request.getFullName() != null) address.setFullName(request.getFullName().trim());
        if (request.getMobileNumber() != null) address.setMobileNumber(request.getMobileNumber().trim());
        if (request.getAddressType() != null) address.setAddressType(request.getAddressType());
        if (request.getStreetAddress() != null) address.setStreetAddress(request.getStreetAddress().trim());
        if (request.getApartmentSuite() != null) address.setApartmentSuite(request.getApartmentSuite().trim());
        if (request.getCity() != null) address.setCity(request.getCity().trim());
        if (request.getState() != null) address.setState(request.getState().trim());
        if (request.getPostalCode() != null) address.setPostalCode(request.getPostalCode().trim());
        if (request.getCountry() != null) address.setCountry(request.getCountry().trim());

        if (request.isDefault()) {
            List<Address> all = addressRepository.findByUserId(userId);
            for (Address a : all) {
                a.setDefault(a.getId().equals(addressId));
            }
            addressRepository.saveAll(all);
            address.setDefault(true);
        }

        address.setUpdatedAt(Instant.now());
        Address updated = addressRepository.save(address);
        return AddressResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        Address target = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        List<Address> all = addressRepository.findByUserId(userId);
        for (Address a : all) {
            a.setDefault(a.getId().equals(target.getId()));
        }
        addressRepository.saveAll(all);
    }
}
