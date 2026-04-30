package com.smart.smart_backend.infrastructure.adapter.habit;

import com.smart.smart_backend.application.dto.habit.DailyEntryWithLogsResult;
import com.smart.smart_backend.application.dto.habit.log.ExerciseLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.MoodLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.NutritionLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.PersonalLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.SleepLogResponseDto;
import com.smart.smart_backend.application.dto.habit.log.StudyLogResponseDto;
import com.smart.smart_backend.application.port.out.habit.DailyEntryRepositoryPort;
import com.smart.smart_backend.domain.model.habit.DailyEntry;
import com.smart.smart_backend.infrastructure.mapper.habit.DailyEntryEntityMapper;
import com.smart.smart_backend.infrastructure.mapper.habit.LogEntityMapper;
import com.smart.smart_backend.infrastructure.model.habit.DailyEntryEntity;
import com.smart.smart_backend.infrastructure.repository.habit.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DailyEntryRepositoryAdapter implements DailyEntryRepositoryPort, com.smart.smart_backend.application.port.out.register.DailyEntryRepositoryPort {

    private final JpaDailyEntryRepository jpaDailyEntryRepository;
    private final JpaStudyLogRepository jpaStudyLogRepository;
    private final JpaExerciseLogRepository jpaExerciseLogRepository;
    private final JpaNutritionLogRepository jpaNutritionLogRepository;
    private final JpaMoodLogRepository jpaMoodLogRepository;
    private final JpaSleepLogRepository jpaSleepLogRepository;
    private final JpaPersonalLogRepository jpaPersonalLogRepository;

    private final DailyEntryEntityMapper dailyEntryEntityMapper;
    private final LogEntityMapper logEntityMapper;

    @Override
    public Optional<DailyEntry> findByIdAndUserId(Long id, Long userId) {
        return jpaDailyEntryRepository.findByIdAndUserId(id, userId)
                .map(dailyEntryEntityMapper::toDomain);
    }

    @Override
    public Optional<DailyEntry> findByUserIdAndDate(Long userId, LocalDate date) {
        return jpaDailyEntryRepository.findByUserIdAndDate(userId, date)
                .map(dailyEntryEntityMapper::toDomain);
    }

    @Override
    public DailyEntry save(DailyEntry dailyEntry) {
        DailyEntryEntity entity = dailyEntryEntityMapper.toEntity(dailyEntry);
        DailyEntryEntity saved = jpaDailyEntryRepository.save(entity);
        return dailyEntryEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<DailyEntryWithLogsResult> findByUserIdAndDateWithLogs(Long userId, LocalDate date) {
        return jpaDailyEntryRepository.findByUserIdAndDate(userId, date)
                .map(this::mapToResult);
    }

    @Override
    public List<DailyEntryWithLogsResult> findWeeklyEntriesWithLogs(Long userId, LocalDate startDate, LocalDate endDate) {
        List<DailyEntryEntity> entries = jpaDailyEntryRepository.findAllByUserIdAndDateBetweenOrderByDateAsc(userId, startDate, endDate);
        
        List<Long> entryIds = entries.stream().map(DailyEntryEntity::getId).collect(Collectors.toList());
        
        // Optimize by fetching all logs for these entries at once
        var studyLogsMap = jpaStudyLogRepository.findAllByEntryIdIn(entryIds).stream()
                .map(logEntityMapper::toStudyDto)
                .collect(Collectors.groupingBy(StudyLogResponseDto::entryId));
                
        var exerciseLogsMap = jpaExerciseLogRepository.findAllByEntryIdIn(entryIds).stream()
                .map(logEntityMapper::toExerciseDto)
                .collect(Collectors.groupingBy(ExerciseLogResponseDto::entryId));
                
        var nutritionLogsMap = jpaNutritionLogRepository.findAllByEntryIdIn(entryIds).stream()
                .map(logEntityMapper::toNutritionDto)
                .collect(Collectors.groupingBy(NutritionLogResponseDto::entryId));
                
        var moodLogsMap = jpaMoodLogRepository.findAllByEntryIdIn(entryIds).stream()
                .map(logEntityMapper::toMoodDto)
                .collect(Collectors.groupingBy(MoodLogResponseDto::entryId));
                
        var sleepLogsMap = jpaSleepLogRepository.findAllByEntryIdIn(entryIds).stream()
                .map(logEntityMapper::toSleepDto)
                .collect(Collectors.groupingBy(SleepLogResponseDto::entryId));

        var personalLogsMap = jpaPersonalLogRepository.findAllByEntryIdIn(entryIds).stream()
                .map(logEntityMapper::toPersonalDto)
                .collect(Collectors.groupingBy(PersonalLogResponseDto::entryId));

        return entries.stream().map(entry -> {
            Long id = entry.getId();
            return logEntityMapper.toResult(
                entry,
                studyLogsMap.getOrDefault(id, List.of()).stream().findFirst().orElse(null),
                exerciseLogsMap.getOrDefault(id, List.of()).stream().findFirst().orElse(null),
                nutritionLogsMap.getOrDefault(id, List.of()).stream().findFirst().orElse(null),
                moodLogsMap.getOrDefault(id, List.of()).stream().findFirst().orElse(null),
                sleepLogsMap.getOrDefault(id, List.of()).stream().findFirst().orElse(null),
                personalLogsMap.getOrDefault(id, List.of())
            );
        }).collect(Collectors.toList());
    }

    private DailyEntryWithLogsResult mapToResult(DailyEntryEntity entry) {
        Long entryId = entry.getId();
        
        StudyLogResponseDto studyLog = jpaStudyLogRepository.findByEntryId(entryId)
                .map(logEntityMapper::toStudyDto)
                .orElse(null);
                
        ExerciseLogResponseDto exerciseLog = jpaExerciseLogRepository.findByEntryId(entryId)
                .map(logEntityMapper::toExerciseDto)
                .orElse(null);
                
        NutritionLogResponseDto nutritionLog = jpaNutritionLogRepository.findByEntryId(entryId)
                .map(logEntityMapper::toNutritionDto)
                .orElse(null);
                
        MoodLogResponseDto moodLog = jpaMoodLogRepository.findByEntryId(entryId)
                .map(logEntityMapper::toMoodDto)
                .orElse(null);
                
        SleepLogResponseDto sleepLog = jpaSleepLogRepository.findByEntryId(entryId)
                .map(logEntityMapper::toSleepDto)
                .orElse(null);

        List<PersonalLogResponseDto> personalLogs = jpaPersonalLogRepository.findAllByEntryIdIn(List.of(entryId)).stream()
                .map(logEntityMapper::toPersonalDto)
                .collect(Collectors.toList());

        return logEntityMapper.toResult(entry, studyLog, exerciseLog, nutritionLog, moodLog, sleepLog, personalLogs);
    }
}
