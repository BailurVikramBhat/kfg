package com.kfg.user.service;

import com.kfg.user.domain.User;
import com.kfg.user.dto.CreateUserCommand;
import com.kfg.user.dto.kyc.KycVerificationContext;
import com.kfg.user.dto.kyc.KycVerificationResult;
import com.kfg.user.dto.response.UserResponse;
import com.kfg.user.exception.DuplicateResourceException;
import com.kfg.user.exception.UserNotFoundException;
import com.kfg.user.mapper.UserMapper;
import com.kfg.user.repository.UserRepository;
import com.kfg.user.strategy.factory.KycStrategyFactory;
import com.kfg.user.strategy.kyc.KycVerificationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KycStrategyFactory kycStrategyFactory;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserCommand command) {
        ensureContactDetailsAreAvailable(command);
        KycVerificationContext kycContext = new KycVerificationContext(command.firstName(), command.lastName(), command.email(), command.phoneNumber());
        KycVerificationStrategy strategy = kycStrategyFactory.getStrategy(command.kycType());
        KycVerificationResult result = strategy.verify(kycContext);
        User user = User.builder()
                .firstName(command.firstName())
                .lastName(command.lastName())
                .email(command.email())
                .phoneNumber(command.phoneNumber())
                .kycStatus(result.status())
                .build();
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found with ID: " + id
                        )
                );
    }

    private void ensureContactDetailsAreAvailable(
            CreateUserCommand command
    ) {
        if (userRepository.existsByEmail(command.email())) {
            throw new DuplicateResourceException(
                    "Email already registered: " + command.email()
            );
        }

        if (userRepository.existsByPhoneNumber(command.phoneNumber())) {
            throw new DuplicateResourceException(
                    "Phone number already registered: "
                            + command.phoneNumber()
            );
        }
    }
}
