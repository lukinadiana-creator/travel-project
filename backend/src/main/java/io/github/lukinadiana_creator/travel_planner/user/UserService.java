package io.github.lukinadiana_creator.travel_planner.user;

import io.github.lukinadiana_creator.travel_planner.exception.EmailAlreadyExistsException;
import io.github.lukinadiana_creator.travel_planner.exception.EntityNotFoundException;
import io.github.lukinadiana_creator.travel_planner.exception.ExternalServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public void createUser(String email, String password) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyExistsException("Пользователь с email " + email + "уже существует");
        }
        User user = new User(null, email, password, UserRole.USER);

        try {
            userRepository.save(user);
        } catch (DataAccessException e) {
            throw new ExternalServiceException("Не удалось сохранить пользователя", e);
        }
    }

    public UserDto getUser(String email) {
        User user = userRepository.findUserByEmailIgnoreCase(email).orElseThrow(() -> new EntityNotFoundException("Пользователь с email:" + email + "не найден"));

        return userMapper.toDto(user);
    }

    public UserDto updateUsername(String email, String name) {
        User user = userRepository.findUserByEmailIgnoreCase(email).orElseThrow(() -> new EntityNotFoundException("Пользователь с email:" + email + "не найден"));

        user.setName(name);
        try {
            userRepository.save(user);
            return userMapper.toDto(user);
        } catch (DataAccessException e) {
            throw new ExternalServiceException("Не удалось обновить данные о пользователе", e);
        }
    }
}
