package ro.mpp.triathlon.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.mpp.triathlon.model.Referee;
import ro.mpp.triathlon.repository.IRefereeRepo;
import ro.mpp.triathlon.rest.security.JwtUtils;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/triathlon/auth")
public class AuthController {

    @Autowired
    private IRefereeRepo refereeRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody EventController.LoginRequest loginRequest) {
        Referee referee = refereeRepository.findByUsernameAndPassword(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        if (referee == null) {
            return new ResponseEntity<>("Username sau parola incorecta!", HttpStatus.UNAUTHORIZED);
        }

        String token = JwtUtils.generateToken(loginRequest.getUsername());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("username", loginRequest.getUsername());
        response.put("name", referee.getName());

        return ResponseEntity.ok(response);
    }
}