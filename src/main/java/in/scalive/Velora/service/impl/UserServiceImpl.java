package in.scalive.Velora.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.scalive.Velora.dto.request.UpdateUserRequestDTO;
import in.scalive.Velora.dto.request.UserRequestDTO;
import in.scalive.Velora.dto.response.UserResponseDTO;
import in.scalive.Velora.entity.Cart;
import in.scalive.Velora.entity.Role;
import in.scalive.Velora.entity.User;
import in.scalive.Velora.exception.DuplicateResourceException;
import in.scalive.Velora.exception.ResourceNotFoundException;
import in.scalive.Velora.repository.UserRepository;
import in.scalive.Velora.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private UserRepository userRepo;
	private PasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceImpl(UserRepository userRepo,PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder=passwordEncoder;
	}

	// mapping user to userResponseDTO
	public static UserResponseDTO mapToResponseDTO(User user) {
		return UserResponseDTO.builder().id(user.getId()).fullName(user.getFullName()).email(user.getEmail())
				.phone(user.getPhone()).address(user.getAddress()).isActive(user.getIsActive()).build();
	}

	private User findUserById(Long id) {
		Optional<User> opt = userRepo.findById(id);
		if (opt.isPresent()) {
			return opt.get();
		}
		throw new ResourceNotFoundException("User", "id", id);
	}

	public UserResponseDTO createUser(UserRequestDTO userRequest,Role role) {

	    if (existsByEmail(userRequest.getEmail())) {
	        throw new DuplicateResourceException("user", "email", userRequest.getEmail());
	    }

	    User user = User.builder()
	            .fullName(userRequest.getFullName())
	            .email(userRequest.getEmail())
	            .password(passwordEncoder.encode(userRequest.getPassword()))
	            .phone(userRequest.getPhone())
	            .address(userRequest.getAddress())
	            .role(role) // 🔥 dynamic role
	            .build();

	    Cart cart = Cart.builder().user(user).build();
	    user.setCart(cart);

	    User savedUser = userRepo.save(user);

	    return mapToResponseDTO(savedUser);
	}
	
	@Override
	public UserResponseDTO getUserById(Long id) {
		User user = findUserById(id);
		return mapToResponseDTO(user);
	}

	@Override
	public UserResponseDTO getUserByEmail(String email) {
		Optional<User> opt = userRepo.findByEmail(email);
		if (opt.isPresent()) {
			return mapToResponseDTO(opt.get());
		}
		throw new ResourceNotFoundException("user", "email", email);
	}

	@Override
	public List<UserResponseDTO> getAllUsers() {
		List<User> users = userRepo.findAll();
		List<UserResponseDTO> responseList = new ArrayList<>();
		for (User user : users) {
			responseList.add(mapToResponseDTO(user));
		}
		return responseList;
	}

	@Override
	public List<UserResponseDTO> getActiveUsers() {
		List<User> users = userRepo.findByIsActiveTrue();
		List<UserResponseDTO> responseList = new ArrayList<>();
		for (User user : users) {
			responseList.add(mapToResponseDTO(user));
		}
		return responseList;

	}

	@Override
	public UserResponseDTO updateUser(Long id, UpdateUserRequestDTO userRequest) {
		User user = findUserById(id);
		if (userRequest.getFullName() == null && userRequest.getPassword() == null && userRequest.getPhone() == null
				&& userRequest.getEmail() == null && userRequest.getAddress() == null) {
			throw new IllegalArgumentException("At least one field must be provided for updation");
		}
		if (userRequest.getFullName() != null) {
			if (userRequest.getFullName().isBlank()) {
				throw new IllegalArgumentException("FullName cannot be blank");
			}
			user.setFullName(userRequest.getFullName());
		}
		if (userRequest.getPassword() != null) {
		    if (userRequest.getPassword().isBlank()) {
		        throw new IllegalArgumentException("Password cannot be blank");
		    }
		    user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
		}
		if (userRequest.getPhone() != null) {
			if (userRequest.getPhone().isBlank()) {
				throw new IllegalArgumentException("Phone cannot be blank");
			}
			user.setPhone(userRequest.getPhone());
		}
		if (userRequest.getEmail() != null) {
			if (userRequest.getEmail().isBlank()) {
				throw new IllegalArgumentException("Email cannot be blank");
			}
			if(userRepo.existsByEmail(userRequest.getEmail())) {
				throw new DuplicateResourceException("user","email",userRequest.getEmail());
			}
			user.setEmail(userRequest.getEmail());
		}
		if (userRequest.getAddress() != null) {
			if (userRequest.getAddress().isBlank()) {
				throw new IllegalArgumentException("Address cannot be blank");
			}
			user.setAddress(userRequest.getAddress());
		}
		
		User updatedUser=userRepo.save(user);
		return mapToResponseDTO(updatedUser);
	}

	@Override
	public void activateUser(Long id) {
		User user = findUserById(id);
		user.setIsActive(true);
		userRepo.save(user);

	}

	@Override
	public void deActivateUser(Long id) {
		User user = findUserById(id);
		user.setIsActive(false);
		userRepo.save(user);

	}

	@Override
	public List<UserResponseDTO> searchUser(String keyword) {
		List<User> users = userRepo.searchByNameOrEmail(keyword);
		List<UserResponseDTO> responseList = new ArrayList<>();
		for (User user : users) {
			responseList.add(mapToResponseDTO(user));
		}
		return responseList;

	}

	@Override
	public boolean existsByEmail(String email) {
		return userRepo.existsByEmail(email);
	}

}
