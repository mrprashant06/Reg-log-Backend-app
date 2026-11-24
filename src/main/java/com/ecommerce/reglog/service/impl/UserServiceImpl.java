package com.ecommerce.reglog.service.impl;

import com.ecommerce.reglog.dto.request.AddressRequest;
import com.ecommerce.reglog.dto.request.ChangePasswordRequest;
import com.ecommerce.reglog.dto.request.UpdateProfileRequest;
import com.ecommerce.reglog.dto.response.AddressResponse;
import com.ecommerce.reglog.dto.response.PageResponse;
import com.ecommerce.reglog.dto.response.UserProfileResponse;
import com.ecommerce.reglog.entity.Address;
import com.ecommerce.reglog.entity.Role;
import com.ecommerce.reglog.entity.User;
import com.ecommerce.reglog.enums.AddressType;
import com.ecommerce.reglog.enums.ERole;
import com.ecommerce.reglog.enums.UserStatus;
import com.ecommerce.reglog.exception.BadRequestException;
import com.ecommerce.reglog.exception.ResourceNotFoundException;
import com.ecommerce.reglog.repository.AddressRepository;
import com.ecommerce.reglog.repository.RoleRepository;
import com.ecommerce.reglog.repository.UserRepository;
import com.ecommerce.reglog.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           AddressRepository addressRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(Long userId) {
        User user = findUserById(userId);
        return mapToUserProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUserById(userId);
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber().trim());
        }
        User updated = userRepository.save(user);
        return mapToUserProfileResponse(updated);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirmation password do not match");
        }

        User user = findUserById(userId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password entered is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new BadRequestException("New password cannot be the same as old password");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        User user = findUserById(userId);

        Address address = new Address();
        address.setUser(user);
        address.setFullName(request.getFullName() != null && !request.getFullName().isBlank() ? request.getFullName() : user.getFullName());
        address.setMobileNumber(request.getMobileNumber() != null && !request.getMobileNumber().isBlank() ? request.getMobileNumber() : user.getPhone());
        address.setAddressType(request.getAddressType() != null ? request.getAddressType() : AddressType.HOME);
        address.setStreetAddress(request.getStreetAddress());
        address.setApartmentSuite(request.getApartmentSuite());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry() != null && !request.getCountry().isBlank() ? request.getCountry() : "India");

        if (request.isDefault() || user.getAddresses().isEmpty()) {
            user.getAddresses().forEach(a -> a.setDefault(false));
            address.setDefault(true);
        } else {
            address.setDefault(false);
        }

        user.addAddress(address);
        Address savedAddress = addressRepository.save(address);

        return mapToAddressResponse(savedAddress);
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
        User user = findUserById(userId);
        Address targetAddress = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));

        user.getAddresses().forEach(a -> a.setDefault(a.getId().equals(targetAddress.getId())));
        addressRepository.saveAll(user.getAddresses());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserProfileResponse> adminGetAllUsers(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage;

        if (StringUtils.hasText(keyword)) {
            userPage = userRepository.searchUsers(keyword.trim(), pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        List<UserProfileResponse> content = userPage.getContent().stream()
                .map(this::mapToUserProfileResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse adminGetUserById(Long id) {
        User user = findUserById(id);
        return mapToUserProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse adminUpdateUserStatus(Long id, UserStatus newStatus) {
        User user = findUserById(id);
        user.setStatus(newStatus);
        User updated = userRepository.save(user);
        return mapToUserProfileResponse(updated);
    }

    @Override
    @Transactional
    public UserProfileResponse adminAssignRole(Long id, ERole roleName) {
        User user = findUserById(id);
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));

        user.getRoles().add(role);
        User updated = userRepository.save(user);
        return mapToUserProfileResponse(updated);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        List<AddressResponse> addressResponses = user.getAddresses().stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getStatus(),
                roles,
                addressResponses,
                user.getLastLoginAt(),
                user.getCreatedAt()
        );
    }

    private AddressResponse mapToAddressResponse(Address addr) {
        return AddressResponse.fromEntity(addr);
    }
}
