package com.ecommerce.reglog.controller;

import com.ecommerce.reglog.dto.request.AddressRequest;
import com.ecommerce.reglog.dto.response.AddressResponse;
import com.ecommerce.reglog.dto.response.ApiResponse;
import com.ecommerce.reglog.security.UserPrincipal;
import com.ecommerce.reglog.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "*")
@Tag(name = "Delivery Addresses", description = "Endpoints for managing user delivery address book")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping
    @Operation(summary = "Get All User Addresses")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<AddressResponse> addresses = addressService.getUserAddresses(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Addresses retrieved", addresses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Address by ID")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddressById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        AddressResponse address = addressService.getAddressById(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Address retrieved", address));
    }

    @PostMapping
    @Operation(summary = "Create Delivery Address")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse address = addressService.addAddress(principal.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Address created successfully", address), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Delivery Address")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse address = addressService.updateAddress(principal.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", address));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Delivery Address")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        addressService.deleteAddress(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully"));
    }

    @PatchMapping("/{id}/default")
    @Operation(summary = "Set Default Delivery Address")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        addressService.setDefaultAddress(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Default address updated successfully"));
    }
}
