package com.kfg.onboarding.strategy.factory;

import com.kfg.onboarding.domain.KycType;
import com.kfg.onboarding.exception.UnsupportedKycTypeException;
import com.kfg.onboarding.strategy.kyc.KycVerificationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class KycStrategyFactory {
    private final Map<KycType, KycVerificationStrategy> strategies;

    public KycStrategyFactory(
            List<KycVerificationStrategy> discoveredStrategies
    ) {
        this.strategies = discoveredStrategies.stream()
                .collect(Collectors.toUnmodifiableMap(
                        KycVerificationStrategy::supportedType,
                        Function.identity()
                ));
    }

    public KycVerificationStrategy getStrategy(KycType kycType) {
        KycType requestedType = kycType!=null ? kycType : KycType.STANDARD;
        KycVerificationStrategy strategy = this.strategies.get(requestedType);
        if (strategy == null) {
            throw new UnsupportedKycTypeException(
                    "No KYC strategy configured for: " + requestedType
            );
        }
        return strategy;
    }
}
