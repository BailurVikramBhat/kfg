package com.kfg.user.strategy.factory;

import com.kfg.user.domain.KycType;
import com.kfg.user.exception.UnsupportedKycTypeException;
import com.kfg.user.strategy.kyc.KycVerificationStrategy;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
