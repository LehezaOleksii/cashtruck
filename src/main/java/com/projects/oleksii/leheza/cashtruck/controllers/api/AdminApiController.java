package com.projects.oleksii.leheza.cashtruck.controllers.api;

import com.projects.oleksii.leheza.cashtruck.enums.Role;
import com.projects.oleksii.leheza.cashtruck.exception.ErrorResponse;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@Validated
@Tag(name = "Admins", description = "Methods related to admins")
public class AdminApiController {

    private final UserService userService;

    @Operation(summary = "Update user plan by user Id", description = "Update an existing user plan by its Id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User plan updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User is not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PutMapping(path = "/users/{userId}")
    ResponseEntity<Role> updateUserPlanStatus(@PathVariable("userId") Long userId,
                                              @RequestBody String status) {
        Role role = userService.updateUserRole(userId, Role.valueOf(status));
        return new ResponseEntity<>(role, HttpStatus.OK);
    }
}
