package com.jan.web.security.authentication;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.jan.web.security.request.LoginRequest;
import com.jan.web.security.request.SignupRequest;
import com.jan.web.security.response.JwtResponse;
import com.jan.web.security.response.MessageResponse;
import com.jan.web.security.role.Role;
import com.jan.web.security.role.RoleRepository;
import com.jan.web.security.role.RoleType;
import com.jan.web.security.user.User;
import com.jan.web.security.user.UserCreator;
import com.jan.web.security.user.UserDetailsImpl;
import com.jan.web.security.user.UserRepository;
import com.jan.web.security.utility.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController
{
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JsonWebTokenUtility jsonWebTokenUtility;
    private final UserCreator userCreator;
    private final AuthenticationListener authenticationListener;

    @Autowired
    public AuthenticationController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder encoder,
            JsonWebTokenUtility jsonWebTokenUtility,
            UserCreator userCreator,
            AuthenticationListener authenticationListener)
    {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jsonWebTokenUtility = jsonWebTokenUtility;
        this.userCreator = userCreator;
        this.authenticationListener = authenticationListener;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest)
    {
        if (userRepository.existsByUsername(signUpRequest.getUsername()))
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already taken!"));
        }

        //TODO Jan: validate that fields are not empty and add test cases.
        User user = userCreator.createUser(signUpRequest.getUsername(), encoder.encode(signUpRequest.getPassword()));
        Set<Role> roles = new HashSet<>();

        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
    {
        Authentication authentication;
        try
        {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) //TODO Jan: Test this case
        {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Bad credentials!"));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jsonWebTokenUtility.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwtToken, userDetails.getId(), userDetails.getUsername(), roles));
    }
}
