package in.scalive.Velora.service;

import java.util.List;

import in.scalive.Velora.dto.request.UpdateUserRequestDTO;
import in.scalive.Velora.dto.request.UserRequestDTO;
import in.scalive.Velora.dto.response.UserResponseDTO;
import in.scalive.Velora.entity.Role;

public interface UserService {
	UserResponseDTO createUser(UserRequestDTO userRequest,Role role);

	UserResponseDTO getUserById(Long id);

	UserResponseDTO getUserByEmail(String email);

	List<UserResponseDTO> getAllUsers();

	List<UserResponseDTO> getActiveUsers();

	UserResponseDTO updateUser(Long id, UpdateUserRequestDTO userRequest);

	void activateUser(Long id);

	void deActivateUser(Long id);

	List<UserResponseDTO> searchUser(String keyword);

	boolean existsByEmail(String email);
}
