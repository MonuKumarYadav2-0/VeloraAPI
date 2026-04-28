package in.scalive.Velora.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.scalive.Velora.dto.request.UpdateUserRequestDTO;
import in.scalive.Velora.dto.request.UserRequestDTO;
import in.scalive.Velora.dto.response.ApiResponseDTO;
import in.scalive.Velora.dto.response.UserResponseDTO;
import in.scalive.Velora.entity.Role;
import in.scalive.Velora.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
      private UserService serv;
      
      @Autowired
	  public UserController(UserService serv) {
		this.serv = serv;
	  }
//      @PostMapping("/register-user")
//      public ResponseEntity<ApiResponseDTO<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO userRequest){
//    	        UserResponseDTO dto= serv.createUser(userRequest,Role.USER);
//    	        ApiResponseDTO<UserResponseDTO> obj=ApiResponseDTO.success("user Created successfully",dto);
//    	        return new ResponseEntity<>(obj,HttpStatus.CREATED);
//      }
//      @PostMapping("/register-seller")
//      public ResponseEntity<ApiResponseDTO<UserResponseDTO>> createSeller(@Valid @RequestBody UserRequestDTO userRequest){
//    	        UserResponseDTO dto= serv.createUser(userRequest,Role.SELLER);
//    	        ApiResponseDTO<UserResponseDTO> obj=ApiResponseDTO.success("Seller Created successfully",dto);
//    	        return new ResponseEntity<>(obj,HttpStatus.CREATED);
//      }
      
      
      @GetMapping("/{id}")
      public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUserById(@PathVariable Long id){
    	        UserResponseDTO dto= serv.getUserById(id);
    	        ApiResponseDTO<UserResponseDTO> obj=ApiResponseDTO.success(dto);
    	        return  ResponseEntity.ok(obj);
      }
      
      @GetMapping("/email/{email}")
      public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUserByEmail(@PathVariable String email){
    	        UserResponseDTO dto= serv.getUserByEmail(email);
    	        ApiResponseDTO<UserResponseDTO> obj=ApiResponseDTO.success(dto);
    	        return  ResponseEntity.ok(obj);
      }
      
      @PreAuthorize("hasRole('ADMIN')")
      @GetMapping
      public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getAllUsers(){
    	        List<UserResponseDTO>users=serv.getAllUsers();
    	        ApiResponseDTO<List<UserResponseDTO>> obj=ApiResponseDTO.success(users);
    	        return  ResponseEntity.ok(obj);
      }
      
      @PreAuthorize("hasRole('ADMIN')")
      @GetMapping("/active")
      public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getActiveUsers(){
    	        List<UserResponseDTO>users=serv.getActiveUsers();
    	        ApiResponseDTO<List<UserResponseDTO>> obj=ApiResponseDTO.success(users);
    	        return  ResponseEntity.ok(obj);
      }
      
      @PutMapping("/{id}")
      public ResponseEntity<ApiResponseDTO<UserResponseDTO>> updateUser(@PathVariable Long id,@Valid @RequestBody UpdateUserRequestDTO userRequest ){
    	        UserResponseDTO dto= serv.updateUser(id,userRequest);
    	        ApiResponseDTO<UserResponseDTO> obj=ApiResponseDTO.success("user updated successfully",dto);
    	        return ResponseEntity.ok(obj);
      }
      
      @PreAuthorize("hasRole('ADMIN')")
      @PatchMapping("/{id}/activate")
      public ResponseEntity<ApiResponseDTO<Void>> activateUser(@PathVariable Long id){
    	        serv.activateUser(id);
    	        return ResponseEntity.ok(ApiResponseDTO.success("user activated successfully"));
      }
      
      @PreAuthorize("hasRole('ADMIN')")
      @PatchMapping("/{id}/deactivate")
      public ResponseEntity<ApiResponseDTO<Void>> deActivateUser(@PathVariable Long id){
    	        serv.deActivateUser(id);
    	        return ResponseEntity.ok(ApiResponseDTO.success("user de-activated successfully"));
      }
      
      @GetMapping("/search")
      public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> searchUsers(@RequestParam String keyword){
    	        List<UserResponseDTO>users=serv.searchUser(keyword);
    	        ApiResponseDTO<List<UserResponseDTO>> obj=ApiResponseDTO.success(users);
    	        return  ResponseEntity.ok(obj);
      }
      
      @GetMapping("/check-email")
      public ResponseEntity<ApiResponseDTO<Boolean>> checkEmailExists(@RequestParam String email){
    	        Boolean res=serv.existsByEmail(email);
    	        	 ApiResponseDTO<Boolean> obj=ApiResponseDTO.success(res?"Email already exists":"Email is available for use");
    	        return  ResponseEntity.ok(obj);
      }
      
      @GetMapping("/maikonhu")
      public String whoAmI(Authentication authentication) {
          return authentication.getName() + " -> " + authentication.getAuthorities();
      }
}
