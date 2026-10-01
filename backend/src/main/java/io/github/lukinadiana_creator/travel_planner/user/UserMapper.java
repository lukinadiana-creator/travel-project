package io.github.lukinadiana_creator.travel_planner.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return new UserDto(
                user.getName(),
                user.getEmail()
        );
    }
}
