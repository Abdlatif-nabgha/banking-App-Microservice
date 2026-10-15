package com.bank.accountservice.infrastructure.in.rest.idempotency;

import com.bank.accountservice.infrastructure.out.persistence.idempotency.IdempotencyJpaRepository;
import com.bank.accountservice.infrastructure.out.persistence.idempotency.IdempotencyRecordJpaEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
public class IdempotencyAspect {

    private final IdempotencyJpaRepository repository;
    private final ObjectMapper objectMapper;

    @Around("@annotation(idempotent)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String key = request.getHeader(idempotent.headerName());

        // 1. Si aucun header Idempotency-Key n'est fourni, on exécute normalement
        if (key == null || key.isBlank()) {
            return joinPoint.proceed();
        }

        // 2. On calcule le hash des arguments passés à la méthode (le Body JSON)
        String currentPayload = extractPayload(joinPoint);
        String currentHash = RequestHasher.computeHash(currentPayload);

        // 3. On cherche si la clé existe déjà
        Optional<IdempotencyRecordJpaEntity> existingRecord = repository.findById(key);

        if (existingRecord.isPresent()) {
            IdempotencyRecordJpaEntity record = getRecord(existingRecord, currentHash);

            // Déjà complété -> On désérialise et on retourne le JSON sans toucher au compte !
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            return objectMapper.readValue(record.getResponseBody(), signature.getReturnType());
        }

        // 4. Première fois : on enregistre le statut PENDING
        IdempotencyRecordJpaEntity pendingRecord = IdempotencyRecordJpaEntity.builder()
                .key(key)
                .requestHash(currentHash)
                .status("PENDING")
                .build();
        repository.save(pendingRecord);

        try {
            // 5. On exécute l'opération réelle (ex: deposit)
            Object result = joinPoint.proceed();

            // 6. On sauvegarde la réponse avec le statut COMPLETED
            String responseJson = objectMapper.writeValueAsString(result);
            pendingRecord.setStatus("COMPLETED");
            pendingRecord.setResponseBody(responseJson);
            repository.save(pendingRecord);

            return result;
        } catch (Throwable t) {
            // En cas d'erreur métier ou technique, on supprime la clé pour permettre de réessayer
            repository.deleteById(key);
            throw t;
        }
    }

    private static @NonNull IdempotencyRecordJpaEntity getRecord(Optional<IdempotencyRecordJpaEntity> existingRecord, String currentHash) {
        IdempotencyRecordJpaEntity record = existingRecord.get();

        // Sécurité : même clé mais body différent -> 422 Unprocessable Entity
        if (!record.getRequestHash().equals(currentHash)) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Idempotency-Key reuse error: payload does not match original request"
            );
        }

        // Requête concurrente en cours -> 409 Conflict
        if ("PENDING".equals(record.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A request with this Idempotency-Key is currently being processed"
            );
        }
        return record;
    }

    private String extractPayload(ProceedingJoinPoint joinPoint) {
        try {
            return objectMapper.writeValueAsString(joinPoint.getArgs());
        } catch (Exception e) {
            return "";
        }
    }
}
