package com.codewithmike.eventify.auth;

import com.codewithmike.eventify.user.User;
import com.codewithmike.eventify.user.UserRepository;
import com.codewithmike.eventify.user.dto.UserCreateRequestDto;
import com.codewithmike.eventify.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user signup and login")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with firstname, lastname, email, and password.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"message\": \"user_created\"}")
                            )
                    ),

                    @ApiResponse(
                            responseCode = "400",
                            description = "Email already taken",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"error\": \"username_taken\"}")
                            )
                    )
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserCreateRequestDto req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent())
            return ResponseEntity.badRequest().body(Map.of("error","username_taken"));

        var user = User.builder()
                .firstname(req.getFirstname())
                .lastname(req.getLastname())
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .build();

        userRepository.save(user);
        return ResponseEntity.status(201).body(Map.of("message","user_created"));
    }


    @Operation(
            summary = "Login user",
            description = "Authenticates a user with email and password, returning a JWT token upon success.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful, returns JWT token",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{\"token\": \"<jwt_token_here>\"}")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid email or password",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(example = "{\"error\": \"unauthorized\"}")
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        String password = body.get("password");
        var opt = userRepository.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.status(401).build();
        var user = opt.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.status(401).build();
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(Map.of("token", token));
    }
}
